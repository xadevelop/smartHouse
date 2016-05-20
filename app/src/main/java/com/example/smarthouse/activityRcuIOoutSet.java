package com.example.smarthouse;

import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.GPIO_INPUT_INFO;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareValve;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Element;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class activityRcuIOoutSet extends AdAbstractActivity {

	/** 当前分辨率 */
	private DisplayMetrics dm;
	private int w;
	private TextView tv1, tv2, tv3, tv4, tv5;

	private ListView listview;
	private mBaseAdapter mAdapter;
	private LinearLayout rootLayout;
	private LayoutInflater mInflater1;
	private ListViewHander hander;
	private MyApplication myApp;
	private List<GPIO_INPUT_INFO> infos;
	private int currentID = -1;
	List<WareDev> devs;
	List<showInfo> sInfo;
	private static final int MSG_REFRSH_INFO = 2106;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_rcuioset_out, null);
		mRootView.addView(rootLayout, FF);

		setTextForTitle("设备配置");
		setTextForBackBtn(getString(R.string.app_back));
		setTextForRightBtn(getString(R.string.key_edit));

		Intent intent = getIntent();
		// 获取该Intent所携带的数据
		Bundle bundle = intent.getExtras();
		// 从bundle数据包中取出数据
		currentID = bundle.getInt("index");

		LinearLayout ll = (LinearLayout) findViewById(R.id.linerlayout1);
		ll.setBackgroundColor(Color.BLACK);

		listview = (ListView) findViewById(R.id.outlist); //
		mAdapter = new mBaseAdapter();
		listview.setAdapter(mAdapter); // 为listView添加适配器
		getListData();
		mInflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels / 15; // 当前分辨率 宽度 分为15等份

		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv4 = (TextView) findViewById(R.id.textView4);
		tv5 = (TextView) findViewById(R.id.textView5);

		tv1.setWidth(w * 3); // 为每个表头文本框设置宽度
		tv2.setWidth(w * 3);
		tv3.setWidth(w * 3);
		tv4.setWidth(w * 3);
		tv5.setWidth(w * 3);

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
		sInfo = new ArrayList<showInfo>();
		infos = HomeActivity.mgInfos;
		devs = HomeActivity.mWareDevs;

		if (currentID == -1) {
			return;
		}

		for (int j = 0; j < 2; j++) {
			showInfo info = new showInfo();
			info.outsignal = infos.get(j).outOP[j].nOutput;
			info.name = "";
			if (infos.get(j).outOP[j].inputOP == 0) {
				info.action = "低";
			}else if (infos.get(j).outOP[j].inputOP == 1) {
				info.action = "高";
			}else {
				info.action = "翻转";
			}
			info.time = 0 + "";
			if (infos.get(j).outOP[j].valid == 1) {
				info.val = "有效";
			}else {
				info.val = "无效";
			}
	
			sInfo.add(info);
		}

		mAdapter.notifyDataSetChanged();
	}

	private class showInfo {
		byte outsignal;
		String name;
		String action;
		String time;
		String val;
	}

	private class ListViewHander {
		TextView textview1;
		TextView textview2;
		TextView textview3;
		TextView textview4;
		TextView textview5;
	}

	private class mBaseAdapter extends BaseAdapter { // 继承BaseAdapter类，并重写方法

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (infos != null) {
				return sInfo.size();
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
				contentView = mInflater1.inflate(R.layout.outset_list_header, null); // 使用表头布局test1.xml
				hander = new ListViewHander(); // 与表头的文本控件一一对应
				hander.textview1 = (TextView) contentView.findViewById(R.id.textView1);
				hander.textview2 = (TextView) contentView.findViewById(R.id.textView2);
				hander.textview3 = (TextView) contentView.findViewById(R.id.textView3);
				hander.textview4 = (TextView) contentView.findViewById(R.id.textView4);
				hander.textview5 = (TextView) contentView.findViewById(R.id.textView5);

				contentView.setTag(hander);

				hander.textview1.setWidth(w * 3); // 记住，这里的宽度设置必须和表头文本宽度一致
				hander.textview2.setWidth(w * 3);
				hander.textview3.setWidth(w * 3);
				hander.textview4.setWidth(w * 3);
				hander.textview5.setWidth(w * 3);

				hander.textview1.setText("");
				hander.textview2.setText("");
				hander.textview3.setText("");
				hander.textview4.setText("");
				hander.textview5.setText("");

				hander.textview1.setTextSize(15);
				hander.textview2.setTextSize(15);
				hander.textview3.setTextSize(15);
				hander.textview4.setTextSize(15);
				hander.textview5.setTextSize(15);

			} else {
				hander = (ListViewHander) contentView.getTag();
			}

			// 为listview中的TextView布局控件添加内容
			if (infos == null) {
				return contentView;
			}
			if (currentID == -1) {
				return contentView;
			}
			for (int i = 0; i < sInfo.size(); i++) {
				if (position == i) {
					hander.textview1.setText(sInfo.get(i).outsignal+"");
					hander.textview2.setText(sInfo.get(i).name);
					hander.textview3.setText(sInfo.get(i).action);
					hander.textview4.setText(sInfo.get(i).time);
					hander.textview5.setText(sInfo.get(i).val);
				}
			}
			contentView.setBackgroundColor(Color.GRAY);
			return contentView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn1:
			createSendInfo();
			break;
		default:
			break;
		}
	}
	
	private void createSendInfo() {
		
		byte[] data = CommonUtils.createGpioItem(infos);

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_saveIOSet_output.getValue(), 0, 0, data,
				data.length));

		CommonUtils.sendMsg();
	}

}
