package com.uexperience.moetune.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uexperience.moetune.R;
import com.uexperience.moetune.event.MusicControlEvent;
import com.uexperience.moetune.service.MusicService;
import com.uexperience.moetune.view.SlidableLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/27
 * Project: MoeTune
 * Package: com.uexperience.moetune.fragment
 */
public class NowPlayingFragment extends BaseFragment {
	@BindView(R.id.album_image)
	ImageView mAlbumImage;
	@BindView(R.id.slide_up_button)
	ImageButton mSlideUpButton;
	@BindView(R.id.now_playing_card)
	SlidableLayout mNowPlayingCard;
	@BindView(R.id.play_button)
	ImageButton mPlayButton;

	private EventBus mEventBus = EventBus.getDefault();

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

		mPlayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mEventBus.post(new MusicControlEvent(MusicControlEvent.ACTION_STOP));
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
