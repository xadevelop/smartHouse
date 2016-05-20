package com.example.smarthouse.fragment;

import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareCurtain;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class fragmentCurtainControl extends Fragment implements
		OnClickListener {

	private View view;
	private Button open, stop, close;
	private WareCurtain wareCurtain;
	byte[] devBuff;
	byte[] senddata;

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
				.inflate(R.layout.curtain_control_view, container, false);

		 Bundle data = getArguments();//获得从activity中传递过来的值
		 wareCurtain = (WareCurtain) data.getSerializable("curtain");
	        
		open = (Button) view.findViewById(R.id.curtainOpenBtn);
		stop = (Button) view.findViewById(R.id.curtainStopBtn);
		close = (Button) view.findViewById(R.id.curtainCloseBtn);

		open.setOnClickListener(this);
		stop.setOnClickListener(this);
		close.setOnClickListener(this);

		open.setOnTouchListener(CommonUtils.TouchDark);
		stop.setOnTouchListener(CommonUtils.TouchDark);
		close.setOnTouchListener(CommonUtils.TouchDark);	 

		byte[] state = new byte[4];
		state[0] = wareCurtain.bOnOff;
		state[1] = wareCurtain.timRun;
		state[2] = (byte) wareCurtain.powChn;
		state[3] = 0;

		devBuff = CommonUtils.createWareDevInfo(wareCurtain.dev.getCanCpuId(),
				wareCurtain.dev.getDevName(), wareCurtain.dev.getRoomName(),
				wareCurtain.dev.getType(), wareCurtain.dev.getDevCtrlType(),
				wareCurtain.dev.getDevId(), state);

		return view;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.curtainOpenBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_CURT_CMD.e_curt_offOn.getValue(), devBuff,
					devBuff.length));

			break;
		case R.id.curtainStopBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_CURT_CMD.e_curt_stop.getValue(), devBuff,
					devBuff.length));

			break;
		case R.id.curtainCloseBtn:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_CURT_CMD.e_curt_offOff.getValue(), devBuff,
					devBuff.length));

			break;
		default:
			break;
		}

		CommonUtils.sendMsg();
	}
}
