package com.uexperience.moetune.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.uexperience.moetune.R;
import com.uexperience.moetune.fragment.FragmentException;
import com.uexperience.moetune.fragment.MainFragmentManager;
import com.uexperience.moetune.fragment.NavigationDrawerFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
	@Bind(R.id.drawer_toggle_button)
	ImageButton mDrawerToggleButton;
	@Bind(R.id.loading_indicator)
	ProgressWheel mLoadingIndicator;
	@Bind(R.id.loading_indicator_icon)
	ImageView mLoadingIndicatorIcon;

	private NavigationDrawerFragment mNavigationDrawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		initView();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainFragmentManager.onDestroy();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if(position == MainFragmentManager.getCurrentFragmentIndex()) return;

		if(position == 3){
			MainActivity.this.finish();
			System.exit(0);
			return;
		}

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		if(MainFragmentManager.getCurrentFragment()!=null){
			fragmentTransaction
					.hide(MainFragmentManager.getCurrentFragment())
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
					.commit();
			mLoadingIndicator.setVisibility(View.VISIBLE);
			mLoadingIndicatorIcon.setVisibility(View.VISIBLE);
		}

		/**
		 * 不保留Fragment
		 */
//			fragmentTransaction.replace(
//					R.id.main_fragment_container,
//					MainFragmentManager.changeToFragment(position))
//					.commit();
		/**
		 * 保留Fragment
		 */
//			if(MainFragmentManager.getCurrentFragment() !=null){
//				if(MainFragmentManager.isFragmentInitialized(position)){
//					fragmentTransaction.hide(MainFragmentManager.getCurrentFragment())
//							.show(MainFragmentManager.changeToFragment(position))
//							.commit();
//				}else {
//					fragmentTransaction.hide(MainFragmentManager.getCurrentFragment())
//							.add(R.id.main_fragment_container, MainFragmentManager.changeToFragment(position))
//							.commit();
//				}
//			}else {
//				fragmentTransaction
//						.add(R.id.main_fragment_container, MainFragmentManager.changeToFragment(position))
//						.commit();
//			}
	}

	@Override
	public void onDrawerTransitionFinished(int targetPosition) {
		try {
			if(targetPosition == MainFragmentManager.getCurrentFragmentIndex()) return;
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			/**
			 * 不保留Fragment
			 */
			fragmentTransaction
					.setCustomAnimations(R.anim.fragment_fade_in,R.anim.fragment_fade_out)
					.replace(
							R.id.main_fragment_container,
							MainFragmentManager.changeToFragment(targetPosition))
					.commit();
			mLoadingIndicator.setVisibility(View.GONE);
			mLoadingIndicatorIcon.setVisibility(View.GONE);
			/**
			 * Keep Memory in control
			 */
			System.gc();
		} catch (FragmentException e) {
			e.printStackTrace();
		}
	}

	private void initView(){
		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		mDrawerToggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mNavigationDrawerFragment.toggleDrawer();
			}
		});
	}
}
