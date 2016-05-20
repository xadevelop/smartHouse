package com.pullmi.entity;

public class VideoPacket {

	private String hostAddr;
	private String hostIp;
	private String assiAddr;
	private String assiIp;
	private int	   timestamp;
	private int    dataType;
	private int    frameNo;
	private int	   frameLen;
	private int	   totalPackage;
	private int    currPackage;
	private int    dataLen;
	private int    packLen;
	
	public VideoPacket() {}
	
	public VideoPacket(
			String hostAddr,
			String hostIp,
			String assiAddr,
			String assiIp,
			int timestamp,
			int dataType,
			int frameNo,
			int frameLen,
			int totalPackage,
			int currPackage,
			int dataLen,
			int packLen
			) {
		this.hostAddr = hostAddr;
		this.hostIp = hostIp;
		this.assiAddr = assiAddr;
		this.assiIp = assiIp;
		this.timestamp = timestamp;
		this.dataType = dataType;
		this.frameNo = frameNo;
		this.frameLen = frameLen;
		this.totalPackage = totalPackage;
		this.currPackage = currPackage;
		this.dataLen = dataLen;
		this.packLen = packLen;
	}

	public String getHostAddr() {
		return hostAddr;
	}

	public void setHostAddr(String hostAddr) {
		this.hostAddr = hostAddr;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getAssiAddr() {
		return assiAddr;
	}

	public void setAssiAddr(String assiAddr) {
		this.assiAddr = assiAddr;
	}

	public String getAssiIp() {
		return assiIp;
	}

	public void setAssiIp(String assiIp) {
		this.assiIp = assiIp;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getFrameNo() {
		return frameNo;
	}

	public void setFrameNo(int frameNo) {
		this.frameNo = frameNo;
	}

	public int getFrameLen() {
		return frameLen;
	}

	public void setFrameLen(int frameLen) {
		this.frameLen = frameLen;
	}

	public int getTotalPackage() {
		return totalPackage;
	}

	public void setTotalPackage(int totalPackage) {
		this.totalPackage = totalPackage;
	}

	public int getCurrPackage() {
		return currPackage;
	}

	public void setCurrPackage(int currPackage) {
		this.currPackage = currPackage;
	}

	public int getDataLen() {
		return dataLen;
	}

	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

	public int getPackLen() {
		return packLen;
	}

	public void setPackLen(int packLen) {
		this.packLen = packLen;
	}
	
}
