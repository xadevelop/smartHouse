package com.example.smarthouse;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class VideoTalkActivity extends AdAbstractActivity {

	
	private LinearLayout talkLayout;
	
	public static final int FRAME_WIDTH = 352;
	public static final int FRAME_HEIGHT = 240;
	
	public static final String CALLEND_FROM_DOOR = "com.pullmi.callend.from.door";
	public static final String CALLEND_FROM_ROOM_FINISHED = "com.pullmi.callend.from.room.finished";
	
	private static ImageView incomeVideo;
	private static ImageView outcomeVideo;
	
	private class VideoTalkCallendReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
//			if(intent.getAction().equals(CALLEND_FROM_DOOR)) {
//				LogUtils.LOGE("Monitor", "--------> receive callend from door receiver -------->");
//				Intent callendFromDoorFinished = new Intent(Monitor.CALLEND_FROM_DOOR_FINISHED);
//				sendBroadcast(callendFromDoorFinished);
//				finish();
//			} else if(intent.getAction().equals(CALLEND_FROM_ROOM_FINISHED)) {
//				finish();
//			}
		}
		
	}
	
	
	private static final int MSG_UPDATE_INCOME_VIDEO = 1001;
	private static final int MSG_UPDATE_OUTCOME_VIDEO = 1002;
	
	private static Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case MSG_UPDATE_INCOME_VIDEO:
				incomeVideo.setImageBitmap(bitmap);
				break;
				
			case MSG_UPDATE_OUTCOME_VIDEO:
				break;
				
				default:
					break;
			}
		}
		
	};
	
	VideoTalkCallendReceiver videoTalkCallendReceiver;
	
	public static Bitmap bitmap = Bitmap.createBitmap(FRAME_WIDTH, FRAME_HEIGHT, Bitmap.Config.ARGB_8888);
	
	public static void updateImg() {
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_UPDATE_INCOME_VIDEO;
		mHandler.sendMessage(msg);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		talkLayout = (LinearLayout) layoutInflater.inflate(R.layout.avtivity_video_talk, null);
		mRootView.addView(talkLayout, FF);
		
		initComponent();
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	private void initComponent() {
		setTextForTitle(getString(R.string.activity_title_video_talk));
		
		setTextForBackBtn(getString(R.string.app_back));
		
		setTextForRightBtn(getString(R.string.activity_talk_right_btn));
		
		incomeVideo = (ImageView) findViewById(R.id.income_video);
		outcomeVideo = (ImageView) findViewById(R.id.outcome_video);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//register receiver
		videoTalkCallendReceiver = new VideoTalkCallendReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CALLEND_FROM_DOOR);
		filter.addAction(CALLEND_FROM_ROOM_FINISHED);
		registerReceiver(videoTalkCallendReceiver, filter);
		
		//send activity started broadcast
//		Intent startFinished = new Intent(Monitor.VIDEOTALK_ACTIVITY_STARTED);
//		sendBroadcast(startFinished);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(videoTalkCallendReceiver != null) {
			unregisterReceiver(videoTalkCallendReceiver);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn4:
			finish();
			break;
			
		case R.id.title_btn1:
			break;
			
			default:
				break;
		}
	}

}
