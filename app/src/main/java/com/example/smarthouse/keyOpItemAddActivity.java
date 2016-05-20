package com.example.smarthouse;

import java.util.ArrayList;
import java.util.List;

import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareBoardChnout;
import com.pullmi.entity.WareBoardKeyInput;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareKeyOpItem;
import com.pullmi.utils.LogUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class keyOpItemAddActivity extends AdAbstractActivity {

	private LinearLayout createLayout;
	private Spinner devUnitIdSp, devTypeSp, devIdSp, keyOpCmdSp, keyOpSp;
	private Button save;
	private List<String> devUidList, devTypeList, devNameList, devCmdList, keyOpList;
	private List<WareDev> mWareDevs;
	private List<WareBoardChnout> mWareBoardChnouts;
	private ArrayAdapter<String> devUnitIdadapter, devTypeadapter, devIdadapter, keyOpCmdadapter,
			keyOpadapter;
	private String currentCpuid;
	private int currentDevType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initComponent();
	}

	private void initComponent() {
		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_keyopitem_add, null);
		mRootView.addView(createLayout, FF);

		devUnitIdSp = (Spinner) findViewById(R.id.Spinner1);
		devTypeSp = (Spinner) findViewById(R.id.Spinner2);
		devIdSp = (Spinner) findViewById(R.id.Spinner3);
		keyOpCmdSp = (Spinner) findViewById(R.id.Spinner4);
		keyOpSp = (Spinner) findViewById(R.id.Spinner5);

		save = (Button) findViewById(R.id.SpinnerSave);
		save.setOnClickListener(this);
		save.setOnTouchListener(TouchDark);

		initData();

		devUnitIdadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				devUidList);

		devIdadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				devNameList);
		keyOpCmdadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				devCmdList);
		keyOpadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				keyOpList);
		
		devUnitIdadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		devIdadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		keyOpCmdadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		keyOpadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

		devUnitIdSp.setAdapter(devUnitIdadapter);
		devIdSp.setAdapter(devIdadapter);
		keyOpCmdSp.setAdapter(keyOpCmdadapter);
		keyOpSp.setAdapter(keyOpadapter);

		devUnitIdSp.setSelection(0, true);

		devUnitIdSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				devTypeList.clear();
				devNameList.clear();
				devCmdList.clear();
				devIdadapter.notifyDataSetChanged();
				keyOpCmdadapter.notifyDataSetChanged();

				String cpuid = "";
				String cpuName = devUidList.get(arg2);
				for (int i = 0; i < mWareDevs.size(); i++) {
					if (cpuName.equals(CommonUtils.getGBstr(mWareBoardChnouts.get(i).boardName))) {
						cpuid = CommonUtils.printHexString(mWareBoardChnouts.get(i).devUnitID);
						currentCpuid = cpuid;
						break;
					}
				}

				List<WareDev> list = removeDuplicate(mWareDevs, 1);
				for (int j = 0; j < list.size(); j++) {
					if (cpuid.equals(CommonUtils.printHexString(list.get(j).canCpuId))) {
						switch (list.get(j).devType) {
						case 0:
							devTypeList.add(getString(R.string.e_ware_airCond));
							break;
						case 3:
							devTypeList.add(getString(R.string.e_ware_light));
							break;
						case 4:
							devTypeList.add(getString(R.string.e_ware_curtain));
							break;
						case 7:
							devTypeList.add(getString(R.string.e_ware_fresh_air));
							break;
						default:
							break;
						}
					}
				}
				
				devTypeadapter = new ArrayAdapter<String>(keyOpItemAddActivity.this,
						android.R.layout.simple_spinner_item, devTypeList);
				devTypeadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
				devTypeSp.setAdapter(devTypeadapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		devTypeSp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				devNameList.clear();
				devCmdList.clear();

				if (devTypeList.get(arg2).equals(getString(R.string.e_ware_airCond))) {
					currentDevType = 0;
				} else if (devTypeList.get(arg2).equals(getString(R.string.e_ware_light))) {
					currentDevType = 3;
				} else if (devTypeList.get(arg2).equals(getString(R.string.e_ware_curtain))) {
					currentDevType = 4;
				} else if (devTypeList.get(arg2).equals(getString(R.string.e_ware_fresh_air))) {
					currentDevType = 7;
				}
				for (int j = 0; j < mWareDevs.size(); j++) {
					if (currentCpuid.equals(CommonUtils.printHexString(mWareDevs.get(j).canCpuId))
							&& currentDevType == mWareDevs.get(j).devType) {
						devNameList.add(CommonUtils.getGBstr(mWareDevs.get(j).devName));
					}
				}
				devIdadapter.notifyDataSetChanged();

				if (currentDevType == 0) {
					/*devCmdList.add(UdpProPkt.E_86KEY_AIR_CMD.e_86key_air_power.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_AIR_CMD.e_86key_air_mode.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_AIR_CMD.e_86key_air_spd.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_AIR_CMD.e_86key_air_tempInc.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_AIR_CMD.e_86key_air_tempDec.getValue() + "");*/
					
					devCmdList.add("开关");
					devCmdList.add("模式");
					devCmdList.add("风速");
					devCmdList.add("温度+");
					devCmdList.add("温度-");
					keyOpCmdadapter.notifyDataSetChanged();
				} else if (currentDevType == 3) {
					/*devCmdList.add(UdpProPkt.E_86KEY_CTRL_TYPE.e_86keyCtrl_null.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_CTRL_TYPE.e_86keyCtrl_offOn.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_CTRL_TYPE.e_86keyCtrl_onOff.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_CTRL_TYPE.e_86keyCtrl_power.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_CTRL_TYPE.e_86keyCtrl_dark.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_CTRL_TYPE.e_86keyCtrl_bright.getValue() + "");
					devCmdList.add(UdpProPkt.E_86KEY_CTRL_TYPE.e_86keyCtrl_cmd_total.getValue()
							+ "");*/
					devCmdList.add("无操作");
					devCmdList.add("打开");
					devCmdList.add("关闭");
					devCmdList.add("开关");
					devCmdList.add("调暗");
					devCmdList.add("调亮");
					
					keyOpCmdadapter.notifyDataSetChanged();
				} else if (currentDevType == 4) {
					//devCmdList.add(UdpProPkt.E_86KEY_MUTEX_TYPE.e_86keyMutex_null.getValue() + "");
					//devCmdList.add(UdpProPkt.E_86KEY_MUTEX_TYPE.e_86keyMutex_on.getValue() + "");
					//devCmdList.add(UdpProPkt.E_86KEY_MUTEX_TYPE.e_86keyMutex_off.getValue() + "");
					//devCmdList.add(UdpProPkt.E_86KEY_MUTEX_TYPE.e_86keyMutex_stop.getValue() + "");
					//devCmdList.add(UdpProPkt.E_86KEY_MUTEX_TYPE.e_86keyMutex_loop.getValue() + "");
					
					devCmdList.add("无操作");
					devCmdList.add("打开");
					devCmdList.add("关闭");
					devCmdList.add("暂停");
					devCmdList.add("开关停");
					
					keyOpCmdadapter.notifyDataSetChanged();
				} else {
					devCmdList.add(-1 + "");
				}
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
		devTypeList = new ArrayList<String>();
		devNameList = new ArrayList<String>();
		devCmdList = new ArrayList<String>();
		keyOpList = new ArrayList<String>();

		keyOpList.add("按下");
		keyOpList.add("弹起");

		// 建立数据源
		String[] devs = getResources().getStringArray(R.array.devType);
		for (int i = 0; i < devs.length; i++) {
			devTypeList.add(devs[i]);
		}
		
		mWareDevs = HomeActivity.mWareDevs;

		mWareBoardChnouts = HomeActivity.mBoardChnouts;
		List<WareBoardChnout> list = removeDuplicate(mWareBoardChnouts);
		// 初始化devCpuId
		for (int i = 0; i < list.size(); i++) {
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
		WareKeyOpItem item = new WareKeyOpItem();
		item.devUnitID = CommonUtils.hexStringToBytes(currentCpuid);
		item.devType = (byte) currentDevType;

		String devName = devIdSp.getSelectedItem().toString();
		String keyOpCmd = keyOpCmdSp.getSelectedItem().toString();
		String keyOp = keyOpSp.getSelectedItem().toString();

		//item.keyOpCmd = (byte) Integer.parseInt(keyOpCmd);
		//item.keyOp = (byte) Integer.parseInt(keyOp);
		item.keyOpCmd = (byte) keyOpCmdSp.getSelectedItemPosition();
		item.keyOp = (byte) keyOpSp.getSelectedItemPosition();
		
		for (int i = 0; i < mWareDevs.size(); i++) {
			if (currentCpuid.equals(CommonUtils.printHexString(mWareDevs.get(i).canCpuId))
					&& currentDevType == mWareDevs.get(i).devType
					&& devName.equals(CommonUtils.getGBstr(mWareDevs.get(i).devName))) {
				item.devId = mWareDevs.get(i).devId;
			}
		}

		HomeActivity.mKeyOpAddItems.add(item);
	}

	public static List<WareBoardChnout> removeDuplicate(List<WareBoardChnout> list) {
		List<WareBoardChnout> devs = new ArrayList<WareBoardChnout>();

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
	public static List<WareDev> removeDuplicate(List<WareDev> list, int flag) {
		List<WareDev> devs = new ArrayList<WareDev>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (flag == 0) {
					if (new String(devs.get(j).canCpuId).equals(new String(devs.get(i).canCpuId))) {
						devs.remove(j);
					}
				}
				if (flag == 1) {
					if (devs.get(j).devType == devs.get(i).devType) {
						devs.remove(j);
					}
				}

			}
		}

		return devs;
	}
}
