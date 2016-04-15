package com.uexperience.moetune.tool;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/20
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MusicCache {
	private final static int AUDIO_STREAM_FINISHED = 0;
	private final static int AUDIO_STREAM_FINISHED_WITH_EXCEPTION = 1;

	private Context mContext;
	private DiskLruCache mDiskCache;

	public MusicCache(@NonNull Context context) throws IOException {
		this.mContext = context;

		File cacheDir = getFileCache("music");
		if (!cacheDir.exists()) cacheDir.mkdirs();
		this.mDiskCache = DiskLruCache.open(cacheDir, 1, 1, 200 * 1024 * 1024);
	}

	private File getFileCache(String cacheFileName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			if (mContext.getExternalCacheDir() != null) {
				cachePath = mContext.getExternalCacheDir().getPath();
			} else {
				cachePath = mContext.getCacheDir().getPath();
			}
		} else {
			cachePath = mContext.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + cacheFileName);
	}

	public DiskLruCache.Snapshot get(String key) throws IOException {
		return mDiskCache.get(key);
	}

	public DiskLruCache.Editor edit(String key) throws IOException {
		return mDiskCache.edit(key);
	}

	public StreamingPipe getStreamingPipe(
			@NonNull InputStream inputStream, @NonNull DiskLruCache.Editor editor) throws IOException {
		return new StreamingPipe(inputStream, editor.newOutputStream(0));
	}

	public void flush() throws IOException {
		mDiskCache.flush();
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
		public int read(@NonNull byte[] buffer, int offset, int length) throws IOException {
//			Log.d("StreamingTask","StreamingPipe Read");
			int result = inputStream.read(buffer, offset, length);
			if (outputStream != null) {
				outputStream.write(buffer, offset, length);
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
