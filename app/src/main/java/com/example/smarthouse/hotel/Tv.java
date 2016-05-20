package com.example.smarthouse.hotel;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.smarthouse.ActivitySetBoxControl;
import com.example.smarthouse.ActivityTvControl;
import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareSetBox;
import com.pullmi.entity.WareTv;
import com.pullmi.utils.LogUtils;

/**
 * Created by maibenben on 2016/4/25.
 */
public class Tv extends Fragment implements OnClickListener {
	GridView mGridview;
	View view;

	private List<String> list_zi;
	private List<String> list_ying;
	private Button power, volup, vodown, chup, chdown, num1, num2, num3, num4,
			num5, num6, num7, num8, num9, numplus, num0, last_ch;
	private static byte[] devBuff;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.hotle_tv_channel_activity, null);

		inti();
		//event();

		return view;
	}

	private void event() {

		mGridview.setAdapter(new MyAdapter(list_zi, list_ying, getActivity()));
		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView textView = (TextView) view
						.findViewById(R.id.item_textview);

				String text = textView.getText().toString();
				// *******************************待处理
				for (int i = 0; i < activityHotel.mTvs.size(); i++) {
					if (list_zi.get(position)
							.equals(CommonUtils.getGBstr(activityHotel.mTvs
									.get(i).dev.devName))) {
						final WareTv currentTv = activityHotel.mTvs
								.get(position);
						Intent intent = new Intent();
						intent.setClass(getActivity(), ActivityTvControl.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("tv", currentTv);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}

				Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void inti() {

		if (activityHotel.mTvs.size() > 0) {
			WareTv wareTv = activityHotel.mTvs.get(0);

			devBuff = CommonUtils.createWareDevInfo(wareTv.dev.getCanCpuId(),
					wareTv.dev.getDevName(), wareTv.dev.getRoomName(),
					wareTv.dev.getType(), wareTv.dev.getDevCtrlType(),
					wareTv.dev.getDevId(), CommonUtils.getZerobyte());
		} else {
			devBuff = CommonUtils.getZerobyte();
		}

		power = (Button) view.findViewById(R.id.remote_power);
		volup = (Button) view.findViewById(R.id.remote_volup);
		vodown = (Button) view.findViewById(R.id.remote_voldown);
		chup = (Button) view.findViewById(R.id.remote_chup);
		chdown = (Button) view.findViewById(R.id.remote_chdown);
		num0 = (Button) view.findViewById(R.id.btn_num_0);
		num1 = (Button) view.findViewById(R.id.btn_num_1);
		num2 = (Button) view.findViewById(R.id.btn_num_2);
		num3 = (Button) view.findViewById(R.id.btn_num_3);
		num4 = (Button) view.findViewById(R.id.btn_num_4);
		num5 = (Button) view.findViewById(R.id.btn_num_5);
		num6 = (Button) view.findViewById(R.id.btn_num_6);
		num7 = (Button) view.findViewById(R.id.btn_num_7);
		num8 = (Button) view.findViewById(R.id.btn_num_8);
		num9 = (Button) view.findViewById(R.id.btn_num_9);
		numplus = (Button) view.findViewById(R.id.btn_plus_100);
		last_ch = (Button) view.findViewById(R.id.btn_last_ch);

		power.setOnClickListener(this);
		volup.setOnClickListener(this);
		vodown.setOnClickListener(this);
		chup.setOnClickListener(this);
		chdown.setOnClickListener(this);
		num0.setOnClickListener(this);
		num1.setOnClickListener(this);
		num2.setOnClickListener(this);
		num3.setOnClickListener(this);
		num4.setOnClickListener(this);
		num5.setOnClickListener(this);
		num6.setOnClickListener(this);
		num7.setOnClickListener(this);
		num8.setOnClickListener(this);
		num9.setOnClickListener(this);
		numplus.setOnClickListener(this);
		last_ch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.remote_power:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_offOn.getValue());
			break;
		case R.id.remote_volup:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_numRt.getValue());
			break;
		case R.id.remote_voldown:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_numLf.getValue());
			break;
		case R.id.remote_chup:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_numUp.getValue());
			break;
		case R.id.remote_chdown:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_numDn.getValue());
			break;
		case R.id.btn_num_0:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num0.getValue());
			break;
		case R.id.btn_num_1:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num1.getValue());
			break;
		case R.id.btn_num_2:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num2.getValue());
			break;
		case R.id.btn_num_3:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num3.getValue());
			break;
		case R.id.btn_num_4:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num4.getValue());
			break;
		case R.id.btn_num_5:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num5.getValue());
			break;
		case R.id.btn_num_6:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num6.getValue());
			break;
		case R.id.btn_num_7:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num7.getValue());
			break;
		case R.id.btn_num_8:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num8.getValue());
			break;
		case R.id.btn_num_9:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_num9.getValue());
			break;
		case R.id.btn_last_ch:
			createConCmd(UdpProPkt.E_TV_CMD.e_tv_userDef1.getValue());
			break;
		default:
			break;
		}
	}

	private void createConCmd(int cmd) {
		if (devBuff.length > 0) {
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0, cmd,
					devBuff, devBuff.length));
			
			CommonUtils.sendMsg();
		}
	}
}
