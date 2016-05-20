package com.example.smarthouse;

import com.example.smarthouse.fragment.fragmentLockControl;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareLock;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class activityLockControl extends AdAbstractActivity {

	private WareLock wareLock;
	private LinearLayout rootLayout;
	private FragmentManager fm;
	private FragmentTransaction transaction;
	private Fragment menuFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_control_view, null);
		mRootView.addView(rootLayout, FF);

		initComponent();

		Intent intent = this.getIntent();
		wareLock = (WareLock) intent.getSerializableExtra("lock");

		fm = getFragmentManager();
		transaction = fm.beginTransaction();
		menuFragment = new fragmentLockControl();
		Bundle data = new Bundle();
		data.putSerializable("lock", wareLock);
		menuFragment.setArguments(data);// 通过Bundle向Activity中传递值

		transaction.add(R.id.devControl, menuFragment);
		transaction.commit();

	}

	private void initComponent() {

		setTextForTitle(getString(R.string.e_ware_lock));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
			
	}
}
