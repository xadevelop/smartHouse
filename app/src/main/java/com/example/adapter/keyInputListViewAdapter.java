package com.example.adapter;

import java.util.ArrayList;

import com.example.smarthouse.R;
import com.example.smarthouse.chnOpItemActivity;
import com.example.smarthouse.keyopItemActivity;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class keyInputListViewAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> list;
	private byte[] devUnitID;
	private int boardType;

	class ViewHolder {

		TextView tvName;
		Button test, edit;
	}

	public keyInputListViewAdapter(Context context, ArrayList<String> lst, byte[] devUnitID,
			int boardType) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = lst;
		this.devUnitID = devUnitID;
		this.boardType = boardType;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 页面
		ViewHolder holder;
		LayoutInflater inflater = LayoutInflater.from(context);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.keyinput_set_item, null);
			holder = new ViewHolder();
			holder.test = (Button) convertView.findViewById(R.id.key_test);
			holder.edit = (Button) convertView.findViewById(R.id.key_edit);

			holder.tvName = (TextView) convertView.findViewById(R.id.tv_key_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(list.get(position));
		holder.test.setOnTouchListener(TouchDark);
		holder.test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		holder.edit.setOnTouchListener(TouchDark);
		holder.edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				if (boardType == 1) {
					intent.setClass(context, keyopItemActivity.class);
					bundle.putInt("index", position);
				} else if (boardType == 0) {
					intent.setClass(context, chnOpItemActivity.class);
					bundle.putString("devName", list.get(position));
				}
				bundle.putByteArray("devUnitID", devUnitID);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	public static final OnTouchListener TouchDark = new OnTouchListener() {

		public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1,
				0, -50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
				0, 0, 0, 0, 0, 1, 0 };

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
		}
	};
}