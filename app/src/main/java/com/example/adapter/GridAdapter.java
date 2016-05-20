package com.example.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class GridAdapter extends GridView {
	public GridAdapter(Context context) {
		super(context);

	}

	public GridAdapter(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
