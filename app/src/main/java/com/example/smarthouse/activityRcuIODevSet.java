package com.example.smarthouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Test;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.GPIO_INPUT_INFO;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareValve;
import com.pullmi.utils.LogUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class activityRcuIODevSet extends AdAbstractActivity {

	/** 当前分辨率 */
	private DisplayMetrics dm;
	private int w;
	private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
	private Button add;

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
	private View currentView;
	private static final int MSG_REFRSH_INFO = 2106;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_rcuioset_dev, null);
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

		listview = (ListView) findViewById(R.id.devsetList); //
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
		tv6 = (TextView) findViewById(R.id.textView6);
		tv7 = (TextView) findViewById(R.id.textView7);

		add = (Button) findViewById(R.id.adddev);

		add.setOnClickListener(this);
		add.setOnTouchListener(CommonUtils.TouchDark);

		tv1.setWidth(w * 3); // 为每个表头文本框设置宽度
		tv2.setWidth(w * 3);
		tv3.setWidth(w * 3);
		tv4.setWidth(w * 3);
		tv5.setWidth(w * 3);
		tv6.setWidth(w * 3);
		tv7.setWidth(w * 3);

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
			for (int i = 0; i < devs.size(); i++) {
				if (Arrays.equals(infos.get(currentID).item[j].uid, devs.get(i).canCpuId)
						&& infos.get(currentID).item[j].devID == devs.get(i).devId
						&& infos.get(currentID).item[j].devType == devs.get(i).devType) {
					showInfo info = new showInfo();
					info.roomName = CommonUtils.getGBstr(devs.get(i).roomName);
					info.param1 = infos.get(currentID).item[j].param1 + "";
					int type = devs.get(i).devType;
					if (type == 0) {
						info.devType = "空调";
						if (infos.get(currentID).item[j].param1 == 0) {
							info.param1 = "自动";
						} else if (infos.get(currentID).item[j].param1 == 1) {
							info.param1 = "制热";
						} else if (infos.get(currentID).item[j].param1 == 2) {
							info.param1 = "制冷";
						} else {
							info.param1 = infos.get(currentID).item[j].param1 + "";
						}
					} else if (type == 1) {
						info.devType = "电视";
					} else if (type == 2) {
						info.devType = "机顶盒";
					} else if (type == 3) {
						info.devType = "灯光";
					} else if (type == 4) {
						info.devType = "窗帘";
					}

					info.devName = CommonUtils.getGBstr(devs.get(i).devName);
					int statu = infos.get(currentID).item[j].bOnoff;
					if (statu == 0) {
						info.bOnOff = "关闭";
					} else {
						info.bOnOff = "打开";
					}

					info.param2 = infos.get(currentID).item[j].param2 + "";
					info.time = infos.get(currentID).itemRTime[j] + "";

					sInfo.add(info);
				}
			}
		}

		mAdapter.notifyDataSetChanged();
	}

	private class showInfo {
		String roomName;
		String devType;
		String devName;
		String bOnOff;
		String param1;
		String param2;
		String time;
	}

	private class ListViewHander {
		TextView textview1;
		TextView textview2;
		TextView textview3;
		EditText textview4;
		EditText textview5;
		EditText textview6;
		EditText textview7;
	}
	
	private Integer index = -1; 
	
	private class mBaseAdapter extends BaseAdapter { // 继承BaseAdapter类，并重写方法

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (sInfo != null) {
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
				contentView = mInflater1.inflate(R.layout.devset_list_edit_header, null); // 使用表头布局test1.xml
				hander = new ListViewHander(); // 与表头的文本控件一一对应
				hander.textview1 = (TextView) contentView.findViewById(R.id.textView1);
				hander.textview2 = (TextView) contentView.findViewById(R.id.textView2);
				hander.textview3 = (TextView) contentView.findViewById(R.id.textView3);
				hander.textview4 = (EditText) contentView.findViewById(R.id.textView4);
				hander.textview5 = (EditText) contentView.findViewById(R.id.textView5);
				hander.textview6 = (EditText) contentView.findViewById(R.id.textView6);
				hander.textview7 = (EditText) contentView.findViewById(R.id.textView7);

				contentView.setTag(hander);

				hander.textview1.setWidth(w * 3); // 记住，这里的宽度设置必须和表头文本宽度一致
				hander.textview2.setWidth(w * 3);
				hander.textview3.setWidth(w * 3);
				hander.textview4.setWidth(w * 3);
				hander.textview5.setWidth(w * 3);
				hander.textview6.setWidth(w * 3);
				hander.textview7.setWidth(w * 3);

				hander.textview1.setText("");
				hander.textview2.setText("");
				hander.textview3.setText("");
				hander.textview4.setText("");
				hander.textview5.setText("");
				hander.textview6.setText("");
				hander.textview7.setText("");

				hander.textview1.setTextSize(15);
				hander.textview2.setTextSize(15);
				hander.textview3.setTextSize(15);
				hander.textview4.setTextSize(15);
				hander.textview5.setTextSize(15);
				hander.textview6.setTextSize(15);
				hander.textview7.setTextSize(15);
				
				LogUtils.LOGE(TAG, "当前pos:" + position);
				if (position == 0) {
					hander.textview4.addTextChangedListener(new textWatcher(hander.textview4));
					hander.textview5.addTextChangedListener(new textWatcher(hander.textview5));
					hander.textview6.addTextChangedListener(new textWatcher(hander.textview6));
					hander.textview7.addTextChangedListener(new textWatcher(hander.textview7));
				}else {
					hander.textview4.addTextChangedListener(new textWatcher2(hander.textview4));
					hander.textview5.addTextChangedListener(new textWatcher2(hander.textview5));
					hander.textview6.addTextChangedListener(new textWatcher2(hander.textview6));
					hander.textview7.addTextChangedListener(new textWatcher2(hander.textview7));
				}
			

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
					hander.textview1.setText(sInfo.get(i).roomName);
					hander.textview2.setText(sInfo.get(i).devType);
					hander.textview3.setText(sInfo.get(i).devName);
					hander.textview4.setText(sInfo.get(i).bOnOff);
					hander.textview5.setText(sInfo.get(i).param1);
					hander.textview6.setText(sInfo.get(i).param2);
					hander.textview7.setText(sInfo.get(i).time);
				}
			}
			contentView.setBackgroundColor(Color.GRAY);
			return contentView;
		}
	}

	private class textWatcher implements TextWatcher{

		private EditText editID = null;
		
		public textWatcher(EditText id){
			editID = id;
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			//
			 if (s != null && !"".equals(s.toString())) {
				 if (editID == hander.textview4) {
	                 LogUtils.LOGE(TAG, "当前变化pos:" + hander.textview4.getText().toString());
				}
				 if (editID == hander.textview5) { 
	                 LogUtils.LOGE(TAG, "当前变化pos:" + hander.textview5.getText().toString());
				}
             }

		}
	};
	
	private class textWatcher2 implements TextWatcher{

		private EditText editID = null;
		
		public textWatcher2(EditText id){
			editID = id;
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			//
			 if (s != null && !"".equals(s.toString())) {
				 if (editID == hander.textview6) {
	                 LogUtils.LOGE(TAG, "当前变化pos:" + hander.textview6.getText().toString());
				}
				 if (editID == hander.textview7) { 
	                 LogUtils.LOGE(TAG, "当前变化pos:" + hander.textview7.getText().toString());
				}
             }

		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.adddev:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("index", currentID);
			intent.putExtras(bundle);
			intent.setClass(activityRcuIODevSet.this, activityRcuIODevSelect.class);
			startActivity(intent);
			break;
		case R.id.title_btn1:
			getNewInfoFromUI();
			break;
		default:
			break;
		}
	}

	private void getNewInfoFromUI() {
		// TODO Auto-generated method stub
		for (int i = 0; i < sInfo.size(); i++) {
			String statu, param1, param2, time;

			statu = hander.textview4.getText().toString();
			if (statu.equals("关闭")) {
				infos.get(currentID).item[i].bOnoff = 0;
			} else if (statu.equals("打开")) {
				infos.get(currentID).item[i].bOnoff = 1;
			}

			param1 = hander.textview5.getText().toString();
			if (param1.equals("自动")) {
				infos.get(currentID).item[i].param1 = 0;
			} else if (statu.equals("制热")) {
				infos.get(currentID).item[i].param2 = 1;
			} else if (statu.equals("制冷")) {
				infos.get(currentID).item[i].param2 = 2;
			} else {
				infos.get(currentID).item[i].param2 = 0;
			}

			param2 = hander.textview6.getText().toString();
			infos.get(currentID).item[i].param2 = (byte) Integer.parseInt(param2);

			time = hander.textview7.getText().toString();
			infos.get(currentID).itemRTime[i] = (byte) Integer.parseInt(time);
		}

		byte[] data = CommonUtils.createGpioItem(infos);

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_saveIOSet_input.getValue(), 0, 0, data,
				data.length));

		CommonUtils.sendMsg();
	}
}
