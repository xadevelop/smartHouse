package com.example.smarthouse;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import android.os.Environment;
import android.text.StaticLayout;
import android.util.Log;
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

public class TalkActivity extends AdAbstractActivity {

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

    private final int SPEEX_FRAME_BYTE = 128;
    private File fpath;
    private File audioFile;

    DataOutputStream dos = null;

    public native void speex_EchoCanceller_open(int sampleRate, int bufSiz, int totalSize);

    public native void speex_EchoCanceller_close();

    public native short[] speex_EchoCanceller_process(short[] input_frame, short[] echo_frame);

    static {
        System.loadLibrary("speex_jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        talkLayout = (FrameLayout) layoutInflater.inflate(R.layout.activity_talk, null);
        mRootView.addView(talkLayout, FF);

        mVideoView = new VideoPlayView(this);
        talkLayout.addView(mVideoView, FF);

        initComponent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mG711Coder = new G711Coder();

        speex_EchoCanceller_open(8000, 64, 1000);

        TalkService.mTalkActivity = this;
        if (startRecord()) {
            startSoundPlay();
        }
        
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

        speex_EchoCanceller_close();
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
                    byte[] openlockData =
                            CommonUtils.createOpenLock(CommonUtils.getLocalAddr(),
                                    CommonUtils.getLocalIp(), TalkService.mDoorAddr,
                                    TalkService.mDoorIp);
                    DatagramPacket openLockPacket =
                            new DatagramPacket(openlockData, openlockData.length,
                                    InetAddress.getByName(TalkService.mDoorIp), TalkService.PORT);
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
                            byte[] askCallEnd =
                                    CommonUtils.createVideotalkAskCallend(
                                            CommonUtils.getLocalAddr(), CommonUtils.getLocalIp(),
                                            TalkService.mDoorAddr, TalkService.mDoorIp);
                            DatagramPacket askCallendPakcet =
                                    new DatagramPacket(askCallEnd, askCallEnd.length,
                                            InetAddress.getByName(TalkService.mDoorIp),
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
                stopRecord();
                stopSoundPlay();

                finish();
                break;

            default:
                break;
        }
    }

    public void setSpeekModle() {
        try {
            Context mContext = this;
            AudioManager audioManager =
                    (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);

                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-access")
	@SuppressLint("NewApi")
    private void startSoundPlay() {
        setSpeekModle();
        playBufferSize =
                AudioTrack.getMinBufferSize(mFrequency, mChannelConfiguration, mAudioEncoding);
        // mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mFrequency,
        // mChannelConfiguration, mAudioEncoding, playBufferSize*10,
        // AudioTrack.MODE_STREAM);
        mAudioTrack =
                new AudioTrack(AudioManager.STREAM_MUSIC, mFrequency, mChannelConfiguration,
                        mAudioEncoding, playBufferSize * 10, AudioTrack.MODE_STREAM, audioSessionId);
        // mAudioTrack.setStereoVolume(mAudioTrack.getMaxVolume(),
        // mAudioTrack.getMaxVolume());
        // mAudioTrack.getMaxVolume();
		mAudioTrack.setStereoVolume(mAudioTrack.getMaxVolume(),
				mAudioTrack.getMaxVolume());

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
        recordBufferSize =
                AudioRecord.getMinBufferSize(mRecordFrequency, mChannelInConfiguration,
                        mAudioEncoding);

        mAudioRecorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, mRecordFrequency,
                        mChannelInConfiguration, mAudioEncoding, recordBufferSize * 20);

       /* audioSessionId = mAudioRecorder.getAudioSessionId();

        echoCanceler = AcousticEchoCanceler.create(audioSessionId);
        if (AcousticEchoCanceler.isAvailable()) {
            echoCanceler.setEnabled(true);
        } else {
            return false;
        }

        mNoisePress = NoiseSuppressor.create(audioSessionId);
        if (NoiseSuppressor.isAvailable()) {
            mNoisePress.setEnabled(true);
        } else {
            return false;
        }

        mAudioAgc = AutomaticGainControl.create(audioSessionId);
        if (AutomaticGainControl.isAvailable()) {
            mAudioAgc.setEnabled(true);
        } else {
            return false;
        }*/

        mRecordQueue = new AudioQueue();

        mBoolAudioRecording = true;

        mSoundRecordThread = new SoundRecordThread();
        mSoundSendThread = new SoundSendThread();
        // mSoundRecordThread.start();
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
        if (mAudioRecorder != null) {
            mAudioRecorder.stop();
            mAudioRecorder.release();
            mAudioRecorder = null;
        }
    }

    private void stopSoundPlay() {
        if (null != mSoundPlayThread) {
            mBoolAudioPlaying = false;
            mSoundPlayThread.interrupt();
            mSoundPlayThread = null;
        }

        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    void record() {
        try {
            // 开通输出流到指定的文件
            DataOutputStream dos =
                    new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));
            // 根据定义好的几个配置，来获取合适的缓冲大小
            /*
             * int bufferSize = AudioRecord.getMinBufferSize(mFrequency, mChannelConfiguration,
             * mAudioEncoding);
             */

            // 定义缓冲
            short[] buffer = new short[recordBufferSize];

            // 开始录制
            mAudioRecorder.startRecording();
            while (mBoolAudioRecording) {
                // 从bufferSize中读取字节，返回读取的short个数
                // 这里老是出现buffer overflow，不知道是什么原因，试了好几个值，都没用，TODO：待解决
                int bufferReadResult = mAudioRecorder.read(buffer, 0, buffer.length);
                Log.e("bufferReadResult", bufferReadResult + "");
                // 循环将buffer中的音频数据写入到OutputStream中
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.writeShort(buffer[i]);
                }
            }
            // 录制结束
            mAudioRecorder.stop();
            Log.v("The DOS available:", "::" + audioFile.length());
            dos.close();
        } catch (Exception e) {
            // TODO: handle exception
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
                byte[] szOut = new byte[SPEEX_FRAME_BYTE];
                int dataLen = 0;

                while (mBoolAudioPlaying) {
                    // LogUtils.LOGE(TAG, ">>>>>>>>>>>>>>>>>>>> 当前的音频缓冲包个数： "
                    // + mAudioQueue.getQueuelSize());
                    // audioPacket = mAudioQueue.getQueueFirstData();

                    audioPacket = TalkService.getAudioBuff2Read();
                    if (audioPacket != null) {
                        dataLen =
                                mG711Coder.G711DecodeAlawToLinear(szOut, audioPacket.dataBuff, 0,
                                        audioPacket.frameLen);
                        if (dataLen != 0) {
                            Log.e("收到音频长度", dataLen + "----" + audioPacket.frameLen + "");
                            // Log.e("szOut", Bytes2HexString(szOut, dataLen));
                            mAudioTrack.write(szOut, 0, dataLen);
                            TalkService.setAudioBuff2Readed();

                            // 先播放，再录音，消除回音
                            short[] recvShorts, echoShorts;
                            // convert bytes to shorts
                            recvShorts = new short[dataLen / 2];
                            ByteBuffer.wrap(szOut, 0, dataLen).order(ByteOrder.LITTLE_ENDIAN)
                                    .asShortBuffer().get(recvShorts);
                            // 录音
                            int bufflen = SPEEX_FRAME_BYTE / 2;
                            short[] audioShorts = new short[bufflen];
                            mAudioRecorder.startRecording();
                            int len = mAudioRecorder.read(audioShorts, 0, bufflen);
                            if (len != bufflen) {
                                try {
                                    Log.e("", "录音为空");
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // 消除回音
                                echoShorts = speex_EchoCanceller_process(audioShorts, recvShorts); // 加入队列
                                calc1(echoShorts, 0, echoShorts.length);
                                AudioPacket packet;
                                packet = TalkService.getRAudioBuff2Write();
                                if (packet != null && echoShorts != null) {
                                    CommonUtils.shortAry2byteAry(packet.dataBuff, echoShorts,
                                            echoShorts.length);
                                    /*
                                     * for (int i = 0; i < echoShorts.length; i++) {
                                     * dos.writeShort(echoShorts[i]); }
                                     */
                                }
                                TalkService.setRAudioBuff2Writed();
                            }
                        }
                    }
                }

                mBoolPlayThreadStop = true;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    // 从字节数组到十六进制字符串转换
    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    public static String Bytes2HexString(byte[] b, int size) {

        byte[] buff = new byte[2 * size];

        for (int i = 0; i < size; i++) {

            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
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

            try {

                /*
                 * // 开通输出流到指定的文件 dos = new DataOutputStream( new BufferedOutputStream( new
                 * FileOutputStream(audioFile)));
                 */

                while (mBoolAudioRecording) {
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
                        // length = mAudioRecorder.read(packet.dataBuff, 0,
                        // 128);
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
                            for (int i = 0; i < buff.length; i++) {
                                dos.writeShort(buff[i]);
                            }
                        }
                    }
                    // packet.frameLen = length;
                    // mRecordQueue.addAudioPacket(packet);
                }

                mBoolRecordThreadStop = true;

            } catch (Exception e) {
                // TODO: handle exception
            }
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
                byte[] sendData = new byte[SPEEX_FRAME_BYTE];
                int dataLen = 0;
                byte[] sendPacket = new byte[1500];
                int pktLen = 0;
                DatagramPacket dataPakcet =
                        new DatagramPacket(sendPacket, sendPacket.length,
                                InetAddress.getByName(TalkService.mDoorIp), 8302);
                while (mBoolAudioRecording) {
                    // packet = mRecordQueue.getQueueFirstData();
                    packet = TalkService.getRAudioBuff2Read();
                    if (packet != null) {
                        dataLen =
                                mG711Coder.G711EncodeLinerToAlaw(sendData, packet.dataBuff,
                                        packet.frameLen, new byte[] {});
                        TalkService.setRAudioBuff2Readed();

                        LogUtils.LOGE(TAG, "--------> sendData length: " + sendData.length);
                        if (dataLen != 0) {
                            pktLen =
                                    CommonUtils.createVideoTalkAskCalldown(sendPacket,
                                            CommonUtils.getLocalAddr(), CommonUtils.getLocalIp(),
                                            TalkService.mDoorAddr, TalkService.mDoorIp,
                                            (int) (System.currentTimeMillis() - startTime), 1,
                                            ++frameNo, 64, 1, 1, 64, 150, sendData);
                            // DatagramPacket dataPakcet = new
                            // DatagramPacket(sendPacket, sendPacket.length,
                            // InetAddress.getByName(TalkService.mDoorIp),
                            // 8302);
                            dataPakcet.setData(sendPacket, 0, pktLen);
                            socket.send(dataPakcet);
                        }
                    } else {
                        try {
                            Log.e("", "send buff null");
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
