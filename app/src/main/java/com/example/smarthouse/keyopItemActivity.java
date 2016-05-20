package com.example.smarthouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.adapter.keyOpItemListViewAdapter;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareKeyOpItem;
import com.pullmi.utils.LogUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class keyopItemActivity extends AdAbstractActivity implements OnItemLongClickListener {

	private LinearLayout createLayout;
	private ListView listview;
	private ProgressBar progress;
	private Button edit, add;

	private ArrayList<String> arrayList;
	keyOpItemListViewAdapter adapter;

	private byte[] devUnitID;
	private int index;
	byte[] senddata;

	private List<WareKeyOpItem> mKeyOpItems;
	private List<WareKeyOpItem> mKeyOpAddItems;
	private List<WareDev> mWareDevs;

	MyApplication myApp;
	private static final int MSG_REFRSH_INFO = 2102;
	private static final int MSG_SETKEYITEM_INFO = 2001;
	private static final int MSG_DELKEYITEM_INFO = 2002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		devUnitID = intent.getExtras().getByteArray("devUnitID");
		index = intent.getExtras().getInt("index");
	
		LogUtils.LOGE(TAG, "当前索引:"+index);
		getBroadKeyOpItemInfo();

		initComponent();

		myApp = (MyApplication) getApplication();
		myApp.setHandler(mHandler);
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.systemSetting));
		setTextForBackBtn(getString(R.string.key_edit));
		setTextForRightBtn(getString(R.string.save));

		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_keyopitem_set, null);
		mRootView.addView(createLayout, FF);
		listview = (ListView) findViewById(R.id.key_set_lst);
		progress = (ProgressBar) findViewById(R.id.chnProcessBar);

		arrayList = new ArrayList<String>();
		adapter = new keyOpItemListViewAdapter(this, arrayList);
		listview.setAdapter(adapter);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setEnabled(true);
		listview.setOnItemLongClickListener(this);

	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRSH_INFO:
				getListData();
				break;
			case MSG_SETKEYITEM_INFO:
				Toast.makeText(keyopItemActivity.this, "编辑按键配置成功", 0).show();
				getBroadKeyOpItemInfo();
				break;
			case MSG_DELKEYITEM_INFO:
				Toast.makeText(keyopItemActivity.this, "删除按键配置成功", 0).show();
				getListData();
				break;
			default:
				break;
			}
		};
	};

	private void getListData() {

		progress.setVisibility(View.GONE);
		listview.setVisibility(View.VISIBLE);
		
		mKeyOpItems = HomeActivity.mKeyOpItems;
		mWareDevs = HomeActivity.mWareDevs;
		
		if (mKeyOpItems.size() == 0) {
			return;
		}
		LogUtils.LOGE(TAG, "mKeyOpItems:" + mKeyOpItems.size());
		LogUtils.LOGE(TAG, "mWareDevs:" + mWareDevs.size());
		
		arrayList.clear();
		for (int i = 0; i < mKeyOpItems.size(); i++) {
			if (mKeyOpItems.get(i).index == index) {
				for (int j = 0; j < mWareDevs.size(); j++) {
					if (mKeyOpItems.get(i).devId == mWareDevs.get(j).devId
							&& mKeyOpItems.get(i).devType == mWareDevs.get(j).devType
							&& Arrays.equals(mKeyOpItems.get(i).devUnitID, mWareDevs.get(j).canCpuId)) {
						arrayList.add(CommonUtils.getGBstr(mWareDevs.get(j).devName));
					}
				}
				adapter.notifyDataSetChanged();
				LogUtils.LOGE(TAG, "arraylist:" + arrayList.size());
			}
		}
		

	}

	private void getBroadKeyOpItemInfo() {

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getKeyOpItems.getValue(), 0, index, devUnitID,
				devUnitID.length));

		CommonUtils.sendMsg();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn4:
			Intent intent = new Intent();
			intent.setClass(this, keyOpItemAddActivity.class);
			startActivity(intent);
			break;
		case R.id.title_btn1:
			mKeyOpAddItems = HomeActivity.mKeyOpAddItems;
			if (mKeyOpAddItems.size() == 0) {
				break;
			}
			byte[] data = CommonUtils.createKeyOpItemsInfo(devUnitID, mKeyOpAddItems, index);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_setKeyOpItems.getValue(), 0, index, data,
					data.length));

			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		byte[] data = CommonUtils.createDelKeyOpItemInfo(devUnitID, mKeyOpItems.get(arg2));

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delKeyOpItems.getValue(), 0, index, data,
				data.length));

		CommonUtils.sendMsg();

		arrayList.remove(arg2);
		mKeyOpItems.remove(arg2);
		adapter.notifyDataSetChanged();

		return false;
	}
}
