package com.example.smarthouse;

import com.example.smarthouse.fragment.fragmentCurtainControl;
import com.pullmi.entity.WareCurtain;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class activityCurtainControl extends AdAbstractActivity implements
		OnClickListener {

	private LinearLayout rootLayout;
	private FragmentManager fm;
	private FragmentTransaction transaction;
	private Fragment menuFragment;
	private WareCurtain wareCurtain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		rootLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_control_view, null);
		mRootView.addView(rootLayout, FF);

		Intent intent = this.getIntent();
		wareCurtain = (WareCurtain) intent.getSerializableExtra("curtain");

		fm = getFragmentManager();
		transaction = fm.beginTransaction();
		menuFragment = new fragmentCurtainControl();
		Bundle data = new Bundle();
		data.putSerializable("curtain", wareCurtain);
		menuFragment.setArguments(data);// 通过Bundle向Activity中传递值

		transaction.add(R.id.devControl, menuFragment);
		transaction.commit();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
