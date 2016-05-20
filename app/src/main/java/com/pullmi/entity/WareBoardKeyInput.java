package com.pullmi.entity;

import java.io.Serializable;

public class WareBoardKeyInput implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8372972355485111907L;
	public byte[] devUnitID; //12
	public byte[] boardName; //8
	public byte boardType;
	public byte keyCnt;
	public byte bResetKey;
	public byte ledBkType;
	public byte[][] keyName; //6-12
	public byte[] keyAllCtrlType; //6
}
