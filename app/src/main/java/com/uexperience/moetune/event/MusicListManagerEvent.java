package com.uexperience.moetune.event;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/18
 * Project: MoeTune
 * Package: com.uexperience.moetune.event
 */
public class MusicListManagerEvent {
	public final static int LIST_PREPARED = 0;
	public final static int RESULT_FAILED = 1;

	private int mResultCode;

	public MusicListManagerEvent(int resultCode) {
		this.mResultCode = resultCode;
	}

	public int getResultCode() {
		return mResultCode;
	}
}