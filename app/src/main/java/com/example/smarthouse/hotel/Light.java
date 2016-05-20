package com.example.smarthouse.hotel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.activityCurtainControl;
import com.example.smarthouse.activityEditDev;
import com.example.smarthouse.activityLightTuneControl;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareLight;

/**
 * Created by maibenben on 2016/4/25.
 */
public class Light extends Fragment {
	GridView mGridview;
	View view;
	private List<String> list_zi;
	private List<String> list_ying;
	private activityHotel mActivity;
	private MyAdapter_Light adapter_Light;
	private boolean ledState = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_hotel_light, null);

		inti();
		event();

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		mActivity = (activityHotel) activity;
		mActivity.setHandler(mHandler);
	}

	public Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
				adapter_Light.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	private void event() {

		adapter_Light = new MyAdapter_Light(list_zi, getActivity());
		mGridview.setAdapter(adapter_Light);
		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textView = (TextView) view.findViewById(R.id.item_textview);
				// *******************************待处理
				for (int i = 0; i < activityHotel.mLightDataset.size(); i++) {
					if (list_zi.get(position).equals(CommonUtils.getGBstr(activityHotel.mLightDataset.get(i).dev.devName))) {
						final WareLight currentLight = activityHotel.mLightDataset.get(i);
						if (activityHotel.mLightDataset.get(i).bTuneEn == 1) {
							Intent intent = new Intent();
							intent.setClass(getActivity(), activityLightTuneControl.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("light", currentLight);
							intent.putExtras(bundle);
							startActivity(intent);
						}
						if (activityHotel.mLightDataset.get(i).bOnOff == 0) {
							if (currentLight != null) {
								CommonUtils.controlLight(currentLight, 0);
							}
						} else {
							if (currentLight != null) {
								CommonUtils.controlLight(currentLight, 1);
							}
						}
					}
				}
				if (activityHotel.mLightDataset.size() > 0 && list_zi.get(position).equals("全开")) {

					CommonUtils.controlAllDev(UdpProPkt.E_WARE_TYPE.e_ware_light.getValue(), 0x01);
				}
				if (activityHotel.mLightDataset.size() > 0 && list_zi.get(position).equals("全关")) {

					CommonUtils.controlAllDev(UdpProPkt.E_WARE_TYPE.e_ware_light.getValue(), 0x00);
				}
				// Toast.makeText(getActivity(), text,
				// Toast.LENGTH_SHORT).show();
			}
		});

		mGridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				for (int i = 0; i < activityHotel.mLightDataset.size(); i++) {
					if (list_zi.get(arg2).equals(CommonUtils.getGBstr(activityHotel.mLightDataset.get(i).dev.devName))) {
						WareLight currentLight = activityHotel.mLightDataset.get(i);

						Intent intent = new Intent();
						intent.setClass(getActivity(), activityEditDev.class);
						Bundle bundle = new Bundle();
						bundle.putString("dev", "light");
						bundle.putSerializable("light", currentLight);
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
		List<WareLight> lights = activityHotel.mLightDataset;
		list_zi.add("全开");
		list_zi.add("全关");
		for (int i = 0; i < lights.size(); i++) {
			list_zi.add(CommonUtils.getGBstr(lights.get(i).dev.devName));
		}
	}
}
