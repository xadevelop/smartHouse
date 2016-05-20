package com.example.smarthouse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.adapter.sceneListViewAdapter;
import com.example.adapter.scenesetListViewAdapter;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.UnitNode;
import com.pullmi.entity.WareDev;
import com.pullmi.utils.LogUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class createSceneEventActivity2 extends AdAbstractActivity {

	private static final String TAG = "createSceneEventActivity2";

	private LinearLayout createLayout;
	private Button newEvents;

	private ListView listview;
	private ArrayList<String> arrayList;

	ArrayList<HashMap<String, Object>> lst;

	private LinearLayout.LayoutParams WW = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

	private static List<WareDev> mWareDevs;
	byte[] devBuff;
	byte[] senddata;
	private byte eventid;
	private byte[] sceneName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.create_scene_events, null);
		mRootView.addView(createLayout, FF);

		initComponent();

		Intent intent = getIntent();
		eventid = intent.getExtras().getByte("eventid");
		LogUtils.LOGE(TAG, "当前模式" + eventid);
		sceneName = intent.getByteArrayExtra("sceneName");
	}

	private void initComponent() {

		setTextForTitle("情景设置");
		setTextForBackBtn(getString(R.string.app_back));
		setTextForRightBtn(getString(R.string.save));

		listview = (ListView) findViewById(R.id.lst_sys_op_one);

		arrayList = new ArrayList<String>();
		showRooms();

		scenesetListViewAdapter adapter = new scenesetListViewAdapter(this, arrayList);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String roomName = arrayList.get(position);

				Intent intent = new Intent();
				intent.putExtra("roomName", roomName);
				intent.setClass(createSceneEventActivity2.this, controlTimerEventDevActivity.class);
				startActivity(intent);
			}
		});
	}

	private void showRooms() {

		mWareDevs = HomeActivity.mWareDevs;

		List<WareDev> list = removeDuplicate(mWareDevs);
		for (int i = 0; i < list.size(); i++) {
			arrayList.add(CommonUtils.getGBstr(list.get(i).roomName));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn4:
			this.finish();
			break;
		case R.id.title_btn1:
			int devCnt = HomeActivity.mSceneDevs.size();
			if (devCnt == 0) {
				break;
			}
			byte[] data = CommonUtils.createSceneEventItem(HomeActivity.mSceneDevs);

			devBuff = CommonUtils.createSceneEvent(CommonUtils.getGBstr(sceneName), devCnt,
					eventid, data);

			if (isEventldExits(eventid)) {
				GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
						CommonUtils.getLocalIp(),
						UdpProPkt.E_UDP_RPO_DAT.e_udpPro_editSceneEvents.getValue(), 0, 0, devBuff,
						devBuff.length));
			} else {
				GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
						CommonUtils.getLocalIp(),
						UdpProPkt.E_UDP_RPO_DAT.e_udpPro_addSceneEvents.getValue(), 0, 0, devBuff,
						devBuff.length));
			}
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}

	private boolean isEventldExits(int eventid) {
		for (int i = 0; i < HomeActivity.mSceneEvents.size(); i++) {
			if (eventid == HomeActivity.mSceneEvents.get(i).eventld) {
				return true;
			}
		}

		return false;
	}

	public List<WareDev> removeDuplicate(List<WareDev> list) {
		List<WareDev> devs = new ArrayList<WareDev>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (CommonUtils.getGBstr(devs.get(j).roomName).equals(
						CommonUtils.getGBstr(devs.get(i).roomName))) {
					devs.remove(j);
				}
			}
		}
		return devs;
	}
}
