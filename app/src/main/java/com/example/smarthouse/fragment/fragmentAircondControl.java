package com.example.smarthouse.fragment;

import java.util.List;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.MyApplication;
import com.example.smarthouse.R;
import com.example.smarthouse.homeActivityConfirm;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareAirCondDev;

import android.R.array;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class fragmentAircondControl extends Fragment implements OnClickListener {

	private View view;
	private Button open, close;
	private RadioGroup tmModel;
	private TextView tm, tmUp, tmDown;
	private RadioButton autoMode, coolMode, heatMode;
	private WareAirCondDev wareAirCondDev;
	byte[] devBuff;
	byte[] senddata;
	RcuInfo rcuInfo;
	String dstip;
	private int modelValue = 0, curValue = 0, cmdValue = 0;
	private List<WareAirCondDev> airs;
	MyApplication myApp;
	private static final int MSG_REFRSH_INFO = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle data = getArguments();// 获得从activity中传递过来的值
		wareAirCondDev = (WareAirCondDev) data.getSerializable("aircond");

		byte[] state = new byte[8];
		state[0] = wareAirCondDev.bOnOff;
		state[1] = wareAirCondDev.selMode;
		state[2] = wareAirCondDev.selTemp;
		state[3] = wareAirCondDev.selSpd;
		state[4] = wareAirCondDev.selDirect;
		state[5] = wareAirCondDev.rev1;
		state[6] = (byte) wareAirCondDev.powChn;
		state[7] = 0;

		devBuff = CommonUtils.createWareDevInfo(wareAirCondDev.dev.getCanCpuId(),
				wareAirCondDev.dev.getDevName(), wareAirCondDev.dev.getRoomName(),
				wareAirCondDev.dev.getType(), wareAirCondDev.dev.getDevCtrlType(),
				wareAirCondDev.dev.getDevId(), state);

		myApp = (MyApplication) getActivity().getApplication();
		myApp.setHandler(mHandler);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.aircond_control_view, container, false);

		initComponent();

		return view;
	}

	private void initComponent() {

		open = (Button) view.findViewById(R.id.aircondOpenBtn);
		close = (Button) view.findViewById(R.id.aircondCloseBtn);
		tm = (TextView) view.findViewById(R.id.wendu);
		tmUp = (TextView) view.findViewById(R.id.wendu_up);
		tmDown = (TextView) view.findViewById(R.id.wendu_down);
		autoMode = (RadioButton) view.findViewById(R.id.wendu_auto);
		coolMode = (RadioButton) view.findViewById(R.id.wendu_cool);
		heatMode = (RadioButton) view.findViewById(R.id.wendu_heat);
		tmModel = (RadioGroup) view.findViewById(R.id.wendu_model);

		upData();

		open.setOnTouchListener(CommonUtils.TouchDark);
		close.setOnTouchListener(CommonUtils.TouchDark);
		tmUp.setOnTouchListener(CommonUtils.TouchDark);
		tmDown.setOnTouchListener(CommonUtils.TouchDark);
		autoMode.setOnTouchListener(CommonUtils.TouchDark);
		coolMode.setOnTouchListener(CommonUtils.TouchDark);
		heatMode.setOnTouchListener(CommonUtils.TouchDark);
		tmModel.setOnTouchListener(CommonUtils.TouchDark);

		open.setOnClickListener(this);
		close.setOnClickListener(this);
		tmUp.setOnClickListener(this);
		tmDown.setOnClickListener(this);
		autoMode.setOnClickListener(this);
		coolMode.setOnClickListener(this);
		heatMode.setOnClickListener(this);

		tmModel.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (wareAirCondDev.bOnOff == 0) {
					Toast.makeText(getActivity(), "请先开机，再操作", 0).show();
					return;
				}
				if (checkedId == autoMode.getId()) {
					modelValue = UdpProPkt.E_AIR_MODE.e_air_auto.getValue();
				} else if (checkedId == coolMode.getId()) {
					modelValue = UdpProPkt.E_AIR_MODE.e_air_cool.getValue();
				} else if (checkedId == heatMode.getId()) {
					modelValue = UdpProPkt.E_AIR_MODE.e_air_hot.getValue();
				}
			}
		});
	}

	private void upData() {
		tm.setText(wareAirCondDev.selTemp + "");
		tm.setTextColor(Color.RED);
		curValue = Integer.parseInt(tm.getText().toString());

		if (wareAirCondDev.bOnOff == 1) {
			open.setTextColor(Color.RED);
			close.setTextColor(Color.WHITE);
		} else {
			close.setTextColor(Color.RED);
			open.setTextColor(Color.WHITE);
		}

		switch (wareAirCondDev.selMode) {
		case 0:
			autoMode.setChecked(true);
			break;
		case 1:
			heatMode.setChecked(true);
			break;
		case 2:
			coolMode.setChecked(true);
			break;
		default:
			break;
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRSH_INFO:
				airs = HomeActivity.mAircondDataset;
				for (int i = 0; i < airs.size(); i++) {
					if (airs.get(i).dev.devId == wareAirCondDev.dev.devId
							&& airs.get(i).dev.devType == wareAirCondDev.dev.devType) {
						wareAirCondDev = airs.get(i);
						upData();
					}
				}
			}
		}
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.aircondOpenBtn:
			cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue();
			break;
		case R.id.aircondCloseBtn:
			cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOff.getValue();
			break;
		case R.id.wendu_up:
			if (wareAirCondDev.bOnOff == 0) {
				Toast.makeText(getActivity(), "请先开机，再操作", 0).show();
				break;
			}
			curValue++;
			if (curValue > 30) {
				curValue = 30;
			}
			tm.setText(curValue + "");
			switch (curValue) {
			case 14:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp14.getValue();
				break;
			case 15:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp15.getValue();
				break;
			case 16:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp16.getValue();
				break;
			case 17:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp17.getValue();
				break;
			case 18:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp18.getValue();
				break;
			case 19:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp19.getValue();
				break;
			case 20:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp20.getValue();
				break;
			case 21:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp21.getValue();
				break;
			case 22:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp22.getValue();
				break;
			case 23:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp23.getValue();
				break;
			case 24:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp24.getValue();
				break;
			case 25:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp25.getValue();
				break;
			case 26:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp26.getValue();
				break;
			case 27:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp27.getValue();
				break;
			case 28:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp28.getValue();
				break;
			case 29:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp29.getValue();
				break;
			case 30:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp30.getValue();
				break;
			default:
				break;
			}
			break;
		case R.id.wendu_down:
			if (wareAirCondDev.bOnOff == 0) {
				Toast.makeText(getActivity(), "请先开机，再操作", 0).show();
				break;
			}
			curValue--;
			if (curValue < 14) {
				curValue = 14;
			}
			tm.setText(curValue + "");
			switch (curValue) {
			case 14:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp14.getValue();
				break;
			case 15:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp15.getValue();
				break;
			case 16:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp16.getValue();
				break;
			case 17:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp17.getValue();
				break;
			case 18:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp18.getValue();
				break;
			case 19:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp19.getValue();
				break;
			case 20:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp20.getValue();
				break;
			case 21:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp21.getValue();
				break;
			case 22:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp22.getValue();
				break;
			case 23:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp23.getValue();
				break;
			case 24:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp24.getValue();
				break;
			case 25:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp25.getValue();
				break;
			case 26:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp26.getValue();
				break;
			case 27:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp27.getValue();
				break;
			case 28:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp28.getValue();
				break;
			case 29:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp29.getValue();
				break;
			case 30:
				cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp30.getValue();
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

		int value = (modelValue << 5) | cmdValue;
		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(), UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0,
				value, devBuff, devBuff.length));

		CommonUtils.sendMsg();
	}
}
