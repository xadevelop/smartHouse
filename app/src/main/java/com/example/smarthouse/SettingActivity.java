package com.example.smarthouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingActivity extends AdAbstractActivity {

	private LinearLayout registerLayout;
	private TextView timerEventSet, sceneEventSet, chnOutInputSet,rcuSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		registerLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activiyt_setting, null);
		mRootView.addView(registerLayout, FF);

		initComponent();

	}

	private void initComponent() {

		setTextForTitle(getString(R.string.tab_smartHome));

		sceneEventSet = (TextView) findViewById(R.id.sceneEventSetting);
		chnOutInputSet = (TextView) findViewById(R.id.chnOutInputSetting);
		rcuSet = (TextView)findViewById(R.id.rcuSetting);

		sceneEventSet.setOnClickListener(this);
		chnOutInputSet.setOnClickListener(this);
		rcuSet.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.sceneEventSetting:
			intent.setClass(this, createSceneEventActivity.class);
			break;
		case R.id.chnOutInputSetting:
			intent.setClass(this, chnOutInActivity.class);
			break;
		case R.id.rcuSetting:
			intent.setClass(this, rcuInfoActivity.class);
			break;
		default:
			break;
		}
		startActivity(intent);
	}

}
