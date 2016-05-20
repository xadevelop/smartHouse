package com.example.smarthouse.hotel;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.activityAircondControl;
import com.example.smarthouse.activityCurtainControl;
import com.example.smarthouse.activityEditDev;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.entity.WareCurtain;

/**
 * Created by maibenben on 2016/4/25.
 */
public class Curtain extends Fragment {
	GridView mGridview;
	View view;
	List<String> list_zi;
	private boolean curState;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_hotel, null);

		inti();
		event();

		return view;
	}

	private void event() {

		mGridview.setAdapter(new CL_MyAdapter(list_zi, getActivity()));
		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			private byte[] devBuff;

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textView = (TextView) view.findViewById(R.id.item_textview);

				// *******************************待处理
				for (int i = 0; i < activityHotel.mCurtainDataset.size(); i++) {
					if (list_zi.get(position)
							.equals(CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "开")) {

						conCurtain(i, UdpProPkt.E_CURT_CMD.e_curt_offOn.getValue());

					}
					if (list_zi.get(position)
							.equals(CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "停")) {
						conCurtain(i, UdpProPkt.E_CURT_CMD.e_curt_stop.getValue());
					}
					if (list_zi.get(position)
							.equals(CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "关")) {

						conCurtain(i, UdpProPkt.E_CURT_CMD.e_curt_offOff.getValue());
					}
				}

				if (activityHotel.mCurtainDataset.size() > 0) {
					if (list_zi.get(position).equals("全开")) {
						CommonUtils.controlAllDev(UdpProPkt.E_WARE_TYPE.e_ware_curtain.getValue(), 0x01);
					} else if (list_zi.get(position).equals("全关")) {
						CommonUtils.controlAllDev(UdpProPkt.E_WARE_TYPE.e_ware_curtain.getValue(), 0x00);
					} else if (list_zi.get(position).equals("全停")) {
						CommonUtils.controlAllDev(UdpProPkt.E_WARE_TYPE.e_ware_curtain.getValue(), 0x02);
					}
				}

				// Toast.makeText(getActivity(), text,
				// Toast.LENGTH_SHORT).show();
			}
		});
		mGridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < activityHotel.mCurtainDataset.size(); i++) {
					if (list_zi.get(arg2).equals(CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "开")
							|| list_zi.get(arg2).equals(
									CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "关")
							|| list_zi.get(arg2).equals(
									CommonUtils.getGBstr(activityHotel.mCurtainDataset.get(i).dev.devName) + "停")) {
						WareCurtain currentCurtain = activityHotel.mCurtainDataset.get(i);
						Intent intent = new Intent();
						intent.setClass(getActivity(), activityEditDev.class);
						Bundle bundle = new Bundle();
						bundle.putString("dev", "curtain");
						bundle.putSerializable("curtain", currentCurtain);
						intent.putExtras(bundle);
						startActivity(intent);
						return false;
					}
				}
				return false;
			}
		});
	}

	private void inti() {

		mGridview = (GridView) view.findViewById(R.id.gridview);
		list_zi = new ArrayList<String>();
		List<WareCurtain> curtains = activityHotel.mCurtainDataset;
		list_zi.add("全开");
		list_zi.add("全停");
		list_zi.add("全关");
		for (int i = 0; i < curtains.size(); i++) {
			list_zi.add(CommonUtils.getGBstr(curtains.get(i).dev.devName) + "开");
			list_zi.add(CommonUtils.getGBstr(curtains.get(i).dev.devName) + "停");
			list_zi.add(CommonUtils.getGBstr(curtains.get(i).dev.devName) + "关");
		}
	}

	private void conCurtain(int index, int cmd) {

		byte[] devBuff;
		WareCurtain currentCurtain = activityHotel.mCurtainDataset.get(index);

		byte[] state = new byte[4];
		state[0] = currentCurtain.bOnOff;
		state[1] = currentCurtain.timRun;
		state[2] = (byte) currentCurtain.powChn;
		state[3] = 0;

		devBuff = CommonUtils.createWareDevInfo(currentCurtain.dev.getCanCpuId(), currentCurtain.dev.getDevName(),
				currentCurtain.dev.getRoomName(), currentCurtain.dev.getType(), currentCurtain.dev.getDevCtrlType(),
				currentCurtain.dev.getDevId(), state);

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0, cmd, devBuff, devBuff.length));
		CommonUtils.sendMsg();
	}
}
