package com.example.smarthouse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.utils.LogUtils;

import android.R.array;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class rcuInfoActivity extends AdAbstractActivity {

	private LinearLayout createLayout;
	private EditText idText, passText, nameText;
	private Button confirm, cancel;
	private List<RcuInfo> mRcuInfos;
	private boolean isNewGw = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initComponent();

		mRcuInfos = getGwList();
		if (mRcuInfos == null) {
			mRcuInfos = new ArrayList<RcuInfo>();
		}

		/*
		 * SharedPreferences sharedPreferences = getSharedPreferences("profile",
		 * Activity.MODE_PRIVATE); // 使用getString方法获得value，注意第2个参数是value的默认值
		 * idText.setText(sharedPreferences.getString("devid", ""));
		 * passText.setText(sharedPreferences.getString("devpass", ""));
		 */
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.rcu));

		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.rcuinfo_input, null);
		mRootView.addView(createLayout, FF);

		idText = (EditText) findViewById(R.id.id);
		passText = (EditText) findViewById(R.id.pass);
		//nameText = (EditText) findViewById(R.id.name);

		confirm = (Button) findViewById(R.id.confirm);
		cancel = (Button) findViewById(R.id.cancel);

		confirm.setOnTouchListener(TouchDark);
		cancel.setOnTouchListener(TouchDark);

		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	private List<RcuInfo> getGwList() {

		SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
		String jsondata = sharedPreferences.getString("list", "null");
		Gson gson = new Gson();
		if (!jsondata.equals("null")) {
			List<RcuInfo> list = gson.fromJson(jsondata, new TypeToken<List<RcuInfo>>() {
			}.getType());
			for (int i = 0; i < list.size(); i++) {
				RcuInfo p = list.get(i);
				System.out.println(CommonUtils.getGBstr(p.getName()));
			}

			return list;
		} else {
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confirm:
			String id = idText.getText().toString();
			String pass = passText.getText().toString();
			//String name = nameText.getText().toString();

			if (id.equals("") || pass.equals("")) {
				Toast.makeText(this, "输入信息不能为空", 0).show();
			}
			RcuInfo info = new RcuInfo();
			info.setDevUnitID(CommonUtils.hexStringToBytes(id));
			info.setDevUnitPass(pass.getBytes());
			info.setName("".getBytes());
	
			for (int i = 0; i < mRcuInfos.size(); i++) {
				if (CommonUtils.hexStringToBytes(id).equals(mRcuInfos.get(i).getDevUnitID())
						&& Arrays.equals(pass.getBytes(), mRcuInfos.get(i).getDevUnitPass())) {
					Toast.makeText(this, "当前联网模块已存在，请重新输入", 0).show();
					isNewGw = false;
					break;
				}else {
					isNewGw = true;
				}
			}
			if (isNewGw) {
				mRcuInfos.add(info);
				saveInfo(mRcuInfos);
			}

			this.finish();
			break;
		case R.id.cancel:
			this.finish();
			break;
		default:
			break;
		}
	}

	public void saveInfo(List<RcuInfo> datas) {
		Gson gson = new Gson();

		String str = gson.toJson(datas);

		SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("list", str);
		editor.commit();
	}
}
