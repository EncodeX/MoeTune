package com.uexperience.moetune.core;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.jakewharton.disklrucache.DiskLruCache;
import com.uexperience.moetune.tool.MD5Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/18
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MoeTunePlayer {
	private static final int DISK_FILE_CACHE_INDEX = 0;
	private static final int DISK_FILE_CACHE_VERSION = 1;

	/**
	 *
	 * 处理逻辑:
	 *
	 * IDLE: 空闲
	 *      初始化后为INITIALIZED状态
	 *      需要初始化的内容有
	 *      mState mAudioTrack mDiskCache(封装过的)
	 *
	 * INITIALIZED: 已初始化
	 *      已设置音频源uri
	 *      需要初始化的内容为
	 *      mURL mIsCompleted
	 *
	 * PREPARING: 准备中
	 *      初始化音频流 播放前的缓冲
	 *      新的AsyncTask
	 *      并且开始下载
	 *
	 * PREPARED: 准备结束
	 *      开始播放前的状态 可以开始播放
	 *      通知Service
	 *
	 * PLAYING: 正在播放
	 *      播放音频流中
	 *
	 * STOPPED: 停止播放
	 *      若完成播放则置mIsCompleted为真
	 *
	 * PAUSED: 暂停播放
	 *      暂停
	 *
	 * ERROR: 错误
	 *      发生错误
	 *
	 */

	public enum State {
		IDLE,
		INITIALIZED,
		PREPARING,
		PREPARED,
		PLAYING,
		STOPPED,
		PAUSED,
		ERROR;

		@Override
		public String toString() {
			return readableMap.get(this);
		}

		private static Map<State, String> readableMap;

		static {
			readableMap = new HashMap<State, String>(9);
			readableMap.put(IDLE, "IDLE");
			readableMap.put(INITIALIZED, "INITIALIZED");
			readableMap.put(PREPARING, "PREPARING");
			readableMap.put(PREPARED, "PREPARED");
			readableMap.put(PLAYING, "PLAYING");
			readableMap.put(STOPPED, "STOPPED");
			readableMap.put(PAUSED, "PAUSED");
			readableMap.put(ERROR, "ERROR");
		}
	}

	private URL mURL;
	private State mState;
	private File mCacheDir;
	private AudioTrack mAudioTrack;
	private DiskLruCache mDiskCache;
	private StreamingAsyncTask mStreamingTask;
	private int mBufferSize =  2 * AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);

	private boolean mIsCompleted = false;

	public void reset() {
		mURL = null;
		mState = State.IDLE;
//		mLooping = false;
		if (mAudioTrack != null) {
			mAudioTrack.pause();
			mAudioTrack.flush();
			mAudioTrack.stop();
			mAudioTrack.release();
		}

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize, AudioTrack.MODE_STREAM);
		mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
			@Override
			public void onMarkerReached(AudioTrack audioTrack) {
//				if (mCompletionListener != null) {
//					mHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							mCompletionListener.onCompletion(StreamingDownloadMediaPlayer.this);
//						}
//					});
//				}
			}

			@Override
			public void onPeriodicNotification(AudioTrack audioTrack) {

			}
		});
	}

	public File getCacheDir() {
		if (mCacheDir != null && mCacheDir.exists()) {
			mCacheDir.mkdirs();
		}
		return mCacheDir;
	}

	private DiskLruCache getDiskCache() {
		File cacheDir = getCacheDir();
		if (cacheDir != null && mDiskCache == null) {
			try {
				mDiskCache = DiskLruCache.open(cacheDir,DISK_FILE_CACHE_VERSION,1,200*1024*1024);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mDiskCache;
	}

	protected void handleInput(final URL url, final Decoder decoder) throws IOException, BitstreamException, DecoderException, InterruptedException {
		mStreamingTask = new StreamingAsyncTask() {
			@Override
			protected Void doInBackground(URL... params) {
				URL url1 = params[0];
				HttpURLConnection connection = null;
				InputStream inputStream = null;
				Bitstream bitstream = null;
				DiskLruCache diskCache = getDiskCache();
				String key = MD5Util.md5(url.getPath());
				DiskLruCache.Editor diskEditor = null;
				DiskLruCache.Snapshot mp3Snapshot = null;
				try {
					mp3Snapshot = diskCache.get(key);
					if (mp3Snapshot != null) {
						inputStream = mp3Snapshot.getInputStream(DISK_FILE_CACHE_INDEX);
						diskCache.flush();
					}else {
						connection = (HttpURLConnection) url1.openConnection();
						connection.connect();
						diskEditor = diskCache.edit(key);
						inputStream = new StreamingPipe(connection.getInputStream(),diskEditor.newOutputStream(DISK_FILE_CACHE_INDEX));
					}
					bitstream = new Bitstream(inputStream);
					Header header;
					boolean firstPrepared = false;
					int totalBytes = 0;
					int oneShootBytes = 0;
					int totalFrameSize = 0;
					while ((header = bitstream.readFrame()) != null && !isStopped) {
						if (isPaused) {
							pauseLock.lock();
							try {
								unpaused.await();
							}finally {
								pauseLock.unlock();
							}
						}

						if (mAudioTrack.getPlaybackRate() != header.frequency()) {
							mAudioTrack.setPlaybackRate(header.frequency());
						}

						if (!firstPrepared && totalBytes >= mBufferSize - 2 * oneShootBytes) {
							firstPrepared = true;
							notifyPrepared();
						}

						SampleBuffer decoderBuffer = (SampleBuffer) decoder.decodeFrame(header, bitstream);

						oneShootBytes = decoderBuffer.getBufferLength() * 2;
						short[] copyBuffer = new short[decoderBuffer.getBufferLength()];
						System.arraycopy(decoderBuffer.getBuffer(), 0, copyBuffer, 0, decoderBuffer.getBufferLength());
						mAudioTrack.write(copyBuffer, 0, copyBuffer.length);
						totalBytes += oneShootBytes;
						totalFrameSize += 1;
						playedTimeInMS += header.ms_per_frame();
						bitstream.closeFrame();
					}

					if (!isStopped) {
						mAudioTrack.setNotificationMarkerPosition(totalFrameSize);
					}

					if (diskEditor != null) {
						if (isStopped) {
							diskEditor.abort();
						}else {
							diskEditor.commit();
						}
						diskCache.flush();
					}
				} catch (final Exception e) {
					e.printStackTrace();
					if (diskEditor != null) {
						try {
							//任何情况下播放异常中断，都撤销磁盘存储
							diskEditor.abort();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					mState = State.ERROR;
					reset();
//					if (mErrorListener != null) {
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								mErrorListener.onError(MoeTunePlayer.this,e);
//							}
//						});
//					}
				} finally {
					if (mp3Snapshot!=null && inputStream != null) {
//						DiskLruCache.closeQuietly(inputStream);
						mp3Snapshot.close();
					}
					if (bitstream != null) {
						try {
							bitstream.close();
						} catch (BitstreamException e) {
							e.printStackTrace();
						}
					}
					if (connection != null) {
						connection.disconnect();
					}

					isStopped = true;
					isPaused = false;
					isPlaying = false;
				}
				return null;
			}
		};
		mState = State.PREPARING;
		mStreamingTask.execute(url);
	}

	public void prepareAsync() throws DecoderException, InterruptedException, BitstreamException, IOException {
		if (mState == State.INITIALIZED || mState == State.STOPPED) {
			handleInput(mURL, new Decoder());
		} else {
			throw new IllegalStateException("cannot prepareAsync in [" + mState + "] state");
		}
	}

	public void release() {
		if (mAudioTrack != null) {
			mAudioTrack.pause();
			mAudioTrack.flush();
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
		mCacheDir = null;
//		mHandler = null;
		mURL = null;
//		mPreparedListener = null;
		if (mStreamingTask != null) {
			if (!mStreamingTask.isCancelled()) {
				mStreamingTask.cancel(true);
			}
			mStreamingTask = null;
		}
		mState = State.IDLE;
	}

	private void notifyPrepared() {
		mState = State.PREPARED;
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				if (mPreparedListener != null) {
//					mPreparedListener.onPrepared(StreamingDownloadMediaPlayer.this);
//				}
//			}
//		});
	}

	public void start() {
		if (mState == State.PREPARED || mState == State.PAUSED) {
			mState = State.PLAYING;
			mAudioTrack.play();
			mStreamingTask.start();
		} else {
			throw new IllegalStateException("cannot start in [" + mState + "] state");
		}
	}

	public void pause() {
		if (mState == State.PLAYING) {
			mState = State.PAUSED;
			mAudioTrack.pause();
			mStreamingTask.pause();
		} else {
			throw new IllegalStateException("cannot pause in [" + mState + "] state");
		}
	}

	public void stop() {
		if (mState == State.PREPARED || mState == State.PAUSED || mState == State.PLAYING || mState == State.PREPARING) {
			mState = State.STOPPED;
			mStreamingTask.stop();
			mAudioTrack.pause();
			mAudioTrack.flush();
			mAudioTrack.stop();
		} else {
			throw new IllegalStateException("cannot stop in [" + mState + "] state");
		}
	}

	public void seekTo(long millisecond) {
		//TODO
	}

	public long getCurrentPosition() {
		return Math.round(mStreamingTask.getPlayedTimeInMS());
	}

	public long getDuration() {
		//TODO
		return 0;
	}

	public boolean isPlaying() {
		return mState == State.PLAYING;
	}

	public boolean isPaused() {
		return mState == State.PAUSED;
	}

	public boolean isCompleted() {
		return this.mIsCompleted;
	}

	public boolean isPreparing() {
		return mState == State.PREPARING;
	}

	public  boolean isPrepared() {
		return mState == State.PREPARED;
	}

	private abstract class StreamingAsyncTask extends AsyncTask<URL, Void, Void> {
		boolean isPaused = false;
		boolean isStopped = false;
		boolean isPlaying = false;
		ReentrantLock pauseLock = new ReentrantLock();
		Condition unpaused = pauseLock.newCondition();
		float playedTimeInMS;

		public void pause() {
			pauseLock.lock();
			try {
				isPaused = true;
				isPlaying = false;
			} finally {
				pauseLock.unlock();
			}
		}

		public void start() {
			if (isCancelled()) {
				throw new IllegalStateException("play task has been stopped and cancelled");
			}
			resume();
		}

		public void stop() {
			if (isPaused) {
				resume();
			}
			isStopped = true;
			isPlaying = false;
			cancel(true);
		}

		public void resume() {
			pauseLock.lock();
			isPaused = false;
			isPlaying = true;
			try {
				unpaused.signalAll();
			} finally {
				pauseLock.unlock();
			}
		}

		public float getPlayedTimeInMS() {
			return playedTimeInMS;
		}
	}

	final private static class StreamingPipe extends InputStream {

		private InputStream inputStream;
		private OutputStream outputStream;

		private StreamingPipe(@NonNull InputStream inputStream, @NonNull OutputStream outputStream) {
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		@Override
		public int available() throws IOException {
			return inputStream.available();
		}

		@Override
		public void close() throws IOException {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}

		@Override
		public void mark(int readLimit) {
			inputStream.mark(readLimit);
		}

		@Override
		public boolean markSupported() {
			return inputStream.markSupported();
		}

		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException {
			int result = inputStream.read(buffer,offset,length);
			if (outputStream != null) {
				outputStream.write(buffer,offset,length);
			}
			return result;
		}

		@Override
		public synchronized void reset() throws IOException {
			inputStream.reset();
		}

		@Override
		public long skip(long byteCount) throws IOException {
			return inputStream.skip(byteCount);
		}

		@Override
		public int read() throws IOException {
			return inputStream.read();
		}
	}
}
