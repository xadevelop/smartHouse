package com.pullmi.entity;

public class Msg {
	private int id; // 消息包号
	private byte[] msg; // 消息内容
	private int time; // 发送次数
	private long lastsendtime; // 最后发送时间

	 public Msg(byte[] msg, int id) {  
         this.msg = msg;  
         this.id = id;
         this.time = 0;
     }  
	 
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getMsg() {
		return msg;
	}

	public void setMsg(byte[] msg) {
		this.msg = msg;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public long getLastsendtime() {
		return lastsendtime;
	}

	public void setLastsendtime(long lastsendtime) {
		this.lastsendtime = lastsendtime;
	}
}
