package com.pullmi.net;

import java.util.LinkedList;

public class AudioQueue {

	LinkedList<AudioPacket> queuel = new LinkedList<AudioPacket>();
	boolean available = false;
	
	private final int INT_QUEUE_MAXSIZE = 64;
	
	public int getQueueMaxSize() {
		return INT_QUEUE_MAXSIZE;
	}

	public LinkedList<AudioPacket> getQueuel() {
		return queuel;
	}

	public void setQueuel(LinkedList<AudioPacket> queuel) {
		this.queuel = queuel;
	}
	
	public int getQueuelSize() {
		return queuel.size();
	}
	
	public synchronized AudioPacket getQueueFirstData() {
		try {
			if(queuel.isEmpty() || queuel.size() <=0) {
				return null;
			}
			
			AudioPacket packet = queuel.get(0);
			queuel.remove(0);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void addAudioPacket(AudioPacket audioPacket) {
		try {
			if(queuel.size() > INT_QUEUE_MAXSIZE) {
				queuel.clear();
				return;
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
