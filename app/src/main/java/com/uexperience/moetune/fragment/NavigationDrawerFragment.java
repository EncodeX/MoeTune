package com.uexperience.moetune.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.uexperience.moetune.R;
import com.uexperience.moetune.adapter.DrawerMenuAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/25
 * Project: MoeTune
 * Package: com.uexperience.moetune.fragment
 */
public class NavigationDrawerFragment extends Fragment {
	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	@Bind(R.id.drawer_menu_list) ListView mDrawerMenuList;

	private DrawerLayout mDrawerLayout;
	private View mFragmentContainerView;

	private DrawerMenuAdapter mDrawerMenuAdapter;
	private NavigationDrawerCallbacks mCallbacks;
	private DrawerListener mDrawerListener;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_navigation_drawer,container,false);
		ButterKnife.bind(this, rootView);

		mDrawerMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		mDrawerMenuAdapter = new DrawerMenuAdapter(inflater.getContext(),0);
		mDrawerMenuList.setAdapter(mDrawerMenuAdapter);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		selectItem(mDrawerMenuAdapter.getSelectedItemIndex());
		if(mCallbacks!=null){
			mCallbacks.onDrawerTransitionFinished(mDrawerMenuAdapter.getSelectedItemIndex());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAttach(Context context) {
		Log.d("Callback Debug","On Attach");
		super.onAttach(context);
		try {
			mCallbacks = (NavigationDrawerCallbacks) context;
			if(mCallbacks == null){
				Log.d("Callback Debug","Callback is null");
			}
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mDrawerMenuAdapter.getSelectedItemIndex());
	}

	private void selectItem(int position) {
		mDrawerListener.setSelectedItemIndex(position);
		if (mDrawerMenuList != null) {
			mDrawerMenuList.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	public void setUp(int drawerId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(drawerId);
		mDrawerLayout = drawerLayout;
		mDrawerListener = new DrawerListener();

		mDrawerLayout.setDrawerListener(mDrawerListener);
	}

	public void toggleDrawer(){
		if(mDrawerLayout == null )return;

		if(mDrawerLayout.isDrawerOpen(mFragmentContainerView)){
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}else {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
		void onDrawerTransitionFinished(int targetPosition);
	}

	private class DrawerListener implements DrawerLayout.DrawerListener{
		private int mSelectedItemIndex;
		private int mLastState;

		public DrawerListener() {
			this.mSelectedItemIndex = 0;
			mLastState = DrawerLayout.STATE_IDLE;
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {

		}

		@Override
		public void onDrawerOpened(View drawerView) {

		}

		@Override
		public void onDrawerClosed(View drawerView) {
			mDrawerMenuAdapter.setSelectedItemIndex(mSelectedItemIndex);
			if(mCallbacks!=null){
				mCallbacks.onDrawerTransitionFinished(mDrawerMenuAdapter.getSelectedItemIndex());
			}
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			if(newState!=mLastState){
				if(mLastState == DrawerLayout.STATE_SETTLING && newState == DrawerLayout.STATE_DRAGGING) {
					mDrawerMenuAdapter.setSelectedItemIndex(mSelectedItemIndex);
					if(mCallbacks!=null){
						mCallbacks.onDrawerTransitionFinished(mDrawerMenuAdapter.getSelectedItemIndex());
					}
				}
				mLastState = newState;
			}
		}

		public void setSelectedItemIndex(int selectedItemIndex) {
			this.mSelectedItemIndex = selectedItemIndex;
		}
	}
}
