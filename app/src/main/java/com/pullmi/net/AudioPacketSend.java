package com.pullmi.net;

public class AudioPacketSend {

	public int frameLen;
	public short[] dataBuff;
	
	public AudioPacketSend() {
		frameLen = 0;
		dataBuff = null;
	}

	public int getFrameLen() {
		return frameLen;
	}

	public void setFrameLen(int frameLen) {
		this.frameLen = frameLen;
	}

	public short[] getDataBuff() {
		return dataBuff;
	}

	public void setDataBuff(short[] dataBuff) {
		this.dataBuff = dataBuff;
	}
	
}
