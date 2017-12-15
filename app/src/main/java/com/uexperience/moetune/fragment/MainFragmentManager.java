package com.uexperience.moetune.fragment;


import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/27
 * Project: MoeTune
 * Package: com.uexperience.moetune.fragment
 */
public class MainFragmentManager {
	private static Map<Integer,BaseFragment> fragmentMap = new HashMap<>();
	private static int currentFragmentIndex = -1;

	public static BaseFragment changeToFragment(int index) throws FragmentException {
//		if(!fragmentMap.containsKey(index)){
//			BaseFragment newFragment = null;
//
//			switch (index){
//				case 0:
//					newFragment = new NowPlayingFragment();
//					break;
//				case 1:
//					newFragment = new SettingFragment();
//					break;
//				case 2:
//					newFragment = new AboutFragment();
//					break;
//			}
//			if(newFragment == null){
//				throw new FragmentException("Fragment Manager didn't create new fragment instance.");
//			}
//			fragmentMap.put(index,newFragment);
//		}
//
//		currentFragmentIndex = index;
//
//		return fragmentMap.get(index);

		BaseFragment newFragment = null;

		switch (index){
			case 0:
				newFragment = new NowPlayingFragment();
				break;
			case 1:
				newFragment = new SettingFragment();
				break;
			case 2:
				newFragment = new AboutFragment();
				break;
		}

		return newFragment;
	}

	public static boolean isFragmentInitialized(int index){
		return fragmentMap.containsKey(index);
	}

	public static int getCurrentFragmentIndex(){
		return currentFragmentIndex;
	}

	public static BaseFragment getCurrentFragment(){
		if(fragmentMap.containsKey(currentFragmentIndex)){
			return fragmentMap.get(currentFragmentIndex);
		}else {
			return null;
		}
	}

	public static void onDrawerTransitionFinished(){
		fragmentMap.get(currentFragmentIndex).onInitializeView();
	}

	public static void onDestroy(){
		fragmentMap.clear();
		currentFragmentIndex = -1;
		System.gc();
	}
}
