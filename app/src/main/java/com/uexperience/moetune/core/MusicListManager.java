package com.uexperience.moetune.core;

import android.content.Context;
import android.util.Log;

import com.uexperience.moetune.event.MusicListFetcherEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/5
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MusicListManager {
	private Context mContext;

	private List<MusicInfo> mPlaybackList;
	private List<MusicInfo> mPriorityList;
	private MusicListFetcher mMusicListFetcher;

	private EventBus mEventBus = EventBus.getDefault();

	public MusicListManager(Context context) {
		this.mContext = context;

		mPlaybackList = new ArrayList<>();
		mPriorityList = new ArrayList<>();
		mMusicListFetcher = new MusicListFetcher(mContext);

		mEventBus.register(this);
	}

	public void refreshMusicList(){
		mMusicListFetcher.fetchMusicList(30);
	}

	public void onEvent(MusicListFetcherEvent event){
		if(event.getResultCode() == MusicListFetcherEvent.RESULT_SUCCESS){
			Log.d("Fetcher",event.getResult().toString());
		}else if(event.getResultCode() == MusicListFetcherEvent.RESULT_FAILED){
			Log.e("Fetcher",event.getError().getMessage(),event.getError());
		}
	}
}