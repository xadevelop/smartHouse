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
public class Order extends Fragment {
	GridView mGridview;
	View view;
	List<String> list_zi;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_hotel, null);

		inti();
		event();

		return view;
	}

	private void event() {

		mGridview.setAdapter(new Order_MyAdapter(getActivity()));

	}

	private void inti() {

		mGridview = (GridView) view.findViewById(R.id.gridview);
	}
}
