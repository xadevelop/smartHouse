package com.example.smarthouse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareLight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class lightControlView extends AdAbstractActivity {

	private LinearLayout rootLayout;
	private Button open, close;
	private WareLight wareLight;
	byte[] devBuff;
	byte[] senddata;
	RcuInfo rcuInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.light_control_view, null);
		mRootView.addView(rootLayout, FF);

		open = (Button) findViewById(R.id.lightOpenBtn);
		close = (Button) findViewById(R.id.lightCloseBtn);

		open.setOnClickListener(this);
		close.setOnClickListener(this);

		open.setOnTouchListener(TouchDark);
		close.setOnTouchListener(TouchDark);

		Intent intent = this.getIntent();
		wareLight = (WareLight) intent.getSerializableExtra("light");

		byte[] state = new byte[4];
		state[0] = wareLight.bOnOff;
		state[1] = wareLight.bTuneEn;
		state[2] = wareLight.lmVal;
		state[3] = wareLight.powChn;

		devBuff = CommonUtils.createWareDevInfo(wareLight.dev.getCanCpuId(),
				wareLight.dev.getDevName(), wareLight.dev.getRoomName(),
				wareLight.dev.getType(), wareLight.dev.getDevCtrlType(),
				wareLight.dev.getDevId(), state);

		initComponent();
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.e_ware_light));
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.lightOpenBtn:

			senddata = CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LGT_CMD.e_lgt_offOn.getValue(), devBuff,
					devBuff.length);

			break;
		case R.id.lightCloseBtn:

			senddata = CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LGT_CMD.e_lgt_onOff.getValue(), devBuff,
					devBuff.length);

			break;
		default:
			break;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				DatagramPacket packet;
				try {
					packet = new DatagramPacket(senddata, senddata.length,
							InetAddress.getByName(GlobalVars.getDstip()), 8300);

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
}
