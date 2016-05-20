package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.pullmi.entity.WareKeyOpItem;
import com.pullmi.utils.LogUtils;

import android.app.ActionBar.Tab;
import android.content.Context;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class keyOpItemListViewAdapter extends BaseAdapter {

	private Context context;
	ArrayList<String> list;
	private List<WareKeyOpItem> items;

	class ViewHolder {
		TextView tvName;
		RadioGroup rb_state;
		RadioButton open, close;
	}

	public keyOpItemListViewAdapter(Context context, ArrayList<String> lst) {
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
			convertView = inflater.inflate(R.layout.scence_item, null);
			holder = new ViewHolder();
			holder.rb_state = (RadioGroup) convertView
					.findViewById(R.id.rb_state);
			holder.rb_state.setClickable(false);
			holder.open = (RadioButton) convertView.findViewById(R.id.rb_open);
			holder.close = (RadioButton) convertView
					.findViewById(R.id.rb_close);

			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_device_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(list.get(position));
		items = HomeActivity.mKeyOpItems;
		if (items.size() == 0 || position > items.size()) {
			return convertView;
		}
		if (items.get(position).devType == 0) {
			holder.close.setVisibility(View.GONE);
			holder.open.setChecked(true);
			String text = "打开";
			switch (items.get(position).keyOpCmd) {
			case 0:
				text = "开关";
				break;
			case 1:
				text = "模式";
				break;
			case 2:
				text = "风速";
				break;
			case 3:
				text = "温度+";
				break;
			case 4:
				text = "温度-";
				break;
			default:
				break;
			}
			holder.open.setText(text);
		}
		if (items.get(position).devType == 4) {
			holder.close.setVisibility(View.GONE);
			holder.open.setChecked(true);
			String text = "打开";
			switch (items.get(position).keyOpCmd) {
			case 1:
				text = "打开";
				break;
			case 2:
				text = "关闭";
				break;
			case 3:
				text = "暂停";
				break;
			case 4:
				text = "开关停";
				break;
			default:
				break;
			}
			holder.open.setText(text);
		}
		
		if (items.get(position).devType == 3) {
			holder.close.setVisibility(View.GONE);
			holder.open.setChecked(true);
			String text = "打开";
			switch (items.get(position).keyOpCmd) {
			case 1:
				text = "打开";
				break;
			case 2:
				text = "关闭";
				break;
			case 3:
				text = "开关";
				break;
			case 4:
				text = "调亮";
				break;
			case 5:
				text = "调暗";
				break;
			default:
				break;
			}
			holder.open.setText(text);
		}
		if (items.get(position).devType == 7) {
			holder.close.setVisibility(View.GONE);
			holder.open.setChecked(true);
		
			holder.open.setText("操作");
		}

		return convertView;
	}
}