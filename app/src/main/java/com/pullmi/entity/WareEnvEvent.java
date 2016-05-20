package com.pullmi.entity;

import java.io.Serializable;

public class WareEnvEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int datSta;
	public int datEnd;
	public byte[] eventName;
	public byte DenCnt;
	public byte eventld;
	public byte envType;
	public byte rev3;
	public WareEnvDevItem itemAry[];
}
