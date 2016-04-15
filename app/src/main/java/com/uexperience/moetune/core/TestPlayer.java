package com.uexperience.moetune.core;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.uexperience.moetune.tool.MD5Util;
import com.uexperience.moetune.tool.MusicCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/19
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class TestPlayer {

	/**
	 * 处理逻辑:
	 * <p/>
	 * IDLE: 空闲
	 * 初始化后为INITIALIZED状态
	 * 需要初始化的内容有
	 * mState mAudioTrack mDiskCache(封装过的)
	 * <p/>
	 * INITIALIZED: 已初始化
	 * 已设置音频源uri
	 * 需要初始化的内容为
	 * mURL mIsCompleted
	 * <p/>
	 * PREPARING: 准备中
	 * 初始化音频流 播放前的缓冲
	 * 新的AsyncTask
	 * 并且开始下载
	 * <p/>
	 * PREPARED: 准备结束
	 * 开始播放前的状态 可以开始播放
	 * 通知Service
	 * <p/>
	 * PLAYING: 正在播放
	 * 播放音频流中
	 * <p/>
	 * STOPPED: 停止播放
	 * 若完成播放则置mIsCompleted为真
	 * <p/>
	 * PAUSED: 暂停播放
	 * 暂停
	 * <p/>
	 * ERROR: 错误
	 * 发生错误
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

	private Context mContext;
	private State mState;
	private URL mUrl;
	private MusicCache mMusicCache;

	private AsyncStreamingTask mStreamingTask;

	private AudioTrack mAudioTrack;
	private int mBufferSize;
	private int mTotalRate;
	private int mCurrentRate;

	private List<SampleBuffer> mBufferList;
	private boolean mLoopSwitch;
	private ReentrantReadWriteLock readWriteLock;

	public TestPlayer(Context context) {

		this.mContext = context;
		this.mBufferSize = 2 * AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		this.mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize, AudioTrack.MODE_STREAM);
		this.mTotalRate = 0;
		this.mCurrentRate = 0;

		mBufferList = new ArrayList<>();

		mUrl = null;
		mState = State.IDLE;

		try {
			this.mMusicCache = new MusicCache(context);
		} catch (IOException e) {
			e.printStackTrace();
		}

//		// TODO 仅测试
		try {
			this.mStreamingTask = new AsyncStreamingTask();
			mStreamingTask.setUrl(new URL("http://nyan.90g.org/a/6/ad/3be95ac1e18d0d17802a115c5402669e_192.mp3"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		if (mAudioTrack != null) {
			mAudioTrack.pause();
			mAudioTrack.flush();
			mAudioTrack.stop();
			mAudioTrack.release();
		}

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, mBufferSize, AudioTrack.MODE_STREAM);
	}

	public void prepareAsync() {
		mCurrentRate = 0;
		mStreamingTask.prepareAsync();

//		mLoopSwitch = true;
//		PlaybackThread playbackThread = new PlaybackThread();
//		new Thread(playbackThread).start();
	}

	public void play(){

	}

	public void pause(){

	}

	public void stop(){
		if(mStreamingTask.isRunning()){
			mStreamingTask.cancel(true);
		}
	}

	private void notifyPrepared() {
		// TODO 测试
		Log.d("TestPlayer", "Playing Music");
		mAudioTrack.setPositionNotificationPeriod(mAudioTrack.getPlaybackRate() / 10);

		mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
			@Override
			public void onMarkerReached(AudioTrack audioTrack) {
				Log.d("AudioTrack", "onMarkerReached");
			}

			@Override
			public void onPeriodicNotification(AudioTrack audioTrack) {
				mCurrentRate += mAudioTrack.getPlaybackRate() / 10;
				Log.d("AudioTrack", "onPeriodicNotification, playback rate: " + audioTrack.getPlaybackRate() + " current rate: " + mCurrentRate + " total rate: " + mTotalRate);
			}
		});

		mAudioTrack.play();
	}

	public void release() {
		if (mAudioTrack != null) {
			mAudioTrack.pause();
			mAudioTrack.flush();
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
		if (mStreamingTask != null) {
			if (!mStreamingTask.isCancelled()) {
				mStreamingTask.cancel(true);
			}
			mStreamingTask = null;
		}
	}

	/**
	 *
	 * 边下边播逻辑:
	 *
	 * AsyncStreamingTask 与 PlaybackThread 共用一个 PriorityBlockingQueue
	 *
	 * AsyncStreamingTask负责向PriorityBlockingQueue内注入Header元素
	 *
	 * PlaybackThread负责取出Header并解码传递给AudioTrack播放
	 *
	 * 其中
	 *
	 * Header需要再次封装以实现比较性
	 *
	 * PriorityBlockingQueue需要编写比较类
	 *
	 */

	private class PlaybackThread implements Runnable{
		private int mOneShootBytes = 0;
		private int mTotalBytes = 0;
		private boolean mIsPrepared = false;

		@Override
		public void run() {
//			while (true){
//				try {
//					ComparableBuffer comparableBuffer;
//
//					comparableBuffer = mBufferList.take();
//					Log.d("PlaybackThread","Taking buffer");
//
//					if (!mIsPrepared && mTotalBytes >= mBufferSize - 2 * mOneShootBytes) {
//						mIsPrepared = true;
//						notifyPrepared();
//					}
//
//					writeFrame(comparableBuffer.getBuffer());
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}

			while (mLoopSwitch){
				try {
					SampleBuffer sampleBuffer;
					sampleBuffer = mBufferList.get(0);
					mBufferList.remove(0);


				}finally {

				}
			}
		}

		private void writeFrame(SampleBuffer decoderBuffer) {
			final short[] buffer = decoderBuffer.getBuffer();
			final int bufferLength = decoderBuffer.getBufferLength();
			final short[] copyBuffer = new short[bufferLength];
			mOneShootBytes = decoderBuffer.getBufferLength() * 2;
			Log.d("PlaybackThread","decoderBufferLength: "+ bufferLength + " copyBufferLength: "+copyBuffer.length);
			System.arraycopy(buffer, 0, copyBuffer, 0, bufferLength);
			mAudioTrack.write(copyBuffer, 0, copyBuffer.length);
			mTotalBytes += mOneShootBytes;
		}
	}

	private class AsyncStreamingTask extends AsyncTask<String, Integer, String> {
		private URL mUrl = null;
		private boolean mIsRunning = false;
		private HttpURLConnection mConnection = null;
		private InputStream mInputStream = null;
		private Bitstream mBitStream = null;

		private int mOneShootBytes = 0;
		private int mTotalBytes = 0;
		private boolean mIsPrepared = false;

		public AsyncStreamingTask() {}

		@Override
		protected void onPreExecute() {
			mIsRunning = true;
		}

		@Override
		protected String doInBackground(String... strings) {
			Decoder decoder = new Decoder();
			Header header;

			DiskLruCache.Editor diskEditor = null;
			DiskLruCache.Snapshot mp3Snapshot = null;
			final String key = MD5Util.md5(mUrl.getPath());

			try {
				mp3Snapshot = mMusicCache.get(key);

				if (mp3Snapshot != null) {
					Log.d("MusicCache", "Found Disk Cache");
					mInputStream = mp3Snapshot.getInputStream(0);
					mMusicCache.flush();
				} else {
					Log.d("MusicCache", "Disk Cache Not Found, Start to Download");
					mConnection = (HttpURLConnection) mUrl.openConnection();
					mConnection.connect();

					diskEditor = mMusicCache.edit(key);
					mInputStream = mMusicCache.getStreamingPipe(mConnection.getInputStream(), diskEditor);
				}
				mBitStream = new Bitstream(mInputStream);

				mBufferList.clear();

				if ((header = mBitStream.readFrame()) != null) {

					float fileLength;

					if (mp3Snapshot != null) {
						fileLength = mInputStream.available();
					} else {
						fileLength = mConnection.getContentLength();
					}

					final float bitrate = header.bitrate();
					final float playbackTime = fileLength * 8 / bitrate;

					Log.d("AudioTrack", "Bitrate: " + bitrate);
					Log.d("AudioTrack", "File Size: " + fileLength);
					Log.d("AudioTrack", "Playback Time: " + playbackTime);

					if (mAudioTrack.getPlaybackRate() != header.frequency()) {
						mAudioTrack.setPlaybackRate(header.frequency());
					}

					mTotalRate = ((int) Math.floor(10 * playbackTime)) * mAudioTrack.getPlaybackRate() / 10;
					mAudioTrack.setNotificationMarkerPosition(mTotalRate);

					writeFrame((SampleBuffer) decoder.decodeFrame(header, mBitStream));
//					final SampleBuffer buffer = (SampleBuffer) decoder.decodeFrame(header, mBitStream);
//					mBufferList.add(buffer);

//					mBufferList.put(new ComparableBuffer((SampleBuffer) decoder.decodeFrame(header, mBitStream),counter));
//					Log.d("StreamingTask","Putting buffer");
//					counter++;

					mBitStream.closeFrame();
				}

				while ((header = mBitStream.readFrame()) != null) {
					if (isCancelled()){
						Log.d("StreamingTask","on cancelled called, break");
						break;
					}

					if (!mIsPrepared && mTotalBytes >= mBufferSize - 2 * mOneShootBytes) {
						mIsPrepared = true;
						notifyPrepared();
					}

					writeFrame((SampleBuffer) decoder.decodeFrame(header, mBitStream));
//					final SampleBuffer buffer = (SampleBuffer) decoder.decodeFrame(header, mBitStream);
//					mBufferList.add(buffer);
//					mBufferList.put(new ComparableBuffer((SampleBuffer) decoder.decodeFrame(header, mBitStream),counter));
//					Log.d("StreamingTask","Putting buffer");
//					counter++;
					mBitStream.closeFrame();
				}

//				notifyPrepared();
//
//				while(!mBufferList.isEmpty()){
//					writeFrame(mBufferList.get(0));
//					mBufferList.remove(0);
//				}

				if (diskEditor != null) {
					if (isCancelled()) {
						Log.d("StreamingTask","on cancelled called, abort cache");
						diskEditor.abort();
					} else {
						Log.d("StreamingTask","download success, commit cache");
						diskEditor.commit();
					}
					mMusicCache.flush();
				}
			} catch (BitstreamException | IOException e) {
				e.printStackTrace();
				try {
					if (diskEditor != null) {
						Log.d("StreamingTask","error occurred, abort cache");
						diskEditor.abort();
						mMusicCache.flush();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				reset();
			} catch (DecoderException e) {
				e.printStackTrace();
			} finally {
				if (mp3Snapshot != null && mInputStream != null) {
					mp3Snapshot.close();
				}
				if (mBitStream != null) {
					try {
						mBitStream.close();
					} catch (BitstreamException e) {
						e.printStackTrace();
					}
				}
				if (mConnection != null) {
					mConnection.disconnect();
				}
				mLoopSwitch = false;
			}

			return null;
		}

		@Override
		protected void onCancelled() {
			Log.d("StreamingTask","on cancelled");
			this.mUrl = null;
			mLoopSwitch = false;
		}

		@Override
		protected void onPostExecute(String s) {
			Log.d("StreamingTask","on post execute");
			this.mUrl = null;
			mLoopSwitch = false;
		}

		private void writeFrame(SampleBuffer decoderBuffer) {
			final short[] buffer = decoderBuffer.getBuffer();
			final int bufferLength = decoderBuffer.getBufferLength();
			final short[] copyBuffer = new short[bufferLength];
			mOneShootBytes = decoderBuffer.getBufferLength() * 2;
//				Log.d("PlaybackThread","decoderBufferLength: "+ bufferLength + " copyBufferLength: "+copyBuffer.length);
			System.arraycopy(buffer, 0, copyBuffer, 0, bufferLength);
			mAudioTrack.write(copyBuffer, 0, copyBuffer.length);
			mTotalBytes += mOneShootBytes;
		}

		public boolean isRunning() {
			return mIsRunning;
		}

		public void setUrl(URL url){
			this.mUrl = url;
		}

		public void prepareAsync(){
			if(mUrl == null){
				Log.d("AsyncPrepareTask","URL not set");
				return;
			}

			this.execute();
		}
	}
}
