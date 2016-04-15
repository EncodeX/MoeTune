package com.uexperience.moetune.event;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/11/12
 * Project: MoeTune
 * Package: com.uexperience.moetune.event
 */
public class MusicControlEvent {
	public final static int ACTION_PLAY = 0;
	public final static int ACTION_PAUSE = 1;
	public final static int ACTION_NEXT = 2;
	public final static int ACTION_PREV = 3;
	public final static int ACTION_STOP = 4;

	private int mAction;

	public MusicControlEvent(int action) {
		this.mAction = action;
	}

	public int getAction() {
		return mAction;
	}
}
