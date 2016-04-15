package com.uexperience.moetune.core;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.uexperience.moetune.event.MusicListFetcherEvent;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/18
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MusicListFetcher {
	private RequestQueue mRequestQueue;
	private EventBus mEventBus = EventBus.getDefault();

	public MusicListFetcher(Context context) {
		this.mRequestQueue = Volley.newRequestQueue(context);
	}

	public void fetchMusicList(int count){
		JsonObjectRequest request = new JsonObjectRequest(
				"http://moe.fm/listen/playlist?api=json&perpage=" + count + "&api_key=e70358879687af47b1f5842b800dfb6605180b3dd",
				mRequestListener,
				mErrorListener);
		mRequestQueue.add(request);
	}

	private Response.Listener<JSONObject> mRequestListener = new Response.Listener<JSONObject>() {
		@Override
		public void onResponse(JSONObject response) {
//			Log.d("MusicListFetcher",response.toString());
			mEventBus.post(new MusicListFetcherEvent(MusicListFetcherEvent.RESULT_SUCCESS, response));
		}
	};

	private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
//			Log.e("MusicListFetcher", error.getMessage(), error);
			mEventBus.post(new MusicListFetcherEvent(MusicListFetcherEvent.RESULT_FAILED, error));
		}
	};
}
