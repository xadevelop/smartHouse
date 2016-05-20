package com.example.smarthouse;

import com.pullmi.app.GlobalVars;
import com.pullmi.utils.LogUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class AdAbstractActivity extends FragmentActivity implements OnClickListener {

	String TAG = this.getClass().getName();

	protected LayoutParams FF = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
	protected LayoutParams FW = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	protected LayoutInflater layoutInflater;
	protected LinearLayout mmTitle;
	protected LinearLayout mRootView;
	protected Button titleBackBtn;
	protected Button titleRightBtn;
	protected TextView titleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.ad_mm_activity);

		layoutInflater = LayoutInflater.from(this);

		mRootView = (LinearLayout) findViewById(R.id.mm_root_view);

		mmTitle = (LinearLayout) layoutInflater.inflate(R.layout.ad_mm_title, null);
		mRootView.addView(mmTitle, FW);

		init();

		if (isTablet(GlobalVars.getContext())) {
			LogUtils.LOGE(TAG, "is Tablet!");
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			GlobalVars.setTwoPane(true);
		} else {
			LogUtils.LOGE(TAG, "is phone!");
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			GlobalVars.setTwoPane(false);
		}
	}

	protected void init() {
		titleBackBtn = (Button) findViewById(R.id.title_btn4);
		titleRightBtn = (Button) findViewById(R.id.title_btn1);
		titleTextView = (TextView) findViewById(R.id.title);

		titleBackBtn.setOnClickListener(this);
		titleRightBtn.setOnClickListener(this);
	}

	protected void setTextForBackBtn(String text) {
		titleBackBtn.setText(text);
		titleBackBtn.setVisibility(Button.VISIBLE);
	}

	protected void setTextForRightBtn(String text) {
		titleRightBtn.setText(text);
		titleRightBtn.setVisibility(Button.VISIBLE);
	}

	protected void setTextForTitle(String title) {
		titleTextView.setText(title);
	}

	public static final OnTouchListener TouchDark = new OnTouchListener() {

		public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1,
				0, -50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
				0, 0, 0, 0, 0, 1, 0 };

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
		}
	};

	public static final OnTouchListener TouchLight = new OnTouchListener() {

		public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, 50, 0, 1, 0, 0, 50, 0, 0, 1,
				0, 50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
				0, 0, 0, 0, 0, 1, 0 };

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
		}
	};

	/**
	 * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
	 * 
	 * @param context
	 * @return 平板返回 True，手机返回 False
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}
