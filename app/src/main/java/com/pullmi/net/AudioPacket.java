package com.pullmi.net;

public class AudioPacket {

	public int frameLen;
	public byte[] dataBuff;
	
	public AudioPacket() {
		frameLen = 0;
		dataBuff = null;
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
	
}
