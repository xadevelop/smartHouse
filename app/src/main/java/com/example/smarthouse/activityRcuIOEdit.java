package com.example.smarthouse;

import java.util.List;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.GPIO_INPUT_INFO;
import com.pullmi.entity.UdpProPkt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class activityRcuIOEdit extends AdAbstractActivity {

	/** 当前分辨率 */
	private DisplayMetrics dm;
	private int w;
	private TextView tv1, tv2;
	private Button setout, search,setdev;

	private ListView listview;
	private mBaseAdapter mAdapter;
	private LinearLayout rootLayout;
	private LayoutInflater mInflater1;
	private ListViewHander hander;
	private MyApplication myApp;
	private List<GPIO_INPUT_INFO> infos;
	private int currentID = -1;
	private static final int MSG_REFRSH_INFO = 2106;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_rcuioset, null);
		mRootView.addView(rootLayout, FF);

		setTextForTitle("编辑输入");
		setTextForBackBtn(getString(R.string.app_back));
		setTextForRightBtn(getString(R.string.key_edit));

		LinearLayout ll = (LinearLayout) findViewById(R.id.linerlayout1);
		ll.setBackgroundColor(Color.BLACK);

		listview = (ListView) findViewById(R.id.inputIOlist); //
		mAdapter = new mBaseAdapter();
		listview.setAdapter(mAdapter); // 为listView添加适配器
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(activityRcuIOEdit.this, "当前选择项:"+arg2, 0).show();
				currentID = arg2;
			}
		});

		mInflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels / 15; // 当前分辨率 宽度 分为15等份

		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		setdev = (Button)findViewById(R.id.setdev);
		setout = (Button) findViewById(R.id.out);
		search = (Button) findViewById(R.id.search);

		setout.setOnClickListener(this);
		search.setOnClickListener(this);
		setdev.setOnClickListener(this);
		setdev.setOnTouchListener(CommonUtils.TouchDark);
		setout.setOnTouchListener(CommonUtils.TouchDark);
		search.setOnTouchListener(CommonUtils.TouchDark);

		tv1.setWidth(w * 3); // 为每个表头文本框设置宽度
		tv2.setWidth(w * 3);

		myApp = (MyApplication) getApplication();
		myApp.setHandler(mHandler);

	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRSH_INFO:
				getListData();
				break;
			default:
				break;
			}
		};
	};

	private void getListData() {
		infos = HomeActivity.mgInfos;
		mAdapter.notifyDataSetChanged();
	}

	private class ListViewHander {
		TextView textview1;
		TextView textview2;
		CheckBox low, high;
	}

	private class mBaseAdapter extends BaseAdapter { // 继承BaseAdapter类，并重写方法

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (infos != null) {
				return infos.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (contentView == null) {
				contentView = mInflater1.inflate(R.layout.listio_item, null); // 使用表头布局test1.xml
				hander = new ListViewHander(); // 与表头的文本控件一一对应
				hander.textview1 = (TextView) contentView.findViewById(R.id.textView1);
				hander.textview2 = (TextView) contentView.findViewById(R.id.textView2);
				hander.low = (CheckBox) contentView.findViewById(R.id.low);
				hander.high = (CheckBox) contentView.findViewById(R.id.high);

				contentView.setTag(hander);

				hander.textview1.setWidth(w * 3); // 记住，这里的宽度设置必须和表头文本宽度一致
				hander.textview2.setWidth(w * 3);

				hander.textview1.setText("");
				hander.textview2.setText("");

				hander.textview1.setTextSize(15);
				hander.textview2.setTextSize(15);

			} else {
				hander = (ListViewHander) contentView.getTag();
			}

			// 为listview中的TextView布局控件添加内容
			if (infos == null) {
				return contentView;
			}
			for (int j = 0; j < infos.size(); j++) {
				if (position == j) {
					hander.textview1.setText(j + "");
					hander.textview2.setText(CommonUtils.getGBstr(infos.get(j).defName));
					if (infos.get(j).validLevel == 0) {
						hander.low.setChecked(true);
					} else {
						hander.high.setChecked(true);
					}
				}
				contentView.setBackgroundColor(Color.GRAY);
			}

			return contentView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search:
			getInputInfo();
			break;
		case R.id.setdev:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("index", currentID);
			intent.putExtras(bundle);
			intent.setClass(activityRcuIOEdit.this, activityRcuIODevSet.class);
			startActivity(intent);
			break;
		case R.id.out:
			Intent intent1 = new Intent();
			Bundle bundle1 = new Bundle();
			bundle1.putInt("index", currentID);
			intent1.putExtras(bundle1);
			intent1.setClass(activityRcuIOEdit.this, activityRcuIOoutSet.class);
			startActivity(intent1);
			break;
		default:
			break;
		}
	}

	public static void getInputInfo() {

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getIOSet_input.getValue(), 0, 0,
				CommonUtils.getZerobyte(), 0));

		CommonUtils.sendMsg();
	}

}
