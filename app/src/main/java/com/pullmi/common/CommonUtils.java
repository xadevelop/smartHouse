package com.pullmi.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.Attributes.Name;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.ColorMatrixColorFilter;
import android.inputmethodservice.Keyboard;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract.Contacts.Data;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.example.smarthouse.MyApplication;
import com.example.smarthouse.R.string;
import com.pullmi.app.GlobalVars;
import com.pullmi.entity.GPIO_INFO;
import com.pullmi.entity.GPIO_INPUT_INFO;
import com.pullmi.entity.GPIO_OUTPUT_INFO;
import com.pullmi.entity.RUN_DEV_ITEM;
import com.pullmi.entity.RUN_IOOUT_ITEM;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.UnitNode;
import com.pullmi.entity.WareBoardChnout;
import com.pullmi.entity.WareBoardKeyInput;
import com.pullmi.entity.WareChnOpItem;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareEnvDevItem;
import com.pullmi.entity.WareFreshAir;
import com.pullmi.entity.WareKeyOpItem;
import com.pullmi.entity.WareLight;
import com.pullmi.entity.WareLock;
import com.pullmi.entity.WareOther;
import com.pullmi.entity.WareSceneDevItem;
import com.pullmi.entity.WareSceneEvent;
import com.pullmi.entity.WareSetBox;
import com.pullmi.entity.WareTimerDevItem;
import com.pullmi.entity.WareTv;
import com.pullmi.entity.WareValve;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.utils.LogUtils;

public class CommonUtils {

	private static final String TAG = CommonUtils.class.getName();

	public static String mLocalAddr = "";
	public static String mRemoteAddr = "";
	public static String mRemoteIp = "";
	public static String mLocalIp = "";
	public static int m_bSoftVerOld = 0;

	public static void setRemoteAddr(String addr) {
		mRemoteAddr = addr;
	}

	public static void setRemoteIp(String Ip) {
		mRemoteIp = Ip;
	}

	public static String byteToBit(byte b) {
		return "" + ((b >> 7) & 0x1) + ((b >> 6) & 0x1) + ((b >> 5) & 0x1) + ((b >> 4) & 0x1) + ((b >> 3) & 0x1)
				+ ((b >> 2) & 0x1) + ((b >> 1) & 0x1) + ((b >> 0) & 0x1);
	}

	public static boolean isAsk(String str) {
		String s1 = str.substring(str.length() - 2, str.length() - 1);
		String s2 = str.substring(str.length() - 1, str.length());

		return ("0".equals(s1) && "1".equals(s2)) ? true : false;
	}

	public static boolean isReply(String str) {
		String s1 = str.substring(str.length() - 2, str.length() - 1);
		String s2 = str.substring(str.length() - 1, str.length());

		return ("1".equals(s1) && "0".equals(s2)) ? true : false;
	}

	public static void CommonUtils_init() {
		WifiManager wifi = (WifiManager) GlobalVars.getContext().getSystemService(GlobalVars.getContext().WIFI_SERVICE);
		WifiInfo wifiInfo = wifi.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		String ipStr = (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "." + ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);

		CommonUtils.mLocalIp = ipStr;
	}

	public static String getLocalIp() {
		return mLocalIp;
	}

	public static String getLocalAddr() {
		// return "S0001010802000000000";
		return mLocalAddr;
	}

	public static String getRemoteAddr() {
		// return "M0001010";
		return mRemoteAddr;
	}

	public static String getRemoteIp() {
		return mRemoteIp;
	}

	public static final OnTouchListener TouchDark = new OnTouchListener() {

		public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

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

		public final float[] BT_SELECTED = new float[] { 1, 0, 0, 0, 50, 0, 1, 0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0 };
		public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

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

	public static byte[] getZerobyte() {
		// TODO Auto-generated method stub
		byte[] data = new byte[0];

		return data;
	}

	public static String getGBstr(byte[] data) {
		try {
			return new String(data, "GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static void controlLight(WareLight currentLight, int flag) {

		byte[] state = new byte[4];
		state[0] = currentLight.bOnOff;
		state[1] = currentLight.bTuneEn;
		state[2] = currentLight.lmVal;
		state[3] = currentLight.powChn;

		byte[] devBuff = CommonUtils.createWareDevInfo(currentLight.dev.getCanCpuId(), currentLight.dev.getDevName(),
				currentLight.dev.getRoomName(), currentLight.dev.getType(), currentLight.dev.getDevCtrlType(),
				currentLight.dev.getDevId(), state);
		if (flag == 0) {
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0, UdpProPkt.E_LGT_CMD.e_lgt_offOn.getValue(), devBuff,
					devBuff.length));
		} else {
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(), 0, UdpProPkt.E_LGT_CMD.e_lgt_onOff.getValue(), devBuff,
					devBuff.length));
		}

