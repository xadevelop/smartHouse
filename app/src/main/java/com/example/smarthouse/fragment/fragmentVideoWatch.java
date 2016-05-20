package com.example.smarthouse.fragment;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.VideoWatchActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class fragmentVideoWatch extends BackHandledFragment implements OnClickListener {
	private View view;
	private Button watch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_video_talk, container, false);
		
		watch = (Button)view.findViewById(R.id.watchVideo);
		watch.setOnClickListener(this);
		
		return view;
	}

	private long exitTime = 0;

	@Override
	public boolean onBackPressed() {

		if (System.currentTimeMillis() - exitTime > 2000) {
			Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
			// 将系统当前的时间赋值给exitTime
			exitTime = System.currentTimeMillis();
		} else {
			HomeActivity parentActivity = (HomeActivity) getActivity();
			parentActivity.saveObject("scene.dat", 1);
			getActivity().finish();
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.watchVideo:
			VideoWatchActivity.sendNsOrderAsk();
			break;
		default:
			break;
		}
	}
}
