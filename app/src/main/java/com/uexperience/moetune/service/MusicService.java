package com.uexperience.moetune.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uexperience.moetune.core.MNotificationManager;
import com.uexperience.moetune.core.MusicListManager;
import com.uexperience.moetune.core.MusicPlayer;
import com.uexperience.moetune.event.MusicControlEvent;
import com.uexperience.moetune.event.MusicListManagerEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/5
 * Project: MoeTune
 * Package: com.uexperience.moetune.service
 */
public class MusicService extends Service {
	public static boolean mIsServiceRunning = false;

	private EventBus eventBus = EventBus.getDefault();
	private MNotificationManager mNotificationManager;
	private MusicListManager mMusicListManager;

	private MusicPlayer mMusicPlayer;

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
//		Log.d("MusicService","on Bind");
		return new MessageBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
//		Log.d("MusicService","on Unbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
//		Log.d("MusicService","on Rebind");
		super.onRebind(intent);
	}

	/* common */

	@Override
	public void onCreate() {
//		Log.d("MusicService","on Create");
		eventBus.register(this);
		mNotificationManager = new MNotificationManager(this);
		mMusicListManager = new MusicListManager(this);

		mMusicPlayer = new MusicPlayer(this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.d("MusicService","on StartCommand");
		mIsServiceRunning = true;

		mMusicListManager.refreshMusicList();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
//		Log.d("MusicService", "on Destroy");
		eventBus.unregister(this);
		mNotificationManager.onDestroy();
		mIsServiceRunning = false;

		mMusicPlayer.release();
		super.onDestroy();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(MusicControlEvent event){
//		Log.d("EventBus","on music control event");
		if(event.getAction() == MusicControlEvent.ACTION_STOP){
			mMusicPlayer.stop();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(MusicListManagerEvent event){
//		Log.d("EventBus","on music prepared event");
		switch (event.getResultCode()){
			case MusicListManagerEvent.LIST_PREPARED:
				mMusicPlayer.streamUrl(mMusicListManager.getNextMusicUrl());
				break;
		}
	}
}
