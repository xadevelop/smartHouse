package com.pullmi.entity;

public class GPIO_OUTPUT_INFO {
	public byte[] defName; //12
	GPIO_INFO info;//	
	public byte[][] keyUid;		//按键所在模块 4
	public byte[] keyNum;			//按键号  4
	public byte[] keyNumOP;		//按键弹起还是按下 4
	public byte[] opTime; 
	public byte[] opTimer;
	public byte outAct;			//输出动作  0拉低    1拉高    2 翻转
	public byte rev;
	public int rev2;
	public byte[] flg; 			//4
	
}
