package com.example.smarthouse;


import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareValve;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class valueControlView extends AdAbstractActivity {

	private LinearLayout rootLayout;
	private Button open, close,stop;
	private WareValve wareValve;
	byte[] devBuff;
	byte[] senddata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.value_control_view, null);
		mRootView.addView(rootLayout, FF);

		open = (Button) findViewById(R.id.openValue);
		close = (Button) findViewById(R.id.closeValue);
		stop = (Button)findViewById(R.id.stopValue);

		open.setOnClickListener(this);
		close.setOnClickListener(this);
		stop.setOnClickListener(this);
		
		open.setOnTouchListener(CommonUtils.TouchDark);
		close.setOnTouchListener(CommonUtils.TouchDark);
		stop.setOnTouchListener(CommonUtils.TouchDark);

		Intent intent = this.getIntent();
		wareValve = (WareValve) intent.getSerializableExtra("value");

		byte[] state = new byte[4];
		state[0] = wareValve.bOnOff;
		state[1] = wareValve.timRun;
		state[2] = wareValve.powChnOpen;
		state[3] = wareValve.powChnClose;

		devBuff = CommonUtils.createWareDevInfo(wareValve.dev.getCanCpuId(),
				wareValve.dev.getDevName(), wareValve.dev.getRoomName(),
				wareValve.dev.getType(), wareValve.dev.getDevCtrlType(),
				wareValve.dev.getDevId(), state);

		initComponent();
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.e_ware_value));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.openValue:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_VALVE_CMD.e_valve_offOn.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.stopValue:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_VALVE_CMD.e_valve_stop.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case R.id.closeValue:

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
					UdpProPkt.E_VALVE_CMD.e_valve_onOff.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}

}
