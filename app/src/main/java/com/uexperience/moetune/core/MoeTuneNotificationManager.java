package com.uexperience.moetune.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.uexperience.moetune.R;
import com.uexperience.moetune.activity.MainActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/5
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MoeTuneNotificationManager {
	private static final Class<?>[] mSetForegroundSignature = new Class[] {
			boolean.class};
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
			int.class, Notification.class};
	private static final Class<?>[] mStopForegroundSignature = new Class[] {
			boolean.class};

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	private Service mService;
	private Context mContext;

	public MoeTuneNotificationManager(Service service) {
		this.mService = service;
		this.mContext = service.getApplicationContext();

		initNotificationManager();
	}

	public void onDestroy(){
		// Todo 修改1517为final域
		stopForegroundCompat(1517);
	}

	private void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(mService, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
//			Log.w("ApiDemos", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
//			Log.w("ApiDemos", "Unable to invoke method", e);
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	private void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = id;
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			Log.v("Notification Debug", "通知成功？!");
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
		Log.v("Notification Debug", "通知成功？");
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	private void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API.  Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	private void initNotificationManager(){
		//Log.v("Notification Debug","初始化Notification Manager");
		mNM = (NotificationManager)mService.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			mStartForeground = mService.getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = mService.getClass().getMethod("stopForeground",
					mStopForegroundSignature);
//			return;
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
			try {
				mSetForeground = mService.getClass().getMethod("setForeground",
						mSetForegroundSignature);
			} catch (NoSuchMethodException ex) {
				throw new IllegalStateException(
						"OS doesn't have Service.startForeground OR Service.setForeground!");
			}
		}

//		mMoeTuneNotificationManager = new MoeTuneNotificationManager(this);
//		mMoeTuneNotificationManager.setOnNotificationChangedListener(new OnNotificationChangedListener() {
//			@Override
//			public void notificationChanged(Notification notification) {
//				startForegroundCompat(MoeTuneConstants.Config.NOTIFICATION_ID, notification);
//			}
//		});
//		mMoeTuneNotificationManager.setNewNotification();
		setNewNotification();
	}

	private void setNewNotification(){
		RemoteViews remoteViews = new RemoteViews("com.uexperience.moetune", R.layout.notification_layout);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,new Intent(mContext, MainActivity.class), 0);

		builder.setContent(remoteViews);
		builder.setContentIntent(contentIntent);
		// todo 记得换掉这个 下面这个图标是旧版本的
		builder.setSmallIcon(R.drawable.ic_small);
		builder.setContentTitle("Foreground Service");
		builder.setContentText("Make this service run in the foreground.");
		builder.setOngoing(true);
		builder.setPriority(NotificationCompat.PRIORITY_MAX);
		Notification notification = builder.build();
		if(Build.VERSION.SDK_INT >= 16){
			RemoteViews remoteBigViews = new RemoteViews("com.uexperience.moetune",R.layout.notification_big_layout);
			notification.bigContentView = remoteBigViews;
//			currentRemoteBigViews = remoteBigViews;
		}

		// Todo 修改1517为final域
		startForegroundCompat(1517, notification);
//		onNotificationChangedListener.notificationChanged(notification);
//		currentRemoteViews = remoteViews;
//		startForegroundCompat(MoeTuneConstants.Config.NOTIFICATION_ID, notification);
	}
}
