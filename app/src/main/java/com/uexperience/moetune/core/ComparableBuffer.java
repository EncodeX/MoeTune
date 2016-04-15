package com.uexperience.moetune.core;

import android.support.annotation.NonNull;

import javazoom.jl.decoder.SampleBuffer;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/12/10
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class ComparableBuffer implements Comparable {
	private SampleBuffer buffer;
	private int index;

	public ComparableBuffer(SampleBuffer buffer, int index) {
		this.buffer = buffer;
		this.index = index;
	}

	public SampleBuffer getBuffer() {
		return buffer;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int compareTo(@NonNull Object o) {
		if(o instanceof ComparableBuffer){
			return index - ((ComparableBuffer)o).getIndex();
		}
		return 0;
	}
}
