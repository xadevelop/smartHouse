package com.pullmi.net;

import java.util.LinkedList;

public class VideoQueue {

	LinkedList<VideoPacket> queuel = new LinkedList<VideoPacket>();
	
	private final int INT_QUEUE_MAXSIZE = 8;
	
	public int getQueueMaxSize() {
		return INT_QUEUE_MAXSIZE;
	}

	public LinkedList<VideoPacket> getQueuel() {
		return queuel;
	}

	public void setQueuel(LinkedList<VideoPacket> queuel) {
		this.queuel = queuel;
	}
	
	public int getQueuelSize() {
		return queuel.size();
	}
	
	public synchronized VideoPacket getQueueFirstData() {
		try {
			if(queuel.isEmpty() || queuel.size() <= 0) {
				return null;
			}
			
			VideoPacket packet = queuel.get(0);
			queuel.remove(0);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized void addVideoPacket(VideoPacket packet) {
		try {
			if(queuel.size() >= INT_QUEUE_MAXSIZE) {
				//queuel.clear();
				return;
			}
			queuel.add(packet);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public synchronized boolean isVideoPacketFull() 
	{
		boolean ret = false;
		try 
		{
			if(queuel.size()>=INT_QUEUE_MAXSIZE)
				ret = true;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	public void clearQueue() {
		queuel.clear();
	}
}
