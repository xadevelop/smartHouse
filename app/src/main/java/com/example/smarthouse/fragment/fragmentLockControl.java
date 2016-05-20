package com.example.smarthouse.fragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareLock;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class fragmentLockControl extends Fragment implements OnClickListener {

	private LinearLayout rootLayout;
	private Button openLock,stopLock,closeLock;
	private WareLock wareLock;

	byte[] devBuff;
	byte[] senddata;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle data = getArguments();
		wareLock = (WareLock) data.getSerializable("lock");

		byte[] state = new byte[12];
		state[0] = wareLock.bOnOff;
		state[1] = wareLock.timRun;
		state[2] = wareLock.bLockOut;
		state[3] = wareLock.powChnOpen;

		CommonUtils.copyBytes(wareLock.pwd, state, 0, 4, 8);

		devBuff = CommonUtils.createWareDevInfo(wareLock.dev.getCanCpuId(),
				wareLock.dev.getDevName(), wareLock.dev.getRoomName(),
				wareLock.dev.getType(), wareLock.dev.getDevCtrlType(),
				wareLock.dev.getDevId(), state);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		view = inflater
				.inflate(R.layout.lock_control_view2, container, false);
		

		openLock = (Button) view.findViewById(R.id.lockOpenBtn);
		stopLock = (Button)view.findViewById(R.id.lockStopBtn);
		closeLock = (Button)view.findViewById(R.id.lockCloseBtn);
		
		openLock.setOnClickListener(this);
		stopLock.setOnClickListener(this);
		closeLock.setOnClickListener(this);
		
		openLock.setOnTouchListener(CommonUtils.TouchDark);
		stopLock.setOnTouchListener(CommonUtils.TouchDark);
		closeLock.setOnTouchListener(CommonUtils.TouchDark);
		
		return view;
	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lockOpenBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LOCK_CMD.e_lock_open.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.lockStopBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LOCK_CMD.e_lock_stop.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.lockCloseBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_LOCK_CMD.e_lock_close.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}
}
