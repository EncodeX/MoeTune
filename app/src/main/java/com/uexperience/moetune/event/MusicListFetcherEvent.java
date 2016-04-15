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
public class MusicListFetcherEvent {
	public final static int RESULT_SUCCESS = 0;
	public final static int RESULT_FAILED = 1;

	private JSONObject mResult;
	private int mResultCode;
	private VolleyError mError;

	public MusicListFetcherEvent(int resultCode,JSONObject result) {
		this.mResultCode = resultCode;
		this.mResult = result;
		this.mError = null;
	}
	public MusicListFetcherEvent(int resultCode,VolleyError error) {
		this.mResultCode = resultCode;
		this.mResult = null;
		this.mError = error;
	}

	public int getResultCode() {
		return mResultCode;
	}

	public JSONObject getResult() {
		return mResult;
	}

	public VolleyError getError() {
		return mError;
	}
}