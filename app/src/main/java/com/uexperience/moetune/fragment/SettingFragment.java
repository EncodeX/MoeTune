package com.uexperience.moetune.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uexperience.moetune.R;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/27
 * Project: MoeTune
 * Package: com.uexperience.moetune.fragment
 */
public class SettingFragment extends BaseFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_setting, container, false);

		return rootView;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
