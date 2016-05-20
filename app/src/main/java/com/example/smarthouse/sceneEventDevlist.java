package com.example.smarthouse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareSceneDevItem;
import com.pullmi.entity.WareSceneEvent;
import com.pullmi.utils.LogUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class sceneEventDevlist extends AdAbstractActivity {

	private static final String TAG = "sceneEventDevlist";
	private GridView gridView;
	private Button delScene, exeScene;
	private ArrayList<HashMap<String, Object>> lst;
	private SimpleAdapter saItem;

	private List<WareSceneEvent> mWareSceneEvents;

	byte[] devBuff;
	byte[] senddata;
	int eventid;
	private String dstip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scene_deledit_dev_gird);

		gridView = (GridView) findViewById(R.id.grid);
		delScene = (Button) findViewById(R.id.del_scene);
		delScene.setOnClickListener(this);
		exeScene = (Button) findViewById(R.id.exe_scene);
		exeScene.setOnClickListener(this);

		delScene.setOnTouchListener(TouchDark);
		exeScene.setOnTouchListener(TouchDark);

		lst = new ArrayList<HashMap<String, Object>>();
		showDevsByType();
		saItem = new SimpleAdapter(this, lst, // 数据源
				R.layout.alarm_grid_item, // xml实现
				new String[] { "ItemImage", "ItemText" }, // 对应map的Key
				new int[] { R.id.alarm_grid_item_iv, R.id.alarm_grid_item_tv }); // 对应R的Id

		gridView.setAdapter(saItem);
		saItem.notifyDataSetChanged();
	}

	private void showDevsByType() {
		// TODO Auto-generated method stub
		mWareSceneEvents = HomeActivity.mSceneEvents;

		Intent intent = getIntent();
		eventid = intent.getExtras().getByte("eventid");
		LogUtils.LOGE(TAG, "当前模式" + eventid);

		Object res = "", text = "";

		for (int i = 0; i < mWareSceneEvents.size(); i++) {
			int eventID = mWareSceneEvents.get(i).eventld;
			if (eventid == eventID) {
				int devCnt = mWareSceneEvents.get(i).devCnt;
				for (int j = 0; j < devCnt; j++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					int devtype = mWareSceneEvents.get(i).itemAry[j].devType;
					String devName = getDevNameFromWareDevList(mWareSceneEvents
							.get(i).itemAry[j]);
					if (devName.equals("")) {
						continue;
					}
					int bOnOff = mWareSceneEvents.get(i).itemAry[j].bOnOff;
					switch (devtype) {
					case 0: // 空调
						if (bOnOff == 1) {
							res = R.drawable.curtain_open;
						} else {
							res = R.drawable.curtain;
						}
						text = devName;
						break;
					case 1: // 电视
						if (bOnOff == 1) {
							res = R.drawable.curtain_open;
						} else {
							res = R.drawable.curtain;
						}
						text = devName;
						break;
					case 3: // 灯光
						if (bOnOff == 1) {
							res = R.drawable.light_on;
						} else {
							res = R.drawable.light_off;
						}
						text = devName;
						break;
					case 4: // 窗帘
						if (bOnOff == 1) {
							res = R.drawable.curtain_open;
						} else {
							res = R.drawable.curtain;
						}
						text = devName;
						break;
					case 5: // 门锁
						if (bOnOff == 1) {
							res = R.drawable.valve_open;
						} else {
							res = R.drawable.valve_close;
						}
						text = devName;
						break;
					case 6: // 阀门
						if (bOnOff == 1) {
							res = R.drawable.lock_open;
						} else {
							res = R.drawable.lock_close;
						}
						text = devName;
						break;
					case 7: // 新风
						if (bOnOff == 1) {
							res = R.drawable.curtain_open;
						} else {
							res = R.drawable.curtain;
						}
						text = devName;
						break;
					default:
						break;
					}
					map.put("ItemImage", res);
					map.put("ItemText", text);
					lst.add(map);
				}
				break;
			}
		}
	}

	private String getDevNameFromWareDevList(WareSceneDevItem item) {

		String nameString = "";
		for (int i = 0; i < HomeActivity.mWareDevs.size(); i++) {
			int id = HomeActivity.mWareDevs.get(i).devId;
			int devtype = HomeActivity.mWareDevs.get(i).devType;

			if (item.devID == id
					&& item.devType == devtype
					&& Arrays.equals(item.uid,
							HomeActivity.mWareDevs.get(i).canCpuId)) {

				nameString = new String(
						CommonUtils.getGBstr(HomeActivity.mWareDevs.get(i).devName));
			}
		}

		return nameString;
	}

	private void exeScene() {
		// TODO Auto-generated method stub
		createSceneEvents(1);
	}

	private void delScene() {

		createSceneEvents(0);
	}

	private byte[] createSceneEvents(int flag) {
		byte[] data = null;
		int pos = -1;
		for (int i = 0; i < mWareSceneEvents.size(); i++) {
			if (mWareSceneEvents.get(i).eventld == eventid) {
				pos = i;
			}
		}
		if (pos == -1) {
			return null;
		}
		if (flag == 0 && pos == 0) {
			Toast.makeText(this, "该情景模式不能删除", 0).show();
			return null;
		}
		if (flag == 0 && pos == 1) {
			Toast.makeText(this, "该情景模式不能删除", 0).show();
			return null;
		}

		devBuff = CommonUtils.createSceneEvent(mWareSceneEvents.get(pos));

		RcuInfo rcuInfo = new RcuInfo();

		try {
			dstip = InetAddress.getByAddress(rcuInfo.getIpAddr())
					.getHostAddress();
			if (flag == 0) {
				GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(dstip,
						CommonUtils.getLocalIp(),
						UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delSceneEvents
								.getValue(), 0, 0, devBuff, devBuff.length));
				CommonUtils.sendMsg();
			} else if (flag == 1) {
				GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(dstip,
						CommonUtils.getLocalIp(),
						UdpProPkt.E_UDP_RPO_DAT.e_udpPro_exeSceneEvents
								.getValue(), 0, 0, devBuff, devBuff.length));
				CommonUtils.sendMsg();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.del_scene:
			delScene();
			break;
		case R.id.exe_scene:
			exeScene();
		default:
			break;
		}
	}
}
