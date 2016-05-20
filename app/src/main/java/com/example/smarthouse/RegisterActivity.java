package com.example.smarthouse;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class RegisterActivity extends AdAbstractActivity {

	private static final String TAG = "RegisterActivity";

	private FrameLayout registerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		registerLayout = (FrameLayout) layoutInflater.inflate(
				R.layout.activity_register, null);
		mRootView.addView(registerLayout, FF);

		initComponent();

	}

	private void initComponent() {

		setTextForBackBtn(getString(R.string.app_back));
		setTextForRightBtn(getString(R.string.app_register));
		setTextForTitle(getString(R.string.activity_register_title));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_btn4:
			finish();
			break;

		case R.id.title_btn1:
			register();
			break;

		default:
			break;
		}
	}

	private void register() {

	}

}
