package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.smarthouse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class checkboxMySceneAdapter extends BaseAdapter {
	// 填充数据的list
	ArrayList<HashMap<String, Object>> list;
	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;

	// 构造器
	public checkboxMySceneAdapter(ArrayList<HashMap<String, Object>> list,
			Context context) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initDate();
	}

	// 初始化isSelected的数据
	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			// 获得ViewHolder对象
			holder = new ViewHolder();
			// 导入布局并赋值给convertview
			convertView = inflater.inflate(R.layout.scence_item, null);
			holder.devIv = (ImageView) convertView.findViewById(R.id.alarm_grid_item_iv);
			holder.devTv = (TextView) convertView.findViewById(R.id.alarm_grid_item_tv);
			//holder.setCb((CheckBox) convertView.findViewById(R.id.alarm_grid_item_checkbox));
			// 为view设置标签
			convertView.setTag(holder);
		} else {
			// 取出holder
			holder = (ViewHolder) convertView.getTag();
		}
		int type = Integer.parseInt(list.get(position).get("ItemType").toString());
		if (type == 0) {
			holder.devIv.setBackgroundResource(R.drawable.ac);
		}
		if (type == 3) {
			holder.devIv.setBackgroundResource(R.drawable.light);
		}
		if (type == 4) {
			holder.devIv.setBackgroundResource(R.drawable.curtain);
		}
		if (type == 5) {
			holder.devIv.setBackgroundResource(R.drawable.lock_r);
		}
		holder.devTv.setText(list.get(position).get("ItemText")+"");
		// 根据isSelected来设置checkbox的选中状况
		holder.getCb().setChecked(getIsSelected().get(position));
		return convertView;
	}

	public class ViewHolder {
		private CheckBox cb;
		ImageView devIv;
		TextView devTv;
		int devType;
		public CheckBox getCb() {
			return cb;
		}
		public void setCb(CheckBox cb) {
			this.cb = cb;
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		checkboxMySceneAdapter.isSelected = isSelected;
	}
}
