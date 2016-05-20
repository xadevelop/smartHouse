package com.pullmi.entity;

public class GPIO_INPUT_INFO {
	public byte[] defName; // 12
	public GPIO_INFO[] info;//
	public RUN_DEV_ITEM[] item;// 2
	public byte[] itemRTime;// 2
	public byte[] itemRTimer; // 2
	public RUN_IOOUT_ITEM[] outOP;
	public byte[] outOPRTime; // 2
	public byte[] outOPRTimer; // 2
	public byte oldLevel; // 上一个电平状态
	public byte timout;
	public byte rev;
	public byte validLevel;// 有效电平 0低有效 1高有效
	public byte[] flg; // 4

	public byte[] getDefName() {
		return defName;
	}

	public void setDefName(byte[] defName) {
		this.defName = defName;
	}

	public byte[] getItemRTime() {
		return itemRTime;
	}

	public void setItemRTime(byte[] itemRTime) {
		this.itemRTime = itemRTime;
	}

	public byte[] getItemRTimer() {
		return itemRTimer;
	}

	public void setItemRTimer(byte[] itemRTimer) {
		this.itemRTimer = itemRTimer;
	}

	public byte[] getOutOPRTime() {
		return outOPRTime;
	}

	public void setOutOPRTime(byte[] outOPRTime) {
		this.outOPRTime = outOPRTime;
	}

	public byte[] getOutOPRTimer() {
		return outOPRTimer;
	}

	public void setOutOPRTimer(byte[] outOPRTimer) {
		this.outOPRTimer = outOPRTimer;
	}

	public byte getOldLevel() {
		return oldLevel;
	}

	public void setOldLevel(byte oldLevel) {
		this.oldLevel = oldLevel;
	}

	public byte getTimout() {
		return timout;
	}

	public void setTimout(byte timout) {
		this.timout = timout;
	}

	public byte getRev() {
		return rev;
	}

	public void setRev(byte rev) {
		this.rev = rev;
	}

	public byte getValidLevel() {
		return validLevel;
	}

	public void setValidLevel(byte validLevel) {
		this.validLevel = validLevel;
	}

	public byte[] getFlg() {
		return flg;
	}

	public void setFlg(byte[] flg) {
		this.flg = flg;
	}
}
