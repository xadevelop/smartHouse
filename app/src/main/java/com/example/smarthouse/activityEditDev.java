package com.example.smarthouse;

import com.example.smarthouse.fragment.fragmentEditDev;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareFreshAir;
import com.pullmi.entity.WareLight;
import com.pullmi.entity.WareSetBox;
import com.pullmi.entity.WareTv;
import com.pullmi.entity.WareValve;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class activityEditDev extends AdAbstractActivity {

	private LinearLayout rootLayout;

	private WareCurtain wareCurtain;
	private WareLight wareLight;
	private WareAirCondDev wareAirCondDev;
	private WareFreshAir wareFreshAir;
	private WareTv wareTv;
	private WareSetBox wareSetBox;
	private WareValve wareValve;
	private String devTypeString = "";
	private int devTag = -1;

	private FragmentManager fm;
	private FragmentTransaction transaction;
	private Fragment menuFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initComponent();

		Intent intent = this.getIntent();
		devTypeString = intent.getStringExtra("dev");
		if (devTypeString.equals("light")) {
			wareLight = (WareLight) intent.getSerializableExtra("light");
			devTag = 3;
		} else if (devTypeString.equals("curtain")) {
			wareCurtain = (WareCurtain) intent.getSerializableExtra("curtain");
			devTag = 4;
		} else if (devTypeString.equals("aircond")) {
			wareAirCondDev = (WareAirCondDev) intent
					.getSerializableExtra("aircond");
			devTag = 0;
		}else if (devTypeString.equals("freshair")) {
			wareFreshAir = (WareFreshAir) intent
					.getSerializableExtra("freshair");
			devTag = 7;
		}else if (devTypeString.equals("value")) {
			wareValve = (WareValve) intent
					.getSerializableExtra("value");
			devTag = 7;
		}else if (devTypeString.equals("tv")) {
			wareTv = (WareTv) intent
					.getSerializableExtra("tv");
			devTag = 1;
		}else if (devTypeString.equals("setBox")) {
			wareSetBox = (WareSetBox) intent
					.getSerializableExtra("setBox");
			devTag = 2;
		}

		fm = getFragmentManager();
		transaction = fm.beginTransaction();
		menuFragment = new fragmentEditDev();
		Bundle data = new Bundle();
		switch (devTag) {
		case 0:
			data.putString("dev", "curtain");
			data.putSerializable("aircond", wareAirCondDev);
			break;
		case 1:
			data.putString("dev", "tv");
			data.putSerializable("tv", wareTv);
			break;
		case 2:
			data.putString("dev", "setBox");
			data.putSerializable("setBox", wareSetBox);
			break;
		case 3:
			data.putString("dev", "light");
			data.putSerializable("light", wareLight);
			break;
		case 4:
			data.putString("dev", "curtain");
			data.putSerializable("curtain", wareCurtain);
			break;
		case 6:
			data.putString("dev", "value");
			data.putSerializable("value", wareValve);
			break;
		case 7:
			data.putString("dev", "freshair");
			data.putSerializable("freshair", wareFreshAir);
			break;
		default:
			break;
		}

		menuFragment.setArguments(data);// 通过Bundle向Activity中传递值
		transaction.add(R.id.devControl, menuFragment);
		transaction.commit();

	}

	private void initComponent() {

		rootLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_control_view, null);
		mRootView.addView(rootLayout, FF);

		setTextForTitle("编辑设备");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
