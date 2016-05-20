package com.pullmi.app;

import android.content.Context;

public class GlobalVars {

	private static Context context;
	private static int sn;
	private static String devid, devpass;
	private static String dstip;
	private static byte[] senddata;
	private static boolean isTwoPane; // 是否是双页模式

	public static void init(Context c) {
		context = c;
	}

	public static Context getContext() {
		return context;
	}

	public static int getSn() {
		return sn;
	}

	public static void setSn(int num) {
		sn = num;
	}

	public static String getDevid() {
		return devid;
	}

	public static void setDevid(String devid) {
		GlobalVars.devid = devid;
	}

	public static String getDevpass() {
		return devpass;
	}

	public static void setDevpass(String devpass) {
		GlobalVars.devpass = devpass;
	}

	public static String getDstip() {
		return dstip;
	}

	public static void setDstip(String dstip) {
		GlobalVars.dstip = dstip;
	}

	public static byte[] getSenddata() {
		return senddata;
	}

	public static void setSenddata(byte[] senddata) {
		GlobalVars.senddata = senddata;
	}

	public static boolean isTwoPane() {
		return isTwoPane;
	}

	public static void setTwoPane(boolean isTwoPane) {
		GlobalVars.isTwoPane = isTwoPane;
	}
}
