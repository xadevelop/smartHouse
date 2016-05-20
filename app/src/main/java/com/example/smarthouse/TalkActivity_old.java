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

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.audio.G711Coder;
import com.audio.VideoPlayView;
import com.pullmi.common.CommonUtils;
import com.pullmi.net.AudioPacket;
import com.pullmi.net.AudioQueue;
import com.pullmi.service.TalkService;
import com.pullmi.utils.LogUtils;

public class TalkActivity_old extends AdAbstractActivity {

	private static final String TAG = "TalkActivity";

	private ExecutorService executorService = Executors.newCachedThreadPool();

	// audio
	public static boolean mBoolAudioPlaying = false;
	private boolean mBoolAudioRecording = false;
	public static AudioQueue mAudioQueue = null;
	private AudioQueue mRecordQueue = null;
	private SoundPlayThread mSoundPlayThread = null;
	private SoundRecordThread mSoundRecordThread = null;
	private SoundSendThread mSoundSendThread = null;
	private boolean mBoolPlayThreadStop = false;
	private boolean mBoolRecordThreadStop = false;
	private G711Coder mG711Coder = null;
	private AudioTrack mAudioTrack = null;
	private AudioRecord mAudioRecorder = null;
	private final int mFrequency = 8000;
	private final int mRecordFrequency = 8000;
	private final int mChannelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
	private final int mChannelInConfiguration = AudioFormat.CHANNEL_IN_MONO;
	private final int mAudioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private int playBufferSize = 0;
	private int recordBufferSize = 0;

	int audioSessionId;

	AcousticEchoCanceler echoCanceler;
	AutomaticGainControl mAudioAgc;
	NoiseSuppressor mNoisePress;

	private FrameLayout talkLayout;

	private ImageView contactIv;
	private ImageView myIv;

	public static boolean isShow = false;

	MulticastSocket broadcastClass;

	public static VideoPlayView mVideoView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		talkLayout = (FrameLayout) layoutInflater.inflate(
				R.layout.activity_talk, null);
		mRootView.addView(talkLayout, FF);

		mVideoView = new VideoPlayView(this);
		talkLayout.addView(mVideoView, FF);

		initComponent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mG711Coder = new G711Coder();

		//TalkService.mTalkActivity = this;
		/*
		 * if (startRecord()) { startSoundPlay(); }
		 */
		startRecord();
		startSoundPlay();

