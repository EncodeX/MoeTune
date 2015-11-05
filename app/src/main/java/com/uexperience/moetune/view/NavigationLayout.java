package com.uexperience.moetune.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/25
 * Project: MoeTune
 * Package: com.uexperience.moetune.view
 */
public class NavigationLayout extends ScrimInsetsFrameLayout {

	public NavigationLayout(Context context) {
		this(context, null);
	}

	public NavigationLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NavigationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		ViewCompat.setElevation(this, 3);
		ViewCompat.setFitsSystemWindows(this, true);
	}
}
