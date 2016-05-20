package com.example.smarthouse.fragment;

import java.io.UnsupportedEncodingException;
import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareFreshAir;
import com.pullmi.entity.WareLight;
import com.pullmi.entity.WareLock;
import com.pullmi.entity.WareSetBox;
import com.pullmi.entity.WareTv;
import com.pullmi.entity.WareValve;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class fragmentEditDev extends Fragment implements OnClickListener {

	private LinearLayout rootLayout;
	private EditText dev_name, room_name, dev_chns;
	private Button save, del;

	private WareCurtain wareCurtain;
	private WareLight wareLight;
	private WareAirCondDev wareAirCondDev;
	private WareFreshAir wareFreshAir;
	private WareValve wareValve;
	private WareLock wareLock;
	private WareTv wareTv;
	private WareSetBox wareSetBox;
	private String devTypeString = "";
	private int devTag = -1;

	byte[] devBuff;
	byte[] senddata;
	byte[] devNameBytes;
	byte[] roomNameBytes;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle data = getArguments();// 获得从activity中传递过来的值
		devTypeString = data.getString("dev");

		if (devTypeString.equals("light")) {
			wareLight = (WareLight) data.getSerializable("light");
			devTag = 3;
		} else if (devTypeString.equals("curtain")) {
			wareCurtain = (WareCurtain) data.getSerializable("curtain");
			devTag = 4;
		} else if (devTypeString.equals("aircond")) {
			wareAirCondDev = (WareAirCondDev) data.getSerializable("aircond");
			devTag = 0;
		}else if (devTypeString.equals("value")) {
			wareValve = (WareValve) data.getSerializable("value");
			devTag = 6;
		} else if (devTypeString.equals("tv")) {
			wareTv = (WareTv) data.getSerializable("tv");
			devTag = 1;
		} else if (devTypeString.equals("setBox")) {
			wareSetBox = (WareSetBox) data.getSerializable("setBox");
			devTag = 2;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.edit_dev_dialog, container, false);

		dev_name = (EditText) view.findViewById(R.id.dev_name);
		room_name = (EditText) view.findViewById(R.id.dev_room_name);
		dev_chns = (EditText) view.findViewById(R.id.dev_chns);

		save = (Button) view.findViewById(R.id.sava_dev_info);
		del = (Button) view.findViewById(R.id.delete_dev);

		save.setOnTouchListener(CommonUtils.TouchDark);
		del.setOnTouchListener(CommonUtils.TouchDark);

		save.setOnClickListener(this);
		del.setOnClickListener(this);

		devNameBytes = new byte[12];
		roomNameBytes = new byte[12];

		for (int i = 0; i < 12; i++) {
			devNameBytes[i] = 0;
			roomNameBytes[i] = 0;
		}

		switch (devTag) {
		case 0:
			dev_name.setText(CommonUtils.getGBstr(wareAirCondDev.dev.devName));
			room_name.setText(CommonUtils.getGBstr(wareAirCondDev.dev.roomName));
			dev_chns.setText(wareAirCondDev.powChn+"");
			break;
		case 3:
			dev_name.setText(CommonUtils.getGBstr(wareLight.dev.devName));
			room_name.setText(CommonUtils.getGBstr(wareLight.dev.roomName));
			dev_chns.setText(wareLight.powChn+"");
			break;
		case 4:
			dev_name.setText(CommonUtils.getGBstr(wareCurtain.dev.devName));
			room_name.setText(CommonUtils.getGBstr(wareCurtain.dev.roomName));
			dev_chns.setText(wareCurtain.powChn+"");
			break;
		case 6:
			dev_name.setText(CommonUtils.getGBstr(wareValve.dev.devName));
			room_name.setText(CommonUtils.getGBstr(wareValve.dev.roomName));
			dev_chns.setText(wareValve.powChnOpen+"");
			break;
		default:
			break;
		}
		return view;
	}

	private void editDevInfo() {

		if (dev_chns.getText().toString().equals("")) {
			Toast.makeText(getActivity(), "通道号为空，重新输入", 0).show();
			return;
		}
		int chn = Integer.parseInt(dev_chns.getText().toString());

		try {
			byte[] dev_namebytes = dev_name.getText().toString()
					.getBytes("GB2312");
			byte[] room_namebytes = room_name.getText().toString()
					.getBytes("GB2312");

			if (dev_namebytes.length > 12) {
				Toast.makeText(getActivity(), "设备名称太长，重新输入", 0).show();
				return;
			} else {
				for (int i = 0; i < dev_namebytes.length; i++) {
					devNameBytes[i] = dev_namebytes[i];
				}
			}

			if (room_namebytes.length > 12) {
				Toast.makeText(getActivity(), "房间名称太长，重新输入", 0).show();
				return;
			} else {
				for (int i = 0; i < room_namebytes.length; i++) {
					roomNameBytes[i] = room_namebytes[i];
				}
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		switch (devTag) {
		case 0:
			break;
		case 3:
			byte[] state = new byte[4];
			state[0] = wareLight.bOnOff;
			state[1] = wareLight.bTuneEn;
			state[2] = wareLight.lmVal;
			if (chn <= 12) {
				state[3] = (byte) chn;
			} else {
				state[3] = wareLight.powChn;
			}

			devBuff = CommonUtils.createWareDevInfo(
					wareLight.dev.getCanCpuId(), devNameBytes, roomNameBytes,
					wareLight.dev.getType(), wareLight.dev.getDevCtrlType(),
					wareLight.dev.getDevId(), state);

			senddata = CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
					CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_editDev.getValue(), 0,
					UdpProPkt.E_WARE_TYPE.e_ware_light.getValue(), devBuff,
					devBuff.length);
			CommonUtils.sendMsg();
			break;
		case 4:
			byte[] state1 = new byte[4];
			state1[0] = wareCurtain.bOnOff;
			state1[1] = wareCurtain.timRun;
			if (chn <= 12) {
				state1[2] = (byte) chn;
			} else {
				state1[2] = (byte) wareCurtain.powChn;
			}
			state1[3] = 0;

			devBuff = CommonUtils.createWareDevInfo(
					wareCurtain.dev.getCanCpuId(), devNameBytes, roomNameBytes,
					wareCurtain.dev.getType(), wareCurtain.dev.getDevCtrlType(),
					wareCurtain.dev.getDevId(), state1);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_editDev.getValue(), 0,
					UdpProPkt.E_WARE_TYPE.e_ware_curtain.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case 6:
			byte[] state2 = new byte[4];
			state2[0] = wareValve.bOnOff;
			state2[1] = wareValve.timRun;
			if (chn <= 12) {
				state2[2] = (byte) chn;
			} else {
				state2[2] = (byte) wareValve.powChnOpen;
			}
			state2[3] = 0;

			devBuff = CommonUtils.createWareDevInfo(
					wareValve.dev.getCanCpuId(), devNameBytes, roomNameBytes,
					wareValve.dev.getType(), wareValve.dev.getDevCtrlType(),
					wareValve.dev.getDevId(), state2);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_editDev.getValue(), 0,
					UdpProPkt.E_WARE_TYPE.e_ware_valve.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}

	}

	private void delDevInfo() {
		switch (devTag) {
		case 0:
			break;
		case 3:
			byte[] state = new byte[4];
			state[0] = wareLight.bOnOff;
			state[1] = wareLight.bTuneEn;
			state[2] = wareLight.lmVal;
			state[3] = wareLight.powChn;

			devBuff = CommonUtils.createWareDevInfo(
					wareLight.dev.getCanCpuId(), devNameBytes, roomNameBytes,
					wareLight.dev.getType(), wareLight.dev.getDevCtrlType(),
					wareLight.dev.getDevId(), state);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delDev.getValue(), 0,
					UdpProPkt.E_WARE_TYPE.e_ware_light.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case 4:
			byte[] state1 = new byte[4];
			state1[0] = wareCurtain.bOnOff;
			state1[1] = wareCurtain.timRun;
			state1[2] = (byte) wareCurtain.powChn;
			state1[3] = 0;

			devBuff = CommonUtils.createWareDevInfo(
					wareCurtain.dev.getCanCpuId(), devNameBytes, roomNameBytes,
					wareCurtain.dev.getType(), wareCurtain.dev.getDevCtrlType(),
					wareCurtain.dev.getDevId(), state1);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delDev.getValue(), 0,
					UdpProPkt.E_WARE_TYPE.e_ware_curtain.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		case 6:
			byte[] state2 = new byte[4];
			state2[0] = wareValve.bOnOff;
			state2[1] = wareValve.timRun;
			state2[2] = (byte) wareValve.powChnOpen;
			state2[3] = 0;

			devBuff = CommonUtils.createWareDevInfo(
					wareValve.dev.getCanCpuId(), devNameBytes, roomNameBytes,
					wareValve.dev.getType(), wareValve.dev.getDevCtrlType(),
					wareValve.dev.getDevId(), state2);

			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delDev.getValue(), 0,
					UdpProPkt.E_WARE_TYPE.e_ware_valve.getValue(), devBuff,
					devBuff.length));
			CommonUtils.sendMsg();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sava_dev_info:
			editDevInfo();
			getActivity().finish();
			break;
		case R.id.delete_dev:
			delDevInfo();
			getActivity().finish();
			break;
		default:
			break;
		}
	}
}
