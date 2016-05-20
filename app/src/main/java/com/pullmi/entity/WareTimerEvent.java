package com.pullmi.entity;

public class WareTimerEvent {
	public byte[] timSta;
	public byte[] timEnd;
	public byte[] timerName;
	public byte devCnt;
	public byte eventld;
	public byte rev2;
	public byte rev3;
	public WareTimerEvent itemAry[];
	
	public WareTimerEvent(){}
	
	public WareTimerEvent(
			byte[] timSta,
			byte[] timEnd,
			byte[] timerName,
			byte devCnt,
			byte eventld,
			byte rev2,
			byte rev3){
		
		this.timSta = timSta;
		this.timEnd = timEnd;
		this.timerName = timerName;
		this.devCnt = devCnt;
		this.eventld = eventld;
	};
}
