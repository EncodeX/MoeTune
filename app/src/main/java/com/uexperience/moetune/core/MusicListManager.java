package com.uexperience.moetune.core;

import android.content.Context;
import android.util.Log;

import com.uexperience.moetune.event.MusicListFetcherEvent;
import com.uexperience.moetune.event.MusicListManagerEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

	private MusicInfo mNowPlaying;

	public MusicListManager(Context context) {
		mContext = context;

		mPlaybackList = new ArrayList<>();
		mPriorityList = new ArrayList<>();
		mMusicListFetcher = new MusicListFetcher(mContext);

		mEventBus.register(this);
		mNowPlaying = null;
	}

	public void refreshMusicList(){
		mMusicListFetcher.fetchMusicList(30);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(MusicListFetcherEvent event){
		if(event.getResultCode() == MusicListFetcherEvent.RESULT_SUCCESS){
			Log.d("Fetcher",event.getResult().toString());
			JSONArray list = event.getResult().optJSONObject("response")
					.optJSONArray("playlist");

			mPlaybackList.clear();
			for (int i = 0; i < list.length(); i++) {
				JSONObject item = (JSONObject) list.opt(i);
				if(item != null){
					mPlaybackList.add(new MusicInfo(item));
				}
			}
			mEventBus.post(new MusicListManagerEvent(MusicListManagerEvent.LIST_PREPARED));
		}else if(event.getResultCode() == MusicListFetcherEvent.RESULT_FAILED){
			Log.e("Fetcher",event.getError().getMessage(),event.getError());
		}
	}

	public String getNextMusicUrl(){
		int next = 0;
		if (mNowPlaying != null){
			next = mPlaybackList.indexOf(mNowPlaying) + 1;
		}
		next = next >= mPlaybackList.size()? 0:next;
		mNowPlaying = mPlaybackList.get(next);
		return mNowPlaying.getUrl();
	}
}