package com.example.smarthouse.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.adapter.sceneListViewAdapter;
import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.MyApplication;
import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareSceneEvent;
import com.pullmi.utils.LogUtils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class sceneFragment extends BackHandledFragment {

	private static final String TAG = "sceneFragment";

	private GridView gridView;
	private ArrayList<String> arrayList;
	private ProgressBar progress;

	MyApplication myApp;
	private View view;
	private List<WareSceneEvent> mSceneEvents;

	sceneListViewAdapter adapter;

	private static final int MSG_REFRSH_INFO = 2104;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSceneInfo();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_scene_op, container, false);
		gridView = (GridView) view.findViewById(R.id.grid);
		progress = (ProgressBar) view.findViewById(R.id.scene_progress);

		arrayList = new ArrayList<String>();
		
		getListData();
		adapter = new sceneListViewAdapter(getActivity(), arrayList);
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		myApp = (MyApplication) getActivity().getApplication();
		myApp.setHandler(mHandler);

		if (mSceneEvents != null) {
			getListData();
			adapter.notifyDataSetChanged();
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRSH_INFO:
				getListData();
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getListData() {

		gridView.setVisibility(view.VISIBLE);
		progress.setVisibility(view.GONE);

		mSceneEvents = HomeActivity.mSceneEvents;

		arrayList.clear();

		Collections.sort(mSceneEvents, new Comparator() {
			public int compare(Object a, Object b) {
				int one = ((WareSceneEvent) a).eventld;
				int two = ((WareSceneEvent) b).eventld;
				return one - two;
			}
		});

		for (int i = 0; i < mSceneEvents.size(); i++) {
			//LogUtils.LOGE(TAG, "情景模式" + CommonUtils.getGBstr(mSceneEvents.get(i).sceneName));
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

	private long exitTime = 0;

	@Override
	public boolean onBackPressed() {

		if (System.currentTimeMillis() - exitTime > 2000) {
			Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 将系统当前的时间赋值给exitTime
			exitTime = System.currentTimeMillis();
		} else {
			HomeActivity parentActivity = (HomeActivity ) getActivity();
			parentActivity.saveObject("scene.dat",10);
			getActivity().finish();
		}

		return true;
	}

	public static void getSceneInfo() {
		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getSceneEvents.getValue(), 0, 0,
				CommonUtils.getZerobyte(), 0));

		CommonUtils.sendMsg();
	}
}
