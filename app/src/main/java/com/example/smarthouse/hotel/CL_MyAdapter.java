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

public class CL_MyAdapter extends BaseAdapter {

	List<String> mList;
	Context mcontext;

	public CL_MyAdapter(List<String> list, Context context) {
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

		// 需要给imageview给资源(根据服务器的数据判断)；
		for (int i = 0; i < activityHotel.mCurtainDataset.size(); i++) {
			if (mList.get(position).equals(CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "开")) {
				viewHolder.iv.setImageResource(R.drawable.clqk);
			}
			if (mList.get(position).equals(CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "关")) {
				viewHolder.iv.setImageResource(R.drawable.clqg);
			}
			if (mList.get(position).equals(CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "停")) {
				viewHolder.iv.setImageResource(R.drawable.clbk);
			}
		}
		if (mList.get(position).equals("全开")) {
			viewHolder.iv.setImageResource(R.drawable.clqk);
		}
		if (mList.get(position).equals("全关")) {
			viewHolder.iv.setImageResource(R.drawable.clqg);
		}
		if (mList.get(position).equals("全停")) {
			viewHolder.iv.setImageResource(R.drawable.clbk);
		}

		viewHolder.tv.setText(mList.get(position));

		return convertView;
	}

	class ViewHolder {
		TextView tv;
		ImageView iv;
	}
}