package com.example.adapter;

import java.util.ArrayList;

import com.example.smarthouse.R;
import com.pullmi.entity.devkeyValue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class chnOpItemListViewAdapter extends BaseAdapter {

	private Context context;
	ArrayList<devkeyValue> list;
	
	String KeyName = "", cmdName = "", state = "";
	byte cmd = 0;

	class ViewHolder {
		TextView boardName, keyName;
		Button state, cmd;
	}

	public chnOpItemListViewAdapter(Context context, ArrayList<devkeyValue> lst) {
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
			convertView = inflater.inflate(R.layout.chnkey_op_item, null);
			holder = new ViewHolder();

			holder.boardName = (TextView) convertView
					.findViewById(R.id.tv_board_name);
			holder.keyName = (TextView) convertView
					.findViewById(R.id.tv_key_name);
			holder.state = (Button) convertView.findViewById(R.id.key_state);
			holder.cmd = (Button) convertView.findViewById(R.id.key_cmd);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.boardName.setText(list.get(position).boardName);
		holder.keyName.setText(list.get(position).keyName);
		holder.state.setText(list.get(position).op+"");
		holder.cmd.setText(list.get(position).cmdName);
		

		return convertView;
	}
}