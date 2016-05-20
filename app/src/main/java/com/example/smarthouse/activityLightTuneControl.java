package com.example.smarthouse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.smarthouse.fragment.fragmentCurtainControl;
import com.example.smarthouse.fragment.fragmentLightTuneControl;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareLight;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class activityLightTuneControl extends AdAbstractActivity {

	private LinearLayout rootLayout;
	private FragmentManager fm;
	private FragmentTransaction transaction;
	private Fragment menuFragment;
	private WareLight wareLight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_control_view, null);
		mRootView.addView(rootLayout, FF);

		Intent intent = this.getIntent();
		wareLight = (WareLight) intent.getSerializableExtra("light");

		fm = getFragmentManager();
		transaction = fm.beginTransaction();
		menuFragment = new fragmentLightTuneControl();
		Bundle data = new Bundle();
		data.putSerializable("light", wareLight);
		menuFragment.setArguments(data);// 通过Bundle向Activity中传递值

		transaction.add(R.id.devControl, menuFragment);
		transaction.commit();

	}

	private void initComponent() {

		setTextForTitle(getString(R.string.e_ware_light));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
