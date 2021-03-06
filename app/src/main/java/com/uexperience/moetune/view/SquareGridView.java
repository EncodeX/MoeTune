package com.uexperience.moetune.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/25
 * Project: MoeTune
 * Package: com.uexperience.moetune.view
 */
public class SquareGridView extends GridView {
	public SquareGridView(Context context){
		this(context, null);
	}
	public SquareGridView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	public SquareGridView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}

	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
		setMeasuredDimension(getDefaultSize(0,widthMeasureSpec),getDefaultSize(0,heightMeasureSpec));
		int childWidthSize = getMeasuredWidth();
		int childHeightSize = getMeasuredHeight();
		if(childHeightSize>childWidthSize){
			heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize,MeasureSpec.EXACTLY);
		}else{
			widthMeasureSpec = heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize,MeasureSpec.EXACTLY);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