		mVideoView.startPlay();
		isShow = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		isShow = false;
		stopRecord();
		mVideoView.stopPlay();
		stopSoundPlay();
		TalkService.mTalkActivity = null;
		mG711Coder = null;
	}

	private void initComponent() {
		contactIv = (ImageView) findViewById(R.id.contactIv);
		myIv = (ImageView) findViewById(R.id.myIv);

		setTextForBackBtn(getString(R.string.activity_talk_open_door));
		setTextForRightBtn(getString(R.string.activity_talk_call_down));
		setTextForTitle(getString(R.string.activity_talk_title));
	}

	private void openDoor() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] openlockData = CommonUtils.createOpenLock(
							CommonUtils.getLocalAddr(),
							CommonUtils.getLocalIp(), TalkService.mDoorAddr,
							TalkService.mDoorIp);
					DatagramPacket openLockPacket = new DatagramPacket(
							openlockData, openlockData.length,
							InetAddress.getByName(TalkService.mDoorIp),
							TalkService.PORT);
					DatagramSocket socket = new DatagramSocket();
					socket.send(openLockPacket);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_btn4:
			// finish();
			openDoor();
			break;

		case R.id.title_btn1:
			TalkService.mCallStatus = TalkService.e_call_null;
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						byte[] askCallEnd = CommonUtils
								.createVideotalkAskCallend(
										CommonUtils.getLocalAddr(),
										CommonUtils.getLocalIp(),
										TalkService.mDoorAddr,
										TalkService.mDoorIp);
						DatagramPacket askCallendPakcet = new DatagramPacket(
								askCallEnd, askCallEnd.length, InetAddress
										.getByName(TalkService.mDoorIp),
								TalkService.PORT);
						DatagramSocket socket = new DatagramSocket();
						socket.send(askCallendPakcet);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (SocketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			finish();
			break;

		default:
			break;
		}
	}

	public void setSpeekModle() {
		try {
			Context mContext = this;
			AudioManager audioManager = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setMode(AudioManager.ROUTE_SPEAKER);

			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setSpeakerphoneOn(true);

				audioManager
						.setStreamVolume(
								AudioManager.STREAM_VOICE_CALL,
								audioManager
										.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
								AudioManager.STREAM_VOICE_CALL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	private void startSoundPlay() {

		playBufferSize = AudioTrack.getMinBufferSize(mFrequency,
				mChannelConfiguration, mAudioEncoding);
		// 音频数据的采样率8k,单声道,音频数据块是16位
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mFrequency,
				mChannelConfiguration, mAudioEncoding, playBufferSize * 10,
				AudioTrack.MODE_STREAM, audioSessionId);
		// setSpeekModle();
		mAudioTrack.setStereoVolume(mAudioTrack.getMaxVolume(),
				mAudioTrack.getMaxVolume());
		LogUtils.LOGE(TAG, "最大音量:" + mAudioTrack.getMaxVolume());

		Context mContext = this;
		AudioManager audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.STREAM_MUSIC);
		audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,
				AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
		audioManager.adjustVolume(AudioManager.ADJUST_RAISE,
				AudioManager.FLAG_PLAY_SOUND);
		mAudioQueue = new AudioQueue();

		mBoolAudioPlaying = true;

		mSoundPlayThread = new SoundPlayThread();
		mSoundPlayThread.start();
	}

	@SuppressLint("NewApi")
	private boolean startRecord() {
		recordBufferSize = AudioRecord.getMinBufferSize(mRecordFrequency,
				mChannelInConfiguration, mAudioEncoding);
		mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mRecordFrequency, mChannelInConfiguration, mAudioEncoding,
				recordBufferSize * 20);
		mRecordQueue = new AudioQueue();

		mBoolAudioRecording = true;

		mSoundRecordThread = new SoundRecordThread(); // 录音线程
		mSoundSendThread = new SoundSendThread(); // 发送音频线程
		mSoundRecordThread.start();
		mSoundSendThread.start();

		return true;
	}

	private void stopRecord() {
		mBoolAudioRecording = false;

		if (echoCanceler != null) {
			echoCanceler.setEnabled(false);
			echoCanceler.release();
			echoCanceler = null;
		}

		if (mNoisePress != null) {
			mNoisePress.setEnabled(false);
			mNoisePress.release();
			mNoisePress = null;
		}

		if (mAudioAgc != null) {
			mAudioAgc.setEnabled(false);
			mAudioAgc.release();
			mAudioAgc = null;
		}

		mAudioRecorder.stop();
		mAudioRecorder.release();
		mAudioRecorder = null;
	}

	private void stopSoundPlay() {
		if (null != mSoundPlayThread) {
			mBoolAudioPlaying = false;
			mSoundPlayThread.interrupt();
			mSoundPlayThread = null;
		}
		while (!mBoolPlayThreadStop) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
	}

	class SoundPlayThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				mBoolPlayThreadStop = false;
				mAudioTrack.play();
				AudioPacket audioPacket;
				byte[] szOut = new byte[256];
				int dataLen = 0;
				while (mBoolAudioPlaying) {
					// LogUtils.LOGE(TAG, ">>>>>>>>>>>>>>>>>>>> 当前的音频缓冲包个数： "
					// + mAudioQueue.getQueuelSize());
					// audioPacket = mAudioQueue.getQueueFirstData();
					audioPacket = TalkService.getAudioBuff2Read();
					if (audioPacket != null) {
						// byte[] sourceData = new
						// byte[audioPacket.dataBuff.length];
						// System.arraycopy(audioPacket.dataBuff, 0, sourceData,
						// 0, audioPacket.dataBuff.length);
						// szOut = mG711Coder.G711DecodeAlawToLinear(sourceData,
						// 0, audioPacket.dataBuff.length);
						dataLen = mG711Coder.G711DecodeAlawToLinear(szOut,
								audioPacket.dataBuff, 0, audioPacket.frameLen);
						if (dataLen != 0) {
							mAudioTrack.write(szOut, 0, dataLen);
						}
						TalkService.setAudioBuff2Readed();
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				mBoolPlayThreadStop = true;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	class SoundRecordThread extends Thread {
		@Override
		public void run() {
			super.run();
			mAudioRecorder.startRecording();
			short[] buff = new short[64];
			int length = 0;
			mBoolRecordThreadStop = false;
			AudioPacket packet;
			while (mBoolAudioRecording) {
				//
				// packet = new AudioPacket();
				// packet.dataBuff = new byte[128];
				packet = TalkService.getRAudioBuff2Write();
				if (packet == null) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				} else {
					// length = mAudioRecorder.read(packet.dataBuff, 0, 128);
					length = mAudioRecorder.read(buff, 0, 64);
					// calc1((short[])(packet.dataBuff), 0, 64);
					// System.arraycopy(buff, 0, packet.dataBuff, 0, 128);

					if (length != 64) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						calc1(buff, 0, 64);
						CommonUtils.shortAry2byteAry(packet.dataBuff, buff, 64);
						TalkService.setRAudioBuff2Writed();
					}
				}
				// packet.frameLen = length;
				// mRecordQueue.addAudioPacket(packet);
			}
			mBoolRecordThreadStop = true;
		}
	}

	void calc1(short[] lin, int off, int len) {
		int i, j;
		for (i = 0; i < len; i++) {
			j = lin[i + off];
			lin[i + off] = (short) (j >> 2);
		}
	}

	class SoundSendThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				DatagramSocket socket = new DatagramSocket();
				long startTime = System.currentTimeMillis();
				int frameNo = 0;
				AudioPacket packet;
				byte[] sendData = new byte[256];
				int dataLen = 0;
				byte[] sendPacket = new byte[1500];
				int pktLen = 0;
				DatagramPacket dataPakcet = new DatagramPacket(sendPacket,
						sendPacket.length,
						InetAddress.getByName(TalkService.mDoorIp), 8302);
				while (mBoolAudioRecording) {
					// packet = mRecordQueue.getQueueFirstData();
					packet = TalkService.getRAudioBuff2Read();
					if (packet != null) {
						dataLen = mG711Coder
								.G711EncodeLinerToAlaw(sendData,
										packet.dataBuff, packet.frameLen,
										new byte[] {});
						TalkService.setRAudioBuff2Readed();
						// LogUtils.LOGE(TAG, "--------> sendData length: " +
						// sendData.length);
						if (dataLen != 0) {
							pktLen = CommonUtils
									.createVideoTalkAskCalldown(
											sendPacket,
											CommonUtils.getLocalAddr(),
											CommonUtils.getLocalIp(),
											TalkService.mDoorAddr,
											TalkService.mDoorIp,
											(int) (System.currentTimeMillis() - startTime),
											1, ++frameNo, 64, 1, 1, 64, 150,
											sendData);
							// DatagramPacket dataPakcet = new
							// DatagramPacket(sendPacket, sendPacket.length,
							// InetAddress.getByName(TalkService.mDoorIp),
							// 8302);
							dataPakcet.setData(sendPacket, 0, pktLen);
							socket.send(dataPakcet);
						}
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
