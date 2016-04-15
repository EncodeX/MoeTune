package com.uexperience.moetune.core;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 14-9-18
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MusicCoverUrl {
	private String small;
	private String medium;
	private String square;
	private String large;

	public MusicCoverUrl(String small, String medium, String square, String large) {
		this.small = small;
		this.medium = medium;
		this.square = square;
		this.large = large;
	}

	public String getSmall() {
		return small;
	}

	public void setSmall(String small) {
		this.small = small;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getSquare() {
		return square;
	}

	public void setSquare(String square) {
		this.square = square;
	}

	public String getLarge() {
		return large;
	}

	public void setLarge(String large) {
		this.large = large;
	}
}
