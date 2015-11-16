package com.uexperience.moetune.fragment;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uexperience.moetune.R;
import com.uexperience.moetune.view.SlidableLayout;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/27
 * Project: MoeTune
 * Package: com.uexperience.moetune.fragment
 */
public class NowPlayingFragment extends BaseFragment {
	@Bind(R.id.album_image)
	ImageView mAlbumImage;
	@Bind(R.id.slide_up_button)
	ImageButton mSlideUpButton;
	@Bind(R.id.now_playing_card)
	SlidableLayout mNowPlayingCard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_now_playing, container, false);
		ButterKnife.bind(this, rootView);
		Picasso.with(getContext()).load(R.drawable.ic_app_album).into(mAlbumImage);

		mSlideUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mNowPlayingCard.toggleView(SlidableLayout.SlideDirection.UP);
			}
		});

		return rootView;
	}

	@Override
	public void onInitializeView() {
		super.onInitializeView();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
