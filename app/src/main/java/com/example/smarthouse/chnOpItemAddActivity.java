package com.example.smarthouse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pullmi.common.CommonUtils;
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

public class chnOpItemAddActivity extends AdAbstractActivity {

	private LinearLayout createLayout;
	private Spinner devUnitIdSp, keyNameSp, keyOpCmdSp, keyOpSp;
	private Button save;
	private List<String> devUidList, keyNameList, keyOpCmdList, keyOpList;
	private List<WareBoardKeyInput> mWareBoardKeyInputs;
	private ArrayAdapter<String> devUnitIdadapter, keyNameadapter, keyOpCmdadapter, keyOpadapter;
	private int currentKeyID, currentKeyCmdOp, currentOp;

	private byte[] devUnitID;
	private String devName;
	private int devtype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		devUnitID = intent.getExtras().getByteArray("devUnitID");
		devName = intent.getExtras().getString("devName");

		HomeActivity.mChnOpAddItems.clear();
		for (int i = 0; i < HomeActivity.mWareDevs.size(); i++) {
			if (Arrays.equals(devUnitID, HomeActivity.mWareDevs.get(i).canCpuId)
					&& devName.equals(CommonUtils.getGBstr(HomeActivity.mWareDevs.get(i).devName))) {
				devtype = HomeActivity.mWareDevs.get(i).devType;
			}
		}

		initComponent();
	}

	private void initComponent() {
		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_chnopitem_add, null);
		mRootView.addView(createLayout, FF);

		devUnitIdSp = (Spinner) findViewById(R.id.Spinner1);
		keyNameSp = (Spinner) findViewById(R.id.Spinner2);
		keyOpCmdSp = (Spinner) findViewById(R.id.Spinner3);
		keyOpSp = (Spinner) findViewById(R.id.Spinner4);

		save = (Button) findViewById(R.id.SpinnerSave);
		save.setOnClickListener(this);
		save.setOnTouchListener(TouchDark);

		initData();

		devUnitIdadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
				devUidList);
		keyNameadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
				keyNameList);
		keyOpCmdadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				keyOpCmdList);
		keyOpadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyOpList);

		devUnitIdadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		keyNameadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		keyOpCmdadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		keyOpadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		
		devUnitIdSp.setAdapter(devUnitIdadapter);
		keyNameSp.setAdapter(keyNameadapter);
		keyOpCmdSp.setAdapter(keyOpCmdadapter);
		keyOpSp.setAdapter(keyOpadapter);

		if (devtype == 3 || devtype == 4) {
			keyOpCmdSp.setSelection(1);
		}

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
				currentKeyID = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		keyOpCmdSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				currentKeyCmdOp = position;
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
		keyOpList = new ArrayList<String>();
		keyOpCmdList = new ArrayList<String>();
		keyNameList = new ArrayList<String>();

		/*
		 * for (int i = 0; i < 2; i++) { keyOpList.add(i + ""); }
		 */
		keyOpList.add("按下");
		keyOpList.add("弹起");

		if (devtype == 0) {
			keyOpCmdList.add("开关");
			keyOpCmdList.add("模式");
			keyOpCmdList.add("风速");
			keyOpCmdList.add("温度+");
			keyOpCmdList.add("温度-");
		} else if (devtype == 3) {
			keyOpCmdList.add("");
			keyOpCmdList.add("打开");
			keyOpCmdList.add("关闭");
			keyOpCmdList.add("开关");
			keyOpCmdList.add("变亮");
			keyOpCmdList.add("变暗");
		} else if (devtype == 4) {
			keyOpCmdList.add("");
			keyOpCmdList.add("打开");
			keyOpCmdList.add("关闭");
			keyOpCmdList.add("停止");
			keyOpCmdList.add("开关停");
		} else {
			keyOpCmdList.add("操作");
		}

		mWareBoardKeyInputs = HomeActivity.mBoardKeyInputs;
		List<WareBoardKeyInput> list = removeDuplicate(mWareBoardKeyInputs);
		// 初始化devCpuId
		for (int i = 0; i < list.size(); i++) {
			// String cpuid = CommonUtils.printHexString(list.get(i).devUnitID);
			String cpuName = CommonUtils.getGBstr(list.get(i).boardName);
			devUidList.add(cpuName);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.SpinnerSave:
			setAddInfoToList();
			Toast.makeText(this, "添加成功", 0).show();
			break;
		default:
			break;
		}
	}

	private void setAddInfoToList() {
		// TODO Auto-generated method stub
		WareChnOpItem item = new WareChnOpItem();

		String uid = null;
		String uidName = devUnitIdSp.getSelectedItem().toString();
		for (int i = 0; i < mWareBoardKeyInputs.size(); i++) {
			if (uidName.equals(CommonUtils.getGBstr(mWareBoardKeyInputs.get(i).boardName))) {
				uid = CommonUtils.printHexString(mWareBoardKeyInputs.get(i).devUnitID);
			}
		}
		// int keyOp = Integer.parseInt(keyOpSp.getSelectedItem().toString());
		int keyOp = currentOp;
		item.devUnitID = CommonUtils.hexStringToBytes(uid);

		item.keyDownCmd = new byte[6];
		item.keyUpCmd = new byte[6];
		// 按下
		if (keyOp == 0) {
			item.keyDownValid = (byte) (1 << currentKeyID);
			item.keyDownCmd[currentKeyID] = (byte) currentKeyCmdOp;
			item.keyUpValid = 0;
		} else if (keyOp == 1) {
			item.keyUpValid = (byte) (1 << currentKeyID);
			item.keyUpCmd[currentKeyID] = (byte) currentKeyCmdOp;
			item.keyDownValid = 0;
		}

		item.rev1 = 0;
		item.rev2 = 0;
		item.rev3 = 0;

		HomeActivity.mChnOpAddItems.add(item);
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
