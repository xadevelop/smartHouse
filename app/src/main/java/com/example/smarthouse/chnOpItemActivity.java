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

import com.example.adapter.chnOpItemListViewAdapter;
import com.example.adapter.keyInputListViewAdapter;
import com.example.adapter.keyOpItemListViewAdapter;
import com.example.adapter.sceneEventsListViewAdapter;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareBoardKeyInput;
import com.pullmi.entity.WareChnOpItem;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareKeyOpItem;
import com.pullmi.entity.devkeyValue;
import com.pullmi.utils.LogUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class chnOpItemActivity extends AdAbstractActivity implements
		OnItemLongClickListener {

	private LinearLayout createLayout;
	private ListView listview;
	private ProgressBar progress;
	private Button edit, add;

	private ArrayList<devkeyValue> arrayList;
	chnOpItemListViewAdapter adapter;

	private byte[] devUnitID;
	private String devName;
	byte[] senddata;

	private List<WareChnOpItem> mChnOpItems;
	private List<WareBoardKeyInput> mBoardKeyInputs;
	private List<WareDev> mWareDevs;

	MyApplication myApp;
	private static final int MSG_REFRSH_INFO = 2103;
	private static final int MSG_SETCHNOPITEM_INFO = 2003;
	private static final int MSG_DELCHNOPITEM_INFO = 2004;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		devUnitID = intent.getExtras().getByteArray("devUnitID");
		devName = intent.getExtras().getString("devName");

		getBroadChnOpItemInfo();

		initComponent();

		myApp = (MyApplication) getApplication();
		myApp.setHandler(mHandler);
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.systemSetting));
		setTextForBackBtn(getString(R.string.key_edit));
		setTextForRightBtn(getString(R.string.save));

		createLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_keyopitem_set, null);
		mRootView.addView(createLayout, FF);
		listview = (ListView) findViewById(R.id.key_set_lst);
		progress = (ProgressBar) findViewById(R.id.chnProcessBar);

		arrayList = new ArrayList<devkeyValue>();
		adapter = new chnOpItemListViewAdapter(this, arrayList);

		listview.setAdapter(adapter);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setEnabled(true);
		listview.setOnItemLongClickListener(this);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRSH_INFO:
				getListData(1);
				break;
			case MSG_SETCHNOPITEM_INFO:
				Toast.makeText(chnOpItemActivity.this, "编辑设备按键配置成功", 0).show();
				getBroadChnOpItemInfo();
				break;
			case MSG_DELCHNOPITEM_INFO:
				Toast.makeText(chnOpItemActivity.this, "删除设备按键配置成功", 0).show();
				getBroadChnOpItemInfo();
				break;
			default:
				break;
			}
		};
	};

	private void getListData(int flag) {

		progress.setVisibility(View.GONE);
		listview.setVisibility(View.VISIBLE);

		mChnOpItems = HomeActivity.mChnOpItems;
		mBoardKeyInputs = HomeActivity.mBoardKeyInputs;

		arrayList.clear();

		getKeyList();
		/*
		 * for (int i = 0; i < arrayList.size(); i++) { LogUtils.LOGE(TAG,
		 * arrayList.get(i).keyName); }
		 */

		adapter.notifyDataSetChanged();
	}

	private void getKeyList() {
		mChnOpItems = HomeActivity.mChnOpItems;

		mBoardKeyInputs = HomeActivity.mBoardKeyInputs;

		for (int i = 0; i < mChnOpItems.size(); i++) {

			if (mChnOpItems.get(0).keyDownValid > 0) {
				// 显示按下按键的名称,命令
				String binaryString = Integer
						.toBinaryString(mChnOpItems.get(i).keyDownValid);
				System.out.println(binaryString);
				int pos = -1;
				for (int j = 0; j < 6; j++) {
					if (get(mChnOpItems.get(i).keyDownValid, j) == 1) {
						pos = j;
						devkeyValue value = new devkeyValue();
						switch (pos) {
						case 0:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[0]);
							value.cmd = mChnOpItems.get(i).keyDownCmd[0];

							break;
						case 1:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[1]);
							value.cmd = mChnOpItems.get(i).keyDownCmd[1];
							break;
						case 2:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[2]);
							value.cmd = mChnOpItems.get(i).keyDownCmd[2];
							break;
						case 3:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[3]);
							value.cmd = mChnOpItems.get(i).keyDownCmd[3];
							break;
						case 4:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[4]);
							value.cmd = mChnOpItems.get(i).keyDownCmd[4];
							break;
						case 5:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[5]);
							value.cmd = mChnOpItems.get(i).keyDownCmd[5];
							break;
						default:
							break;
						}
						// 根据cmd显示命令名称
						value.cmdName = getCmdNameByType(
								mChnOpItems.get(i).devType, value.cmd);
						value.boardName = getBoardName(mChnOpItems.get(i).devUnitID);
						value.op = "按下";
						arrayList.add(value);
					}
				}

			}
			if (mChnOpItems.get(i).keyUpValid > 0) {
				// 显示按下按键的名称,命令
				String binaryString = Integer
						.toBinaryString(mChnOpItems.get(i).keyUpValid);
				System.out.println(binaryString);
				int pos = -1;
				for (int j = 0; j < 6; j++) {
					if (get(mChnOpItems.get(i).keyUpValid, j) == 1) {
						pos = j;
						devkeyValue value = new devkeyValue();
						switch (pos) {
						case 0:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[0]);
							value.cmd = mChnOpItems.get(i).keyUpCmd[0];
							break;
						case 1:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[1]);
							value.cmd = mChnOpItems.get(i).keyUpCmd[1];
							break;
						case 2:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[2]);
							value.cmd = mChnOpItems.get(i).keyUpCmd[2];
							break;
						case 3:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[3]);
							value.cmd = mChnOpItems.get(i).keyUpCmd[3];
							break;
						case 4:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[4]);
							value.cmd = mChnOpItems.get(i).keyUpCmd[4];
							break;
						case 5:
							value.keyName = CommonUtils
									.getGBstr(mBoardKeyInputs.get(i).keyName[5]);
							value.cmd = mChnOpItems.get(i).keyUpCmd[5];
							break;
						default:
							break;
						}
						// 根据cmd显示命令名称
						value.cmdName = getCmdNameByType(
								mChnOpItems.get(i).devType, value.cmd);
						value.boardName = getBoardName(mChnOpItems.get(i).devUnitID);
						value.op = "弹起";
						arrayList.add(value);
					}
				}
			}
		}
	}

	public static int get(int num, int index) {
		return (num & (0x1 << index)) >> index;
	}

	private String getBoardName(byte[] uid) {
		String boardName = "";

		for (int i = 0; i < mBoardKeyInputs.size(); i++) {
			if (Arrays.equals(uid, mBoardKeyInputs.get(i).devUnitID)) {
				boardName = CommonUtils
						.getGBstr(mBoardKeyInputs.get(i).boardName);
			}
		}
		return boardName;
	}

	private String getCmdNameByType(byte devType, byte cmd) {
		// TODO Auto-generated method stub
		String cmdName = "";
		if (devType == 0) {
			switch (cmd) {
			case 0:
				cmdName = "开关";
				break;
			case 1:
				cmdName = "模式";
				break;
			case 2:
				cmdName = "风速";
				break;
			case 3:
				cmdName = "温度+";
				break;
			case 4:
				cmdName = "温度-";
				break;
			default:
				break;
			}
		} else if (devType == 3) {
			switch (cmd) {
			case 1:
				cmdName = "打开";
				break;
			case 2:
				cmdName = "关闭";
				break;
			case 3:
				cmdName = "开关";
				break;
			case 4:
				cmdName = "变亮";
				break;
			case 5:
				cmdName = "变暗";
				break;
			default:
				break;
			}
		} else if (devType == 4) {
			switch (cmd) {
			case 1:
				cmdName = "打开";
				break;
			case 2:
				cmdName = "关闭";
				break;
			case 3:
				cmdName = "停止";
				break;
			case 4:
				cmdName = "开关停";
				break;
			default:
				break;
			}
		} else {
			cmdName = "操作";
		}

		return cmdName;
	}

	private void getBroadChnOpItemInfo() {
		mWareDevs = HomeActivity.mWareDevs;
		mChnOpItems = new ArrayList<WareChnOpItem>();

		WareDev dev = new WareDev();
		for (int i = 0; i < mWareDevs.size(); i++) {
			if (Arrays.equals(devUnitID, mWareDevs.get(i).canCpuId)
					&& devName
							.equals(CommonUtils.getGBstr(mWareDevs.get(i).devName))) {
				dev = mWareDevs.get(i);
				break;
			}
		}
		byte[] data = createChnOpItemInfo(devUnitID, dev, mChnOpItems);
		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
				GlobalVars.getDstip(), CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getChnOpItems.getValue(), 0,
				0, data, data.length));

		CommonUtils.sendMsg();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn4:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("devName", devName);
			bundle.putByteArray("devUnitID", devUnitID);
			intent.putExtras(bundle);
			intent.setClass(this, chnOpItemAddActivity.class);
			startActivity(intent);
			break;
		case R.id.title_btn1:
			List<WareChnOpItem> mChnOpAddItems = HomeActivity.mChnOpAddItems;
			if (mChnOpAddItems.size() == 0) {
				break;
			}
			WareDev dev = new WareDev();
			for (int i = 0; i < mWareDevs.size(); i++) {
				if (Arrays.equals(devUnitID, mWareDevs.get(i).canCpuId)
						&& devName
								.equals(CommonUtils.getGBstr(mWareDevs.get(i).devName))) {
					dev = mWareDevs.get(i);
					break;
				}
			}
			byte[] data = createChnOpItemInfo(devUnitID, dev, mChnOpAddItems);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_setChnOpItems.getValue(),
					0, 0, data, data.length));

			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		WareDev dev = new WareDev();
		for (int i = 0; i < mWareDevs.size(); i++) {
			if (Arrays.equals(devUnitID, mWareDevs.get(i).canCpuId)
					&& devName
							.equals(CommonUtils.getGBstr(mWareDevs.get(i).devName))) {
				dev = mWareDevs.get(i);
				WareChnOpItem item = getDelitem(arg2);
				byte[] data = createDelChnOpItemInfo(devUnitID, dev, item);
				GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars
						.getDstip(), CommonUtils.getLocalIp(),
						UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delChnOpItems
								.getValue(), 0, 1, data, data.length));
				break;
			}

		}

		CommonUtils.sendMsg();

		arrayList.remove(arg2);
		adapter.notifyDataSetChanged();

		return false;
	}

	private WareChnOpItem getDelitem(int arg2) {
		// TODO Auto-generated method stub
		WareChnOpItem item = new WareChnOpItem();
		item.keyDownCmd = new byte[6];
		item.keyUpCmd = new byte[6];

		for (int i = 0; i < mBoardKeyInputs.size(); i++) {

			if (CommonUtils.getGBstr(mBoardKeyInputs.get(i).boardName).equals(
					arrayList.get(arg2).boardName)) {
				item.devUnitID = mBoardKeyInputs.get(i).devUnitID;
				for (int j = 0; j < 6; j++) {
					if (CommonUtils.getGBstr(mBoardKeyInputs.get(i).keyName[j])
							.equals(arrayList.get(arg2).keyName)) {
						if (arrayList.get(arg2).op.equals("弹起")) {
							item.keyUpValid = (byte) (1 << j);
							item.keyUpCmd[j] = 1;
						} else if (arrayList.get(arg2).op.equals("按下")) {
							item.keyDownValid = (byte) (1 << j);
							item.keyDownCmd[j] = 1;
						}
					}
				}
			}
		}

		item.rev1 = 0;
		item.rev2 = 0;
		item.rev3 = 0;

		return item;
	}

	private byte[] createDelChnOpItemInfo(byte[] devUnitID, WareDev dev,
			WareChnOpItem item) {
		// TODO Auto-generated method stub
		int item_size = 32;

		byte[] data = new byte[12 + 1 + 1 + item_size];

		CommonUtils.copyBytes(devUnitID, data, 0, 0, 12);

		data[12 + 0] = dev.devType;
		data[12 + 1] = dev.devId;

		CommonUtils.copyBytes(item.devUnitID, data, 0, 14, 12);
		data[14 + 12] = item.keyDownValid;
		data[14 + 13] = item.keyUpValid;
		data[14 + 14] = (byte) item.rev1;
		data[14 + 15] = 0;

		CommonUtils.copyBytes(item.keyDownCmd, data, 0, 14 + 16, 6);

		data[14 + 22] = (byte) item.rev2;
		data[14 + 23] = 0;

		CommonUtils.copyBytes(item.keyUpCmd, data, 0, 14 + 24, 6);

		data[14 + 30] = (byte) item.rev3;
		data[14 + 31] = 0;

		return data;
	}

	private byte[] createChnOpItemInfo(byte[] devUnitID, WareDev dev,
			List<WareChnOpItem> items) {
		// TODO Auto-generated method stub
		int item_num = items.size();
		int item_size = 32;

		byte[] data = new byte[12 + 1 + 1 + item_num * item_size];

		CommonUtils.copyBytes(dev.canCpuId, data, 0, 0, 12);

		data[12 + 0] = dev.devType;
		data[12 + 1] = dev.devId;

		for (int i = 0; i < item_num; i++) {
			CommonUtils.copyBytes(items.get(i).devUnitID, data, 0, i
					* item_size + 14, 12);
			data[i * item_size + 14 + 12] = items.get(i).keyDownValid;
			data[i * item_size + 14 + 13] = items.get(i).keyUpValid;
			data[i * item_size + 14 + 14] = (byte) items.get(i).rev1;
			data[i * item_size + 14 + 15] = 0;

			CommonUtils.copyBytes(items.get(i).keyDownCmd, data, 0, i
					* item_size + 14 + 16, 6);

			data[i * item_size + 14 + 22] = (byte) items.get(i).rev2;
			data[i * item_size + 14 + 23] = 0;

			CommonUtils.copyBytes(items.get(i).keyUpCmd, data, 0, i * item_size
					+ 14 + 24, 6);

			data[i * item_size + 14 + 30] = (byte) items.get(i).rev3;
			data[i * item_size + 14 + 31] = 0;
		}

		return data;
	}
}
