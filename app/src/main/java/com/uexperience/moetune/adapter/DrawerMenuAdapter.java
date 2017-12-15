package com.uexperience.moetune.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uexperience.moetune.R;
import com.uexperience.moetune.model.DrawerMenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/25
 * Project: MoeTune
 * Package: com.uexperience.moetune.adapter
 */
public class DrawerMenuAdapter extends BaseAdapter {

	private static final int[] mListImage = new int[]{
			R.drawable.ic_drawer_play,
			R.drawable.ic_drawer_settings,
			R.drawable.ic_drawer_about,
			R.drawable.ic_drawer_exit
	};

	private static final int[]mListImageSelected = new int[]{
			R.drawable.ic_drawer_play_selected,
			R.drawable.ic_drawer_settings_selected,
			R.drawable.ic_drawer_about_selected,
			R.drawable.ic_drawer_exit
	};

	private static final int[] mListTitle = new int[]{
			R.string.title_section1,
			R.string.title_section2,
			R.string.title_section3,
			R.string.title_section4
	};

	private int mSelectedItemIndex;
	private Context mContext;
	private List<DrawerMenuItem> mItemList;

	public DrawerMenuAdapter(Context context, int selectedItemIndex) {
		this.mContext = context;
		mSelectedItemIndex = selectedItemIndex;
		mItemList = new ArrayList<>();

		for(int i = 0;i<mListTitle.length;i++){
			mItemList.add(new DrawerMenuItem(mListImage[i],mListImageSelected[i],mListTitle[i]));
		}
	}

	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Object getItem(int i) {
		return mItemList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		DrawerMenuItem item = mItemList.get(i);

		ViewHolderItem viewHolderItem;
		if(view == null){
			view = LayoutInflater.from(mContext).inflate(R.layout.drawer_list_item, viewGroup, false);
			viewHolderItem = new ViewHolderItem(view);
			view.setTag(viewHolderItem);
		}else{
			viewHolderItem = (ViewHolderItem)view.getTag();
		}

		if(mSelectedItemIndex == i){
			viewHolderItem.icon.setImageResource(item.getIconSelected());
			viewHolderItem.title.setTextSize(18.0f);
			viewHolderItem.title.setTypeface(null, Typeface.BOLD);
			viewHolderItem.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
		}else {
			viewHolderItem.icon.setImageResource(item.getIcon());
			viewHolderItem.title.setTextSize(14.0f);
			viewHolderItem.title.setTypeface(null, Typeface.NORMAL);
			viewHolderItem.title.setTextColor(Color.parseColor("#666666"));
		}
		viewHolderItem.title.setText(mContext.getString(item.getTitle()));

//		if(mSelectedItemIndex == i){
//			viewHolderItem.itemLayout.setBackgroundResource(R.color.menuSelected);
//		}else{
//			viewHolderItem.itemLayout.setBackgroundResource(R.drawable.ripple_drawer_menu);
//		}

		return view;
	}

	public void setSelectedItemIndex(int selectedItemIndex) {
		this.mSelectedItemIndex = selectedItemIndex;
		notifyDataSetChanged();
	}

	public int getSelectedItemIndex() {
		return mSelectedItemIndex;
	}

	public static class ViewHolderItem{
		@BindView(R.id.drawer_item_image) ImageView icon;
		@BindView(R.id.drawer_item_title)TextView title;
		@BindView(R.id.drawer_item_layout)RelativeLayout itemLayout;

		public ViewHolderItem(View itemView) {
			ButterKnife.bind(this,itemView);
		}
	}
}
