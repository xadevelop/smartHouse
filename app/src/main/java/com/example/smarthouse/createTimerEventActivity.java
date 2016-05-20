package com.example.smarthouse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.pullmi.common.CommonUtils;
import com.pullmi.common.DbOperator;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.UnitNode;

import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore.RightsStatus;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class createTimerEventActivity extends AdAbstractActivity {

	private List<UnitNode> nodes;
	private static final String TAG = "createTimerEventActivity";

	private LinearLayout rootLayout;
	private LinearLayout createLayout;

	private LinearLayout.LayoutParams WW = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);

	private Button createTimer;
	byte[] senddata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		createLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.create_time_event, null);
		mRootView.addView(createLayout, FF);

		rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
		createTimer = (Button) findViewById(R.id.creatTimerEvent);
		createTimer.setOnClickListener(this);

		searchTimerEvents();

		initComponent();
	}

	private void initComponent() {

		setTextForTitle("定时设置");
	}

	private void searchTimerEvents() {
		byte[] buffer = new byte[0];

		senddata = CommonUtils.preSendUdpProPkt(CommonUtils.getLocalIp(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getTimerEvents.getValue(), 0,
				0, buffer, buffer.length);

		new Thread(new Runnable() {
			@Override
			public void run() {
				DatagramPacket packet;
				try {
					packet = new DatagramPacket(senddata, senddata.length,
							InetAddress.getByName(CommonUtils.getLocalIp()),
							8300);

					DatagramSocket sockTx = new DatagramSocket();
					sockTx.send(packet);

					sockTx.close();
					sockTx = null;
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.creatTimerEvent:
			Intent intent = new Intent();
			intent.setClass(this, createTimerEventActivity2.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
