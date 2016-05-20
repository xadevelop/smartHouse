package com.example.smarthouse;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.adapter.scenesetListViewAdapter;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareSceneEvent;
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

public class createSceneEventActivity extends AdAbstractActivity implements
		OnItemClickListener {

	private static final String TAG = "createSceneEventActivity";

	private LinearLayout createLayout;
	private Button newModel;
	private ListView listview;
	private ProgressBar progress;
	private ArrayList<String> arrayList;
	private List<WareSceneEvent> mSceneEvents;
	private List<WareSceneEvent> mScenetemp;

	scenesetListViewAdapter adapter;

	private MyApplication myApp;

	private static final int MSG_REFRSH_INFO = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//getSceneInfo();
		
		initComponent();

		myApp = (MyApplication) getApplication();
		myApp.setHandler(mHandler);
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.scene_title));

		createLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.create_scene_event, null);
		mRootView.addView(createLayout, FF);

		newModel = (Button) findViewById(R.id.newModel);
		newModel.setOnClickListener(this);
		newModel.setOnTouchListener(TouchDark);

		//progress = (ProgressBar) findViewById(R.id.scene_progress);
		listview = (ListView) findViewById(R.id.lst_model);

		mScenetemp = new ArrayList<WareSceneEvent>();
		
		arrayList = new ArrayList<String>();
		getListData();
		adapter = new scenesetListViewAdapter(this, arrayList);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		adapter.notifyDataSetChanged();
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

	@SuppressWarnings("unchecked")
	private void getListData() {

		//progress.setVisibility(View.GONE);

		mSceneEvents = HomeActivity.mSceneEvents;
		mScenetemp.clear();

		Collections.sort(mSceneEvents, new Comparator() {
			public int compare(Object a, Object b) {
				int one = ((WareSceneEvent) a).eventld;
				int two = ((WareSceneEvent) b).eventld;
				return one - two;
			}
		});
		
		for (int i = 0; i < mSceneEvents.size(); i++) {
			mScenetemp.add(mSceneEvents.get(i));
		}

		LogUtils.LOGE(TAG, "情景模式个数" + mSceneEvents.size());

		arrayList.clear();

		for (int i = 0; i < mSceneEvents.size(); i++) {
			arrayList.add(CommonUtils.getGBstr(mSceneEvents.get(i).sceneName));
		}

		for (int i = 0; i < arrayList.size() - 1; i++) {
			for (int j = arrayList.size() - 1; j > i; j--) {
				if (arrayList.get(i).equals(arrayList.get(j))) {
					arrayList.remove(j);
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(this, createSceneEventActivity2.class);
		Bundle bundle = new Bundle();

		bundle.putByte("eventid", mScenetemp.get(position).eventld);
		bundle.putByteArray("sceneName", mScenetemp.get(position).sceneName);

		intent.putExtras(bundle);
		startActivity(intent);
	}

	// 回调方法，从第二个页面回来的时候会执行这个方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 根据上面发送过去的请求吗来区别
		switch (requestCode) {
		case 0:
			if (data == null) {
				return;
			}
			String modeName = data.getStringExtra("mode");
			arrayList.add(modeName);
			adapter.notifyDataSetChanged();

			WareSceneEvent item = new WareSceneEvent();
			item.eventld = (byte) (mScenetemp.size());
			try {
				item.sceneName = modeName.getBytes("GB2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mScenetemp.add(item);

			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.newModel:
			Intent intent = new Intent();
			intent.setClass(this, activityAddSceneModel.class);
			int requestCode = 0;
			startActivityForResult(intent, requestCode);
			break;
		default:
			break;
		}
	}

	public static void getSceneInfo() {

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
				GlobalVars.getDstip(), CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getSceneEvents.getValue(), 0,
				0, CommonUtils.getZerobyte(), 0));

		CommonUtils.sendMsg();
	}
}
