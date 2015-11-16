package com.uexperience.moetune.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uexperience.moetune.core.MoeTuneNotificationManager;
import com.uexperience.moetune.event.MusicControlEvent;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/5
 * Project: MoeTune
 * Package: com.uexperience.moetune.service
 */
public class MusicService extends Service {
	public static boolean mIsServiceRunning = false;

	/*Service状态*/
	public enum PlayerState{
		IDLE,
		INITIALIZED,
		PREPARING,
		PREPARED,
		PLAYING,
		STOPPED,
		PAUSED,
		COMPLETED,
		ERROR;

		@Override
		public String toString() {
			return readableMap.get(this);
		}

		private static Map<PlayerState, String> readableMap;

		static {
			readableMap = new HashMap<PlayerState, String>(9);
			readableMap.put(IDLE, "IDLE");
			readableMap.put(INITIALIZED, "INITIALIZED");
			readableMap.put(PREPARING, "PREPARING");
			readableMap.put(PREPARED, "PREPARED");
			readableMap.put(PLAYING, "PLAYING");
			readableMap.put(STOPPED, "STOPPED");
			readableMap.put(PAUSED, "PAUSED");
			readableMap.put(COMPLETED, "COMPLETED");
			readableMap.put(ERROR, "ERROR");
		}
	}

	private EventBus eventBus = EventBus.getDefault();
	private MoeTuneNotificationManager mNotificationManager;

	public MusicService() {}


	/* 服务绑定 */

	public class MessageBinder extends Binder {
		public MusicService getService(){
			return MusicService.this;
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("MusicService","on Bind");
		return new MessageBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("MusicService","on Unbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d("MusicService","on Rebind");
		super.onRebind(intent);
	}

	/* common */

	@Override
	public void onCreate() {
		Log.d("MusicService","on Create");
		eventBus.register(this);
		mNotificationManager = new MoeTuneNotificationManager(this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("MusicService","on StartCommand");
		mIsServiceRunning = true;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d("MusicService", "on Destroy");
		eventBus.unregister(this);
		mNotificationManager.onDestroy();
		mIsServiceRunning = false;
		super.onDestroy();
	}

	public void onEvent(MusicControlEvent event){
		Log.d("EventBus","on event");
	}
}
