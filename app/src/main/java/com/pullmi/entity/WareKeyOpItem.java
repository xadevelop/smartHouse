package com.pullmi.entity;

import java.io.Serializable;

public class WareKeyOpItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3302607752463272412L;
	public byte[] devUnitID; //12
	public byte devType;
	public byte devId;
	public byte keyOpCmd;
	public byte keyOp;
	public byte index;
}
