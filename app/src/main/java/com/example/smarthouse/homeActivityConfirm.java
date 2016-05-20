package com.example.smarthouse;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class homeActivityConfirm extends AdAbstractActivity {

	private Button confirm;
	private LinearLayout registerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		registerLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_home_confirm, null);
		mRootView.addView(registerLayout, FF);

		Window window = getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		// 设置窗口的大小及透明度
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = layoutParams.WRAP_CONTENT;
		layoutParams.gravity = Gravity.CENTER;
		// layoutParams.alpha = 1.5f;
		window.setAttributes(layoutParams);

		confirm = (Button) findViewById(R.id.input_dialog_confimPass);
		confirm.setOnClickListener(this);
		confirm.setOnTouchListener(TouchDark);
		
		initComponent();
	}

	private void initComponent() {

		setTextForTitle("");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.input_dialog_confimPass:
			this.finish();
			break;

		default:
			break;
		}
	}
}
