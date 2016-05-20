package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.smarthouse.R;
import com.pullmi.utils.LogUtils;

import android.app.ActionBar.Tab;
import android.content.Context;
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

public class sceneEventsListViewAdapter extends BaseAdapter {

	private Context context;
	// 用于记录每个RadioButton的状态，并保证只可选一个
	public HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	ArrayList<HashMap<String, Object>> list;

	class ViewHolder {
		TextView tvName;
		RadioGroup rb_state;
	}

	public sceneEventsListViewAdapter(Context context,
			ArrayList<HashMap<String, Object>> lst) {
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

			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_device_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(list.get(position).get("ItemText").toString());

		holder.rb_state
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						// states.clear();
						switch (checkedId) {
						case R.id.rb_open:
							LogUtils.LOGE("", "选中打开");
							states.put(String.valueOf(position), true);
							break;
						case R.id.rb_close:
							LogUtils.LOGE("", "选中关闭");
							states.put(String.valueOf(position), false);
							break;
						default:
							break;
						}
					}
				});
	

		return convertView;
	}

	/**
	 * 获取所有主题的项目的选中状态容器
	 * 
	 * @return
	 */
	public HashMap<String, Boolean> getSubjectItemMap() {
		return this.states;
	}
}