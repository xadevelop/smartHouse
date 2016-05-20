package com.pullmi.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class CommonUtils {
	
	private static final String TAG = "CommonUtils";

	public static String getLocalRoomNo() {
		return "S0001010809000000000";
	}
	
	public static InetAddress getLocalIp(Context mContext) throws UnknownHostException {
		WifiManager wm = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
		WifiInfo wifiInfo = wm.getConnectionInfo();
		int ip = wifiInfo.getIpAddress();
		
		String ipStr = (ip & 0xff) + "." + ((ip >> 8) & 0xff) + "." + ((ip >> 16) & 0xff) + "." + ((ip >> 24) & 0xff);
		
		LogUtils.LOGE(TAG, "roomip: " + ipStr);
		
		return InetAddress.getByName(ipStr);
	}
}
