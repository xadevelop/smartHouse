package com.example.adapter;

import java.util.ArrayList;

import com.example.smarthouse.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class scenesetListViewAdapter extends BaseAdapter {

	private Context context;
	ArrayList<String> list;

	class ViewHolder {
		TextView tvName;
	}

	public scenesetListViewAdapter(Context context, ArrayList<String> lst) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = lst;
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
			convertView = inflater.inflate(R.layout.chnout_in_list_item, null);
			holder = new ViewHolder();

			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_board_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(list.get(position));

		return convertView;
	}
}