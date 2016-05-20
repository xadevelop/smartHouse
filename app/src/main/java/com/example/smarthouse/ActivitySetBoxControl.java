package com.example.smarthouse;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareSetBox;
import com.pullmi.entity.WareTv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivitySetBoxControl extends Activity implements OnClickListener {

	public Button power, volup, voldown, chup, chdown, tvpower, mute, exit, menu, ok, up, down,
			left, right;
	private WareSetBox wareSetBox;
	private byte[] devBuff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == 1) {
			setContentView(R.layout.activity_settopbox_channel);
		} else {
			setContentView(R.layout.activity_settopbox_channel_hor);
		}

		initView();

		Intent intent = this.getIntent();
		wareSetBox = (WareSetBox) intent.getSerializableExtra("setBox");

		devBuff = CommonUtils.createWareDevInfo(wareSetBox.dev.getCanCpuId(),
				wareSetBox.dev.getDevName(), wareSetBox.dev.getRoomName(),
				wareSetBox.dev.getType(), wareSetBox.dev.getDevCtrlType(),
				wareSetBox.dev.getDevId(), CommonUtils.getZerobyte());
	}

	public void initView() {
		power = (Button) findViewById(R.id.remote_power);
		voldown = (Button) findViewById(R.id.remote_voldown);
		volup = (Button) findViewById(R.id.remote_volup);
		chup = (Button) findViewById(R.id.remote_chup);
		chdown = (Button) findViewById(R.id.remote_chdown);
		tvpower = (Button)findViewById(R.id.remote_tv_power);
		mute = (Button)findViewById(R.id.remote_mute);
		exit =  (Button)findViewById(R.id.remote_exit);
		menu =  (Button)findViewById(R.id.remote_menu);
		ok = (Button)findViewById(R.id.remote_ok);
		up = (Button)findViewById(R.id.remote_up);
		down = (Button)findViewById(R.id.remote_down);
		left = (Button)findViewById(R.id.remote_left);
		right = (Button)findViewById(R.id.remote_right);
		

		power.setOnClickListener(this);
		voldown.setOnClickListener(this);
		volup.setOnClickListener(this);
		chup.setOnClickListener(this);
		chdown.setOnClickListener(this);
		tvpower.setOnClickListener(this);
		mute.setOnClickListener(this);
		exit.setOnClickListener(this);
		menu.setOnClickListener(this);
		ok.setOnClickListener(this);
		up.setOnClickListener(this);
		down.setOnClickListener(this);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.remote_power:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_offOn.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_volup:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numVInc.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_voldown:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numVDec.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_chup:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numPInc.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_chdown:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numPDec.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_tv_power:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TV_CMD.e_tv_offOn.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_menu:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numDemand.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_mute:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_mute.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_ok:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_enter.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_up:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numUp.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_down:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numDn.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_left:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numInfo.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_right:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
					0, UdpProPkt.E_TVUP_CMD.e_tvUP_numRt.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}
}
