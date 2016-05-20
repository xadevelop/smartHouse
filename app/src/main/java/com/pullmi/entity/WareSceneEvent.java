package com.pullmi.entity;

import java.io.Serializable;

public class WareSceneEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9164065895025538399L;
	public byte[] sceneName;
	public byte devCnt;
	public byte eventld;
	public byte rev2;
	public byte rev3;
	public WareSceneDevItem[] itemAry;
}
