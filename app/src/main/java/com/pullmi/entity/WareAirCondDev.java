package com.pullmi.entity;

import java.io.Serializable;

public class WareAirCondDev implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public WareDev dev;
	public byte bOnOff;
	public byte selMode;
	public byte selTemp;
	public byte selSpd;
	public byte selDirect;
	public byte rev1;
	public int powChn;
}
