package com.pullmi.entity;

import java.io.Serializable;

public class WareCurtain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public WareDev dev;
	public byte bOnOff;
	public byte timRun;
	public int powChn;

	public byte getOnOff() {
		return bOnOff;
	}

	public byte getTimRun() {
		return timRun;
	}

	public WareDev getDev() {
		return dev;
	}
}