		sendMsg();
	}

	public static void controlAllDev(int devType, int cmd) {
		byte[] buf = new byte[1];

		buf[0] = (byte) cmd;
		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrl_allDevs.getValue(), 0, devType, buf, 1));

		sendMsg();
	}

	public static void sendMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DatagramPacket packet;
				try {
					packet = new DatagramPacket(GlobalVars.getSenddata(), GlobalVars.getSenddata().length,
							InetAddress.getByName(GlobalVars.getDstip()), 8300);

					DatagramSocket sockTx = new DatagramSocket();
					sockTx.send(packet);

					sockTx.close();
					sockTx = null;
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Get Pkt Sn
	 * 
	 * @return
	 */
	public static int getPktSn() {
		return new Random().nextInt(com.pullmi.common.Constants.sn++);
	}

	public static byte[] preSendUdpProPkt(String dstAddr, String srcAddr, int datType, int subType1, int subType2, byte[] buffer,
			int len) {
		UdpProPkt dataPacket = new UdpProPkt();

		try {
			dataPacket.dstIp = InetAddress.getByName(dstAddr).getAddress();
			dataPacket.srcIp = InetAddress.getByName(srcAddr).getAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataPacket.dataLen = len;

		dataPacket.isAck = UdpProPkt.IS_ACK.IS_ACK.getValue();
		dataPacket.datType = datType;
		dataPacket.subType1 = subType1;
		dataPacket.subType2 = subType2;

		dataPacket.currPkt = 0;
		dataPacket.snPkt = GlobalVars.getSn() + 1;
		dataPacket.sumPkt = 1;

		GlobalVars.setSn(dataPacket.snPkt);

		dataPacket.uidDst = new byte[12];
		dataPacket.uidSrc = new byte[12];
		dataPacket.pwdDst = new byte[8];

		for (int i = 0; i < 12; i++) {
			dataPacket.uidSrc[i] = (byte) 0xff;
		}

		/*
		 * if (GlobalVars.getDevid().equals("") ||
		 * GlobalVars.getDevpass().equals("")) { dataPacket.pwdDst =
		 * "09702443".getBytes(); dataPacket.uidDst[0] = 0x37;
		 * dataPacket.uidDst[1] = (byte) 0xff; dataPacket.uidDst[2] = (byte)
		 * 0xd9; dataPacket.uidDst[3] = 0x05; dataPacket.uidDst[4] = 0x42;
		 * 
		 * dataPacket.uidDst[5] = 0x4e; dataPacket.uidDst[6] = 0x32;
		 * dataPacket.uidDst[7] = 0x34; dataPacket.uidDst[8] = 0x09;
		 * dataPacket.uidDst[9] = 0x70;
		 * 
		 * dataPacket.uidDst[10] = 0x24; dataPacket.uidDst[11] = 0x43; } else {
		 * dataPacket.pwdDst = GlobalVars.getDevpass().getBytes();
		 * dataPacket.uidDst = hexStringToBytes(GlobalVars.getDevid()); }
		 */

		dataPacket.pwdDst = GlobalVars.getDevpass().getBytes();
		dataPacket.uidDst = hexStringToBytes(GlobalVars.getDevid());

		dataPacket.dat = new byte[len];
		for (int i = 0; i < buffer.length; i++) {
			dataPacket.dat[i] = buffer[i];
		}

		byte[] data = dataPacket.getData();

		return data;
	}

	public static byte[] preSendUdpAckPkt(String dstAddr, String srcAddr) throws UnknownHostException {
		UdpProPkt dataPacket = new UdpProPkt();

		dataPacket.dstIp = InetAddress.getByName(dstAddr).getAddress();
		dataPacket.srcIp = InetAddress.getByName(srcAddr).getAddress();

		dataPacket.isAck = UdpProPkt.IS_ACK.NOT_ACK.getValue();
		dataPacket.datType = UdpProPkt.E_UDP_RPO_DAT.e_udpPro_handShake.getValue();
		dataPacket.subType1 = 0;
		dataPacket.subType2 = 0;

		dataPacket.snPkt = GlobalVars.getSn() + 1;
		GlobalVars.setSn(dataPacket.snPkt);

		dataPacket.sumPkt = 1;
		dataPacket.currPkt = 0;
		dataPacket.dataLen = 0;

		dataPacket.uidDst = new byte[12];
		dataPacket.uidSrc = new byte[12];
		dataPacket.pwdDst = new byte[8];

		for (int i = 0; i < 12; i++) {
			dataPacket.uidSrc[i] = (byte) 0xff;
		}

		/*
		 * if (GlobalVars.getDevid().equals("") ||
		 * GlobalVars.getDevpass().equals("")) { dataPacket.pwdDst =
		 * "09702443".getBytes(); dataPacket.uidDst[0] = 0x37;
		 * dataPacket.uidDst[1] = (byte) 0xff; dataPacket.uidDst[2] = (byte)
		 * 0xd9; dataPacket.uidDst[3] = 0x05; dataPacket.uidDst[4] = 0x42;
		 * 
		 * dataPacket.uidDst[5] = 0x4e; dataPacket.uidDst[6] = 0x32;
		 * dataPacket.uidDst[7] = 0x34; dataPacket.uidDst[8] = 0x09;
		 * dataPacket.uidDst[9] = 0x70;
		 * 
		 * dataPacket.uidDst[10] = 0x24; dataPacket.uidDst[11] = 0x43; } else {
		 * dataPacket.pwdDst = GlobalVars.getDevpass().getBytes();
		 * dataPacket.uidDst = hexStringToBytes(GlobalVars.getDevid()); }
		 */

		dataPacket.pwdDst = GlobalVars.getDevpass().getBytes();
		dataPacket.uidDst = hexStringToBytes(GlobalVars.getDevid());

		dataPacket.dat = new byte[0];

		byte[] data = dataPacket.getData();

		return data;
	}

	public static void getDevInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				byte[] temp = new byte[0];
				byte[] data;
				RcuInfo rcuInfo = new RcuInfo();
				try {
					String dstip = InetAddress.getByAddress(rcuInfo.getIpAddr()).getHostAddress();
					// LogUtils.LOGE(TAG, "开始发送获取设备信息命令  " + dstip);
					data = CommonUtils.preSendUdpProPkt(dstip, CommonUtils.getLocalIp(),
							UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getDevsInfo.getValue(), 0, 0, temp, 0);

					DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(dstip), 8300);
					DatagramSocket socket = new DatagramSocket();
					socket.send(packet);
					socket.close();
					socket = null;

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void getSceneInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				byte[] temp = new byte[0];
				byte[] data;
				RcuInfo rcuInfo = new RcuInfo();
				try {
					String dstip = InetAddress.getByAddress(rcuInfo.getIpAddr()).getHostAddress();
					// LogUtils.LOGE(TAG, "开始发送获取设备信息命令  " + dstip);
					data = CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(), CommonUtils.getLocalIp(),
							UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getSceneEvents.getValue(), 0, 0, CommonUtils.getZerobyte(), 0);

					DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(dstip), 8300);
					DatagramSocket socket = new DatagramSocket();
					socket.send(packet);
					socket.close();
					socket = null;

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	public static String printHexString(byte[] b) {
		String a = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}

			a = a + hex;
		}

		return a;
	}

	public static List<WareTv> extractTvs(byte[] data) {

		int count = 0;
		int tv_size = 40;// tv结构体字节数,没有设备属性
		if (data != null) {
			count = data.length / tv_size;
		}

		List<WareTv> tvs = new ArrayList<WareTv>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareTv tv = new WareTv();
			byte[] curtData = new byte[tv_size];
			for (int m = 0; m < tv_size; m++) {
				curtData[m] = localData[i * tv_size + m];
			}

			tv.dev = wareDevInfo(curtData);
			tvs.add(tv);
		}

		return tvs;
	}

	public static List<WareSetBox> extractSetBoxs(byte[] data) {

		int count = 0;
		int tv_size = 40;// tv结构体字节数,没有设备属性
		if (data != null) {
			count = data.length / tv_size;
		}

		List<WareSetBox> setboxs = new ArrayList<WareSetBox>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareSetBox setbox = new WareSetBox();
			byte[] curtData = new byte[tv_size];
			for (int m = 0; m < tv_size; m++) {
				curtData[m] = localData[i * tv_size + m];
			}

			setbox.dev = wareDevInfo(curtData);
			setboxs.add(setbox);
		}

		return setboxs;
	}

	public static List<WareValve> extractValves(byte[] data) {

		int count = 0;
		int value_size = 44;// valve结构体字节数
		if (data != null) {
			count = data.length / value_size;
		}

		List<WareValve> valves = new ArrayList<WareValve>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareValve valve = new WareValve();
			byte[] curtData = new byte[value_size];
			for (int m = 0; m < value_size; m++) {
				curtData[m] = localData[i * value_size + m];
			}

			valve.dev = wareDevInfo(curtData);
			valve.bOnOff = localData[i * value_size + 40];
			valve.timRun = localData[i * value_size + 40 + 1];

			valves.add(valve);
		}

		return valves;
	}

	public static List<WareFreshAir> extractFreshairs(byte[] data) {

		int count = 0;
		int freshair_size = 44;// valve结构体字节数
		if (data != null) {
			count = data.length / freshair_size;
		}

		List<WareFreshAir> freshairs = new ArrayList<WareFreshAir>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareFreshAir freshair = new WareFreshAir();
			byte[] curtData = new byte[freshair_size];
			for (int m = 0; m < freshair_size; m++) {
				curtData[m] = localData[i * freshair_size + m];
			}

			freshair.dev = wareDevInfo(curtData);
			freshair.bOnOff = localData[i * freshair_size + 40];
			freshair.spdSel = localData[i * freshair_size + 40 + 1];
			freshair.powChn = byteToInt(new byte[] { localData[i * freshair_size + 40 + 2], localData[i * freshair_size + 40 + 3] });

			freshairs.add(freshair);
		}

		return freshairs;
	}

	/**
	 * get curtains
	 * 
	 * @param data
	 * @return
	 */
	public static List<WareCurtain> extractCurtains(byte[] data) {

		int count = 0;
		int curtain_size = 44;
		if (data != null) {
			count = data.length / curtain_size;
		}

		List<WareCurtain> curtains = new ArrayList<WareCurtain>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareCurtain curtain = new WareCurtain();
			byte[] curtData = new byte[curtain_size];
			for (int m = 0; m < curtain_size; m++) {
				curtData[m] = localData[i * curtain_size + m];
			}

			curtain.dev = wareDevInfo(curtData);
			curtain.bOnOff = localData[i * curtain_size + 40];
			curtain.timRun = localData[i * curtain_size + 40 + 1];
			curtain.powChn = byteToInt(new byte[] { localData[i * curtain_size + 40 + 2], localData[i * curtain_size + 40 + 3] });

			curtains.add(curtain);
		}

		return curtains;
	}

	/**
	 * Extract light data
	 * 
	 * @param data
	 */
	public static List<WareLight> extractLights(byte[] data) {

		int count = 0;
		int ligthSize = 44;
		if (data != null) {
			count = (data.length) / ligthSize;
		}
		if (count == 0) {
			return null;
		}

		List<WareLight> lights = new ArrayList<WareLight>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareLight light = new WareLight();
			byte[] curtData = new byte[ligthSize];
			for (int m = 0; m < ligthSize; m++) {
				curtData[m] = localData[i * ligthSize + m];
			}

			light.dev = wareDevInfo(curtData);
			light.bOnOff = localData[i * ligthSize + 40];
			light.bTuneEn = localData[i * ligthSize + 41];
			light.lmVal = localData[i * ligthSize + 42];
			light.powChn = localData[i * ligthSize + 43];

			lights.add(light);
		}

		return lights;
	}

	/**
	 * Extract lock data
	 * 
	 * @param data
	 */
	public static List<WareLock> extractLocks(byte[] data) {

		int count = 0;
		int lockSize = 52;
		if (data != null) {
			count = (data.length) / lockSize;
		}
		List<WareLock> locks = new ArrayList<WareLock>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareLock lock = new WareLock();
			byte[] lightData = new byte[lockSize];
			for (int m = 0; m < lockSize; m++) {
				lightData[m] = localData[i * lockSize + m];
			}

			lock.dev = wareDevInfo(lightData);

			lock.bOnOff = localData[i * lockSize + 40 + 0];
			lock.timRun = localData[i * lockSize + 41 + 0];
			lock.bLockOut = localData[i * lockSize + 42 + 0];
			lock.powChnOpen = localData[i * lockSize + 43 + 0];
			lock.pwd = new byte[8];
			copyBytes(localData, lock.pwd, 44, 0, 8);

			locks.add(lock);
		}

		return locks;
	}

	public static List<WareAirCondDev> extractAirconds(byte[] data) {

		int count = 0;
		int air_size = 48;// 结构体大小
		if (data != null) {
			count = (data.length) / air_size;
		}
		List<WareAirCondDev> airDevs = new ArrayList<WareAirCondDev>();

		byte[] localData = data.clone();
		for (int i = 0; i < count; i++) {
			WareAirCondDev airDev = new WareAirCondDev();
			byte[] devData = new byte[air_size];
			for (int m = 0; m < air_size; m++) {
				devData[m] = localData[i * air_size + m];
			}

			airDev.dev = wareDevInfo(devData);

			airDev.bOnOff = localData[i * air_size + 40 + 0];
			airDev.selMode = localData[i * air_size + 40 + 1];
			airDev.selTemp = localData[i * air_size + 40 + 2];
			airDev.selSpd = localData[i * air_size + 40 + 3];
			airDev.selDirect = localData[i * air_size + 40 + 4];
			airDev.powChn = byteToInt(new byte[] { localData[i * air_size + 40 + 6], localData[i * air_size + 40 + 7] });

			airDevs.add(airDev);
		}

		return airDevs;
	}

	public static List<WareBoardKeyInput> extractKeyInputs(byte[] data) {

		int count = 0;
		int key_size = 106;// 结构体大小
		if (data != null) {
			count = (data.length) / key_size;
		}

		byte[] localData = data.clone();

		List<WareBoardKeyInput> keys = new ArrayList<WareBoardKeyInput>();

		for (int i = 0; i < count; i++) {
			WareBoardKeyInput keyInput = new WareBoardKeyInput();
			keyInput.devUnitID = new byte[12];
			keyInput.boardName = new byte[12];
			keyInput.keyName = new byte[6][12];
			keyInput.keyAllCtrlType = new byte[6];

			copyBytes(localData, keyInput.devUnitID, i * key_size + 0, 0, 12);
			copyBytes(localData, keyInput.boardName, i * key_size + 12, 0, 12);

			keyInput.boardType = localData[i * key_size + 24 + 0];
			keyInput.keyCnt = localData[i * key_size + 24 + 1];
			keyInput.bResetKey = localData[i * key_size + 24 + 2];
			keyInput.ledBkType = localData[i * key_size + 24 + 3];

			for (int j = 0; j < keyInput.keyCnt; j++) {
				copyBytes(localData, keyInput.keyName[j], i * key_size + 24 + 4 + j * 12, 0, 12);
			}

			copyBytes(localData, keyInput.keyAllCtrlType, i * key_size + 100, 0, 6);

			keys.add(keyInput);
		}

		return keys;
	}

	public static List<WareKeyOpItem> extractKeyOpItems(byte[] data, int index) {

		int count = 0;
		int item_size = 16;// 结构体大小
		if (data != null) {
			count = (data.length) / item_size;
		}

		byte[] localData = data.clone();

		List<WareKeyOpItem> keyItems = new ArrayList<WareKeyOpItem>();

		for (int i = 0; i < count; i++) {
			WareKeyOpItem key = new WareKeyOpItem();

			key.devUnitID = new byte[12];
			copyBytes(localData, key.devUnitID, i * item_size + 0, 0, 12);
			key.index = (byte) index;
			key.devType = localData[i * item_size + 12 + 0];
			key.devId = localData[i * item_size + 12 + 1];
			key.keyOpCmd = localData[i * item_size + 12 + 2];
			key.keyOp = localData[i * item_size + 12 + 3];

			keyItems.add(key);
		}

		return keyItems;
	}

	public static List<WareChnOpItem> extractChnOpItems(byte[] data) {
		// TODO Auto-generated method stub
		int count = 0;
		int item_size = 32;// 结构体大小
		if (data != null) {
			count = (data.length) / item_size;
		}

		byte[] localData = data.clone();

		List<WareChnOpItem> ChnOpItems = new ArrayList<WareChnOpItem>();

		WareChnOpItem key = new WareChnOpItem();

		key.cpuid = new byte[12];
		copyBytes(localData, key.cpuid, 0, 0, 12);
		key.devType = localData[12 + 0];
		key.devid = localData[12 + 1];

		for (int i = 0; i < count; i++) {
			key.devUnitID = new byte[12];

			copyBytes(localData, key.devUnitID, i * item_size + 12 + 2, 0, 12);
			key.keyDownValid = localData[i * item_size + 12 + 14 + 0];
			key.keyUpValid = localData[i * item_size + 12 + 14 + 1];
			key.rev1 = byteToInt(new byte[] { localData[i * item_size + 12 + 14 + 2], localData[i * item_size + 12 + 14 + 3] });
			key.keyDownCmd = new byte[6];
			copyBytes(localData, key.keyDownCmd, i * item_size + 12 + 14 + 4, 0, 6);
			key.rev2 = byteToInt(new byte[] { localData[i * item_size + 12 + 14 + 10], localData[i * item_size + 12 + 14 + 11] });
			key.keyUpCmd = new byte[6];
			copyBytes(localData, key.keyUpCmd, i * item_size + 12 + 14 + 12, 0, 6);
			key.rev3 = byteToInt(new byte[] { localData[i * item_size + 12 + 14 + 18], localData[i * item_size + 12 + 14 + 19] });

			ChnOpItems.add(key);
		}

		return ChnOpItems;
	}

	public static List<WareBoardChnout> extractChnouts(byte[] data) {

		// LogUtils.LOGE(TAG, "输出控制板数据大小" + data.length);
		int count = 0;
		int chnout_size = 28 + 144;// 结构体大小
		if (data != null) {
			count = (data.length) / chnout_size;
		}

		byte[] localData = data.clone();

		List<WareBoardChnout> outs = new ArrayList<WareBoardChnout>();

		for (int i = 0; i < count; i++) {
			WareBoardChnout out = new WareBoardChnout();
			out.devUnitID = new byte[12];
			out.boardName = new byte[12];
			out.chnName = new byte[12][12];

			copyBytes(localData, out.devUnitID, i * chnout_size + 0, 0, 12);
			copyBytes(localData, out.boardName, i * chnout_size + 12, 0, 12);

			out.boardType = localData[i * chnout_size + 24 + 0];
			out.chnCnt = localData[i * chnout_size + 24 + 1];
			out.bOnline = localData[i * chnout_size + 24 + 2];
			out.rev2 = localData[i * chnout_size + 24 + 3];

			for (int j = 0; j < out.chnCnt; j++) {
				copyBytes(localData, out.chnName[j], i * chnout_size + 24 + 4 + j * 12, 0, 12);
			}

			outs.add(out);
		}

		return outs;
	}

	public static List<WareSceneEvent> extractEvents(byte[] data) {

		int count = 0;
		int events_size = 16 + 20 * 32;// 结构体大小
		if (data != null) {
			count = (data.length) / events_size;
		}
		List<WareSceneEvent> enentDevs = new ArrayList<WareSceneEvent>();

		byte[] localData = data.clone();

		for (int i = 0; i < count; i++) {
			WareSceneEvent item = new WareSceneEvent();

			item.sceneName = new byte[12];
			copyBytes(localData, item.sceneName, i * events_size, 0, 12);
			item.devCnt = localData[i * events_size + 12];
			item.eventld = localData[i * events_size + 13];

			int devCnt = localData[i * events_size + 12];
			item.itemAry = new WareSceneDevItem[devCnt];

			byte[] devData = new byte[20];
			for (int j = 0; j < devCnt; j++) {
				copyBytes(localData, devData, 16 + j * 20, 0, 20);
				item.itemAry[j] = sceneItemInfo(devData);
			}

			enentDevs.add(item);
		}

		return enentDevs;
	}

	public static List<GPIO_INPUT_INFO> extractIOinfo(byte[] data) {
		// TODO Auto-generated method stub
		int count = 0;
		int infos_size = 84;// 结构体大小
		if (data != null) {
			count = (data.length) / infos_size;
		}
		List<GPIO_INPUT_INFO> ios = new ArrayList<GPIO_INPUT_INFO>();

		byte[] localData = data.clone();

		for (int i = 0; i < count; i++) {
			GPIO_INPUT_INFO gpio = new GPIO_INPUT_INFO();

			gpio.defName = new byte[12];
			copyBytes(localData, gpio.defName, i * infos_size, 0, 12);

			gpio.item = new RUN_DEV_ITEM[2];
			gpio.item[0] = new RUN_DEV_ITEM();
			gpio.item[0].uid = new byte[12];
			copyBytes(localData, gpio.item[0].uid, i * infos_size + 20, 0, 12);
			gpio.item[0].devType = localData[i * infos_size + 12 + 20];
			gpio.item[0].devID = localData[i * infos_size + 12 + 24];
			gpio.item[0].bOnoff = localData[i * infos_size + 12 + 25];
			gpio.item[0].param1 = localData[i * infos_size + 12 + 26];
			gpio.item[0].param2 = localData[i * infos_size + 12 + 27];

			gpio.item[1] = new RUN_DEV_ITEM();
			gpio.item[1].uid = new byte[12];
			copyBytes(localData, gpio.item[1].uid, i * infos_size + 12 + 28, 0, 12);
			gpio.item[1].devType = localData[i * infos_size + 40 + 12];
			gpio.item[1].devID = localData[i * infos_size + 44 + 12];
			gpio.item[1].bOnoff = localData[i * infos_size + 45 + 12];
			gpio.item[1].param1 = localData[i * infos_size + 46 + 12];
			gpio.item[1].param2 = localData[i * infos_size + 47 + 12];

			gpio.itemRTime = new byte[2];
			gpio.itemRTime[0] = localData[i * infos_size + 60];
			gpio.itemRTime[1] = localData[i * infos_size + 61];

			gpio.outOP = new RUN_IOOUT_ITEM[2];
			gpio.outOP[0] = new RUN_IOOUT_ITEM();
			gpio.outOP[0].nOutput = localData[i * infos_size + 64];
			gpio.outOP[0].inputOP = localData[i * infos_size + 65];
			gpio.outOP[0].rev1 = localData[i * infos_size + 66];
			gpio.outOP[0].valid = localData[i * infos_size + 67];

			gpio.outOP[1] = new RUN_IOOUT_ITEM();
			gpio.outOP[1].nOutput = localData[i * infos_size + 68];
			gpio.outOP[1].inputOP = localData[i * infos_size + 69];
			gpio.outOP[1].rev1 = localData[i * infos_size + 70];
			gpio.outOP[1].valid = localData[i * infos_size + 71];

			gpio.outOPRTime = new byte[2];
			gpio.outOPRTime[0] = localData[i * infos_size + 72];
			gpio.outOPRTime[1] = localData[i * infos_size + 73];

			gpio.validLevel = localData[i * infos_size + 79];

			gpio.flg = new byte[4];

			ios.add(gpio);
		}

		return ios;
	}

	public static List<GPIO_OUTPUT_INFO> extractOutinfo(byte[] data) {
		// TODO Auto-generated method stub
		int count = 0;
		int infos_size = 92;// 结构体大小
		if (data != null) {
			count = (data.length) / infos_size;
		}
		List<GPIO_OUTPUT_INFO> ios = new ArrayList<GPIO_OUTPUT_INFO>();

		byte[] localData = data.clone();

		for (int i = 0; i < count; i++) {
			GPIO_OUTPUT_INFO gpio = new GPIO_OUTPUT_INFO();

			gpio.defName = new byte[12];
			copyBytes(localData, gpio.defName, i * infos_size, 0, 12);

			gpio.keyUid = new byte[4][12];
			for (int j = 0; j < 4; j++) {
				copyBytes(localData, gpio.keyUid[j], i * infos_size + 20 + 12 * i, 0, 12);
			}
			gpio.keyNum = new byte[4];
			gpio.keyNum[0] = localData[i * infos_size + 69];
			gpio.keyNum[1] = localData[i * infos_size + 70];
			gpio.keyNum[2] = localData[i * infos_size + 71];
			gpio.keyNum[3] = localData[i * infos_size + 72];

			gpio.keyNumOP = new byte[4];
			gpio.keyNumOP[0] = localData[i * infos_size + 73];
			gpio.keyNumOP[1] = localData[i * infos_size + 74];
			gpio.keyNumOP[2] = localData[i * infos_size + 75];
			gpio.keyNumOP[3] = localData[i * infos_size + 76];

			gpio.opTime = new byte[4];
			gpio.opTime[0] = localData[i * infos_size + 77];
			gpio.opTime[1] = localData[i * infos_size + 78];
			gpio.opTime[2] = localData[i * infos_size + 79];
			gpio.opTime[3] = localData[i * infos_size + 80];

			gpio.opTimer = new byte[4];
			gpio.opTimer[0] = localData[i * infos_size + 81];
			gpio.opTimer[1] = localData[i * infos_size + 82];
			gpio.opTimer[2] = localData[i * infos_size + 83];
			gpio.opTimer[3] = localData[i * infos_size + 84];

			gpio.outAct = localData[i * infos_size + 85];
			gpio.outAct = localData[i * infos_size + 86];
			gpio.rev2 = localData[i * infos_size + 87];

			gpio.flg = new byte[4];
			gpio.flg[0] = localData[i * infos_size + 88];
			gpio.flg[0] = localData[i * infos_size + 89];
			gpio.flg[0] = localData[i * infos_size + 90];
			gpio.flg[0] = localData[i * infos_size + 91];

			ios.add(gpio);
		}

		return ios;
	}

	private static WareSceneDevItem sceneItemInfo(byte[] devData) {
		WareSceneDevItem dev = new WareSceneDevItem();

		byte[] canCpuId = new byte[12];

		for (int m = 0; m < 12; m++) {
			canCpuId[m] = devData[m];
		}
		dev.uid = canCpuId;
		dev.devType = devData[12];
		dev.devID = devData[16];
		dev.bOnOff = devData[17];

		return dev;
	}

	private static WareDev wareDevInfo(byte[] devData) {
		WareDev dev = new WareDev();

		byte[] canCpuId = new byte[12];
		byte[] namebyte = new byte[12];
		byte[] roomName = new byte[12];

		for (int m = 0; m < 12; m++) {
			canCpuId[m] = devData[m];
			namebyte[m] = devData[m + 12];
			roomName[m] = devData[m + 24];
		}

		dev.setCanCpuId(canCpuId);
		dev.setDevName(namebyte);
		dev.setRoomName(roomName);

		dev.setType(devData[36]);
		dev.setDevId(devData[37]);
		dev.setDevCtrlType(devData[38]);

		return dev;
	}

	/**
	 * Broacast UDP packet.
	 * 
	 * @param castClass
	 * @param packet
	 * @throws IOException
	 */
	public static void castUdpPacket(MulticastSocket castClass, DatagramPacket packet) throws IOException {
		castClass.send(packet);
	}

	/**
	 * cast byte array into int value
	 * 
	 * @param value
	 * @return
	 */
	public static int byteToInt(byte[] value) {
		int length = value.length;
		int intValue = value[length - 1] & 0xff;

		for (int i = length - 2; i >= 0; i--) {
			intValue = (intValue << 8 * (length - i - 1)) | (value[i] & 0xff);
		}

		return intValue;
	}

	public static void shortAry2byteAry(byte[] byteAry, short[] shortAry, int shortLen) {
		byte[] b = byteAry;
		short[] s = shortAry;
		int j = 0;
		for (int i = 0; i < shortLen; i++) {
			b[j] = (byte) (s[i] & 0x00ff);
			j++;
			b[j] = (byte) (s[i] >> 8);
			j++;
		}
	}

	/**
	 * Highg bit is ahead
	 * 
	 * @param value
	 * @return
	 */
	public static int byteToIntHB(byte[] value) {
		int length = value.length;
		int intValue = value[0] & 0xff;
		for (int i = 1; i < length; i++) {
			intValue = (intValue << 8 * i) | (value[i] & 0xff);
		}
		return intValue;
	}

	// 形如0001转换为1
	public static int byteCharToInt(byte[] value) {
		int length = value.length;
		int intValue = 0;
		for (int i = 0; i < length; i++) {
			intValue += (int) ((value[i] - 0x30) * (Math.pow(10, length - i - 1)));
		}
		return intValue;
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * Rely Ack Packet
	 * 
	 * @param packet
	 */
	public static byte[] reply(DatagramPacket packet) {

		byte[] receivedData = packet.getData();

		byte[] data = new byte[packet.getLength()];

		for (int i = 0; i < data.length; i++) {
			data[i] = receivedData[i];
		}

		// is ack
		data[1 + 7] = (byte) (1 & 0xff);

		// dst addr
		int[] dstAddr = new int[4];
		dstAddr[0] = data[11] & 0xff;
		dstAddr[1] = data[12] & 0xff;
		dstAddr[2] = data[13] & 0xff;
		dstAddr[3] = data[14] & 0xff;

		// src addr
		int[] srcAddr = new int[4];
		srcAddr[0] = data[15] & 0xff;
		srcAddr[1] = data[16] & 0xff;
		srcAddr[2] = data[17] & 0xff;
		srcAddr[3] = data[18] & 0xff;

		data[11] = (byte) (srcAddr[0] & 0xff);
		data[12] = (byte) (srcAddr[1] & 0xff);
		data[13] = (byte) (srcAddr[2] & 0xff);
		data[14] = (byte) (srcAddr[3] & 0xff);

		data[15] = (byte) (dstAddr[0] & 0xff);
		data[16] = (byte) (dstAddr[1] & 0xff);
		data[17] = (byte) (dstAddr[2] & 0xff);
		data[18] = (byte) (dstAddr[3] & 0xff);

		return data;
	}

	/**
	 * cast byte array to string
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Get str from byte
	 * 
	 * @param data
	 * @return
	 */
	public static String getStr(byte[] data) {
		int dataLen = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0) {
				dataLen = i;
				break;
			}
		}
		try {
			return new String(data, 0, dataLen, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * create a UDP packet, RcuInfo, total length 64 bytes
	 * 
	 * @param devUnitID
	 * @param devUnitPass
	 * @param name
	 * @param IpAddr
	 * @param SubMask
	 * @param Gateway
	 * @param centerServ
	 * @param roomNum
	 * @param macAddr
	 * @throws UnknownHostException
	 */
	public static byte[] createRcuInfo(String devUnitID, String devUnitPass, String name, String IpAddr, String SubMask,
			String Gateway, String centerServ, String roomNum, String macAddr) {

		byte[] data = new byte[64];

		byte[] devUnitIDbytes = devUnitID.getBytes();
		copyBytes(devUnitIDbytes, data, 0, 0, devUnitIDbytes.length);

		byte[] devUnitPassBytes = devUnitPass.getBytes();
		copyBytes(devUnitPassBytes, data, 0, 0 + 12, devUnitPassBytes.length);

		byte[] nameBytes = name.getBytes();
		copyBytes(nameBytes, data, 0, 0 + 12 + 8, nameBytes.length);

		byte[] IpAddrBytes = IpAddr.getBytes();
		copyBytes(IpAddrBytes, data, 0, 0 + 12 + 8 + 12, IpAddrBytes.length);

		byte[] SubMaskBytes = SubMask.getBytes();
		copyBytes(SubMaskBytes, data, 0, 0 + 12 + 8 + 12 + 4, SubMaskBytes.length);

		byte[] GatewayBytes = Gateway.getBytes();
		copyBytes(GatewayBytes, data, 0, 0 + 12 + 8 + 12 + 4 + 4, GatewayBytes.length);

		byte[] centerServBytes = centerServ.getBytes();
		copyBytes(centerServBytes, data, 0, 0 + 12 + 8 + 12 + 4 + 4 + 4, centerServBytes.length);

		byte[] roomNumBytes = roomNum.getBytes();
		copyBytes(roomNumBytes, data, 0, 0 + 12 + 8 + 12 + 4 + 4 + 4, roomNumBytes.length);

		byte[] macAddrBytes = macAddr.getBytes();
		copyBytes(macAddrBytes, data, 0, 0 + 12 + 8 + 12 + 4 + 4 + 4 + 4, macAddrBytes.length);
		return data;
	}

	public static byte[] createChnOpItemsInfo(List<WareChnOpItem> items) {
		// TODO Auto-generated method stub

		int keyNums = items.size();
		byte[] data = new byte[3 + keyNums * 32];

		copyBytes(items.get(0).cpuid, data, 0, 0, 12);
		data[12 + 0] = items.get(0).devType;
		data[12 + 1] = items.get(0).devid;

		for (int i = 0; i < keyNums; i++) {
			copyBytes(items.get(i).devUnitID, data, 0, i * 32 + 14, 12);
			data[i * 32 + 26 + 0] = items.get(i).keyDownValid;
			data[i * 32 + 26 + 1] = items.get(i).keyUpValid;
			data[i * 32 + 26 + 2] = (byte) items.get(i).rev1;
			data[i * 32 + 26 + 3] = 0;
			copyBytes(items.get(i).keyDownCmd, data, 0, i * 32 + 26 + 4, 12);
			data[i * 32 + 26 + 16] = (byte) items.get(i).rev2;
			data[i * 32 + 26 + 17] = 0;
			copyBytes(items.get(i).keyUpCmd, data, 0, i * 32 + 26 + 18, 12);
			data[i * 32 + 26 + 30] = (byte) items.get(i).rev3;
			data[i * 32 + 26 + 31] = 0;
		}

		return null;
	}

	public static byte[] createKeyOpItemsInfo(byte[] devUnitID, List<WareKeyOpItem> items, int index) {
		// TODO Auto-generated method stub
		int keyNums = items.size();
		byte[] data = new byte[12 + keyNums * 16];

		copyBytes(devUnitID, data, 0, 0, 12);
		for (int i = 0; i < keyNums; i++) {
			copyBytes(items.get(i).devUnitID, data, 0, i * 16 + 12, 12);
			data[i * 16 + 24 + 0] = items.get(i).devType;
			data[i * 16 + 24 + 1] = items.get(i).devId;
			data[i * 16 + 24 + 2] = items.get(i).keyOpCmd;
			data[i * 16 + 24 + 3] = items.get(i).keyOp;
		}

		return data;
	}

	public static byte[] createDelKeyOpItemInfo(byte[] devUnitID, WareKeyOpItem item) {
		// TODO Auto-generated method stub
		byte[] data = new byte[12 + 16];

		copyBytes(devUnitID, data, 0, 0, 12);

		copyBytes(item.devUnitID, data, 0, 12, 12);
		data[24 + 0] = item.devType;
		data[24 + 1] = item.devId;
		data[24 + 2] = item.keyOpCmd;
		data[24 + 3] = item.keyOp;

		return data;
	}

	// create a UDP packet WareDev
	public static byte[] createWareDevInfo(byte[] canCpuId, byte[] devName, byte[] roomName, int devType, int devCtrlType,
			int devId, byte[] buffer) {
		byte[] data = new byte[40 + buffer.length];

		copyBytes(canCpuId, data, 0, 0, canCpuId.length);
		copyBytes(devName, data, 0, 0 + 12, devName.length);
		copyBytes(roomName, data, 0, 0 + 12 + 12, roomName.length);

		data[0 + 36] = (byte) (devType & 0xff);
		data[0 + 37] = (byte) (devId & 0xff);
		data[0 + 38] = (byte) (devCtrlType & 0xff);
		data[0 + 39] = (byte) (buffer.length & 0xff);

		for (int i = 0; i < buffer.length; i++) {
			data[i + 40] = buffer[i];
		}

		return data;
	}

	// create a UDP packet TimerEvent
	public static byte[] createTimerEvent(byte[] timSta, byte[] timEnd, byte[] timerName, int devCnt, int eventld, String cpuID,
			int devID, byte bOnOff) {

		byte[] data = new byte[40];

		copyBytes(timSta, data, 0, 0, timSta.length);
		copyBytes(timEnd, data, 0, 0 + 4, timEnd.length);
		copyBytes(timerName, data, 0, 0 + 4 + 4, timerName.length);

		data[0 + 20] = (byte) (devCnt & 0xff);
		data[0 + 21] = (byte) (eventld & 0xff);

		byte[] cpuidbyte = cpuID.getBytes();
		copyBytes(cpuidbyte, data, 0, 22, cpuidbyte.length);

		data[22 + cpuidbyte.length] = (byte) devID;

		data[22 + cpuidbyte.length + 1] = bOnOff;

		return data;
	}

	public static byte[] createGpioItem(List<GPIO_INPUT_INFO> dev) {

		int count = dev.size();
		int itemLen = 84;
		byte[] data = new byte[itemLen * count];

		for (int i = 0; i < count; i++) {
			GPIO_INPUT_INFO gpio = dev.get(i);
			copyBytes(gpio.defName, data, 0, i * itemLen, 12);
			for (int j = 0; j < 8; j++) {
				data[i * itemLen + 12 + j] = 0;
			}
			copyBytes(gpio.item[0].uid, data, 0, i * itemLen + 20, 12);
			data[i * itemLen + 32] = gpio.item[0].devType;
			data[i * itemLen + 33] = gpio.item[0].lmVal;
			data[i * itemLen + 34] = gpio.item[0].rev2;
			data[i * itemLen + 35] = gpio.item[0].rev3;
			data[i * itemLen + 36] = gpio.item[0].devID;
			data[i * itemLen + 37] = gpio.item[0].bOnoff;
			data[i * itemLen + 38] = gpio.item[0].param1;
			data[i * itemLen + 39] = gpio.item[0].param2;

			copyBytes(gpio.item[1].uid, data, 0, i * itemLen + 40, 12);
			data[i * itemLen + 52] = gpio.item[1].devType;
			data[i * itemLen + 53] = gpio.item[1].lmVal;
			data[i * itemLen + 54] = gpio.item[1].rev2;
			data[i * itemLen + 55] = gpio.item[1].rev3;
			data[i * itemLen + 56] = gpio.item[1].devID;
			data[i * itemLen + 57] = gpio.item[1].bOnoff;
			data[i * itemLen + 58] = gpio.item[1].param1;
			data[i * itemLen + 59] = gpio.item[1].param2;

			data[i * itemLen + 60] = gpio.itemRTime[0];
			data[i * itemLen + 61] = gpio.itemRTime[1];
			data[i * itemLen + 62] = 0;
			data[i * itemLen + 63] = 0;

			data[i * itemLen + 64] = gpio.outOP[0].nOutput;
			data[i * itemLen + 65] = gpio.outOP[0].inputOP;
			data[i * itemLen + 66] = gpio.outOP[0].rev1;
			data[i * itemLen + 67] = gpio.outOP[0].valid;

			data[i * itemLen + 68] = gpio.outOP[1].nOutput;
			data[i * itemLen + 69] = gpio.outOP[1].inputOP;
			data[i * itemLen + 70] = gpio.outOP[1].rev1;
			data[i * itemLen + 71] = gpio.outOP[1].valid;

			data[i * itemLen + 72] = gpio.outOPRTime[0];
			data[i * itemLen + 73] = gpio.outOPRTime[1];
			data[i * itemLen + 74] = 0;
			data[i * itemLen + 75] = 0;

			data[i * itemLen + 76] = gpio.oldLevel;
			data[i * itemLen + 77] = gpio.timout;
			data[i * itemLen + 78] = gpio.rev;
			data[i * itemLen + 79] = gpio.validLevel;

			for (int j = 0; j < 4; j++) {
				data[i * itemLen + 80 + j] = 0;
			}
		}

		return data;
	}

	public static byte[] createOutItem(List<GPIO_OUTPUT_INFO> dev) {

		int count = dev.size();
		int itemLen = 92;
		byte[] data = new byte[itemLen * count];

		for (int i = 0; i < count; i++) {
			GPIO_OUTPUT_INFO gpio = dev.get(i);
			copyBytes(gpio.defName, data, 0, i * itemLen, 12);
			for (int j = 0; j < 8; j++) {
				data[i * itemLen + 12 + j] = 0;
			}
			for (int j = 0; j < 4; j++) {
				copyBytes(gpio.keyUid[j], data, 0, i * itemLen + 20 + j * 12, 12);
			}

			data[i * itemLen + 69] = gpio.keyNum[0];
			data[i * itemLen + 70] = gpio.keyNum[0];
			data[i * itemLen + 71] = gpio.keyNum[0];
			data[i * itemLen + 72] = gpio.keyNum[0];

			data[i * itemLen + 73] = gpio.keyNumOP[0];
			data[i * itemLen + 74] = gpio.keyNumOP[0];
			data[i * itemLen + 75] = gpio.keyNumOP[0];
			data[i * itemLen + 76] = gpio.keyNumOP[0];

			data[i * itemLen + 77] = gpio.opTime[0];
			data[i * itemLen + 78] = gpio.opTime[0];
			data[i * itemLen + 79] = gpio.opTime[0];
			data[i * itemLen + 80] = gpio.opTime[0];

			data[i * itemLen + 81] = gpio.opTimer[0];
			data[i * itemLen + 82] = gpio.opTimer[0];
			data[i * itemLen + 83] = gpio.opTimer[0];
			data[i * itemLen + 84] = gpio.opTimer[0];

			data[i * itemLen + 85] = gpio.outAct;
			data[i * itemLen + 86] = gpio.rev;
			data[i * itemLen + 87] = (byte) gpio.rev2;

			for (int j = 0; j < 4; j++) {
				data[i * itemLen + 88 + j] = 0;
			}
		}

		return data;
	}

	// create a sceneEventItem
	public static byte[] createSceneEventItem(List<WareSceneDevItem> dev) {

		int count = dev.size();
		int itemLen = 20;
		byte[] data = new byte[20 * count];

		for (int i = 0; i < count; i++) {
			WareSceneDevItem item = dev.get(i);
			copyBytes(item.uid, data, 0, i * itemLen, 12);
			data[i * itemLen + 12] = item.devType;
			data[i * itemLen + 13] = 0;
			data[i * itemLen + 14] = 0;
			data[i * itemLen + 15] = 0;
			data[i * itemLen + 16] = item.devID;
			data[i * itemLen + 17] = item.bOnOff;
			data[i * itemLen + 18] = 0;
			data[i * itemLen + 19] = 0;
		}

		return data;
	}

	// create a UDP packet SceneEvent
	public static byte[] createSceneEvent(WareSceneEvent item) {

		int devCnt = item.devCnt;
		byte[] data = new byte[16 + devCnt * 20];

		copyBytes(item.sceneName, data, 0, 0, 12);
		data[12] = item.devCnt;
		data[13] = item.eventld;
		data[14] = 0;
		data[15] = 0;

		for (int i = 0; i < devCnt; i++) {
			copyBytes(item.itemAry[i].uid, data, 0, 16 + i * 20, 12);
			data[16 + i * 20 + 12] = item.itemAry[i].devType;
			data[16 + i * 20 + 13] = 0;
			data[16 + i * 20 + 14] = 0;
			data[16 + i * 20 + 15] = 0;
			data[16 + i * 20 + 16] = item.itemAry[i].devID;
			data[16 + i * 20 + 17] = item.itemAry[i].bOnOff;
			data[16 + i * 20 + 18] = item.itemAry[i].param1;
			data[16 + i * 20 + 19] = item.itemAry[i].param2;
		}

		return data;
	}

	// create a UDP packet SceneEvent
	public static byte[] createSceneEvent(String sceneName, int devCnt, int eventID, byte[] itemAry) {

		byte[] data = new byte[16 + devCnt * 20];

		try {
			byte[] nameBytes = sceneName.getBytes("GB2312");
			copyBytes(nameBytes, data, 0, 0, nameBytes.length);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		data[12] = (byte) devCnt;
		data[13] = (byte) eventID;
		data[14] = 0;
		data[15] = 0;

		for (int i = 0; i < itemAry.length; i++) {
			data[16 + i] = itemAry[i];
		}

		return data;
	}

	/**
	 * create a UDP packet, cmd: NSORDER, CMD PROPER: ASK, total length 56 bytes
	 * 
	 * @param localAddr
	 * @param localIp
	 * @param remoteAddr
	 * @param remoteIp
	 * @throws UnknownHostException
	 */
	public static byte[] createNsorderAsk(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[56];

		// header
		String header = "XXXCID";
		byte[] headerData = header.getBytes();
		copyBytes(headerData, data, 0, 0, headerData.length);

		// cmd
		data[0 + 6] = (byte) (Constants.UDP_CMD.NSORDER.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);

		// local address
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 8, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(getLocalIp()).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 28, localIpBytes.length);

		// remote addr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 32 + 0, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = new byte[4];
		for (byte b : remoteIpBytes) {
			b = (byte) (0 & 0xff);
		}
		copyBytes(remoteIpBytes, data, 0, 52 + 0, remoteIpBytes.length);

		return data;
	}

	/**
	 * create a reply UDP packet, CMD: NSORDER, CMD Property:REPLY, total length
	 * 57 bytes
	 * 
	 * @param localAddr
	 * @param localIp
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createNsorderReply(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];

		// header
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// cmd
		data[0 + 6] = (byte) (Constants.UDP_CMD.NSORDER.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.REPLY.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		LogUtils.LOGE(TAG, "" + bytesToHexString(remoteAddrBytes));
		copyBytes(remoteAddrBytes, data, 0, 0 + 8, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 28, remoteIpBytes.length);

		// room machine count
		data[0 + 32] = (byte) (1 & 0xff);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		LogUtils.LOGE(TAG, bytesToHexString(localAddrBytes));
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		LogUtils.LOGE(TAG, "remoteAddr: " + new String(remoteAddrBytes) + ", remote ip: " + new String(remoteIp)
				+ ", localAddr: " + new String(localAddr) + ", localIp: " + new String(localIp));

		return data;
	}

	/**
	 * create a UDP packet, CMD: VIDEOTALK, CMD Property: ASK, SUB_CDM:CALL,
	 * with length 62 bytes
	 * 
	 * @param localAddr
	 * @param localIp
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideoTaklAskCall(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[62];

		// header
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// CMD property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub CMD
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALL.getValue() & 0xff);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 9, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 29, localIpBytes.length);

		// remote addr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 33, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = remoteIp.getBytes();
		copyBytes(remoteIpBytes, data, 0, 0 + 53, remoteIpBytes.length);

		// reserved byte
		data[0 + 57] = (byte) (0 & 0xff);

		// braocast group
		byte[] broadcastBytes = InetAddress.getByName("255.255.255.255").getAddress();
		copyBytes(broadcastBytes, data, 0, 0 + 58, broadcastBytes.length);

		return data;
	}

	/**
	 * create as UDP packet, CMD: VIDEOTALK, Cmd property: ASK, SUB CMD: LINEUSE
	 * 
	 * @param localAddr
	 * @param ip
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkAskLineuse(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.LINEUSE.getValue() & 0xff);

		// remoteAddr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 9, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		return data;
	}

	/**
	 * create as UDP packet, CMD: VIDEOTALK, Cmd property: ASK, SUB CMD:
	 * CALLANSWER
	 * 
	 * @param localAddr
	 * @param ip
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkAskCallanswer(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[62];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLANSWER.getValue() & 0xff);

		// remoteAddr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 9, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		// reserve byte
		data[0 + 57] = (byte) (0 & 0xff);

		// broadcast group
		byte[] broadcastBytes = InetAddress.getByName("255.255.255.255").getAddress();
		copyBytes(broadcastBytes, data, 0, 0 + 58, broadcastBytes.length);

		return data;
	}

	/**
	 * create as UDP packet, CMD: VIDEOTALK, Cmd property: ASK, SUB CMD:
	 * CALLSTART
	 * 
	 * @param localAddr
	 * @param ip
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkAskCallstart(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLSTART.getValue() & 0xff);

		// remoteAddr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 9, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		return data;
	}

	/**
	 * create as UDP packet, CMD: VIDEOTALK, Cmd property: REPLY, SUB CMD:
	 * CALLSTART
	 * 
	 * @param localAddr
	 * @param ip
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkReplyCallstart(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.REPLY.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLSTART.getValue() & 0xff);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 9, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 29, localIpBytes.length);

		// remote addr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 33, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 53, remoteIpBytes.length);

		return data;
	}

	/**
	 * create a UDP packet, CMD: VIDEOTALK, Cmd property: ASK, SUB CMD:
	 * CALLCONFIRM, with length 61 bytes
	 * 
	 * @param localAddr
	 * @param localIp
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkAskCallconfirm(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[61];

		// header
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// cmd
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLCONFIRM.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 9, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		// reserved
		byte[] confSns = new byte[4];
		for (byte b : confSns) {
			b = (byte) (0 & 0xff);
		}
		copyBytes(confSns, data, 0, 0 + 57, confSns.length);

		return data;

	}

	/**
	 * create a UDP packet, send callconfirm reply packet
	 * 
	 * @param localAddr
	 * @param localIp
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkReplyCallconfirm(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[61];

		// header
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// cmd
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.REPLY.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLCONFIRM.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 9, remoteAddrBytes.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		// reserved
		byte[] confSns = new byte[4];
		for (byte b : confSns) {
			b = (byte) (0 & 0xff);
		}
		copyBytes(confSns, data, 0, 0 + 57, confSns.length);

		return data;

	}

	/**
	 * create a UDP packet, reply callend
	 * 
	 * @param localAddr
	 * @param localIp
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkReplyCallend(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.REPLY.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLEND.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrByte = remoteAddr.getBytes();
		copyBytes(remoteAddrByte, data, 0, 0 + 9, remoteAddrByte.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		return data;
	}

	public static byte[] createVideoWatchAskCallend(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		// copyBytes(headerBytes, data, 0, 0, headerBytes.length);
		System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOWATCH.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLEND.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrByte = remoteAddr.getBytes();
		// copyBytes(remoteAddrByte, data, 0, 0+32, remoteAddrByte.length);
		System.arraycopy(remoteAddrByte, 0, data, 0 + 9, remoteAddrByte.length);

		// remote ip
		byte[] remoteIpByte = InetAddress.getByName(remoteIp).getAddress();
		// copyBytes(localIpByte, data, 0, 0+28, localIpByte.length);
		System.arraycopy(remoteIpByte, 0, data, 0 + 29, remoteIpByte.length);

		// local addr
		byte[] localAddrByte = localAddr.getBytes();
		// copyBytes(localAddrByte, data, 0, 0+8, localAddrByte.length);
		System.arraycopy(localAddrByte, 0, data, 0 + 33, localAddrByte.length);

		// local ip
		byte[] localIpByte = InetAddress.getByName(localIp).getAddress();
		// copyBytes(localIpByte, data, 0, 0+28, localIpByte.length);
		System.arraycopy(localIpByte, 0, data, 0 + 53, localIpByte.length);

		return data;
	}

	public static byte[] createVideoWatchNsOrderAsk(String localAddr, String localIp, String remoteAddr)
			throws UnknownHostException {
		byte[] data = new byte[56];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		// copyBytes(headerBytes, data, 0, 0, headerBytes.length);
		System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.NSORDER.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);

		// local addr
		byte[] localAddrByte = localAddr.getBytes();
		// copyBytes(localAddrByte, data, 0, 0+8, localAddrByte.length);
		System.arraycopy(localAddrByte, 0, data, 0 + 8, localAddrByte.length);

		// local ip
		byte[] localIpByte = InetAddress.getByName(localIp).getAddress();
		// copyBytes(localIpByte, data, 0, 0+28, localIpByte.length);
		System.arraycopy(localIpByte, 0, data, 0 + 28, localIpByte.length);

		// remote addr
		byte[] remoteAddrByte = remoteAddr.getBytes();
		// copyBytes(remoteAddrByte, data, 0, 0+32, remoteAddrByte.length);
		System.arraycopy(remoteAddrByte, 0, data, 0 + 32, remoteAddrByte.length);

		data[0 + 52] = (byte) (0);
		data[0 + 53] = (byte) (0);
		data[0 + 54] = (byte) (0);
		data[0 + 55] = (byte) (0);

		return data;
	}

	public static byte[] createVideoWatchAskCall(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[62];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOWATCH.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALL.getValue() & 0xff);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 9, localAddrBytes.length);

		// local ip byte
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 29, localIpBytes.length);

		// remote addr byte
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 33, remoteAddrBytes.length);

		// remote ip byte
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 53, remoteIpBytes.length);

		// reserved
		data[0 + 57] = (byte) (0 & 0xff);

		// group bytes
		data[0 + 58] = (byte) (236 & 0xff);
		data[0 + 59] = (byte) (168 & 0xff);
		data[0 + 60] = (byte) (3 & 0xff);
		data[0 + 61] = (byte) (119 & 0xff);

		return data;
	}

	public static byte[] createVideoWatchAskCallconfirm(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[61];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOWATCH.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLCONFIRM.getValue() & 0xff);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 9, localAddrBytes.length);

		// local ip byte
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 29, localIpBytes.length);

		// remote addr byte
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 33, remoteAddrBytes.length);

		// remote ip byte
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 53, remoteIpBytes.length);

		data[0 + 57] = (byte) (0 & 0xff);
		data[0 + 58] = (byte) (0 & 0xff);
		data[0 + 59] = (byte) (0 & 0xff);
		data[0 + 60] = (byte) (0 & 0xff);

		return data;
	}

	public static byte[] createVideoWatchReplyCallconfirm(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[61];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(data, headerBytes, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOWATCH.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.REPLY.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLCONFIRM.getValue() & 0xff);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 9, localAddrBytes.length);

		// local ip byte
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 29, localIpBytes.length);

		// remote addr byte
		byte[] remoteAddrBytes = remoteAddr.getBytes();
		copyBytes(remoteAddrBytes, data, 0, 0 + 33, remoteAddrBytes.length);

		// remote ip byte
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 53, remoteIpBytes.length);

		data[0 + 57] = (byte) (0 & 0xff);
		data[0 + 58] = (byte) (0 & 0xff);
		data[0 + 59] = (byte) (0 & 0xff);
		data[0 + 60] = (byte) (0 & 0xff);

		return data;
	}

	public static int createVideoTalkAskCalldown(byte[] buff, String localAddr, String localIp, String remoteAddr,
			String remoteIp, int timestamp, int dataType, int frameNo, int frameLen, int totalPackages, int currPackage,
			int dataLen, int packLen, byte[] audioData) throws UnknownHostException {
		byte[] data = buff;// new byte[150];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLDOWN.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrByte = remoteAddr.getBytes();
		copyBytes(remoteAddrByte, data, 0, 0 + 9, remoteAddrByte.length);
		// remote addr ip
		byte[] remoteIpByte = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpByte, data, 0, 0 + 29, remoteIpByte.length);
		// remote addr
		byte[] localAddrByte = localAddr.getBytes();
		copyBytes(localAddrByte, data, 0, 0 + 33, localAddrByte.length);
		// local ip
		byte[] localIpByte = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpByte, data, 0, 0 + 53, localIpByte.length);

		// timestamp
		/*
		 * byte[] timestampByte = new byte[4]; timestampByte[3] =
		 * (byte)((timestamp>>24) & 0xff); timestampByte[2] =
		 * (byte)((timestamp>>16) & 0xff); timestampByte[1] =
		 * (byte)((timestamp>>8) & 0xff); timestampByte[0] = (byte)((timestamp)
		 * & 0xff); copyBytes(timestampByte, data, 0, 57, timestampByte.length);
		 */
		data[57] = (byte) ((timestamp) & 0xff);
		data[58] = (byte) ((timestamp >> 8) & 0xff);
		data[59] = (byte) ((timestamp >> 16) & 0xff);
		data[60] = (byte) ((timestamp >> 24) & 0xff);

		// data type
		/*
		 * byte[] dataTypeByte = new byte[2]; dataTypeByte[1] = (byte)((dataType
		 * >> 8) & 0xff); dataTypeByte[0] = (byte)((dataType) & 0xff);
		 * copyBytes(dataTypeByte, data, 0, 61, dataTypeByte.length);
		 */
		data[61] = (byte) ((dataType) & 0xff);
		data[62] = (byte) ((dataType >> 8) & 0xff);

		// frame no
		/*
		 * byte[] frameNoByte = new byte[2]; frameNoByte[1] = (byte)((frameNo >>
		 * 8) & 0xff); frameNoByte[0] = (byte)(frameNo & 0xff);
		 * copyBytes(frameNoByte, data, 0, 63, frameNoByte.length);
		 */
		data[63] = (byte) ((frameNo) & 0xff);
		data[64] = (byte) ((frameNo >> 8) & 0xff);

		// frame len
		/*
		 * byte[] frameLenByte = new byte[4]; frameLenByte[3] = (byte)((frameLen
		 * >> 24) & 0xff); frameLenByte[2] = (byte)((frameLen >> 16) & 0xff);
		 * frameLenByte[1] = (byte)((frameLen >> 8) & 0xff); frameLenByte[0] =
		 * (byte)((frameLen) & 0xff); copyBytes(frameLenByte, data, 0, 65,
		 * frameLenByte.length);
		 */
		data[65] = (byte) ((frameLen) & 0xff);
		data[66] = (byte) ((frameLen >> 8) & 0xff);
		data[67] = (byte) ((frameLen >> 16) & 0xff);
		data[68] = (byte) ((frameLen >> 24) & 0xff);

		// total package
		/*
		 * byte[] totalPackageByte = new byte[2]; totalPackageByte[1] =
		 * (byte)((totalPackages >> 8) & 0xff); totalPackageByte[0] =
		 * (byte)((totalPackages) & 0xff); copyBytes(totalPackageByte, data, 0,
		 * 69, totalPackageByte.length);
		 */
		data[69] = (byte) ((totalPackages) & 0xff);
		data[70] = (byte) ((totalPackages >> 8) & 0xff);

		// curr package
		/*
		 * byte[] currpackageByte = new byte[2]; currpackageByte[1] =
		 * (byte)((currPackage >> 8) & 0xff); currpackageByte[0] =
		 * (byte)((currPackage) & 0xff); copyBytes(currpackageByte, data, 0, 71,
		 * currpackageByte.length);
		 */
		data[71] = (byte) ((currPackage) & 0xff);
		data[72] = (byte) ((currPackage >> 8) & 0xff);

		// data len
		/*
		 * byte[] dataLenByte = new byte[2]; dataLenByte[1] = (byte)((dataLen >>
		 * 8) & 0xff); dataLenByte[0] = (byte)((dataLen) & 0xff);
		 * copyBytes(dataLenByte, data, 0, 73, dataLenByte.length);
		 */
		data[73] = (byte) ((dataLen) & 0xff);
		data[74] = (byte) ((dataLen >> 8) & 0xff);

		// pack len
		/*
		 * byte[] packLenByte = new byte[2]; packLenByte[1] = (byte)((packLen >>
		 * 8) & 0xff); packLenByte[0] = (byte)((packLen) & 0xff);
		 * copyBytes(packLenByte, data, 0, 75, packLenByte.length);
		 */
		data[75] = (byte) ((packLen) & 0xff);
		data[76] = (byte) ((packLen >> 8) & 0xff);

		data[77] = (byte) (32 & 0xff);
		data[78] = (byte) (0 & 0xff);
		data[79] = (byte) (34 & 0xff);
		data[80] = (byte) (0 & 0xff);

		// audioData
		copyBytes(audioData, data, 0, 86, audioData.length);

		return 150;
	}

	public static byte[] createOpenLock(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];
		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.REMOTEOPENLOCK.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrByte = remoteAddr.getBytes();
		copyBytes(remoteAddrByte, data, 0, 0 + 9, remoteAddrByte.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local Ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		return data;
	}

	/**
	 * create a UDP packet, ask callend
	 * 
	 * @param localAddr
	 * @param localIp
	 * @param remoteAddr
	 * @param remoteIp
	 * @return
	 * @throws UnknownHostException
	 */
	public static byte[] createVideotalkAskCallend(String localAddr, String localIp, String remoteAddr, String remoteIp)
			throws UnknownHostException {
		byte[] data = new byte[57];

		// header bytes
		byte[] headerBytes = "XXXCID".getBytes();
		copyBytes(headerBytes, data, 0, 0, headerBytes.length);

		// CMD
		data[0 + 6] = (byte) (Constants.UDP_CMD.VIDEOTALK.getValue() & 0xff);
		// cmd property
		data[0 + 7] = (byte) (Constants.SUB_CMD.ASK.getValue() & 0xff);
		// sub cmd
		data[0 + 8] = (byte) (Constants.SUB_CMD.CALLEND.getValue() & 0xff);

		// remote addr
		byte[] remoteAddrByte = remoteAddr.getBytes();
		copyBytes(remoteAddrByte, data, 0, 0 + 9, remoteAddrByte.length);

		// remote ip
		byte[] remoteIpBytes = InetAddress.getByName(remoteIp).getAddress();
		copyBytes(remoteIpBytes, data, 0, 0 + 29, remoteIpBytes.length);

		// local addr
		byte[] localAddrBytes = localAddr.getBytes();
		copyBytes(localAddrBytes, data, 0, 0 + 33, localAddrBytes.length);

		// local ip
		byte[] localIpBytes = InetAddress.getByName(localIp).getAddress();
		copyBytes(localIpBytes, data, 0, 0 + 53, localIpBytes.length);

		return data;
	}

	/**
	 * determine whether the header is XXXCID
	 * 
	 * @param header
	 * @return
	 */
	public static boolean isHeaderRight(byte[] header) {

		if (header == null) {
			return false;
		} else if ("XXXCID".equals(new String(header))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * determine whether the header is head
	 * 
	 * @param header
	 * @return
	 */
	public static boolean isControlHeaderRight(byte[] header) {

		if (header == null) {
			return false;
		} else if ("head".equals(new String(header))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * copy bytes from srcStart index for src bytes to dstStart index for dst
	 * bytes with data of he length
	 * 
	 * @param src
	 * @param dst
	 * @param srcStart
	 * @param dstStart
	 * @param length
	 */
	public static void copyBytes(byte[] src, byte[] dst, int srcStart, int dstStart, int length) {
		for (int i = 0; i < length; i++) {
			dst[dstStart + i] = src[srcStart + i];
		}
	}

}
