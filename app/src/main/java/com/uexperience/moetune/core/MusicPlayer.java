package com.uexperience.moetune.core;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;

import com.vincan.medialoader.MediaLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/18
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MusicPlayer {
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

	private MediaPlayer mMediaPlayer;
	private MediaLoader mMediaLoader;

	private Context mContext;

	public MusicPlayer(Context context) {
		this.mContext = context;

		AudioAttributes.Builder builder = new AudioAttributes.Builder();
		builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);

		this.mMediaPlayer = new MediaPlayer();
		this.mMediaPlayer.setAudioAttributes(builder.build());
		this.mMediaPlayer.setOnPreparedListener(mOnPreparedListener);

		this.mMediaLoader = MediaLoader.getInstance(mContext);
	}

	public void streamUrl(String mediaUrl){
		try {
			this.mMediaPlayer.setDataSource(mContext,
					Uri.parse(this.mMediaLoader.getProxyUrl(mediaUrl)));

			this.mMediaPlayer.prepareAsync();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void release(){
		this.mMediaPlayer.stop();
		this.mMediaPlayer.release();
		this.mMediaPlayer = null;
	}

	public void stop(){
		this.mMediaPlayer.stop();
	}

	public void pause(){
		this.mMediaPlayer.pause();
	}

	public void resume(){
		this.mMediaPlayer.start();
	}

	private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mediaPlayer) {
			mMediaPlayer.start();
		}
	};
}
