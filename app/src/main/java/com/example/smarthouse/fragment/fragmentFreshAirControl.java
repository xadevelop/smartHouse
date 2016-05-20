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
import com.pullmi.entity.WareFreshAir;
import com.pullmi.entity.WareLight;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class fragmentFreshAirControl extends Fragment implements
		OnClickListener {

	private Button open, close, big, normal, small;
	private WareFreshAir freshair;
	private View view;
	byte[] devBuff;
	byte[] senddata;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle data = getArguments();// 获得从activity中传递过来的值
		freshair = (WareFreshAir) data.getSerializable("freshair");

		byte[] state = new byte[4];
		state[0] = freshair.bOnOff;
		state[1] = freshair.spdSel;
		state[2] = (byte) freshair.powChn;
		state[3] = 0;

		devBuff = CommonUtils.createWareDevInfo(freshair.dev.getCanCpuId(),
				freshair.dev.getDevName(), freshair.dev.getRoomName(),
				freshair.dev.getType(), freshair.dev.getDevCtrlType(),
				freshair.dev.getDevId(), state);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.freshair_control_view, container,
				false);
		open = (Button) view.findViewById(R.id.freshairOpenBtn);
		close = (Button) view.findViewById(R.id.freshCloseBtn);
		big = (Button) view.findViewById(R.id.freshairBig);
		normal = (Button) view.findViewById(R.id.freshairNormal);
		small = (Button) view.findViewById(R.id.freshairSmall);

		open.setOnClickListener(this);
		close.setOnClickListener(this);
		big.setOnClickListener(this);
		normal.setOnClickListener(this);
		small.setOnClickListener(this);

		open.setOnTouchListener(CommonUtils.TouchDark);
		close.setOnTouchListener(CommonUtils.TouchDark);
		big.setOnTouchListener(CommonUtils.TouchDark);
		normal.setOnTouchListener(CommonUtils.TouchDark);
		small.setOnTouchListener(CommonUtils.TouchDark);

		return view;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.freshairOpenBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_FRESHAIR_CMD.e_freshair_open.getValue(),
					devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.freshCloseBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_FRESHAIR_CMD.e_freshair_close.getValue(),
					devBuff, devBuff.length));
			CommonUtils.sendMsg();
		case R.id.freshairBig:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_FRESHAIR_CMD.e_freshair_spd_high.getValue(),
					devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.freshairSmall:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_FRESHAIR_CMD.e_freshair_spd_low.getValue(),
					devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.freshairNormal:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_FRESHAIR_CMD.e_freshair_spd_mid.getValue(),
					devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}
}
