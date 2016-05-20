package com.example.smarthouse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.GPIO_INPUT_INFO;
import com.pullmi.entity.GPIO_OUTPUT_INFO;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareBoardKeyInput;
import com.pullmi.entity.WareChnOpItem;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareKeyOpItem;

import android.content.Intent;
import android.content.ClipData.Item;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class activityRcuOutAddKey extends AdAbstractActivity {

	private LinearLayout createLayout;
	private Spinner devUnitIdSp, keyNameSp, keyOpSp;
	private Button save;
	private List<String> devUidList, keyNameList, keyOpList;
	private List<WareBoardKeyInput> mWareBoardKeyInputs;
	private ArrayAdapter<String> devUnitIdadapter, keyNameadapter, keyOpadapter;
	private int currentKeyID, currentOp;
	private int currentID;
	private List<GPIO_OUTPUT_INFO> infos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		// 获取该Intent所携带的数据
		Bundle bundle = intent.getExtras();
		// 从bundle数据包中取出数据
		currentID = bundle.getInt("index");
		initComponent();
	}

	private void initComponent() {
		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_rcuoutkey_add, null);
		mRootView.addView(createLayout, FF);

		devUnitIdSp = (Spinner) findViewById(R.id.Spinner1);
		keyNameSp = (Spinner) findViewById(R.id.Spinner2);
		keyOpSp = (Spinner) findViewById(R.id.Spinner4);

		save = (Button) findViewById(R.id.SpinnerSave);
		save.setOnClickListener(this);
		save.setOnTouchListener(TouchDark);

		initData();

		devUnitIdadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				devUidList);
		keyNameadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				keyNameList);
		keyOpadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				keyOpList);

		devUnitIdadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		keyNameadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		keyOpadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		devUnitIdSp.setAdapter(devUnitIdadapter);
		keyNameSp.setAdapter(keyNameadapter);
		keyOpSp.setAdapter(keyOpadapter);

		devUnitIdSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				keyNameList.clear();

				String cpuid = null;
				String uidName = devUidList.get(arg2);
				for (int i = 0; i < mWareBoardKeyInputs.size(); i++) {
					if (uidName.equals(CommonUtils.getGBstr(mWareBoardKeyInputs.get(i).boardName))) {
						cpuid = CommonUtils.printHexString(mWareBoardKeyInputs.get(i).devUnitID);
					}
				}
				List<WareBoardKeyInput> lst = removeDuplicate(mWareBoardKeyInputs);

				for (int i = 0; i < lst.size(); i++) {
					String uid = CommonUtils.printHexString(lst.get(i).devUnitID);
					if (uid.equals(cpuid)) {
						for (int j = 0; j < lst.get(i).keyCnt; j++) {
							try {
								keyNameList.add(new String(lst.get(i).keyName[j], "GB2312"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

				keyNameadapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		keyNameSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				currentKeyID = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		keyOpSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentOp = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initData() {

		keyOpList = new ArrayList<String>();
		devUidList = new ArrayList<String>();
		keyNameList = new ArrayList<String>();

		keyOpList.add("按下");
		keyOpList.add("弹起");

		mWareBoardKeyInputs = HomeActivity.mBoardKeyInputs;
		List<WareBoardKeyInput> list = removeDuplicate(mWareBoardKeyInputs);
		// 初始化devCpuId
		for (int i = 0; i < list.size(); i++) {
			String cpuName = CommonUtils.getGBstr(list.get(i).boardName);
			devUidList.add(cpuName);
		}

		keyNameList.add("按键1");
		keyNameList.add("按键2");
		keyNameList.add("按键3");
		keyNameList.add("按键4");
		keyNameList.add("按键5");
		keyNameList.add("按键6");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.SpinnerSave:
			setAddInfoToList();
			createSendInfo();
			break;
		default:
			break;
		}
	}

	private void setAddInfoToList() {
		// TODO Auto-generated method stub

		String uid = null;
		String uidName = devUnitIdSp.getSelectedItem().toString();
		for (int i = 0; i < mWareBoardKeyInputs.size(); i++) {
			if (uidName.equals(CommonUtils.getGBstr(mWareBoardKeyInputs.get(i).boardName))) {
				uid = CommonUtils.printHexString(mWareBoardKeyInputs.get(i).devUnitID);
			}
		}
		// int keyOp = Integer.parseInt(keyOpSp.getSelectedItem().toString());
		byte[] temp = new byte[12];
		for (int i = 0; i < 12; i++) {
			temp[i] = 0;
		}
		infos = HomeActivity.mgOuts;
		for (int i = 0; i < 4; i++) {
			if (Arrays.equals(infos.get(currentID).keyUid[i], temp)) {
				infos.get(currentID).keyUid[i] = CommonUtils.hexStringToBytes(uid);
				
				infos.get(currentID).keyNum[i] = (byte) currentKeyID;
				infos.get(currentID).keyNumOP[i] = (byte) currentOp;
				break;
			}
		}
	}

	private void createSendInfo() {

		byte[] data = CommonUtils.createOutItem(infos);

		GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(GlobalVars.getDstip(),
				CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_saveIOSet_output.getValue(), 0, 0, data,
				data.length));

		CommonUtils.sendMsg();
	}

	public static List<WareBoardKeyInput> removeDuplicate(List<WareBoardKeyInput> list) {
		List<WareBoardKeyInput> devs = new ArrayList<WareBoardKeyInput>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (new String(devs.get(j).devUnitID).equals(new String(devs.get(i).devUnitID))) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}
}
