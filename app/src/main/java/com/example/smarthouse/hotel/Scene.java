package com.example.smarthouse.hotel;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.activityCurtainControl;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareLight;
import com.pullmi.entity.WareSceneEvent;

/**
 * Created by maibenben on 2016/4/25.
 */
public class Scene extends Fragment {
	GridView mGridview;
	View view;
	private List<String> list_zi;
	byte[] devBuff;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_hotel, null);

		inti();
		event();

		return view;
	}

	private void event() {

		mGridview.setAdapter(new Scene_MyAdapter(list_zi, getActivity()));
		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// *******************************待处理
				for (int i = 0; i < activityHotel.mSceneEvents.size(); i++) {
					if (list_zi.get(position).equals(
							CommonUtils.getGBstr(activityHotel.mSceneEvents
									.get(i).sceneName).trim())) {
						devBuff = CommonUtils
								.createSceneEvent(activityHotel.mSceneEvents
										.get(i));
						GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
								GlobalVars.getDstip(),
								CommonUtils.getLocalIp(),
								UdpProPkt.E_UDP_RPO_DAT.e_udpPro_exeSceneEvents
										.getValue(), 0, 0, devBuff,
								devBuff.length));
						CommonUtils.sendMsg();
					}
				}
				// Toast.makeText(getActivity(), text,
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void inti() {

		mGridview = (GridView) view.findViewById(R.id.gridview);
		list_zi = new ArrayList<String>();
		List<WareSceneEvent> scenes = activityHotel.mSceneEvents;
		for (int i = 0; i < scenes.size(); i++) {
			list_zi.add(CommonUtils.getGBstr(scenes.get(i).sceneName).trim());
		}
	}
}
