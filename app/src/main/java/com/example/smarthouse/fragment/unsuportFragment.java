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

public class unsuportFragment extends BackHandledFragment {

	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_un_error, container, false);
		
		return view;
	}

	private long exitTime = 0;

	@Override
	public boolean onBackPressed() {

		if (System.currentTimeMillis() - exitTime > 2000) {
			Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 将系统当前的时间赋值给exitTime
			exitTime = System.currentTimeMillis();
		} else {
			HomeActivity parentActivity = (HomeActivity) getActivity();
			parentActivity.saveObject("scene.dat", 1);
			getActivity().finish();
		}

		return true;
	}
}
