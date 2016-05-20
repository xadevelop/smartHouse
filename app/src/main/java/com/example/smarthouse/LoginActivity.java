package com.example.smarthouse;

import com.example.smarthouse.hotel.activityHotel;
import com.pullmi.common.CommonUtils;
import com.pullmi.service.TalkService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends AdAbstractActivity {

	private LinearLayout loginLayout;

	private EditText mNameEt;
	private EditText mPassEt;
	private CheckBox mALoginBox;

	private Button regBtn;
	private String userInfoConfigName = "userInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loginLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_login, null);
		mRootView.addView(loginLayout, FF);

		initComponent();

	}

	private void initComponent() {

		mNameEt = (EditText) findViewById(R.id.lname);
		mPassEt = (EditText) findViewById(R.id.lpass);
		mALoginBox = (CheckBox) findViewById(R.id.auto_login);
		
		setTextForBackBtn(getString(R.string.app_register));
		setTextForRightBtn(getString(R.string.action_log_out));
		setTextForTitle(getString(R.string.activity_login_title));

		regBtn = (Button) findViewById(R.id.reg_btn);
		regBtn.setOnClickListener(this);
		regBtn.setOnTouchListener(TouchDark);

		//if (mALoginBox.isChecked()) {
			getUserInfo();
		//}
	}

	private void getUserInfo() {
		SharedPreferences config = getSharedPreferences(userInfoConfigName, 0);
		String name = config.getString("name", "");
		String pass = config.getString("pass", "");

		mNameEt.setText(name);
		mPassEt.setText(pass);
	}

	private void setUserInfo() {
		SharedPreferences config = getSharedPreferences(userInfoConfigName, 0);
		SharedPreferences.Editor editor = config.edit();
		// 存放数据
		editor.putString("name", mNameEt.getText().toString());
		editor.putString("pass", mPassEt.getText().toString());
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		CommonUtils.CommonUtils_init();
		Toast.makeText(this, "ip: " + CommonUtils.getLocalIp(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_btn1: // 退出按钮
			finish();
			break;

		case R.id.title_btn4: // 注册按钮
			showRegister();
			break;

		case R.id.reg_btn: // 登录按钮
			//if (mALoginBox.isChecked()) {
				// 保存ID
				setUserInfo();
			//}
			
			String dyh = mNameEt.getText().toString().trim();
			String room = mPassEt.getText().toString().trim();
			
			
			CommonUtils.mLocalAddr = "S"+dyh+room+"000000000";
			CommonUtils.mRemoteAddr = "M"+dyh+"0000000000000";
			Log.e("err ", ">>>>>>>>>>>mLocalAddr: " + CommonUtils.mLocalAddr);
			Log.e("err ", ">>>>>>>>>>>mRemoteAddr: " + CommonUtils.mRemoteAddr );
			
			startMonitor(); // 启动监视器
			startMain();
			finish();
		default:
			break;
		}
	}

	private void startMain() {
		// TODO Auto-generated method stu
		//Intent intent = new Intent(this, HomeActivity.class);
		//startActivity(intent);
		
		Intent intent = new Intent(this, activityHotel.class);
		startActivity(intent);
	}

	private void showRegister() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	private void startMonitor() {
		Intent monitor = new Intent(this, TalkService.class);
		startService(monitor);
	}

}
