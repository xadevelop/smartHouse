package com.example.smarthouse.fragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareLight;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class fragmentLightTuneControl extends Fragment implements
		OnClickListener {

	private View view;
	private Button open, close, bright, dark;
	private WareLight wareLight;
	byte[] devBuff;
	byte[] senddata;
	RcuInfo rcuInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater
				.inflate(R.layout.light_tune_control_view, container, false);
		open = (Button) view.findViewById(R.id.lightOpenBtn);
		close = (Button) view.findViewById(R.id.lightCloseBtn);
		bright = (Button) view.findViewById(R.id.bright);
		dark = (Button) view.findViewById(R.id.dark);

		open.setOnClickListener(this);
		close.setOnClickListener(this);
		bright.setOnClickListener(this);
		dark.setOnClickListener(this);

		open.setOnTouchListener(CommonUtils.TouchDark);
		close.setOnTouchListener(CommonUtils.TouchDark);
		bright.setOnTouchListener(CommonUtils.TouchDark);
		dark.setOnTouchListener(CommonUtils.TouchDark);

		Bundle data = getArguments();// 获得从activity中传递过来的值
		wareLight = (WareLight) data.getSerializable("light");

		byte[] state = new byte[4];
		state[0] = wareLight.bOnOff;
		state[1] = wareLight.bTuneEn;
		state[2] = wareLight.lmVal;
		state[3] = wareLight.powChn;

		devBuff = CommonUtils.createWareDevInfo(wareLight.dev.getCanCpuId(),
				wareLight.dev.getDevName(), wareLight.dev.getRoomName(),
				wareLight.dev.getType(), wareLight.dev.getDevCtrlType(),
				wareLight.dev.getDevId(), state);

		return view;
	}

	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.lightOpenBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LGT_CMD.e_lgt_offOn.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.lightCloseBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LGT_CMD.e_lgt_onOff.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.bright:
			byte[] state = new byte[4];
			state[0] = wareLight.bOnOff;
			state[1] = wareLight.bTuneEn;
			state[2] = 80;
			state[3] = wareLight.powChn;

			devBuff = CommonUtils.createWareDevInfo(
					wareLight.dev.getCanCpuId(), wareLight.dev.getDevName(),
					wareLight.dev.getRoomName(), wareLight.dev.getType(),
					wareLight.dev.getDevCtrlType(), wareLight.dev.getDevId(),
					state);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LGT_CMD.e_lgt_bright.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.dark:
			byte[] state1 = new byte[4];
			state1[0] = wareLight.bOnOff;
			state1[1] = wareLight.bTuneEn;
			state1[2] = 10;
			state1[3] = wareLight.powChn;

			devBuff = CommonUtils.createWareDevInfo(
					wareLight.dev.getCanCpuId(), wareLight.dev.getDevName(),
					wareLight.dev.getRoomName(), wareLight.dev.getType(),
					wareLight.dev.getDevCtrlType(), wareLight.dev.getDevId(),
					state1);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LGT_CMD.e_lgt_dark.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}
}
