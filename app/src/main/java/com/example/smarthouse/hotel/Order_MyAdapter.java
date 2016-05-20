package com.example.smarthouse.hotel;

import java.util.List;

import com.example.smarthouse.R;
import com.example.smarthouse.hotel.MyAdapter_Light.ViewHolder;
import com.pullmi.common.CommonUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Order_MyAdapter extends BaseAdapter {

	Context mcontext;

	public Order_MyAdapter(Context context) {
		mcontext = context;
	}

	@Override
	public int getCount() {
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		return position; 
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mcontext).inflate(R.layout.hotel_order_gridview_item, null);

			viewHolder = new ViewHolder();
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.light_image);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.iv.setImageResource(mThumbIds[position]); 

		return convertView;
	}

	class ViewHolder {
		ImageView iv;
	}

	private Integer[] mThumbIds = { R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5, R.drawable.c6};
}