package com.example.smarthouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.info.TimeInfo;
import com.pullmi.common.DbOperator;
import com.pullmi.entity.UnitNode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class createTimerEventActivity2 extends AdAbstractActivity {

	private List<UnitNode> nodes;
	private static final String TAG = "createTimerEventActivity2";

	private LinearLayout createLayout;
	private ListView listview;
	private TimeOpAdapter adapter;
	private ArrayList<TimeInfo> arrayList;

	private static final int MSG_UPDATE_LIGHT = 1001;
	private static final int MSG_UPDATE_CURTAIN = 1002;
	private static final int MSG_UPDATE_AIRCOND = 1003;
	private static final int MSG_UPDATE_LOCKS = 1005;
	private static final int MSG_UPDATE_VALUE = 1006;
	TimeInfo info;

	private LinearLayout.LayoutParams WW = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		createLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.create_timer_events, null);
		mRootView.addView(createLayout, FF);

		listview = (ListView) findViewById(R.id.lst_sys_op_one);
		arrayList = new ArrayList<TimeInfo>();

		// 查询数据库，找到房间名称
		nodes = DbOperator.getDbOperator().queryAllUnitNodesByRoom();
		for (int i = 0; i < nodes.size(); i++) {
			Log.e(TAG, nodes.get(i).roomName + "");
			info = new TimeInfo();
			String roomName = nodes.get(i).roomName;
			info.setTextView(roomName);

			arrayList.add(info);
		}

		adapter = new TimeOpAdapter(this, arrayList);
		listview.setAdapter(adapter);
		initComponent();
	}

	private void initComponent() {

		setTextForTitle("定时设置");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_LIGHT:
				break;
			case MSG_UPDATE_CURTAIN:
				break;
			case MSG_UPDATE_AIRCOND:
				break;
			case MSG_UPDATE_LOCKS:
				break;
			case MSG_UPDATE_VALUE:
				break;
			default:
				break;
			}
		};
	};

	public class TimeOpAdapter extends BaseAdapter {

		private ArrayList<TimeInfo> infos;
		ViewHolder viewHolder;

		public TimeOpAdapter(Context context, ArrayList<TimeInfo> infos) {

			this.infos = infos;
		}

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			viewHolder = null;

			if (convertView == null) {

				viewHolder = new ViewHolder();

				convertView = LayoutInflater.from(
						createTimerEventActivity2.this).inflate(
						R.layout.activity_list_item_op, null);

				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.txt_list_item);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}
			viewHolder.textView.setText(infos.get(position).getTextView());
			final String roomName = infos.get(position).getTextView();

			viewHolder.textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.e(TAG, "textview click");
					Intent intent = new Intent();
					intent.putExtra("roomName", roomName);
					intent.setClass(createTimerEventActivity2.this,
							controlTimerEventDevActivity.class);
					startActivity(intent);
				}
			});

			return convertView;
		}

		class ViewHolder {

			private TextView textView;

			private GridView gridView;

		}
	}
}
