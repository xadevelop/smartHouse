package com.pullmi.entity;

import java.io.Serializable;

public class WareBoardChnout implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3053533526442278099L;
	public byte[] devUnitID; //12
	public byte[] boardName; //8
	public byte boardType;
	public byte chnCnt;
	public byte bOnline;
	public byte rev2;
	public byte chnName[][];
}
