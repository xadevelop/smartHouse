package com.example.smarthouse;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.adapter.chnOutInListViewAdapter;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareBoardChnout;
import com.pullmi.entity.WareBoardKeyInput;
import com.pullmi.utils.LogUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class chnOutInActivity extends AdAbstractActivity implements OnItemClickListener {

	private static final String TAG = "chnOutInActivity";
	MyApplication myApp;
	private LinearLayout createLayout;
	private Button out, in;
	private ProgressBar progress;
	private ListView listview;
	private ArrayList<String> arrayList;
	chnOutInListViewAdapter adapter;

	private int currentFlag = 1;
	private List<WareBoardKeyInput> mBoardKeyInputs;
	private List<WareBoardChnout> mBoardChnouts;
	String dstip;

	private static final int MSG_REFRSH_INFO = 2101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initComponent();
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.module_set));

		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.set_chnout_keymap, null);
		mRootView.addView(createLayout, FF);

		out = (Button) findViewById(R.id.chnout_module);
		in = (Button) findViewById(R.id.chnin_module);
		out.setOnClickListener(this);
		in.setOnClickListener(this);

		out.setOnTouchListener(TouchDark);
		in.setOnTouchListener(TouchDark);

		progress = (ProgressBar) findViewById(R.id.chnProcessBar);
		listview = (ListView) findViewById(R.id.chnList);

		arrayList = new ArrayList<String>();
		adapter = new chnOutInListViewAdapter(this, arrayList);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		adapter.notifyDataSetChanged();

		myApp = (MyApplication) getApplication();
		myApp.setHandler(mHandler);

		getBroadKeyInfo();
		getBroadChnoutInfo();

	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRSH_INFO:
				if (currentFlag == 1) {
					getListData(1);
				}else {
					getListData(0);
				}

				break;
			default:
				break;
			}
		};
	};

	private void getListData(int flag) {

		//progress.setVisibility(View.GONE);
		//listview.setVisibility(View.VISIBLE);

		mBoardKeyInputs = HomeActivity.mBoardKeyInputs;
		mBoardChnouts = HomeActivity.mBoardChnouts;
		LogUtils.LOGE(TAG, "chnouts:"+mBoardChnouts.size() + "  inputs:"+mBoardKeyInputs.size());

		if (flag == 0) {
			// 输出模块
			arrayList.clear();
			for (int i = 0; i < mBoardChnouts.size(); i++) {
				arrayList.add(CommonUtils.getGBstr(mBoardChnouts.get(i).boardName));
			}
		} else if (flag == 1) {
			// 输入模块
			arrayList.clear();
			for (int i = 0; i < mBoardKeyInputs.size(); i++) {
				arrayList.add(CommonUtils.getGBstr(mBoardKeyInputs.get(i).boardName));
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chnin_module:
			currentFlag = 1;
			getListData(1);
			break;
		case R.id.chnout_module:
			currentFlag = 0;
			getListData(0);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

		Intent intent = new Intent();
		intent.setClass(this, KeyInputSetActivity.class);
		Bundle bundle = new Bundle();
		TextView text = (TextView) view.findViewById(R.id.tv_board_name);
		String boardName = text.getText().toString();
		bundle.putInt("boardType", currentFlag);
		bundle.putString("boardName", boardName);
		if (currentFlag == 0) {
			bundle.putByteArray("devUnitID", mBoardChnouts.get(position).devUnitID);
		} else {
			bundle.putByteArray("devUnitID", mBoardKeyInputs.get(position).devUnitID);
		}

		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void getBroadKeyInfo() {

		// TODO Auto-generated method stub
		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getBoards.getValue(), 1,
				0, CommonUtils.getZerobyte(), 0));

		CommonUtils.sendMsg();
	}

	public void getBroadChnoutInfo() {

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getBoards.getValue(), 0,
				0, CommonUtils.getZerobyte(), 0));

		CommonUtils.sendMsg();

	}
	
	public void saveObject(String name, int flag) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = this.openFileOutput(name, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			if (flag == 11) {
				oos.writeObject(mBoardChnouts);
			} else if (flag == 12) {
				oos.writeObject(mBoardKeyInputs);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			// 这里是保存文件产生异常
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// fos流关闭异常
					e.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					// oos流关闭异常
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		saveObject("chnouts.dat", 11);
		saveObject("inputs.dat", 12);
	}
}
