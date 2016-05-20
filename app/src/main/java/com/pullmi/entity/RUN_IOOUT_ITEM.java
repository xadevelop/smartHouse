package com.pullmi.entity;

public class RUN_IOOUT_ITEM {
	public byte nOutput;//
	public byte inputOP;// 输入信号对输出信号的操作 0 关闭 1 打开 2 翻转
	public byte rev1;
	public byte valid; // 本项是否有效 1有效

	public byte getnOutput() {
		return nOutput;
	}

	public void setnOutput(byte nOutput) {
		this.nOutput = nOutput;
	}

	public byte getInputOP() {
		return inputOP;
	}

	public void setInputOP(byte inputOP) {
		this.inputOP = inputOP;
	}

	public byte getRev1() {
		return rev1;
	}

	public void setRev1(byte rev1) {
		this.rev1 = rev1;
	}

	public byte getValid() {
		return valid;
	}

	public void setValid(byte valid) {
		this.valid = valid;
	}
}
