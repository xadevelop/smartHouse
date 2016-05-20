package com.pullmi.net;

public class VideoPacket {

	public int frameLen;
	public byte[] dataBuff;
	public int totalPackages;
	public int lastPackage;
	public int frameNo;
	public byte[] pktValid;
	
	public VideoPacket() {
		frameLen = 0;
		dataBuff = null;
		totalPackages = 0;
		lastPackage = 0;
		frameNo = -1;
	}

	public int getFrameLen() {
		return frameLen;
	}

	public void setFrameLen(int frameLen) {
		this.frameLen = frameLen;
	}

	public byte[] getDataBuff() {
		return dataBuff;
	}

	public void setDataBuff(byte[] dataBuff) {
		this.dataBuff = dataBuff;
	}

	public int getTotalPackages() {
		return totalPackages;
	}

	public void setTotalPackages(int totalPackages) {
		this.totalPackages = totalPackages;
	}

	public int getLastPackage() {
		return lastPackage;
	}

	public void setLastPackage(int lastPackage) {
		this.lastPackage = lastPackage;
	}

	public int getFrameNo() {
		return frameNo;
	}

	public void setFrameNo(int frameNo) {
		this.frameNo = frameNo;
	}

	
	
	
}
