package com.example.smarthouse.fragment;


import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareValve;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class fragmentValueControl extends Fragment implements OnClickListener {

	private Button open, close,stop;
	private WareValve wareValve;
	byte[] devBuff;
	byte[] senddata;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle data = getArguments();
		wareValve = (WareValve) data.getSerializable("value");

		byte[] state = new byte[4];
		state[0] = wareValve.bOnOff;
		state[1] = wareValve.timRun;
		state[2] = wareValve.powChnOpen;
		state[3] = wareValve.powChnClose;

		devBuff = CommonUtils.createWareDevInfo(wareValve.dev.getCanCpuId(),
				wareValve.dev.getDevName(), wareValve.dev.getRoomName(),
				wareValve.dev.getType(), wareValve.dev.getDevCtrlType(),
				wareValve.dev.getDevId(), state);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.value_control_view, container, false);

		open = (Button) view.findViewById(R.id.openValue);
		close = (Button) view.findViewById(R.id.closeValue);
		stop = (Button)view.findViewById(R.id.stopValue);

		open.setOnClickListener(this);
		close.setOnClickListener(this);
		stop.setOnClickListener(this);
		
		open.setOnTouchListener(CommonUtils.TouchDark);
		close.setOnTouchListener(CommonUtils.TouchDark);
		stop.setOnTouchListener(CommonUtils.TouchDark);

		return view;
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
