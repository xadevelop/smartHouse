package com.example.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareSceneEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

public class sceneListViewAdapter extends BaseAdapter {

	private Context context;
	ArrayList<String> list;

	private List<WareSceneEvent> mWareSceneEvents;
	byte[] devBuff;

	class ViewHolder {
		Button button;
	}

	public sceneListViewAdapter(Context context, ArrayList<String> lst) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = lst;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 页面
		ViewHolder holder;
		LayoutInflater inflater = LayoutInflater.from(context);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.scene_list_item, null);
			holder = new ViewHolder();

			holder.button = (Button) convertView.findViewById(R.id.sceneName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.button.setText(list.get(position));
		//holder.button.setOnTouchListener(CommonUtils.TouchDark);
		holder.button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				createSceneEvents(1, position);
			}
		});
		
		holder.button.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				createSceneEvents(0, position);
				return true;
			}
		});

		return convertView;
	}

	private void createSceneEvents(int flag, int postion) {
		mWareSceneEvents = HomeActivity.mSceneEvents;
		int pos = -1;
		for (int i = 0; i < mWareSceneEvents.size(); i++) {
			if (mWareSceneEvents.get(i).eventld == postion) {
				pos = i;
			}
		}
		if (pos == -1) {
			return;
		}
		if (flag == 0 && pos == 0) {
			Toast.makeText(context, "该情景模式不能删除", 0).show();
			return;
		}
		if (flag == 0 && pos == 1) {
			Toast.makeText(context, "该情景模式不能删除", 0).show();
			return;
		}

		devBuff = CommonUtils.createSceneEvent(mWareSceneEvents.get(pos));

		if (flag == 0) {
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delSceneEvents.getValue(),
					0, 0, devBuff, devBuff.length));
			CommonUtils.sendMsg();
		} else if (flag == 1) {
			GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
					GlobalVars.getDstip(), CommonUtils.getLocalIp(),
					UdpProPkt.E_UDP_RPO_DAT.e_udpPro_exeSceneEvents.getValue(),
					0, 0, devBuff, devBuff.length));
			CommonUtils.sendMsg();
		}
	}
}