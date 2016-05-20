package com.pullmi.entity;

public class WareChnOpItem {
	public byte[] cpuid; //12输出板
	public byte devType;
	public byte devid;
	
	public byte[] devUnitID; // 12　输入板
	public byte keyDownValid; // 按键板最多６个按键
	public byte keyUpValid; // 按键板最多６个按键
	public int rev1;
	public byte[] keyDownCmd; // 6
	public int rev2;
	public byte[] keyUpCmd; // 6
	public int rev3;
}
