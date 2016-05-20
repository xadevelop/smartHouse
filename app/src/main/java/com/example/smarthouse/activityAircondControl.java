package com.example.smarthouse;

import com.example.smarthouse.fragment.fragmentAircondControl;
import com.pullmi.entity.WareAirCondDev;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class activityAircondControl extends AdAbstractActivity {

	private LinearLayout rootLayout;
	private FragmentManager fm;
	private FragmentTransaction transaction;
	private Fragment menuFragment;
	private WareAirCondDev wareAirCondDev;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		wareAirCondDev = (WareAirCondDev) intent.getSerializableExtra("aircond");

		initComponent();

		fm = getFragmentManager();
		transaction = fm.beginTransaction();
		menuFragment = new fragmentAircondControl();
		Bundle data = new Bundle();
		data.putSerializable("aircond", wareAirCondDev);
		menuFragment.setArguments(data);// 通过Bundle向Activity中传递值

		transaction.add(R.id.devControl, menuFragment);
		transaction.commit();
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.e_ware_airCond));

		rootLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_control_view, null);
		mRootView.addView(rootLayout, FF);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
	}
}
