package com.pullmi.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.smarthouse.TalkActivity;
import com.example.smarthouse.TalkActivity_old;
import com.example.smarthouse.VideoWatchActivity;
import com.pullmi.common.CommonUtils;
import com.pullmi.common.Constants;
import com.pullmi.net.VideoPacket;
import com.pullmi.net.AudioPacket;
import com.pullmi.utils.LogUtils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class TalkService extends Service {

	public static final String TAG = "TalkService";
	private static final String INFO_TAG = "CALLUP";

	private Object lock = new Object();

	public static final byte[] header = { -1, -40, -1, -32, 0, 16, 74, 70, 73,
			70, 0, 1, 1, 2, 0, 118, 0, 118, 0, 0, -1, -37, 0, 67, 0, 13, 9, 10,
			11, 10, 8, 13, 11, 10, 11, 14, 14, 13, 15, 19, 32, 21, 19, 18, 18,
			19, 39, 28, 30, 23, 32, 46, 41, 49, 48, 46, 41, 45, 44, 51, 58, 74,
			62, 51, 54, 70, 55, 44, 45, 64, 87, 65, 70, 76, 78, 82, 83, 82, 50,
			62, 90, 97, 90, 80, 96, 74, 81, 82, 79, -1, -37, 0, 67, 1, 14, 14,
			14, 19, 17, 19, 38, 21, 21, 38, 79, 53, 45, 53, 79, 79, 79, 79, 79,
			79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79,
			79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79,
			79, 79, 79, 79, 79, 79, 79, 79, 79, 79, 79, -1, -64, 0, 17, 8, 1,
			-32, 2, -128, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, -1, -60, 0, 31, 0,
			0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5,
			6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5,
			5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19,
			81, 97, 7, 34, 113, 20, 50, -127, -111, -95, 8, 35, 66, -79, -63,
			21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, 22, 23, 24, 25, 26,
			37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70,
			71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102,
			103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, -125,
			-124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107,
			-106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88,
			-87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61,
			-60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41,
			-40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22,
			-15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0,
			3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6,
			7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5,
			4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7,
			97, 113, 19, 34, 50, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35,
			51, 82, -16, 21, 98, 114, -47, 10, 22, 36, 52, -31, 37, -15, 23,
			24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69,
			70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101,
			102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122,
			-126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109,
			-108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90,
			-89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70,
			-62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43,
			-42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23,
			-22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1,
			0, 2, 17, 3, 17, 0, 63, 0 };

	public static final byte[] tail = { -1, -39 };

	private WindowManager mWm;
	private WindowManager.LayoutParams mwParams;
	private View callDialogView;
	private RingtoneManager mRm;

	private Uri alertUri = RingtoneManager
			.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	MediaPlayer mMediaPlayer;

	// 线程池
	private ExecutorService executorService = Executors.newCachedThreadPool();

	private MulticastSocket castClass;

	private String talkGroup;
	private String watchGroup;

	// IP组播组238.9.9.1
	public static final String WAITING_GROP = "238.9.9.1";
	// 端口8302
	public static final int PORT = 8302;
	// 超时2s
	private static final int SO_TIMEOUT = 2000;

	public static String mDoorAddr;
	public static String mDoorIp;
	public static String tmpDoorAddr;
	public static String tmpDoorIp;

	public static final int e_call_null = 0;
	public static final int e_call_host_call = 1;
	public static final int e_call_slave_beCall = 2;
	public static final int e_call_host_watch = 3;
	public static final int e_call_slave_beWatch = 4;
	public static final int e_call_host_calling = 5;
	public static final int e_call_slave_beCalling = 6;

	public static int mCallStatus;
	public static int mWatchTimout;
	public static int mCallTimout;

	public static VideoPacket mVideoBuffAry[];
	public static int vpktReadPos;
	public static int vpktWtPos;
	public static int vpktCnt;
	public final static int maxVideoPktCnt = 4;

	public static AudioPacket mAudioBuffAry[];
	public static int aupktReadPos;
	public static int aupktWtPos;
	public static int aupktCnt;
	public final static int maxAudioPktCnt = 32;

	public static AudioPacket mRAudioBuffAry[];
	public static int auRpktReadPos;
	public static int auRpktWtPos;
	public static int auRpktCnt;
	public final static int maxRAudioPktCnt = 32;

	private boolean isConfirming = false;

	Future talkTask;
	Future confirmTask;

	public static TalkActivity mTalkActivity;
	public static VideoWatchActivity mWatchActivity;

	private AlertDialog callDialog = null;

	private VideoPacket mCurrentVideoPacket;
	private AudioPacket mCurrentAudioPacket;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static final int MSG_ID_SHOW_CALLDIALOG = 1001;
	private static final int MSG_ID_DISMISS_CALLDIALOG = 1002;
	private static final int MSG_ID_TIMER_FOR_CALL = 1003;
	private static final int MSG_ID_TIMER_FOR_WATCH = 1004;

	private boolean flag = true;

	private class ThreadDiss extends Thread {
		@Override
		public void run() {
			super.run();
			while (flag) {
				try {
					Thread.sleep(30000);
					Message msg = mHandler.obtainMessage();
					msg.what = MSG_ID_DISMISS_CALLDIALOG;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ID_SHOW_CALLDIALOG:
				if (!callDialog.isShowing()) {
					Window win = callDialog.getWindow();
					/*
					 * LayoutParams params = win.getAttributes();//new
					 * LayoutParams(); params.x = 0;//设置x坐标 params.y = 0;//设置y坐标
					 * win.setAttributes(params);
					 */
					win.setGravity(Gravity.BOTTOM);
					// 显示来电Dialog
					callDialog.show();
					new ThreadDiss().start();
				}
				try {
					mMediaPlayer = new MediaPlayer(); // 播放音频,播放器初始化,播放来电音乐
					mMediaPlayer.setDataSource(TalkService.this, alertUri);
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
					mMediaPlayer.setLooping(true);
					mMediaPlayer.prepare();
					mMediaPlayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case MSG_ID_DISMISS_CALLDIALOG:
				if (callDialog.isShowing()) {
					callDialog.dismiss();
				}
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
					mMediaPlayer.release();
					mMediaPlayer = null;
				}

				if (mTalkActivity != null && mTalkActivity.isShow) {
					mTalkActivity.finish();
					mTalkActivity = null;
				}
				break;
			case MSG_ID_TIMER_FOR_CALL:
				LogUtils.LOGE(
						TAG,
						">>>>MSG_ID_TIMER_FOR_CALL "
								+ Integer.toString(mCallStatus));
				mCallTimout++;
				switch (mCallStatus) {
				case e_call_null:
					break;
				case e_call_host_call:
				case e_call_slave_beCall:
					if (mCallTimout >= 30)// 应答超时
					{
						if (callDialog.isShowing()) {
							callDialog.dismiss();
						}
						if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
							mMediaPlayer.stop();
							mMediaPlayer.release();
							mMediaPlayer = null;
						}

						if (mTalkActivity != null && mTalkActivity.isShow) {
							mTalkActivity.finish();
							mTalkActivity = null;
						}
						mCallStatus = e_call_null;
						exitGroup(castClass, talkGroup);
					}
					break;
				case e_call_host_watch:
				case e_call_slave_beWatch:
				case e_call_host_calling:
					break;
				case e_call_slave_beCalling:
					if (mCallTimout >= 6)// 在线确认包超时
					{
						if (mTalkActivity != null && mTalkActivity.isShow) {
							mTalkActivity.finish();
							mTalkActivity = null;
						}
						mCallStatus = e_call_null;
						exitGroup(castClass, talkGroup);
					}
					break;
				}
				break;
			case MSG_ID_TIMER_FOR_WATCH:
				LogUtils.LOGE(
						TAG,
						">>>>MSG_ID_TIMER_FOR_WATCH "
								+ Integer.toString(mCallStatus));
				switch (mCallStatus) {
				case e_call_host_watch:
					mWatchTimout++;
					if (mWatchTimout > 30) {
						if (mWatchActivity != null) {
							mWatchActivity.exitSelf();
						}
						mCallStatus = e_call_null;
						exitGroup(castClass, talkGroup);
					}
					break;
				}
				break;
			}
		}
	};

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				getApplicationContext());
		builder.setTitle("提示");
		builder.setMessage("有新来电");
		builder.setPositiveButton("接听", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread(new Runnable() {
					public void run() {

						try {
							DatagramSocket socket = new DatagramSocket();
							byte[] callstartData = CommonUtils
									.createVideotalkAskCallstart(
											CommonUtils.getLocalAddr(),
											CommonUtils.getLocalIp(),
											mDoorAddr, mDoorIp);
							DatagramPacket callstartPacket = new DatagramPacket(
									callstartData, callstartData.length,
									InetAddress.getByName(mDoorIp), PORT);
							socket.send(callstartPacket);
							socket = null;
						} catch (SocketException e) {
							e.printStackTrace();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();

				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
					mMediaPlayer.release();
					mMediaPlayer = null;
				}

				flag = false;
			}
		});

		builder.setNegativeButton("挂机", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread(new Runnable() {
					public void run() {

						try {
							DatagramSocket socket = new DatagramSocket();
							byte[] callendData = CommonUtils
									.createVideotalkAskCallend(
											// 发送结束通话指令
											CommonUtils.getLocalAddr(),
											CommonUtils.getLocalIp(),
											mDoorAddr, mDoorIp);
							DatagramPacket callendPacket = new DatagramPacket(
									callendData, callendData.length,
									InetAddress.getByName(mDoorIp), PORT);
							socket.send(callendPacket);
						} catch (SocketException e) {
							e.printStackTrace();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
					mMediaPlayer.release();
					mMediaPlayer = null;
				}
				flag = false;
			}
		});

		// 创建来电dialog,提示接听还是拒绝
		// AlertDialog dialog = builder.create();
		callDialog = builder.create();
		callDialog.setCancelable(false);
		callDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.e(TAG, "--------> heade length: " + header.length);

		mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
		mwParams = new WindowManager.LayoutParams();
		mwParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mwParams.flags = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
		mwParams.width = 400;
		mwParams.height = 400;

		createDialog();

		// clear audio queue
		if (TalkActivity_old.mAudioQueue != null) {
			TalkActivity_old.mAudioQueue.clearQueuel();
		}

		doStart();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	public synchronized static int getVideoBuff2Write() {
		int ret = 0;
		if (vpktCnt >= maxVideoPktCnt) {
			return -1;
		}

		ret = vpktWtPos;
		for (int i = 0; i < 20; i++) {
			mVideoBuffAry[vpktWtPos].pktValid[i] = 0;
		}

		return ret;
	}

	public synchronized static boolean ChkVideoBuffWriteOK() {
		boolean ret = true;
		if (mVideoBuffAry[vpktWtPos].totalPackages > 20)
			return false;

		for (int i = 0; i < mVideoBuffAry[vpktWtPos].totalPackages; i++) {
			if (mVideoBuffAry[vpktWtPos].pktValid[i] == 0) {
				return false;
			}
		}

		return ret;
	}

	public synchronized static int setVideoBuff2Writed() {
		int ret = 0;
		if (vpktCnt >= maxVideoPktCnt) {
			return -1;
		}

		ret = vpktWtPos;
		vpktCnt++;
		vpktWtPos++;
		if (vpktWtPos >= maxVideoPktCnt)
			vpktWtPos = 0;
		return ret;
	}

	public synchronized static void setVideoBuffLastWrPosFree() {
		if (vpktCnt == 0)
			return;
		if (vpktWtPos == 0)
			vpktWtPos = maxVideoPktCnt - 1;
		else
			vpktWtPos--;
		vpktCnt--;
	}

	public synchronized static int SetVideoBuff2Readed() {
		int ret;
		if (vpktCnt == 0)
			return -1;

		ret = vpktReadPos;
		vpktCnt--;
		vpktReadPos++;
		if (vpktReadPos >= maxVideoPktCnt)
			vpktReadPos = 0;
		return ret;
	}

	public synchronized static int getVideoBuff2Read() {
		int ret;
		if (vpktCnt == 0)
			return -1;

		ret = vpktReadPos;

		return ret;
	}

	public synchronized static AudioPacket getAudioBuff2Read() {
		AudioPacket ret = null;
		if (aupktCnt == 0)
			return null;

		ret = mAudioBuffAry[aupktReadPos];

		return ret;
	}

	public synchronized static AudioPacket setAudioBuff2Readed() {
		AudioPacket ret = null;
		if (aupktCnt == 0)
			return null;

		ret = mAudioBuffAry[aupktReadPos];
		aupktCnt--;
		aupktReadPos++;
		if (aupktReadPos >= maxAudioPktCnt)
			aupktReadPos = 0;
		return ret;
	}

	public synchronized static AudioPacket getAudioBuff2Write() {
		AudioPacket ret = null;
		if (aupktCnt >= maxAudioPktCnt) {
			return null;
		}

		ret = mAudioBuffAry[aupktWtPos];

		return ret;
	}

	public synchronized static AudioPacket setAudioBuff2Writed() {
		AudioPacket ret = null;
		if (aupktCnt >= maxAudioPktCnt) {
			return null;
		}

		ret = mAudioBuffAry[aupktWtPos];
		aupktCnt++;
		aupktWtPos++;
		if (aupktWtPos >= maxAudioPktCnt)
			aupktWtPos = 0;
		return ret;
	}

	public synchronized static AudioPacket getRAudioBuff2Read() {
		AudioPacket ret = null;
		if (auRpktCnt == 0)
			return null;

		ret = mRAudioBuffAry[auRpktReadPos];

		return ret;
	}

	public synchronized static AudioPacket setRAudioBuff2Readed() {
		AudioPacket ret = null;
		if (auRpktCnt == 0)
			return null;

		ret = mRAudioBuffAry[auRpktReadPos];
		auRpktCnt--;
		auRpktReadPos++;
		if (auRpktReadPos >= maxRAudioPktCnt)
			auRpktReadPos = 0;
		return ret;
	}

	public synchronized static AudioPacket getRAudioBuff2Write() {
		AudioPacket ret = null;
		if (auRpktCnt >= maxRAudioPktCnt) {
			return null;
		}

		ret = mRAudioBuffAry[auRpktWtPos];

		return ret;
	}

	public synchronized static AudioPacket setRAudioBuff2Writed() {
		AudioPacket ret = null;
		if (auRpktCnt >= maxRAudioPktCnt) {
			return null;
		}

		ret = mRAudioBuffAry[auRpktWtPos];
		auRpktCnt++;
		auRpktWtPos++;
		if (auRpktWtPos >= maxRAudioPktCnt)
			auRpktWtPos = 0;
		return ret;
	}

	public void initVideoBuffQue() {
		// LogUtils.LOGE(TAG, ">>>initVideoBuffQue ***start**");
		vpktReadPos = 0;
		vpktWtPos = 0;
		vpktCnt = 0;
		mVideoBuffAry = new VideoPacket[maxVideoPktCnt];
		for (int i = 0; i < maxVideoPktCnt; i++) {
			mVideoBuffAry[i] = new VideoPacket();
			mVideoBuffAry[i].frameNo = 0;
			mVideoBuffAry[i].totalPackages = 0;
			mVideoBuffAry[i].lastPackage = 0;
			mVideoBuffAry[i].frameLen = 0;
			// LogUtils.LOGE(TAG, ">>>initVideoBuffQue ***dataBuff**");
			mVideoBuffAry[i].dataBuff = new byte[20 * 1024];
			mVideoBuffAry[i].pktValid = new byte[20];
			// copy header
			CommonUtils.copyBytes(header, mVideoBuffAry[i].dataBuff, 0, 0,
					header.length);
		}
		// LogUtils.LOGE(TAG, ">>>initVideoBuffQue *****");
	}

	public void initAudioBuffQue() {
		aupktReadPos = 0;
		aupktWtPos = 0;
		aupktCnt = 0;
		mAudioBuffAry = new AudioPacket[maxAudioPktCnt];
		for (int i = 0; i < maxAudioPktCnt; i++) {
			mAudioBuffAry[i] = new AudioPacket();
			mAudioBuffAry[i].frameLen = 64;
			mAudioBuffAry[i].dataBuff = new byte[64];
		}
		// LogUtils.LOGE(TAG, ">>>initAudioBuffQue *****");
	}

	public void initRAudioBuffQue() {
		auRpktReadPos = 0;
		auRpktWtPos = 0;
		auRpktCnt = 0;
		mRAudioBuffAry = new AudioPacket[maxRAudioPktCnt];
		for (int i = 0; i < maxRAudioPktCnt; i++) {
			mRAudioBuffAry[i] = new AudioPacket();
			mRAudioBuffAry[i].frameLen = 128;
			mRAudioBuffAry[i].dataBuff = new byte[128];
		}
		// LogUtils.LOGE(TAG, ">>>initAudioBuffQue *****");
	}

	private void doStart() {
		Future future = executorService.submit(new Runnable() {

			@Override
			public void run() {
				mCallStatus = e_call_null;//
				try {
					InetAddress fromIP;
					byte[] buffer = new byte[1500];
					DatagramPacket packet = new DatagramPacket(buffer,
							buffer.length);
					castClass = new MulticastSocket(PORT); // 多点广播
					castClass.joinGroup(InetAddress.getByName(WAITING_GROP));// 使用组播套接字joinGroup(),将其加入到一个组播
					initVideoBuffQue();
					initAudioBuffQue();
					initRAudioBuffQue();

					while (true) {// 循环接收组播消息
						packet.setLength(buffer.length);

						castClass.receive(packet);

						fromIP = packet.getAddress();
						// LogUtils.LOGE(TAG,
						// ">>>rev data from "+fromIP.getHostAddress());
						if (CommonUtils.mLocalIp.equals(fromIP.getHostAddress()))
							continue;
						// 处理消息
						extractData(packet);
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
					LogUtils.LOGE(TAG,
							">>>UnknownHostException" + e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					LogUtils.LOGE(TAG, ">>>IOException" + e.getMessage());
				}
			}
		});
	}

	public void replyVedioCallAsk(String rAddr, String rIp) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtils.LOGE(TAG,
							"++++++++++++++++++++ replyVedioCallAsk-REPLY ++++++++++++++++++++");
					DatagramSocket socket = new DatagramSocket();
					byte[] callanswerData = CommonUtils
							.createVideotalkAskCallanswer(
									// 对讲请求应答
									CommonUtils.getLocalAddr(),
									CommonUtils.getLocalIp(), mDoorAddr,
									mDoorIp);
					DatagramPacket callanswerPacket = new DatagramPacket(
							callanswerData, callanswerData.length, InetAddress
									.getByName(mDoorIp), PORT);
					socket.send(callanswerPacket);

					// 请求应答后,播放来电提示音乐
					Message showDialogMsg = mHandler.obtainMessage();
					showDialogMsg.what = MSG_ID_SHOW_CALLDIALOG; // 发送消息通知有对讲请求
					showDialogMsg.sendToTarget();

					socket = null;
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

	public void replyNsOrder(String rAddr, String rIp) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtils.LOGE(TAG, ">>>>NSORDER-REPLY>>>>");
					byte[] replyData = CommonUtils.createNsorderReply(
							CommonUtils.getLocalAddr(),
							CommonUtils.getLocalIp(), tmpDoorAddr, tmpDoorIp);
					DatagramPacket replyPacket = new DatagramPacket(replyData,
							replyData.length, InetAddress.getByName(tmpDoorIp),
							PORT);
					DatagramSocket socket = new DatagramSocket();
					socket.send(replyPacket);
					socket = null;
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

	public void exitGroup(MulticastSocket sock, String grp) {
		try {
			InetAddress groupAddr;
			groupAddr = InetAddress.getByName(grp);
			sock.leaveGroup(groupAddr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			LogUtils.LOGE(TAG, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			LogUtils.LOGE(TAG, e.getMessage());
		}// 退出组播组
	}

	public void addGroup(MulticastSocket sock, String grp) {
		try {
			InetAddress groupAddr;
			groupAddr = InetAddress.getByName(grp);
			sock.joinGroup(groupAddr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			LogUtils.LOGE(TAG, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			LogUtils.LOGE(TAG, e.getMessage());
		}// 加入组播组
	}

	public void threadSleep(int msTime) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void extractData(DatagramPacket packet) {

		byte[] localData = packet.getData();
		int packetLen = packet.getLength();
		if (packetLen < 56) {
			return;
		}

		byte[] header = new byte[6];
		CommonUtils.copyBytes(localData, header, 0, 0, 6);
		if (!CommonUtils.isHeaderRight(header)) {
			return;
		}

		int cmd = localData[0 + 6] & 0xff;
		int cmdProperty = localData[7] & 0xff;
		int subCmd = localData[8] & 0xff;
		// LogUtils.LOGE(TAG,">>>>>>>cmd="+Integer.toString(cmd)+"ack="+Integer.toString(cmdProperty));
		switch (cmd) {
		case 154:// NSORDER
			if ((cmdProperty & 0x01) != 0) { // 主叫命令
				byte[] roomAddrBytes = new byte[20];
				CommonUtils.copyBytes(localData, roomAddrBytes, 0 + 32, 0, 20);
				if (CommonUtils.getLocalAddr()
						.equals(new String(roomAddrBytes))) {
					byte[] doorAddrBytes = new byte[20];
					byte[] dooIpBytes = new byte[4];
					// 本机编址
					CommonUtils.copyBytes(localData, doorAddrBytes, 0 + 8, 0,
							20);
					// 对方IP
					CommonUtils.copyBytes(localData, dooIpBytes, 0 + 28, 0, 4);
					tmpDoorAddr = new String(doorAddrBytes);
					try {
						tmpDoorIp = InetAddress.getByAddress(dooIpBytes)
								.getHostAddress();
						replyNsOrder(tmpDoorAddr, tmpDoorIp);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
				}
			} else if ((cmdProperty & 0x02) != 0) { // 应答命令
				if (mCallStatus != e_call_null
						|| VideoWatchActivity.mWaitNsReply == false) {
					LogUtils.LOGE(
							TAG,
							">>>>>>>mCallStatus="
									+ Integer.toString(mCallStatus));
					return;
				}

				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(getApplicationContext(),
						VideoWatchActivity.class);
				startActivity(intent);
				LogUtils.LOGE(TAG, ">>>>>>>start watch activity");

				byte[] doorAddrBytes = new byte[20];
				System.arraycopy(localData, 0 + 33, doorAddrBytes, 0, 20);
				byte[] doorIpBytes = new byte[4];
				System.arraycopy(localData, 0 + 53, doorIpBytes, 0, 4);
				// LogUtils.LOGE(TAG, ">>>>>>>> door addr: " + new
				// String(doorAddrBytes));
				try {
					mDoorIp = InetAddress.getByAddress(doorIpBytes)
							.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
					mDoorIp = "";
				}
				CommonUtils.mRemoteIp = mDoorIp;
				mDoorAddr = new String(doorAddrBytes);
				CommonUtils.mRemoteAddr = mDoorAddr;
				VideoWatchActivity.sendVideowatchAskCall();
				VideoWatchActivity.mWaitNsReply = false;
			}
			break;
		case 150:// VIDEOTALK
			switch (subCmd) {
			case 1:// call
				if ((cmdProperty & 0x01) != 0) {
					if (mCallStatus != e_call_null)
						return;
					LogUtils.LOGE(TAG,
							">>>>>>>>>>>>>>>>>>>>> TalkThread started >>>>>>>>>>>>>>>>>>>>");
					byte[] doorAddrBytes = new byte[20];
					byte[] dooIpBytes = new byte[4];
					CommonUtils.copyBytes(localData, doorAddrBytes, 0 + 9, 0,
							20);
					CommonUtils.copyBytes(localData, dooIpBytes, 0 + 29, 0, 4);
					mDoorAddr = new String(doorAddrBytes);
					try {
						mDoorIp = InetAddress.getByAddress(dooIpBytes)
								.getHostAddress();
					} catch (UnknownHostException e) {
						e.printStackTrace();
						mDoorIp = "";
					}// 以上代码取得门口机地址编号和IP

					StringBuilder sb = new StringBuilder();
					sb.append("236.");
					sb.append(mDoorIp.substring(mDoorIp.indexOf(".") + 1,
							mDoorIp.length()));
					talkGroup = sb.toString();// 准备跳转至相应组播组
					addGroup(castClass, talkGroup);
					mCurrentVideoPacket = null;

					Intent intent = new Intent(getApplicationContext(),
							TalkActivity_old.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(intent);

					mCallStatus = e_call_slave_beCall;
					mCallTimout = 0;
					new Thread(new SendConfirmTask()).start();
					// LogUtils.LOGE(TAG,
					// "------------------ VIDEOTALK_ASK_CALL --------------------");
					replyVedioCallAsk(mDoorAddr, mDoorIp);
				}
				break;
			case 6:// Constants.SUB_CMD.CALLSTART.getValue()
					// LogUtils.LOGE(TAG, ">>>>>>>>>>>VIDEOTALK_CALLSTART pkt");
				if (mCallStatus != e_call_slave_beCall)
					return;
				if ((cmdProperty & 0x02) != 0) {
					byte[] callStartIpBytes = new byte[4];
					CommonUtils.copyBytes(localData, callStartIpBytes, 0 + 53,
							0, 4);
					String callStartIp;

					try {
						callStartIp = InetAddress
								.getByAddress(callStartIpBytes)
								.getHostAddress();
						// LogUtils.LOGE(TAG,
						// ">>>>>>>>>>>VIDEOTALK_CALLSTART ip "+callStartIp);
						if (CommonUtils.getLocalIp().equals(callStartIp)) {
							mCallStatus = e_call_slave_beCalling;
							mCallTimout = 0;
						} else {
							if (mMediaPlayer != null
									&& mMediaPlayer.isPlaying()) {
								mMediaPlayer.stop();
								mMediaPlayer.release();
								mMediaPlayer = null;
							}

							if (callDialog.isShowing()) {
								callDialog.dismiss();
							}

							if (this.mTalkActivity != null
									&& mTalkActivity.isShow) {
								mTalkActivity.finish();
								mCallStatus = e_call_null;
								mTalkActivity = null;
							}
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}// 取得接听端的IP
				} else {
					LogUtils.LOGE(TAG,
							">>>>>>>>>>>VIDEOTALK_REPLY_CALLSTART err");
				}
				break;
			case 9:// Constants.SUB_CMD.CALLCONFIRM.getValue()
				if ((cmdProperty & 0x01) != 0) {
					mCallTimout = 0;
				} else if ((cmdProperty & 0x02) != 0)// if(CommonUtils.isReply(cmdPropertyStr))
				{
					mCallTimout = 0;
				}
				break;
			case 30:// Constants.SUB_CMD.CALLEND.getValue()
				if ((cmdProperty & 0x01) != 0) {
					// LogUtils.LOGE(TAG,
					// "++++++++++++++++++++ VIDEOTALK_ASK_CALLEND ++++++++++++++++++++++");
					try {
						byte[] replyCallendData = CommonUtils
								.createVideotalkReplyCallend(
										CommonUtils.getLocalAddr(),
										CommonUtils.getLocalIp(), mDoorAddr,
										mDoorIp);
						DatagramPacket replyCallendPacket = new DatagramPacket(
								replyCallendData, replyCallendData.length,
								InetAddress.getByName(mDoorIp), PORT);
						DatagramSocket socket = new DatagramSocket();
						socket.send(replyCallendPacket);

						// leave group
						Thread.sleep(2000);
						castClass.leaveGroup(InetAddress.getByName(talkGroup));

						Message dismissDialogMsg = mHandler.obtainMessage();
						dismissDialogMsg.what = MSG_ID_DISMISS_CALLDIALOG;
						dismissDialogMsg.sendToTarget();

					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if ((cmdProperty & 0x02) != 0)// if(CommonUtils.isReply(cmdPropertyStr))
				{
					// LogUtils.LOGE(TAG,
					// "+++++++++++++++++++++ VIDEOTALK_REPLY_CALLEND +++++++++++++++++++++");
					// Thread.sleep(2000);
					exitGroup(castClass, talkGroup);

					Message dismissDialogMsg = mHandler.obtainMessage();
					dismissDialogMsg.what = MSG_ID_DISMISS_CALLDIALOG;
					dismissDialogMsg.sendToTarget();
				}
				mCallStatus = e_call_null;
				break;
			case 7:
			case 8:
				if (mCallStatus == e_call_null)
					return;
				extractTalkData(packet);
				break;
			default:
				LogUtils.LOGE(TAG,
						">>>>>>vedio subCmd err: " + Integer.toString(subCmd));
			}
			break;
		case 152:// VIDEOWATCH
			switch (subCmd) {
			case 1:// call
				break;
			case 4:// CALLANSWER
				if (VideoWatchActivity.mWaitWatchCallReply == true) {
					VideoWatchActivity.mWaitWatchCallReply = false;

					StringBuilder sb = new StringBuilder();
					sb.append("236.");
					sb.append(mDoorIp.substring(mDoorIp.indexOf(".") + 1,
							mDoorIp.length()));
					talkGroup = sb.toString();// 准备跳转至相应组播组
					addGroup(castClass, talkGroup);

					mCurrentVideoPacket = null;
					mCallStatus = e_call_host_watch;
					mWatchTimout = 0;
					new Thread(new watchTimerTask()).start();
				}
				break;
			case 9:// CALLCONFIRM
				break;
			case 30:// CALLEND
				mCallStatus = e_call_null;
				exitGroup(castClass, talkGroup);
				break;
			case 7:// CALLUP
			case 8:// CALLDOWN
				if (mCallStatus != e_call_host_watch)
					return;
				extractWatchData(packet);
				break;
			}
			break;
		}
	}

	private void sendVideoWatchAskCallData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] data = CommonUtils.createVideoWatchAskCall(
							CommonUtils.getLocalAddr(),
							CommonUtils.getLocalIp(), mDoorAddr, mDoorIp);
					// LogUtils.LOGE(TAG, CommonUtils.bytesToHexString(data));
					DatagramPacket packet = new DatagramPacket(data,
							data.length, InetAddress.getByName(mDoorIp), PORT);
					DatagramSocket socket = new DatagramSocket();
					socket.send(packet);
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

	public void sendWatchConfirmPkt() {
		LogUtils.LOGE(TAG, ">>>>>>>>>>>>>>>sendWatchConfirmPkt ");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] confirmData = CommonUtils
							.createVideoWatchAskCallconfirm(
									CommonUtils.getLocalAddr(),
									CommonUtils.getLocalIp(), mDoorAddr,
									mDoorIp);
					DatagramPacket confirmPacket = new DatagramPacket(
							confirmData, confirmData.length, InetAddress
									.getByName(mDoorIp), PORT);
					DatagramSocket socket = new DatagramSocket();
					socket.send(confirmPacket);
					LogUtils.LOGE(TAG, ">>>>>>>>>>>>>>>sendWatchConfirmPkt ");
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

	private class watchTimerTask implements Runnable {

		@Override
		public void run() {
			try {
				byte[] confirmData = CommonUtils
						.createVideoWatchAskCallconfirm(
								CommonUtils.getLocalAddr(),
								CommonUtils.getLocalIp(), mDoorAddr, mDoorIp);
				DatagramPacket confirmPacket = new DatagramPacket(confirmData,
						confirmData.length, InetAddress.getByName(mDoorIp),
						PORT);
				DatagramSocket socket = new DatagramSocket();

				while (mCallStatus != e_call_null) {
					Thread.sleep(1000);
					socket.send(confirmPacket);

					Message timerMsg = mHandler.obtainMessage();
					timerMsg.what = MSG_ID_TIMER_FOR_WATCH;
					timerMsg.sendToTarget();
				}
				LogUtils.LOGE(TAG, ">>>>>>>>>>>>>>>watchTimerTask over");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private class SendConfirmTask implements Runnable {

		@Override
		public void run() {
			try {
				byte[] confirmData = CommonUtils.createVideotalkAskCallconfirm(
						CommonUtils.getLocalAddr(), CommonUtils.getLocalIp(),
						mDoorAddr, mDoorIp);
				DatagramPacket callconfirmPacket = new DatagramPacket(
						confirmData, confirmData.length,
						InetAddress.getByName(mDoorIp), PORT);
				DatagramSocket socket = new DatagramSocket();
				while (mCallStatus != e_call_null) {
					if (mCallStatus == e_call_slave_beCalling) {
						socket.send(callconfirmPacket);
						LogUtils.LOGE(TAG, ">>>>send confirm pkt to "
								+ mDoorAddr + " " + mDoorIp);
					}
					Thread.sleep(1000);

					Message timerMsg = mHandler.obtainMessage();
					timerMsg.what = MSG_ID_TIMER_FOR_CALL;
					timerMsg.sendToTarget();
				}
				socket = null;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	synchronized private void extractWatchData(DatagramPacket packet) {
		int packetLen = packet.getLength();
		byte[] localData = packet.getData();// new byte[packetLen];
		// System.arraycopy(packet.getData().clone(), 0, localData, 0,
		// packetLen);

		int cmdProperty = localData[0 + 7] & 0xff;
		int subCmd = localData[0 + 8] & 0xff;
		if (packetLen < 68)
			return;
		// LogUtils.LOGE(TAG,">>>>>>>subCmd="+Integer.toString(subCmd));
		byte[] talkdata = new byte[68];
		System.arraycopy(localData, 0 + 9, talkdata, 0, talkdata.length);
		// int timestamp = (talkdata[0+48] & 0xff) | ((talkdata[1+48]<<8)) |
		// ((talkdata[2+48]<<16)) | ((talkdata[3+48]<<24));

		int dataType = (talkdata[0 + 52] & 0xff) | (talkdata[1 + 52] << 8);
		int frameNo = (talkdata[0 + 54] & 0xff) | ((talkdata[1 + 54] << 8));
		int frameLen = (talkdata[0 + 56] & 0xff) | (talkdata[1 + 56] << 8);
		int totalPackages = (talkdata[0 + 60] & 0xff)
				| ((talkdata[1 + 60] << 8));
		int currPackage = (talkdata[0 + 62] & 0xff) | ((talkdata[1 + 62] << 8));
		int dataLength = (talkdata[0 + 64] & 0xff) | ((talkdata[1 + 64] << 8));
		int packLen = (talkdata[0 + 66] & 0xff) | ((talkdata[1 + 66] << 8));
		if (dataType == 2) {
			if (mCurrentVideoPacket == null) {
				// 第一个视频帧
				if (currPackage == 1) {
					int i = TalkService.getVideoBuff2Write();
					if (i == -1) {
						mCurrentVideoPacket = null;
						return;
					}
					mCurrentVideoPacket = TalkService.mVideoBuffAry[i];
					mCurrentVideoPacket.frameNo = frameNo;
					mCurrentVideoPacket.totalPackages = totalPackages;
					mCurrentVideoPacket.lastPackage = currPackage;
					mCurrentVideoPacket.frameLen = frameLen + header.length
							+ tail.length;
					if (mCurrentVideoPacket.frameLen > (20 * 1024)) {
						mCurrentVideoPacket = null;
						LogUtils.LOGE(TAG, ">>>>>>>>>>>>> frameLen>(20*1024)");
						return;
					}
					mCurrentVideoPacket.pktValid[currPackage - 1] = 1;
					CommonUtils.copyBytes(packet.getData(),
							mCurrentVideoPacket.dataBuff, 86, header.length,
							dataLength);
				}
			} else {
				// 非第一个视频帧
				if (mCurrentVideoPacket.frameNo == frameNo) {
					if (currPackage <= mCurrentVideoPacket.totalPackages
							&& currPackage > 0) {
						mCurrentVideoPacket.pktValid[currPackage - 1] = 1;
						CommonUtils.copyBytes(packet.getData(),
								mCurrentVideoPacket.dataBuff, 86, header.length
										+ (currPackage - 1) * 1200, dataLength);
					} else {
						mCurrentVideoPacket = null;
						return;
					}
				} else if (mCurrentVideoPacket.frameNo != frameNo) {
					// 新的视频帧收到
					// copy tail
					mCurrentVideoPacket.dataBuff[mCurrentVideoPacket.frameLen - 2] = tail[0];
					mCurrentVideoPacket.dataBuff[mCurrentVideoPacket.frameLen - 1] = tail[1];
					if (this.ChkVideoBuffWriteOK()) {
						TalkService.setVideoBuff2Writed();
						mCurrentVideoPacket = null;
					}

					if (currPackage == 1) {
						int i = TalkService.getVideoBuff2Write();
						if (i == -1) {
							mCurrentVideoPacket = null;
							return;
						}
						mCurrentVideoPacket = TalkService.mVideoBuffAry[i];
						mCurrentVideoPacket.frameNo = frameNo;
						mCurrentVideoPacket.totalPackages = totalPackages;
						mCurrentVideoPacket.lastPackage = currPackage;
						mCurrentVideoPacket.frameLen = frameLen + header.length
								+ tail.length;
						if (mCurrentVideoPacket.frameLen > (20 * 1024)) {
							mCurrentVideoPacket = null;
							LogUtils.LOGE(TAG,
									">>>>>>>>>>>>> frameLen>(20*1024)");
							return;
						}
						mCurrentVideoPacket.pktValid[currPackage - 1] = 1;
						CommonUtils.copyBytes(packet.getData(),
								mCurrentVideoPacket.dataBuff, 86,
								header.length, dataLength);
					}
				}
			}
		}
	}

	synchronized private void extractTalkData(DatagramPacket packet) {
		int packetLen = packet.getLength();
		byte[] localData = packet.getData();
		// System.arraycopy(packet.getData().clone(), 0, localData, 0,
		// packetLen);
		int cmdProperty = localData[7] & 0xff;
		if ((cmdProperty & 0x01) != 0) {
			int dataType = (localData[9 + 52] & 0xff)
					| (localData[9 + 53] << 8);

			if (dataType == 1 && TalkActivity_old.mBoolAudioPlaying) {
				mCurrentAudioPacket = TalkService.getAudioBuff2Write();
				if (mCurrentAudioPacket == null)
					return;
				System.arraycopy(localData, 86, mCurrentAudioPacket.dataBuff,
						0, 64);
				TalkService.setAudioBuff2Writed();
				// LogUtils.LOGE(TAG,
				// ">>>>>>>>>>>>> 接到音频帧 >>>>>>>>>>>>>, 音频帧长度: " + dataLength +
				// ", 缓冲区长度： " + TalkActivity.mAudioQueue.getQueuelSize());
				return;
			}

			if (dataType == 2 && TalkActivity_old.isShow) {
				int frameNo = (localData[9 + 54] & 0xff)
						| ((localData[9 + 55] << 8));
				int frameLen = (localData[9 + 56] & 0xff)
						| (localData[9 + 57] << 8);
				int totalPackages = (localData[9 + 60] & 0xff)
						| ((localData[9 + 61] << 8));
				int currPackage = (localData[9 + 62] & 0xff)
						| ((localData[9 + 63] << 8));
				int dataLength = (localData[9 + 64] & 0xff)
						| ((localData[9 + 65] << 8));
				int packLen = (localData[9 + 66] & 0xff)
						| ((localData[9 + 67] << 8));

				if (mCurrentVideoPacket == null) {
					// 第一个视频帧
					if (currPackage == 1) {
						int i = TalkService.getVideoBuff2Write();
						if (i == -1) {
							mCurrentVideoPacket = null;
							LogUtils.LOGE(TAG,
									">>>>>>>>>>>>> getVideoBuff2Write -1");
							return;
						}
						mCurrentVideoPacket = TalkService.mVideoBuffAry[i];
						mCurrentVideoPacket.frameNo = frameNo;
						mCurrentVideoPacket.totalPackages = totalPackages;
						mCurrentVideoPacket.lastPackage = currPackage;
						mCurrentVideoPacket.frameLen = frameLen + header.length
								+ tail.length;
						if (mCurrentVideoPacket.frameLen > (20 * 1024)) {
							mCurrentVideoPacket = null;
							LogUtils.LOGE(TAG,
									">>>>>>>>>>>>> frameLen>(20*1024)");
							return;
						}
						mCurrentVideoPacket.pktValid[currPackage - 1] = 1;
						// LogUtils.LOGE(TAG,
						// ">>>>>>>>>>>>> getVideo 1st frm "+Integer.toString(dataLength));
						CommonUtils.copyBytes(packet.getData(),
								mCurrentVideoPacket.dataBuff, 86,
								header.length, dataLength);
					}
				} else {
					// 非第一个视频帧
					if (mCurrentVideoPacket.frameNo == frameNo) {
						if (currPackage <= mCurrentVideoPacket.totalPackages
								&& currPackage > 0) {
							mCurrentVideoPacket.pktValid[currPackage - 1] = 1;
							// LogUtils.LOGE(TAG, ">>>>>>>>>>>>> getVideopkt "+
							// currPackage + " "+Integer.toString(dataLength));
							CommonUtils.copyBytes(packet.getData(),
									mCurrentVideoPacket.dataBuff, 86,
									header.length + (currPackage - 1) * 1200,
									dataLength);
						} else {
							mCurrentVideoPacket = null;
							LogUtils.LOGE(TAG, ">>>>>>>>>>>>> currPackage err "
									+ currPackage);
							return;
						}
					} else if (mCurrentVideoPacket.frameNo != frameNo) {
						// 新的视频帧收到
						// copy tail
						mCurrentVideoPacket.dataBuff[mCurrentVideoPacket.frameLen - 2] = tail[0];
						mCurrentVideoPacket.dataBuff[mCurrentVideoPacket.frameLen - 1] = tail[1];
						if (this.ChkVideoBuffWriteOK())
						// if(true)
						{
							TalkService.setVideoBuff2Writed();
							mCurrentVideoPacket = null;
							// LogUtils.LOGE(TAG,
							// ">>>>>>>>>>>>> setVideoBuff2Writed ");
						} else {
							LogUtils.LOGE(TAG, ">>>>>>>>>>>>> chk err ");
						}

						if (currPackage == 1) {
							int i = TalkService.getVideoBuff2Write();
							if (i == -1) {
								mCurrentVideoPacket = null;
								LogUtils.LOGE(TAG,
										">>>>>>>>>>>>> getVideoBuff err 2 ");
								return;
							}
							mCurrentVideoPacket = TalkService.mVideoBuffAry[i];
							mCurrentVideoPacket.frameNo = frameNo;
							mCurrentVideoPacket.totalPackages = totalPackages;
							mCurrentVideoPacket.lastPackage = currPackage;
							mCurrentVideoPacket.frameLen = frameLen
									+ header.length + tail.length;
							if (mCurrentVideoPacket.frameLen > (20 * 1024)) {
								mCurrentVideoPacket = null;
								LogUtils.LOGE(TAG,
										">>>>>>>>>>>>> frameLen>(20*1024)");
								return;
							}
							mCurrentVideoPacket.pktValid[currPackage - 1] = 1;
							// LogUtils.LOGE(TAG,
							// ">>>>>>>>>>>>> getVideoBuff 1st frm 2 "+
							// Integer.toString(dataLength));
							CommonUtils.copyBytes(packet.getData(),
									mCurrentVideoPacket.dataBuff, 86,
									header.length, dataLength);
						}
					}
				}
			}
		}

	}

	private Future submitTask(Runnable task) {
		Future result = null;
		if (!this.executorService.isTerminated()
				&& !this.executorService.isShutdown() && task != null) {
			result = executorService.submit(task);
		}
		return result;
	}

}
