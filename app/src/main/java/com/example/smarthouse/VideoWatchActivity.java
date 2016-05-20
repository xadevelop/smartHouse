package com.example.smarthouse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.audio.VideoPlayView;
import com.pullmi.common.CommonUtils;
import com.pullmi.service.TalkService;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

public class VideoWatchActivity extends AdAbstractActivity {
	
	private static final String TAG = "VideoWatchActivity";
	
	private MulticastSocket watchClass;
	
	private ExecutorService executorService;
	
	FrameLayout watchLayout;
	
	public static VideoPlayView mVideoView = null;
	
	private String doorAddr;
	private String doorIp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		watchLayout = (FrameLayout) layoutInflater.inflate(R.layout.activity_video_watch, null);
		mRootView.addView(watchLayout, FF);
		
		mVideoView = new VideoPlayView(this);
		watchLayout.addView(mVideoView, FF);
		
		setTextForBackBtn("返回");
		setTextForTitle("远程监视");
		
		setTextForRightBtn("停止监视");	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TalkService.mWatchActivity = this;		
		mVideoView.startPlay();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mVideoView.stopPlay();
		TalkService.mWatchActivity = null;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_btn4:
			sendVideowatchAskCallend();
			TalkService.mCallStatus = TalkService.e_call_null;
			finish();
			break;			
		case R.id.title_btn1:
			sendVideowatchAskCallend();
			TalkService.mCallStatus = TalkService.e_call_null;
			finish();
			break;			
		default:
			break;
		}
	}
	
	public  void exitSelf()
	{
		sendVideowatchAskCallend();
		TalkService.mCallStatus = TalkService.e_call_null;
		finish();
	}
	
	private void sendVideowatchAskCallend() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte[] videowatchCallend;
				Log.e(TAG, "+++++++++++++++++sendVideowatchAskCallend");
				try {
					videowatchCallend = CommonUtils.createVideoWatchAskCallend(CommonUtils.getLocalAddr(), CommonUtils.getLocalIp(), CommonUtils.getRemoteAddr(), CommonUtils.mRemoteIp);
					DatagramPacket packet = new DatagramPacket(videowatchCallend, videowatchCallend.length, InetAddress.getByName(CommonUtils.mRemoteIp), 8302);
					DatagramSocket socket = new DatagramSocket();
					socket.send(packet);
					socket.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void sendVideowatchAskCall() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
				Log.e(TAG, "+++++++++++++++++sendVideowatchAskCall");
				try {
					byte[] data = CommonUtils.createVideoWatchAskCall(CommonUtils.getLocalAddr(), CommonUtils.getLocalIp(), CommonUtils.mRemoteAddr, CommonUtils.mRemoteIp);
					DatagramPacket watchpacket = new DatagramPacket(data, data.length, InetAddress.getByName(CommonUtils.mRemoteIp), 8302);
					DatagramSocket socket = new DatagramSocket();
					socket.send(watchpacket);
					socket.close();
					socket = null;
					mWaitWatchCallReply = true;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static boolean mWaitNsReply = false;
	public static boolean mWaitWatchCallReply = false;
	public static void sendNsOrderAsk() 
	{
		new Thread(new Runnable() 
		{			
			@Override
			public void run() {
				byte[] nsorderData;
				Log.e(TAG, "+++++++++++++++++++++++++++++++ sendNsOrderAsk for watch------------》");
				try 
				{
					nsorderData = CommonUtils.createVideoWatchNsOrderAsk(CommonUtils.getLocalAddr(), CommonUtils.getLocalIp(), CommonUtils.getRemoteAddr());
					DatagramPacket packet = new DatagramPacket(nsorderData, nsorderData.length, InetAddress.getByName("238.9.9.1"), 8302);
					DatagramSocket socket = new DatagramSocket();
					socket.send(packet);
					socket.close();
					socket = null;
					mWaitNsReply = true;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();				
	}


}
