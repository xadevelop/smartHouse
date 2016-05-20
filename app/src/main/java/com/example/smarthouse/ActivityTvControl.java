package com.example.smarthouse;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.entity.WareLight;
import com.pullmi.entity.WareTv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityTvControl extends Activity implements OnClickListener {

	public Button power, volup, voldown, chup, chdown,menu,mute,fav,exit,info;
	private WareTv wareTv;
	private byte[] devBuff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tv_channel);
		initView();
		
		Intent intent = this.getIntent();
		wareTv = (WareTv) intent.getSerializableExtra("tv");

		devBuff = CommonUtils.createWareDevInfo(wareTv.dev.getCanCpuId(),
				wareTv.dev.getDevName(), wareTv.dev.getRoomName(),
				wareTv.dev.getType(), wareTv.dev.getDevCtrlType(),
				wareTv.dev.getDevId(), CommonUtils.getZerobyte());
	}

	public void initView() {
		power = (Button) findViewById(R.id.remote_power);
		voldown = (Button) findViewById(R.id.remote_voldown);
		volup = (Button) findViewById(R.id.remote_volup);
		chup = (Button) findViewById(R.id.remote_chup);
		chdown = (Button) findViewById(R.id.remote_chdown);
		menu = (Button)findViewById(R.id.remote_menu);
		mute = (Button)findViewById(R.id.remote_mute);
		fav = (Button)findViewById(R.id.remote_fav);
		exit = (Button)findViewById(R.id.remote_exit);
		info = (Button)findViewById(R.id.remote_info);

		power.setOnClickListener(this);
		voldown.setOnClickListener(this);
		volup.setOnClickListener(this);
		chup.setOnClickListener(this);
		chdown.setOnClickListener(this);
		menu.setOnClickListener(this);
		mute.setOnClickListener(this);
		fav.setOnClickListener(this);
		exit.setOnClickListener(this);
		info.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.remote_power:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_offOn.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_volup:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_numRt.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_voldown:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_numLf.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_chup:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_numUp.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_chdown:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_numDn.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_menu:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_numMenu.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_mute:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_mute.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_info:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_userDef1.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_fav:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_userDef2.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.remote_exit:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_TV_CMD.e_tv_userDef3.getValue(), devBuff, devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}
}
