package com.example.smarthouse.hotel;

import java.util.List;

import com.example.smarthouse.R;
import com.example.smarthouse.hotel.MyAdapter_Light.ViewHolder;
import com.pullmi.common.CommonUtils;
import com.pullmi.utils.LogUtils;

import android.app.ActionBar.Tab;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Scene_MyAdapter extends BaseAdapter {

	List<String> mList;
	Context mcontext;

	public Scene_MyAdapter(List<String> list, Context context) {
		mList = list;
		mcontext = context;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mcontext).inflate(R.layout.hotel_light_gridview_item, null);

			viewHolder = new ViewHolder();
			viewHolder.tv = (TextView) convertView.findViewById(R.id.light_item_textview);
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.light_image);

			convertView.setTag(viewHolder);
		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}

		LogUtils.LOGE("", mList.get(position));
		if (mList.get(position).equals("全开模式")) {
			viewHolder.iv.setImageResource(R.drawable.qk);
		} else if (mList.get(position).equals("全关模式")) {
			viewHolder.iv.setImageResource(R.drawable.qg);
		}else {
			viewHolder.iv.setImageResource(R.drawable.sz);
		}

		viewHolder.tv.setText(mList.get(position));

		return convertView;
	}

	class ViewHolder {
		TextView tv;
		ImageView iv;
	}
}