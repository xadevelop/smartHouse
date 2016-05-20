package com.example.smarthouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class activityRcuIODevSelect extends AdAbstractActivity {

	/** 当前分辨率 */
	private DisplayMetrics dm;
	private int w;
	private TextView tv1, tv2, tv3, tv4;
	private CheckBox cBox;

	private ListView listview;
	private mBaseAdapter mAdapter;
	private LinearLayout rootLayout;
	private LayoutInflater mInflater1;
	private ListViewHander hander;

	private List<GPIO_INPUT_INFO> infos;
	private List<showInfo> sInfos;
	private int currentID = -1;
	List<WareDev> devs;
	private Map<Integer, Boolean> isSelected;
	private int[] num;
	private int selNum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_rcuioadd_dev, null);
		mRootView.addView(rootLayout, FF);

		setTextForTitle("设备配置");
		setTextForBackBtn(getString(R.string.app_back));
		setTextForRightBtn(getString(R.string.save));

		Intent intent = getIntent();
		// 获取该Intent所携带的数据
		Bundle bundle = intent.getExtras();
		// 从bundle数据包中取出数据
		currentID = bundle.getInt("index");

		LinearLayout ll = (LinearLayout) findViewById(R.id.linerlayout1);
		ll.setBackgroundColor(Color.BLACK);

		getListData();

		listview = (ListView) findViewById(R.id.devlist); //
		mAdapter = new mBaseAdapter();
		listview.setAdapter(mAdapter); // 为listView添加适配器

		mInflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels / 15; // 当前分辨率 宽度 分为15等份

		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv4 = (TextView) findViewById(R.id.textView4);

		tv1.setWidth(w * 3); // 为每个表头文本框设置宽度
		tv2.setWidth(w * 3);
		tv3.setWidth(w * 3);
		tv4.setWidth(w * 3);

		num = new int[2];
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < devs.size(); i++) {
			isSelected.put(i, false);
		}
	}

	private void getListData() {
		infos = HomeActivity.mgInfos;
		devs = HomeActivity.mWareDevs;
		sInfos = new ArrayList<showInfo>();

		for (int i = 0; i < devs.size(); i++) {
			showInfo info = new showInfo();
			info.roomName = CommonUtils.getGBstr(devs.get(i).roomName);
			int type = devs.get(i).devType;
			if (type == 0) {
				info.devType = "空调";
			} else if (type == 1) {
				info.devType = "电视";
			} else if (type == 2) {
				info.devType = "机顶盒";
			} else if (type == 3) {
				info.devType = "灯光";
			} else if (type == 4) {
				info.devType = "窗帘";
			} else if (type == 6) {
				info.devType = "阀门";
			}

			info.devName = CommonUtils.getGBstr(devs.get(i).devName);
			info.devid = devs.get(i).devId;
			info.devtype = devs.get(i).devType;
			info.cpuid = devs.get(i).canCpuId;
			
			sInfos.add(info);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn1:
			getSelectItem();
			break;

		default:
			break;
		}
	}

	private void getSelectItem() {
		int items = 0;
		for (int i = 0; i < devs.size(); i++) {
			if (isSelected.get(i).booleanValue() == true) {
				items++;
			}
		}

		LogUtils.LOGE("", "选择设备数:" + items);
		if (items > 2) {
			Toast.makeText(activityRcuIODevSelect.this, "选择设备太多，最多选择两个", 0).show();
		} else {
			for (int i = 0; i < devs.size(); i++) {
				if (isSelected.get(i).booleanValue() == true) {
					num[selNum] = i;
					selNum++;
				}
			}
			createSendInfo();
		}
	}

	private void createSendInfo() {

		for (int i = 0; i < selNum; i++) {
			infos.get(currentID).item[i].uid = sInfos.get(num[i]).cpuid;
			infos.get(currentID).item[i].devID = sInfos.get(num[i]).devid;
			infos.get(currentID).item[i].devType = sInfos.get(num[i]).devtype;
		}
		byte[] data = CommonUtils.createGpioItem(infos);

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_saveIOSet_input.getValue(), 0, 0, data,
				data.length));

		CommonUtils.sendMsg();
	}

	private class showInfo {
		String roomName;
		String devType;
		String devName;
		byte[] cpuid;
		byte devid;
		byte devtype;
		byte action;
		byte param1;
		byte param2;
	}

	private class ListViewHander {
		TextView textview1;
		TextView textview2;
		TextView textview3;
		CheckBox cBox;
	}

	private class mBaseAdapter extends BaseAdapter { // 继承BaseAdapter类，并重写方法

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return sInfos.size();
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
		public View getView(final int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (contentView == null) {
				contentView = mInflater1.inflate(R.layout.listio_adddev_item, null); // 使用表头布局test1.xml
				hander = new ListViewHander(); // 与表头的文本控件一一对应
				hander.textview1 = (TextView) contentView.findViewById(R.id.textView1);
				hander.textview2 = (TextView) contentView.findViewById(R.id.textView2);
				hander.textview3 = (TextView) contentView.findViewById(R.id.textView3);
				hander.cBox = (CheckBox) contentView.findViewById(R.id.pin);

				contentView.setTag(hander);

				hander.textview1.setWidth(w * 3); // 记住，这里的宽度设置必须和表头文本宽度一致
				hander.textview2.setWidth(w * 3);
				hander.textview3.setWidth(w * 3);
				hander.cBox.setWidth(w * 3);

				hander.textview1.setText("");
				hander.textview2.setText("");
				hander.textview3.setText("");
				hander.cBox.setText("");

				hander.textview1.setTextSize(15);
				hander.textview2.setTextSize(15);
				hander.textview3.setTextSize(15);

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
			for (int i = 0; i < sInfos.size(); i++) {
				if (position == i) {
					hander.textview1.setText(sInfos.get(i).roomName);
					hander.textview2.setText(sInfos.get(i).devType);
					hander.textview3.setText(sInfos.get(i).devName);
				}
			}
			hander.cBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox box = (CheckBox) v;
					LogUtils.LOGE("", position + "---" + box.isChecked());
					if (box.isChecked()) {
						isSelected.put(position, true);
					} else {
						isSelected.put(position, false);
					}
				}
			});
			hander.cBox.setChecked(isSelected.get(position));
			contentView.setBackgroundColor(Color.GRAY);
			return contentView;
		}
	}
}
