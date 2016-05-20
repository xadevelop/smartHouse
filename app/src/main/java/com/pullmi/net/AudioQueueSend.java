package com.pullmi.net;

import java.util.LinkedList;

public class AudioQueueSend {

	LinkedList<AudioPacketSend> queuel = new LinkedList<AudioPacketSend>();
	boolean available = false;
	
	private final int INT_QUEUE_MAXSIZE = 100;
	
	public int getQueueMaxSize() {
		return INT_QUEUE_MAXSIZE;
	}

	public LinkedList<AudioPacketSend> getQueuel() {
		return queuel;
	}

	public void setQueuel(LinkedList<AudioPacketSend> queuel) {
		this.queuel = queuel;
	}
	
	public int getQueuelSize() {
		return queuel.size();
	}
	
	public synchronized AudioPacketSend getQueueFirstData() {
		try {
			if(queuel.isEmpty() || queuel.size() <=0) {
				return null;
			}
			
			AudioPacketSend packet = queuel.get(0);
			queuel.remove(0);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void addAudioPacket(AudioPacketSend audioPacket) {
		try {
			if(queuel.size() > INT_QUEUE_MAXSIZE) {
				queuel.clear();
			}
			queuel.add(audioPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearQueuel() {
		queuel.clear();
	}
}
