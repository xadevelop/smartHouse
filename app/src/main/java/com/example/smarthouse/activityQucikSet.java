package com.example.smarthouse;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class activityQucikSet extends AdAbstractActivity {

	private Button inSet, outSet, inDel, outDel;
	private LinearLayout registerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		registerLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_quick_set, null);
		mRootView.addView(registerLayout, FF);

		setTextForTitle("快速配置");
		setTextForBackBtn(getString(R.string.app_back));

		inSet = (Button) findViewById(R.id.inSetmod);
		outSet = (Button) findViewById(R.id.outSetmod);
		inDel = (Button) findViewById(R.id.inDelmod);
		outDel = (Button) findViewById(R.id.outDelmod);

		inSet.setOnClickListener(this);
		outSet.setOnClickListener(this);
		inDel.setOnClickListener(this);
		outDel.setOnClickListener(this);

		inSet.setOnTouchListener(TouchDark);
		outSet.setOnTouchListener(TouchDark);
		inDel.setOnTouchListener(TouchDark);
		outDel.setOnTouchListener(TouchDark);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.inSetmod:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_quick_setDevKey.getValue(), 0, 1,
					CommonUtils.getZerobyte(), 0));
			CommonUtils.sendMsg();
			break;
		case R.id.outSetmod:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_quick_setDevKey.getValue(), 0, 0,
					CommonUtils.getZerobyte(), 0));

			CommonUtils.sendMsg();
			break;
		case R.id.inDelmod:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_quick_delDevKey.getValue(), 0, 1,
					CommonUtils.getZerobyte(), 0));

			CommonUtils.sendMsg();
			break;
		case R.id.outDelmod:
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_quick_delDevKey.getValue(), 0, 0,
					CommonUtils.getZerobyte(), 0));

			CommonUtils.sendMsg();
			break;
		case R.id.title_btn4:
			this.finish();
			break;
		default:
			break;
		}

	}
}
