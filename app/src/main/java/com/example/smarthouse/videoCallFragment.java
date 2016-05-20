package com.example.smarthouse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.pullmi.common.CommonUtils;
import com.pullmi.service.TalkService;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class videoCallFragment extends Fragment implements OnClickListener {

	private View view;
	private Button openDoor, watchVideo, hht;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.fragment_video_talk, container, false);

		openDoor = (Button) view.findViewById(R.id.openDoor);
		watchVideo = (Button) view.findViewById(R.id.watchVideo);
		//hht = (Button) view.findViewById(R.id.hht);

		openDoor.setOnClickListener(this);
		watchVideo.setOnClickListener(this);
		hht.setOnClickListener(this);

		return view;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.openDoor:
			openDoor();
			break;
		case R.id.watchVideo:
			VideoWatchActivity.sendNsOrderAsk();
			break;
		default:
			break;
		}
	}

	private void openDoor() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] openlockData = CommonUtils.createOpenLock(
							CommonUtils.getLocalAddr(),
							CommonUtils.getLocalIp(), TalkService.mDoorAddr,
							TalkService.mDoorIp);
					DatagramPacket openLockPacket = new DatagramPacket(
							openlockData, openlockData.length,
							InetAddress.getByName(TalkService.mDoorIp),
							TalkService.PORT);
					DatagramSocket socket = new DatagramSocket();
					socket.send(openLockPacket);
					socket.close();
					socket = null;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
