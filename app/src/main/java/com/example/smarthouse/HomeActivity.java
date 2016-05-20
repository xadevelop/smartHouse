package com.example.smarthouse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.smarthouse.fragment.BackHandledFragment;
import com.example.smarthouse.fragment.BackHandledInterface;
import com.example.smarthouse.fragment.fragmentControlDev;
import com.example.smarthouse.fragment.fragmentVideoWatch;
import com.example.smarthouse.fragment.sceneFragment;
import com.example.smarthouse.fragment.setFragment;
import com.example.smarthouse.fragment.unsuportFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.GPIO_INPUT_INFO;
import com.pullmi.entity.GPIO_OUTPUT_INFO;
import com.pullmi.entity.RcuInfo;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.entity.WareBoardChnout;
import com.pullmi.entity.WareBoardKeyInput;
import com.pullmi.entity.WareChnOpItem;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareFreshAir;
import com.pullmi.entity.WareKeyOpItem;
import com.pullmi.entity.WareLight;
import com.pullmi.entity.WareLock;
import com.pullmi.entity.WareSceneDevItem;
import com.pullmi.entity.WareSceneEvent;
import com.pullmi.entity.WareSetBox;
import com.pullmi.entity.WareTv;
import com.pullmi.entity.WareValve;
import com.pullmi.utils.LogUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HomeActivity extends AdAbstractActivity implements
		BackHandledInterface, fragmentControlDev.Callbacks {

	private LinearLayout registerLayout;
	private Fragment mainFragment;
	private Button mHouse, enMonitor, smScene, smTalkback, smSecurity;

	MyApplication myApp;
	private BackHandledFragment mBackHandedFragment;

	FragmentManager fm;
	FragmentTransaction transaction;

	private MulticastSocket castClass;

	private boolean rcuResetFlag = false;
	private boolean devinfoResetFlag = false;
	int runCount = 0;// 全局变量，用于统计重发次数
	final Handler handler = new Handler();
	public static Handler myHandler;
	public static String currentRoomName = "", oldRoomName = "";
	public static int currentFragmentID = -1; // 当前fragment的编号 0-4

	private List<RcuInfo> mRcuInfos;
	RcuInfo rcuInfo;
	InetAddress fromIP;

	public static List<WareLight> mLightDataset;
	public static List<WareCurtain> mCurtainDataset;
	public static List<WareLock> mLockDataset;
	public static List<WareValve> mValueDataset;
	public static List<WareAirCondDev> mAircondDataset;
	public static List<WareTv> mTvs;
	public static List<WareSetBox> msetBoxs;
	public static List<WareFreshAir> mFreshairDataset;
	public static List<WareDev> mWareDevs;
	public static List<WareSceneDevItem> mSceneDevs;
	public static List<WareSceneEvent> mSceneEvents;
	public static List<WareBoardKeyInput> mBoardKeyInputs;
	public static List<WareBoardChnout> mBoardChnouts;
	public static List<WareKeyOpItem> mKeyOpItems;
	public static List<WareKeyOpItem> mKeyOpAddItems;
	public static List<WareChnOpItem> mChnOpItems;
	public static List<WareChnOpItem> mChnOpAddItems;
	public static List<GPIO_INPUT_INFO> mgInfos;
	public static List<GPIO_OUTPUT_INFO> mgOuts;

	// 线程池
	private ExecutorService executorService;

	private static final int MSG_REFRSH_INFO = 2000;
	private static final int MSG_SETKEYITEM_INFO = 2001;
	private static final int MSG_DELKEYITEM_INFO = 2002;
	private static final int MSG_SETCHNOPITEM_INFO = 2003;
	private static final int MSG_DELCHNOPITEM_INFO = 2004;
	private static final int MSG_GETDEV_SUCCESS = 1000;
	private static final int MSG_CTRL_FAIL = 1001;
	private static final int MSG_CTRL_SUCCESS = 1002;
	private static final int MSG_ADD_FAIL = 1003;
	private static final int MSG_ADD_SUCCESS = 1004;
	private static final int MSG_EDIT_FAIL = 1005;
	private static final int MSG_EDIT_SUCCESS = 1006;
	private static final int MSG_DEL_FAIL = 1007;
	private static final int MSG_DEL_SUCCESS = 1008;
	private static final int MSG_ADDSCENE_SUCCESS = 1009;
	private static final int MSG_DELSCENE_SUCCESS = 1010;
	private static final int MSG_EDITSCENE_SUCCESS = 1011;
	private static final int MSG_INSETMODE_SUCCESS = 1012;
	private static final int MSG_OUTSETMODE_SUCCESS = 1013;
	private static final int MSG_INDELMODE_SUCCESS = 1014;
	private static final int MSG_OUTDELMODE_SUCCESS = 1015;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		registerLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_home, null);
		mRootView.addView(registerLayout, FF);

		initComponent();

		// confirmDialog();
		myApp = (MyApplication) getApplication();

		// 获取网关列表
		mRcuInfos = getGwList();
		if (mRcuInfos != null) {
			if (mRcuInfos.size() == 1) {
				GlobalVars.setDevid(CommonUtils.bytesToHexString(mRcuInfos.get(
						0).getDevUnitID()));
				GlobalVars.setDevpass(new String(mRcuInfos.get(0)
						.getDevUnitPass()));

				executorService = Executors.newSingleThreadExecutor();
				executorService.execute(new CastServer());

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getRcuInfo();
					}
				}).start();
			} else {
				final String[] names = new String[mRcuInfos.size()];
				for (int i = 0; i < mRcuInfos.size(); i++) {
					names[i] = CommonUtils.getGBstr(mRcuInfos.get(i).getName());
				}

				AlertDialog.Builder builder = new Builder(HomeActivity.this);
				// 指定下拉列表的显示数据
				// 设置一个下拉的列表选择项
				builder.setTitle("选择网关");
				builder.setItems(names, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						String name = names[arg1];
						for (int i = 0; i < mRcuInfos.size(); i++) {
							if (name.equals(CommonUtils.getGBstr(mRcuInfos.get(
									i).getName()))) {

								Log.e(TAG, CommonUtils
										.bytesToHexString(mRcuInfos.get(i)
												.getDevUnitID()));
								Log.e(TAG, new String(mRcuInfos.get(i)
										.getDevUnitPass()));

								GlobalVars.setDevid(CommonUtils
										.bytesToHexString(mRcuInfos.get(0)
												.getDevUnitID()));
								GlobalVars.setDevpass(new String(mRcuInfos.get(
										0).getDevUnitPass()));

								executorService = Executors
										.newSingleThreadExecutor();
								executorService.execute(new CastServer());

								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										getRcuInfo();
									}
								}).start();
							}
						}
					}
				});
				builder.show();
			}
		} else {
			Intent intent = new Intent();
			intent.setClass(HomeActivity.this, rcuInfoActivity.class);
			startActivity(intent);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		currentRoomName = "全部";

		LogUtils.LOGE(TAG, "设备总数:" + mWareDevs.size());
		if (mWareDevs.size() == 0 && fileIsExists("dev.dat")) {
			// 得到保存于本地路径的对象
			ArrayList<WareDev> devs = (ArrayList<WareDev>) getObject("dev.dat");
			if (devs != null) {
				mWareDevs = devs;
				if (currentFragmentID == 0) {
					// myApp.getHandler().sendEmptyMessage(MSG_REFRSH_INFO);
					myHandler.sendEmptyMessage(1000);
				}
			} else {
				Toast.makeText(this, "读取本地数据失败，联网查询中", 0).show();
			}
		}
		if (mAircondDataset.size() == 0 && fileIsExists("airs.dat")) {
			ArrayList<WareAirCondDev> airs = (ArrayList<WareAirCondDev>) getObject("airs.dat");
			if (airs != null) {
				mAircondDataset = airs;
			}
		}
		if (mTvs.size() == 0 && fileIsExists("tvs.dat")) {
			ArrayList<WareTv> tvs = (ArrayList<WareTv>) getObject("tvs.dat");
			if (tvs != null) {
				mTvs = tvs;
			}
		}
		if (msetBoxs.size() == 0 && fileIsExists("boxs.dat")) {
			ArrayList<WareSetBox> boxs = (ArrayList<WareSetBox>) getObject("boxs.dat");
			if (boxs != null) {
				msetBoxs = boxs;
			}
		}
		if (mLightDataset.size() == 0 && fileIsExists("lights.dat")) {
			ArrayList<WareLight> ligths = (ArrayList<WareLight>) getObject("lights.dat");
			if (ligths != null) {
				mLightDataset = ligths;
			}
		}
		if (mCurtainDataset.size() == 0 && fileIsExists("crutain.dat")) {
			ArrayList<WareCurtain> curs = (ArrayList<WareCurtain>) getObject("crutain.dat");
			if (curs != null) {
				mCurtainDataset = curs;
			}
		}
		if (mValueDataset.size() == 0 && fileIsExists("values.dat")) {
			ArrayList<WareValve> values = (ArrayList<WareValve>) getObject("values.dat");
			if (values != null) {
				mValueDataset = values;
			}
		}
		if (mLockDataset.size() == 0 && fileIsExists("locks.dat")) {
			ArrayList<WareLock> locks = (ArrayList<WareLock>) getObject("locks.dat");
			if (locks != null) {
				mLockDataset = locks;
			}
		}
		if (mFreshairDataset.size() == 0 && fileIsExists("frairs.dat")) {
			ArrayList<WareFreshAir> frairs = (ArrayList<WareFreshAir>) getObject("frairs.dat");
			if (frairs != null) {
				mFreshairDataset = frairs;
			}
		}
		if (mSceneEvents.size() == 0 && fileIsExists("scene.dat")) {
			ArrayList<WareSceneEvent> scenes = (ArrayList<WareSceneEvent>) getObject("scene.dat");
			if (scenes != null) {
				mSceneEvents = scenes;
			}
		}
	}

	private List<RcuInfo> getGwList() {

		SharedPreferences sharedPreferences = getSharedPreferences("profile",
				Context.MODE_PRIVATE);
		String jsondata = sharedPreferences.getString("list", "null");
		Gson gson = new Gson();
		if (!jsondata.equals("null")) {
			List<RcuInfo> list = gson.fromJson(jsondata,
					new TypeToken<List<RcuInfo>>() {
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

	// 判断文件是否存在
	public boolean fileIsExists(String strFile) {
		try {
			File f = new File(strFile);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private void initComponent() {

		setTextForBackBtn(getString(R.string.tab_setting));
		setTextForRightBtn("房间");
		setTextForTitle("");

		// 设置默认的Fragment
		setDefaultFragment();

		mHouse = (Button) findViewById(R.id.smHouse);
		enMonitor = (Button) findViewById(R.id.enMonitor);
		smScene = (Button) findViewById(R.id.smScene);
		smTalkback = (Button) findViewById(R.id.smTalkback);
		smSecurity = (Button) findViewById(R.id.smSecurity);

		mHouse.setOnClickListener(this);
		enMonitor.setOnClickListener(this);
		smScene.setOnClickListener(this);
		smTalkback.setOnClickListener(this);
		smSecurity.setOnClickListener(this);

		mHouse.setOnTouchListener(TouchDark);
		enMonitor.setOnTouchListener(TouchDark);
		smScene.setOnTouchListener(TouchDark);
		smTalkback.setOnTouchListener(TouchDark);
		smSecurity.setOnTouchListener(TouchDark);

		mLightDataset = new ArrayList<WareLight>();
		mCurtainDataset = new ArrayList<WareCurtain>();
		mLockDataset = new ArrayList<WareLock>();
		mValueDataset = new ArrayList<WareValve>();
		mAircondDataset = new ArrayList<WareAirCondDev>();
		mFreshairDataset = new ArrayList<WareFreshAir>();

		mWareDevs = new ArrayList<WareDev>();
		mSceneDevs = new ArrayList<WareSceneDevItem>();
		mSceneEvents = new ArrayList<WareSceneEvent>();
		mBoardChnouts = new ArrayList<WareBoardChnout>();
		mBoardKeyInputs = new ArrayList<WareBoardKeyInput>();
		mKeyOpItems = new ArrayList<WareKeyOpItem>();
		mKeyOpAddItems = new ArrayList<WareKeyOpItem>();
		mChnOpItems = new ArrayList<WareChnOpItem>();
		mChnOpAddItems = new ArrayList<WareChnOpItem>();
		mTvs = new ArrayList<WareTv>();
		msetBoxs = new ArrayList<WareSetBox>();
		mgInfos = new ArrayList<GPIO_INPUT_INFO>();
		mgOuts = new ArrayList<GPIO_OUTPUT_INFO>();
	}

	private void setDefaultFragment() {
		fm = getFragmentManager();
		transaction = fm.beginTransaction();
		mainFragment = new fragmentControlDev();
		transaction.replace(R.id.menuFragment, mainFragment);
		transaction.commit();

		currentFragmentID = 0;
	}

	Runnable retSendPkt = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (runCount == 5 && currentFragmentID == 0) {
				mHandler.sendEmptyMessage(MSG_GETDEV_SUCCESS);
			}
			if (!rcuResetFlag) {
				getRcuInfo();
			}
			if (rcuResetFlag && !devinfoResetFlag) {
				CommonUtils.getDevInfo();
			}

			handler.postDelayed(this, 1000);
			runCount++;
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GETDEV_SUCCESS:
				if (handler != null) {
					handler.removeCallbacks(retSendPkt);
				}
				break;
			case MSG_CTRL_FAIL:
				Toast.makeText(HomeActivity.this, "控制设备失败", 0).show();
				break;
			case MSG_CTRL_SUCCESS:
				Toast.makeText(HomeActivity.this, "控制设备成功", 0).show();
				break;
			case MSG_EDIT_FAIL:
				Toast.makeText(HomeActivity.this, "设置设备属性失败", 0).show();
				break;
			case MSG_EDIT_SUCCESS:
				Toast.makeText(HomeActivity.this, "设置设备属性成功", 0).show();
				break;
			case MSG_ADD_FAIL:
				Toast.makeText(HomeActivity.this, "添加设备失败", 0).show();
				break;
			case MSG_ADD_SUCCESS:
				Toast.makeText(HomeActivity.this, "添加设备成功", 0).show();
				break;
			case MSG_DEL_FAIL:
				Toast.makeText(HomeActivity.this, "删除设备失败", 0).show();
				break;
			case MSG_DEL_SUCCESS:
				Toast.makeText(HomeActivity.this, "删除设备成功", 0).show();
				break;
			case MSG_ADDSCENE_SUCCESS:
				Toast.makeText(HomeActivity.this, "添加情景模式成功", 0).show();
				break;
			case MSG_DELSCENE_SUCCESS:
				Toast.makeText(HomeActivity.this, "删除情景模式成功", 0).show();
				break;
			case MSG_EDITSCENE_SUCCESS:
				Toast.makeText(HomeActivity.this, "编辑情景模式成功", 0).show();
				break;
			case MSG_INSETMODE_SUCCESS:
				Toast.makeText(HomeActivity.this, "进入设置模式", 0).show();
				break;
			case MSG_OUTSETMODE_SUCCESS:
				Toast.makeText(HomeActivity.this, "退出设置模式", 0).show();
				break;
			case MSG_INDELMODE_SUCCESS:
				Toast.makeText(HomeActivity.this, "进入删除模式", 0).show();
				break;
			case MSG_OUTDELMODE_SUCCESS:
				Toast.makeText(HomeActivity.this, "退出删除模式", 0).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn4:
			showSettingList();
			break;
		case R.id.title_btn1:
			if (currentFragmentID == 0) {
				showRoomList();
			}
			break;
		case R.id.smHouse:
			currentFragmentID = 0;
			currentRoomName = "全部";
			setTextForRightBtn(currentRoomName);
			mainFragment = new fragmentControlDev();
			replaceFragment(mainFragment);
			break;
		case R.id.enMonitor:
			currentFragmentID = 1;
			mainFragment = new unsuportFragment();
			replaceFragment(mainFragment);
			break;
		case R.id.smScene:
			currentFragmentID = 2;
			mainFragment = new sceneFragment();
			replaceFragment(mainFragment);
			break;
		case R.id.smTalkback:
			currentFragmentID = 3;
			mainFragment = new fragmentVideoWatch();
			replaceFragment(mainFragment);
			break;
		case R.id.smSecurity:
			currentFragmentID = 4;
			mainFragment = new unsuportFragment();
			replaceFragment(mainFragment);
			break;
		default:
			break;
		}
	}

	private void showSettingList() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);

		// 指定下拉列表的显示数据
		// 设置一个下拉的列表选择项
		builder.setItems(getResources().getStringArray(R.array.ItemArray),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						switch (which) {
						case 0:
							intent.setClass(HomeActivity.this,
									rcuInfoActivity.class);
							break;
						case 1:
							intent.setClass(HomeActivity.this,
									createSceneEventActivity.class);
							break;
						case 2:
							intent.setClass(HomeActivity.this,
									chnOutInActivity.class);
							break;
						case 3:
							intent.setClass(HomeActivity.this,
									activityRcuIOEdit.class);
							break;
						case 4:
							intent.setClass(HomeActivity.this,
									activityRcuOutEdit.class);
							break;
						case 5:
							intent.setClass(HomeActivity.this,
									activityQucikSet.class);
							break;
						default:
							break;
						}
						startActivity(intent);
					}
				});
		builder.show();
	}

	private void showRoomList() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);

		List<WareDev> mWareRooms = mWareDevs;

		List<WareDev> lst = removeDuplicate(mWareRooms);
		final String[] item = new String[lst.size() + 1];
		for (int i = 0; i < lst.size(); i++) {
			item[i + 1] = CommonUtils.getGBstr(lst.get(i).roomName);
		}
		item[0] = "全部";
		builder.setTitle("选择一个房间");
		// 指定下拉列表的显示数据
		// 设置一个下拉的列表选择项
		builder.setItems(item, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				currentRoomName = item[which];
				myHandler.sendEmptyMessage(1000);
				setTextForRightBtn(item[which]);
			}
		});
		builder.show();
	}

	public List<WareDev> removeDuplicate(List<WareDev> list) {
		List<WareDev> devs = new ArrayList<WareDev>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).roomName, devs.get(j).roomName)) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareDev> removeDuplicateWareDev(List<WareDev> list) {
		List<WareDev> devs = new ArrayList<WareDev>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).canCpuId, devs.get(j).canCpuId)
						&& devs.get(i).devId == devs.get(j).devId
						&& devs.get(i).devType == devs.get(j).devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareLight> removeDuplicateWarelight(List<WareLight> list) {
		List<WareLight> devs = new ArrayList<WareLight>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareTv> removeDuplicateWareTv(List<WareTv> list) {
		List<WareTv> devs = new ArrayList<WareTv>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareSetBox> removeDuplicateWareSetBox(List<WareSetBox> list) {
		List<WareSetBox> devs = new ArrayList<WareSetBox>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareAirCondDev> removeDuplicateWareAir(List<WareAirCondDev> list) {
		List<WareAirCondDev> devs = new ArrayList<WareAirCondDev>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareCurtain> removeDuplicateWarecurtain(List<WareCurtain> list) {
		List<WareCurtain> devs = new ArrayList<WareCurtain>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareLock> removeDuplicateWareLock(List<WareLock> list) {
		List<WareLock> devs = new ArrayList<WareLock>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareValve> removeDuplicateWarevalue(List<WareValve> list) {
		List<WareValve> devs = new ArrayList<WareValve>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	private List<WareBoardKeyInput> removeDuplicateBoardKeyInPuts(
			List<WareBoardKeyInput> list) {
		// TODO Auto-generated method stub
		List<WareBoardKeyInput> devs = new ArrayList<WareBoardKeyInput>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).devUnitID, devs.get(j).devUnitID)) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	private List<WareBoardChnout> removeDuplicateBoardChnouts(
			List<WareBoardChnout> list) {
		// TODO Auto-generated method stub
		List<WareBoardChnout> devs = new ArrayList<WareBoardChnout>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).devUnitID, devs.get(j).devUnitID)) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	public List<WareFreshAir> removeDuplicateWareFresh(List<WareFreshAir> list) {
		List<WareFreshAir> devs = new ArrayList<WareFreshAir>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).dev.canCpuId,
						devs.get(j).dev.canCpuId)
						&& devs.get(i).dev.devId == devs.get(j).dev.devId
						&& devs.get(i).dev.devType == devs.get(j).dev.devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	private List<WareKeyOpItem> removeDuplicateKeyOpItem(
			List<WareKeyOpItem> list) {
		// TODO Auto-generated method stub
		List<WareKeyOpItem> devs = new ArrayList<WareKeyOpItem>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).devUnitID, devs.get(j).devUnitID)
						&& devs.get(i).devId == devs.get(j).devId
						&& devs.get(i).devType == devs.get(j).devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	private List<WareChnOpItem> removeDuplicateChnOpItem(
			List<WareChnOpItem> list) {
		// TODO Auto-generated method stub
		List<WareChnOpItem> devs = new ArrayList<WareChnOpItem>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).devUnitID, devs.get(j).devUnitID)) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	private List<GPIO_OUTPUT_INFO> removeDuplicateRcuOuts(
			List<GPIO_OUTPUT_INFO> list) {
		// TODO Auto-generated method stub
		List<GPIO_OUTPUT_INFO> devs = new ArrayList<GPIO_OUTPUT_INFO>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (Arrays.equals(devs.get(i).defName, devs.get(j).defName)) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	private class CastServer implements Runnable {

		@Override
		public void run() {
			try {
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer,
						buffer.length);
				castClass = new MulticastSocket(
						Integer.parseInt(getString(R.string.socket_port)));

				while (true) {// 循环接收组播消息
					packet.setLength(buffer.length);
					// Log.e(TAG, "--------> recev cast server");
					castClass.receive(packet);

					fromIP = packet.getAddress();
					// LogUtils.LOGE(TAG,
					// ">>>rev data from " + fromIP.getHostAddress());
					if (CommonUtils.mLocalIp.equals(fromIP.getHostAddress()))
						continue;
					// 处理消息
					extractData(packet);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				LogUtils.LOGE(TAG, ">>>UnknownHostException" + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				LogUtils.LOGE(TAG, ">>>IOException" + e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void extractData(DatagramPacket packet) {
		// TODO Auto-generated method stub

		byte[] localData = packet.getData();
		int packetLen = packet.getLength();

		if (packetLen < 56) {
			return;
		}

		byte[] header = new byte[4];
		CommonUtils.copyBytes(localData, header, 0, 0, 4);
		if (!CommonUtils.isControlHeaderRight(header)) {
			return;
		}

		int datLen = CommonUtils.byteToInt(new byte[] { localData[52],
				localData[53] });

		byte[] dstIP = new byte[4];
		byte[] srcIP = new byte[4];

		CommonUtils.copyBytes(localData, srcIP, 4, 0, 4);
		CommonUtils.copyBytes(localData, dstIP, 8, 0, 4);

		byte[] udpProData = new byte[datLen];
		CommonUtils.copyBytes(localData, udpProData, 56, 0, datLen);

		int isAck = CommonUtils.byteToInt(new byte[] { localData[48] });
		int datType = CommonUtils.byteToInt(new byte[] { localData[49] });
		int subType1 = CommonUtils.byteToInt(new byte[] { localData[50] });
		int subType2 = CommonUtils.byteToInt(new byte[] { localData[51] });

		if (isAck == 1) {
			// Log.e(TAG, "需要应答的数据包");
			localData[48] = (byte) UdpProPkt.IS_ACK.NOW_ACK.getValue();
			for (int i = 0; i < 4; i++) {
				localData[i + 4] = dstIP[i];
				localData[i + 8] = srcIP[i];
			}
			replyAck(localData, fromIP.getHostAddress());
		}
		if (isAck == 2) {
			Log.e(TAG, "应答的数据包");
		}

		if (datType != 2) {
			Log.e(TAG, datType + "*******" + subType1 + "*******" + subType2);
		}
		switch (datType) {
		case 0:// e_udpPro_getRcuinfo
				// 更新Rcu信息
			if (subType2 == 1) {
				rcuResetFlag = true;
				setRcuInfo(udpProData);
				handler.postDelayed(retSendPkt, 1000);
			}
			break;
		case 2: // handshake
			// 握手应答
			handShake(fromIP.getHostAddress());
			break;
		case 3: // getDevsInfo
			if (subType1 == 1) {
				devinfoResetFlag = true;
				getDevsInfo(udpProData);
				mWareDevs = removeDuplicateWareDev(mWareDevs);

				Collections.sort(mWareDevs, new Comparator() {
					public int compare(Object a, Object b) {
						int one = ((WareDev) a).getType();
						int two = ((WareDev) b).getType();
						return one - two;
					}
				});
				if (currentFragmentID == 0) {
					mHandler.sendEmptyMessage(MSG_GETDEV_SUCCESS);
					// myApp.getHandler().sendEmptyMessage(1000);
					myHandler.sendEmptyMessage(1000);
				}
			}
			break;
		case 4: // ctrlDev
			if (subType1 == 1) {
				if (subType2 == 0) {
					mHandler.sendEmptyMessage(MSG_CTRL_FAIL);
				} else {
					// mHandler.sendEmptyMessage(MSG_CTRL_SUCCESS);
					// myApp.getHandler().sendEmptyMessage(MSG_GETDEV_SUCCESS);
					mHandler.sendEmptyMessage(1000);
					refreshDevData(udpProData);
				}
			}
			break;
		case 6: // editDev
			if (subType1 == 1) {
				if (subType2 == 0) {
					mHandler.sendEmptyMessage(MSG_EDIT_FAIL);
				} else {
					mHandler.sendEmptyMessage(MSG_EDIT_SUCCESS);
					refreshDevData(udpProData);
				}
			}
			break;
		case 7: // delDev
			if (subType1 == 1) {
				if (subType2 == 0) {
					mHandler.sendEmptyMessage(MSG_DEL_FAIL);
				} else {
					mHandler.sendEmptyMessage(MSG_DEL_SUCCESS);
					delDevData(udpProData);
				}
			}
			break;
		case 8:
			if (subType2 == 0) {
				getkeyOutBoard(udpProData);
			}
			if (subType2 == 1) {
				getKyeInputBoard(udpProData);
				myApp.getHandler().sendEmptyMessage(2101);
			}
			break;
		case 11: // e_udpPro_getKeyOpItems
			if (subType1 == 1) {
				getKeyOpItem(udpProData, subType2);
				myApp.getHandler().sendEmptyMessage(2102);
			}
			break;
		case 12: // e_udpPro_setKeyOpItems
			if (subType1 == 1) {
				myApp.getHandler().sendEmptyMessage(MSG_SETKEYITEM_INFO);
			}
			break;
		case 13: // e_udpPro_delKeyOpItems
			if (subType1 == 1) {
				myApp.getHandler().sendEmptyMessage(MSG_DELKEYITEM_INFO);
			}
			break;
		case 14: // e_udpPro_getChnOpitems
			if (subType1 == 1) {
				getChnOpItem(udpProData);
				myApp.getHandler().sendEmptyMessage(2103);
			}
			break;
		case 15: // e_udpPro_setChnOpitems
			if (subType1 == 1) {
				if (getChnOpItemReply(udpProData))
					myApp.getHandler().sendEmptyMessage(MSG_SETCHNOPITEM_INFO);
			}
			break;
		case 16:
			if (subType1 == 1) {
				if (getChnOpItemReply(udpProData))
					myApp.getHandler().sendEmptyMessage(MSG_DELCHNOPITEM_INFO);
			}
			break;
		case 22: // e_udpPro_getSceneEvents
			if (subType2 == 1) {
				setSceneEvents(udpProData);
				myApp.getHandler().sendEmptyMessage(2104);
			}
			break;
		case 23: // e_udpPro_addSceneEvents
			if (subType2 == 1) {
				mHandler.sendEmptyMessage(MSG_ADDSCENE_SUCCESS);
				mSceneDevs.clear();
				setSceneEvents(udpProData);
			}
			break;
		case 24: // e_udpPro_editSceneEvents
			if (subType2 == 1) {
				mHandler.sendEmptyMessage(MSG_EDITSCENE_SUCCESS);
				mSceneDevs.clear();
				setSceneEvents(udpProData);
			}
			break;
		case 25: // e_udpPro_delSceneEvents
			if (subType2 == 1) {
				mHandler.sendEmptyMessage(MSG_DELSCENE_SUCCESS);
				delSceneEvents(udpProData);
				myApp.getHandler().sendEmptyMessage(2105);
			}
			break;
		case 26: // e_udpPro_exeSceneEvents
			LogUtils.LOGE(TAG, "设备总数  " + mWareDevs.size());
			if (subType2 == 1) {
				// setSceneEvents(udpProData);
				CommonUtils.getDevInfo(); // 重新查询设备状态
			}
			break;
		case 35:// e_udpPro_chns_status
			ctrlDevReply(udpProData);
			// myApp.getHandler().sendEmptyMessage(MSG_GETDEV_SUCCESS);
			myHandler.sendEmptyMessage(1000);
			break;
		case 37:// e_udpPro_getIOSet_input
			if (subType1 == 1) {
				setRcuIOInfo(udpProData);
				myApp.getHandler().sendEmptyMessage(2106);
			}
			break;
		case 38: // e_udpPro_getIOSet_output
			if (subType1 == 1) {
				setRcuOutInfo(udpProData);
				myApp.getHandler().sendEmptyMessage(2107);
			}
			break;
		case 46:// e_udpPro_quick_setDevKey
			if (subType2 == 0) {
				mHandler.sendEmptyMessage(MSG_OUTSETMODE_SUCCESS);
			} else {
				mHandler.sendEmptyMessage(MSG_INSETMODE_SUCCESS);
			}
			break;
		case 47:// e_udpPro_quick_delDevKey
			if (subType2 == 0) {
				mHandler.sendEmptyMessage(MSG_OUTDELMODE_SUCCESS);
			} else {
				mHandler.sendEmptyMessage(MSG_INDELMODE_SUCCESS);
			}
		default:
			break;
		}
	}

	private void setRcuIOInfo(byte[] localData) {
		// TODO Auto-generated method stub
		// Log.e(TAG, "-------rev scene into by type " + localData.length);
		List<GPIO_INPUT_INFO> infos = CommonUtils.extractIOinfo(localData);

		for (int i = 0; i < infos.size(); i++) {
			mgInfos.add(infos.get(i));
		}
	}

	private void setRcuOutInfo(byte[] localData) {
		// TODO Auto-generated method stub
		// Log.e(TAG, "-------rev scene into by type " + localData.length);
		List<GPIO_OUTPUT_INFO> infos = CommonUtils.extractOutinfo(localData);

		for (int i = 0; i < infos.size(); i++) {
			mgOuts.add(infos.get(i));
		}

		mgOuts = removeDuplicateRcuOuts(mgOuts);
	}

	private void delSceneEvents(byte[] localData) {
		// TODO Auto-generated method stub
		// Log.e(TAG, "-------rev scene into by type " + localData.length);
		List<WareSceneEvent> events = CommonUtils.extractEvents(localData);

		for (int i = 0; i < mSceneEvents.size(); i++) {
			if (events.get(0).eventld == mSceneEvents.get(i).eventld) {
				mSceneEvents.remove(i);
			}
		}
	}

	private void setSceneEvents(byte[] localData) {
		// TODO Auto-generated method stub

		// Log.e(TAG, "-------rev scene into by type " + localData.length);
		List<WareSceneEvent> events = CommonUtils.extractEvents(localData);

		if (mSceneEvents.size() == 0) {
			mSceneEvents.add(events.get(0));
		} else {
			boolean flag = false;
			for (int i = 0; i < mSceneEvents.size(); i++) {
				if (mSceneEvents.get(i).eventld == events.get(0).eventld) {
					flag = true;
					mSceneEvents.remove(i);
					mSceneEvents.add(events.get(0));// 修改设置的处理
				}
			}
			if (!flag) {
				mSceneEvents.add(events.get(0));
			}
		}
	}

	private void getKyeInputBoard(byte[] localData) {
		// TODO Auto-generated method stub
		List<WareBoardKeyInput> keys = CommonUtils.extractKeyInputs(localData);
		// mBoardKeyInputs = keys;
		for (int i = 0; i < keys.size(); i++) {
			mBoardKeyInputs.add(keys.get(i));
		}
		mBoardKeyInputs = removeDuplicateBoardKeyInPuts(mBoardKeyInputs);
	}

	private void getkeyOutBoard(byte[] localData) {
		// TODO Auto-generated method stub
		List<WareBoardChnout> outs = CommonUtils.extractChnouts(localData);
		// mBoardChnouts = outs;
		for (int i = 0; i < outs.size(); i++) {
			mBoardChnouts.add(outs.get(i));
		}
		mBoardChnouts = removeDuplicateBoardChnouts(mBoardChnouts);
	}

	private void getChnOpItem(byte[] localData) {
		// TODO Auto-generated method stub
		mChnOpItems.clear();
		int len = localData.length - 14;
		if (len == 0) {
			return;
		}

		List<WareChnOpItem> chnOpItems = CommonUtils
				.extractChnOpItems(localData);
		for (int i = 0; i < chnOpItems.size(); i++) {
			mChnOpItems.add(chnOpItems.get(i));
		}
		// mChnOpItems = chnOpItems;
		mChnOpItems = removeDuplicateChnOpItem(mChnOpItems);
	}

	private boolean getChnOpItemReply(byte[] localData) {
		// TODO Auto-generated method stub
		int len = localData.length - 12;
		if (len == 0) {
			return false;
		}
		if (localData[12] == 1) {
			return true;
		} else {
			return false;
		}
	}

	private void getKeyOpItem(byte[] localData, int subType) {
		// TODO Auto-generated method stub

		int len = localData.length - 12;
		if (len == 0) {
			return;
		}
		mKeyOpItems.clear();
		byte[] data = new byte[len];
		CommonUtils.copyBytes(localData, data, 12, 0, len);

		List<WareKeyOpItem> keyItems = CommonUtils.extractKeyOpItems(data,
				subType);
		// mKeyOpItems = keyItems;
		for (int i = 0; i < keyItems.size(); i++) {
			mKeyOpItems.add(keyItems.get(i));
		}
		mKeyOpItems = removeDuplicateKeyOpItem(mKeyOpItems);
	}

	private void ctrlDevReply(byte[] localData) {
		// TODO Auto-generated method stub
		// 更新设备的最新状态
		int state = CommonUtils.byteToInt(new byte[] { localData[0],
				localData[1] });
		byte[] canCpuId = new byte[12];
		CommonUtils.copyBytes(localData, canCpuId, 2, 0, 12);

		String binaryString = Integer.toBinaryString(state);
		System.out.println(binaryString);
		for (int i = 0; i < 12; i++) {
			if (get(state, i) == 1) {
				for (int j = 0; j < mLightDataset.size(); j++) {
					if (mLightDataset.get(j).powChn == i
							&& Arrays.equals(canCpuId,
									mLightDataset.get(j).dev.canCpuId)) {
						// LogUtils.LOGE(TAG, "当前灯的通道号" + i);
						mLightDataset.get(j).bOnOff = 1;
					}
				}
				for (int j = 0; j < mFreshairDataset.size(); j++) {
					if (9 == i
							&& Arrays.equals(canCpuId,
									mFreshairDataset.get(j).dev.canCpuId)) {
						mFreshairDataset.get(j).bOnOff = 1;
					}
					if (11 == i
							&& Arrays.equals(canCpuId,
									mFreshairDataset.get(j).dev.canCpuId)) {
						mFreshairDataset.get(j).bOnOff = 0;
					}
				}
			} else {
				for (int j = 0; j < mLightDataset.size(); j++) {
					if (mLightDataset.get(j).powChn == i
							&& Arrays.equals(canCpuId,
									mLightDataset.get(j).dev.canCpuId)) {
						mLightDataset.get(j).bOnOff = 0;
						// LogUtils.LOGE(TAG, "当前灯的通道号" + i);
					}
				}
				for (int j = 0; j < mFreshairDataset.size(); j++) {
					if (mFreshairDataset.get(j).powChn == i
							&& Arrays.equals(canCpuId,
									mFreshairDataset.get(j).dev.canCpuId)) {
						mFreshairDataset.get(j).bOnOff = 0;
					}
				}
			}
		}
	}

	public static int get(int num, int index) {
		return (num & (0x1 << index)) >> index;
	}

	private void getDevsInfo(byte[] localData) {
		// TODO Auto-generated method stub

		int datalen = CommonUtils.byteToInt(new byte[] { localData[39] });
		if (datalen > 255)
			return;

		int devType = localData[36] & 0xff;
		switch (devType) {// 保存设备的详细信息
		case 0:// e_ware_airCond
			List<WareAirCondDev> airConds = CommonUtils
					.extractAirconds(localData);

			for (int i = 0; i < airConds.size(); i++) {
				mAircondDataset.add(airConds.get(i));
			}
			mAircondDataset = removeDuplicateWareAir(mAircondDataset);
			for (int i = 0; i < mAircondDataset.size(); i++) {
				mWareDevs.add(mAircondDataset.get(i).dev);
			}
			break;
		case 1:// e_ware_tv
			List<WareTv> tvs = CommonUtils.extractTvs(localData);
			for (int i = 0; i < tvs.size(); i++) {
				mTvs.add(tvs.get(i));
			}
			mTvs = removeDuplicateWareTv(mTvs);
			for (int i = 0; i < mTvs.size(); i++) {
				mWareDevs.add(mTvs.get(i).dev);
			}
			break;
		case 2:// e_ware_tvUP
			List<WareSetBox> setBoxs = CommonUtils.extractSetBoxs(localData);
			for (int i = 0; i < setBoxs.size(); i++) {
				msetBoxs.add(setBoxs.get(i));
			}
			msetBoxs = removeDuplicateWareSetBox(msetBoxs);
			for (int i = 0; i < msetBoxs.size(); i++) {
				mWareDevs.add(msetBoxs.get(i).dev);
			}
			break;
		case 3:// e_ware_light
			List<WareLight> lights = CommonUtils.extractLights(localData);
			for (int i = 0; i < lights.size(); i++) {
				mLightDataset.add(lights.get(i));
			}
			mLightDataset = removeDuplicateWarelight(mLightDataset);
			for (int i = 0; i < mLightDataset.size(); i++) {
				// LogUtils.LOGE(TAG, "灯房间名" +
				// CommonUtils.getGBstr(mLightDataset.get(i).dev.roomName));
				mWareDevs.add(mLightDataset.get(i).dev);
			}
			break;
		case 4: // e_ware_curtain
			List<WareCurtain> curtains = CommonUtils.extractCurtains(localData);
			for (int i = 0; i < curtains.size(); i++) {
				mCurtainDataset.add(curtains.get(i));
			}
			mCurtainDataset = removeDuplicateWarecurtain(mCurtainDataset);
			for (int i = 0; i < mCurtainDataset.size(); i++) {
				mWareDevs.add(mCurtainDataset.get(i).dev);
			}
			break;
		case 5:// e_ware_lock
			List<WareLock> locks = CommonUtils.extractLocks(localData);
			// mLockDataset = locks;
			for (int i = 0; i < locks.size(); i++) {
				mLockDataset.add(locks.get(i));
			}
			mLockDataset = removeDuplicateWareLock(mLockDataset);
			for (int i = 0; i < mLockDataset.size(); i++) {
				mWareDevs.add(mLockDataset.get(i).dev);
			}
			break;
		case 6:// e_ware_valve
			List<WareValve> values = CommonUtils.extractValves(localData);
			// mValueDataset = values;
			for (int i = 0; i < values.size(); i++) {
				mValueDataset.add(values.get(i));
			}
			mValueDataset = removeDuplicateWarevalue(mValueDataset);
			for (int i = 0; i < mValueDataset.size(); i++) {
				mWareDevs.add(mValueDataset.get(i).dev);
			}
			break;
		case 7: // e_ware_fresh_air
			List<WareFreshAir> freshairs = CommonUtils
					.extractFreshairs(localData);
			// mFreshairDataset = freshairs;
			for (int i = 0; i < freshairs.size(); i++) {
				mFreshairDataset.add(freshairs.get(i));
			}
			mFreshairDataset = removeDuplicateWareFresh(mFreshairDataset);
			for (int i = 0; i < mFreshairDataset.size(); i++) {
				mWareDevs.add(mFreshairDataset.get(i).dev);
			}
			break;
		default:
			break;
		}
	}

	private void refreshDevData(byte[] localData) {
		// TODO Auto-generated method stub

		int nodelen = localData.length;
		int datalen = CommonUtils.byteToInt(new byte[] { localData[39] });
		if (datalen > 255)
			return;

		byte[] nodeData = new byte[nodelen];
		for (int i = 0; i < nodeData.length; i++) {
			nodeData[i] = localData[i];
		}

		int devType = localData[36] & 0xff;
		switch (devType) {// 保存设备的详细信息
		case 0:// e_ware_airCond
			List<WareAirCondDev> airConds = CommonUtils
					.extractAirconds(nodeData);
			for (int i = 0; i < airConds.size(); i++) {
				for (int j = 0; j < mAircondDataset.size(); j++) {
					if (airConds.get(i).dev.devId == mAircondDataset.get(j).dev.devId
							&& Arrays.equals(airConds.get(i).dev.canCpuId,
									mAircondDataset.get(j).dev.canCpuId)) {
						mAircondDataset.remove(j);
						mAircondDataset.add(j, airConds.get(i));
					}
				}
			}
			break;
		case 1:// e_ware_tv
			break;
		case 2:// e_ware_tvUP
			break;
		case 3:// e_ware_light
			List<WareLight> lights = CommonUtils.extractLights(nodeData);
			for (int i = 0; i < lights.size(); i++) {
				for (int j = 0; j < mLightDataset.size(); j++) {
					if (lights.get(i).dev.devId == mLightDataset.get(j).dev.devId) {

						for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
							if (mWareDevs.get(j2).devId == mLightDataset.get(j).dev.devId
									&& mWareDevs.get(j2).devType == mLightDataset
											.get(j).dev.devType
									&& mWareDevs.get(j2).devName == mLightDataset
											.get(j).dev.devName) {
								mWareDevs.remove(j2);
							}
						}
						mLightDataset.get(j).dev.devName = lights.get(i).dev.devName;
						mLightDataset.get(j).dev.roomName = lights.get(i).dev.roomName;
						mLightDataset.get(j).powChn = lights.get(i).powChn;
						mLightDataset.get(j).dev.devId = mLightDataset.get(j).powChn;

						mWareDevs.add(mLightDataset.get(j).dev);
					}
				}
			}
			break;
		case 4: // e_ware_curtain
			List<WareCurtain> curtains = CommonUtils.extractCurtains(nodeData);
			for (int i = 0; i < curtains.size(); i++) {
				for (int j = 0; j < mCurtainDataset.size(); j++) {
					if (curtains.get(i).dev.devId == mCurtainDataset.get(j).dev.devId
							&& Arrays.equals(curtains.get(i).dev.canCpuId,
									mCurtainDataset.get(j).dev.canCpuId)) {

						mCurtainDataset.remove(j);
						mCurtainDataset.add(j, curtains.get(i));

					}
				}

				for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
					if (mWareDevs.get(j2).devId == curtains.get(i).dev.devId
							&& mWareDevs.get(j2).devType == curtains.get(i).dev.devType
							&& Arrays.equals(mWareDevs.get(j2).canCpuId,
									curtains.get(i).dev.canCpuId)) {
						mWareDevs.remove(j2);
						mWareDevs.add(curtains.get(i).dev);
					}
				}
			}
			break;
		case 5:// e_ware_lock
			List<WareLock> locks = CommonUtils.extractLocks(nodeData);
			break;
		case 6:// e_ware_valve
			List<WareValve> values = CommonUtils.extractValves(nodeData);
			break;
		case 7: // e_ware_fresh_air
			List<WareFreshAir> freshAirs = CommonUtils
					.extractFreshairs(nodeData);
			for (int i = 0; i < freshAirs.size(); i++) {
				for (int j = 0; j < mFreshairDataset.size(); j++) {
					if (freshAirs.get(i).dev.devId == mFreshairDataset.get(j).dev.devId
							&& Arrays.equals(freshAirs.get(i).dev.canCpuId,
									mFreshairDataset.get(j).dev.canCpuId)) {
						mFreshairDataset.remove(j);
						mFreshairDataset.add(j, freshAirs.get(i));
					}
				}
			}
			break;
		default:
			break;
		}
	}

	private void delDevData(byte[] localData) {
		// TODO Auto-generated method stub

		int nodelen = localData.length;
		int datalen = CommonUtils.byteToInt(new byte[] { localData[39] });
		if (datalen > 255)
			return;

		byte[] nodeData = new byte[nodelen];
		for (int i = 0; i < nodeData.length; i++) {
			nodeData[i] = localData[i];
		}

		int devType = localData[36] & 0xff;
		switch (devType) {// 保存设备的详细信息
		case 0:// e_ware_airCond
			List<WareAirCondDev> airConds = CommonUtils
					.extractAirconds(nodeData);
			for (int i = 0; i < airConds.size(); i++) {
				for (int j = 0; j < mAircondDataset.size(); j++) {
					if (airConds.get(i).dev.devId == mAircondDataset.get(j).dev.devId) {
						for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
							if (mWareDevs.get(j2).devId == mAircondDataset
									.get(j).dev.devType
									&& mWareDevs.get(j2).devType == mAircondDataset
											.get(j).dev.devType
									&& mWareDevs.get(j2).devName == mAircondDataset
											.get(j).dev.devName) {
								mWareDevs.remove(j2);
							}
						}
						mAircondDataset.remove(j);
					}
				}
			}
			break;
		case 1:// e_ware_tv
			break;
		case 2:// e_ware_tvUP
			break;
		case 3:// e_ware_light
			List<WareLight> lights = CommonUtils.extractLights(nodeData);
			for (int i = 0; i < lights.size(); i++) {
				for (int j = 0; j < mLightDataset.size(); j++) {
					if (lights.get(i).dev.devId == mLightDataset.get(j).dev.devId) {
						for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
							if (mWareDevs.get(j2).devId == mLightDataset.get(j).dev.devType
									&& mWareDevs.get(j2).devType == mLightDataset
											.get(j).dev.devType
									&& mWareDevs.get(j2).devName == mLightDataset
											.get(j).dev.devName) {
								mWareDevs.remove(j2);
							}
						}
						mLightDataset.remove(j);
					}
				}
			}
			break;
		case 4: // e_ware_curtain
			List<WareCurtain> curtains = CommonUtils.extractCurtains(nodeData);
			for (int i = 0; i < curtains.size(); i++) {
				for (int j = 0; j < mCurtainDataset.size(); j++) {
					if (curtains.get(i).dev.devId == mCurtainDataset.get(j).dev.devId) {
						for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
							if (mWareDevs.get(j2).devId == mCurtainDataset
									.get(j).dev.devType
									&& mWareDevs.get(j2).devType == mCurtainDataset
											.get(j).dev.devType
									&& mWareDevs.get(j2).devName == mCurtainDataset
											.get(j).dev.devName) {
								mWareDevs.remove(j2);
							}
						}
						mCurtainDataset.remove(j);
					}
				}
			}
			break;
		case 5:// e_ware_lock
			List<WareLock> locks = CommonUtils.extractLocks(nodeData);
			for (int i = 0; i < locks.size(); i++) {
				for (int j = 0; j < mLockDataset.size(); j++) {
					if (locks.get(i).dev.devId == mLockDataset.get(j).dev.devId) {
						for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
							if (mWareDevs.get(j2).devId == mLockDataset.get(j).dev.devType
									&& mWareDevs.get(j2).devType == mLockDataset
											.get(j).dev.devType
									&& mWareDevs.get(j2).devName == mLockDataset
											.get(j).dev.devName) {
								mWareDevs.remove(j2);
							}
						}
						mLockDataset.remove(j);
					}
				}
			}
			break;
		case 6:// e_ware_valve
			List<WareValve> values = CommonUtils.extractValves(nodeData);
			for (int i = 0; i < values.size(); i++) {
				for (int j = 0; j < mValueDataset.size(); j++) {
					if (values.get(i).dev.devId == mLockDataset.get(j).dev.devId) {
						for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
							if (mWareDevs.get(j2).devId == mValueDataset.get(j).dev.devType
									&& mWareDevs.get(j2).devType == mValueDataset
											.get(j).dev.devType
									&& mWareDevs.get(j2).devName == mValueDataset
											.get(j).dev.devName) {
								mWareDevs.remove(j2);
							}
						}
						mValueDataset.remove(j);
					}
				}
			}
			break;
		case 7: // e_ware_fresh_air
			List<WareFreshAir> freshAirs = CommonUtils
					.extractFreshairs(nodeData);
			for (int i = 0; i < freshAirs.size(); i++) {
				for (int j = 0; j < mFreshairDataset.size(); j++) {
					if (freshAirs.get(i).dev.devId == mFreshairDataset.get(j).dev.devId) {
						for (int j2 = 0; j2 < mWareDevs.size(); j2++) {
							if (mWareDevs.get(j2).devId == mFreshairDataset
									.get(j).dev.devType
									&& mWareDevs.get(j2).devType == mFreshairDataset
											.get(j).dev.devType
									&& mWareDevs.get(j2).devName == mFreshairDataset
											.get(j).dev.devName) {
								mWareDevs.remove(j2);
							}
						}
						mFreshairDataset.remove(j);
					}
				}
			}
			break;
		default:
			break;
		}
	}

	private void getRcuInfo() {
		byte[] temp = new byte[0];
		final byte[] sendData = CommonUtils.preSendUdpProPkt(
				getString(R.string.broadcast_ip), CommonUtils.getLocalIp(),
				UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getRcuInfo.getValue(), 0, 0,
				temp, 0);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					DatagramPacket senddata = new DatagramPacket(
							sendData,
							sendData.length,
							InetAddress
									.getByName(getString(R.string.broadcast_ip)),
							Integer.parseInt(getString(R.string.socket_port)));
					castClass.send(senddata);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					LogUtils.LOGE(TAG,
							">>>UnknownHostException" + e.getMessage());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					LogUtils.LOGE(TAG, ">>>IOException" + e.getMessage());
				}
			}
		}).start();
	}

	private void replyAck(byte sendData[], String dstip) {
		try {
			DatagramPacket senddata = new DatagramPacket(sendData,
					sendData.length, InetAddress.getByName(dstip),
					Integer.parseInt(getString(R.string.socket_port)));
			castClass.send(senddata);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			LogUtils.LOGE(TAG, ">>>UnknownHostException" + e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogUtils.LOGE(TAG, ">>>IOException" + e.getMessage());
		}
	}

	private void handShake(String dstip) {
		try {
			byte[] sendData = CommonUtils.preSendUdpAckPkt(dstip,
					CommonUtils.getLocalIp());

			DatagramPacket senddata = new DatagramPacket(sendData,
					sendData.length, InetAddress.getByName(dstip),
					Integer.parseInt(getString(R.string.socket_port)));
			castClass.send(senddata);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			LogUtils.LOGE(TAG, ">>>UnknownHostException" + e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogUtils.LOGE(TAG, ">>>IOException" + e.getMessage());
		}
	}

	private void setRcuInfo(byte[] prodata) {
		// TODO Auto-generated method stub

		byte[] devUnitID = new byte[12];
		byte[] devUnitPass = new byte[8];
		byte[] name = new byte[12];
		byte[] IpAddr = new byte[4];
		byte[] SubMask = new byte[4];
		byte[] GateWay = new byte[4];
		byte[] centerServ = new byte[4];
		byte[] roomNum = new byte[4];
		byte[] macAddr = new byte[6];
		byte[] SoftVersion = new byte[2];
		byte[] HwVersion = new byte[2];

		rcuInfo = new RcuInfo(devUnitID, devUnitPass, name, IpAddr, SubMask,
				GateWay, centerServ, roomNum, macAddr, SoftVersion, HwVersion);

		CommonUtils.copyBytes(prodata, rcuInfo.getDevUnitID(), 0, 0, 12);
		CommonUtils.copyBytes(prodata, rcuInfo.getDevUnitPass(), 12, 0, 8);
		CommonUtils.copyBytes(prodata, rcuInfo.getName(), 20, 0, 12);
		CommonUtils.copyBytes(prodata, rcuInfo.getIpAddr(), 32, 0, 4);
		CommonUtils.copyBytes(prodata, rcuInfo.getSubMask(), 36, 0, 4);
		CommonUtils.copyBytes(prodata, rcuInfo.getGateWay(), 40, 0, 4);
		CommonUtils.copyBytes(prodata, rcuInfo.getCenterServ(), 44, 0, 4);

		CommonUtils.copyBytes(prodata, rcuInfo.getRoomNum(), 48, 0, 4);
		CommonUtils.copyBytes(prodata, rcuInfo.getMacAddr(), 52, 0, 6);
		CommonUtils.copyBytes(prodata, rcuInfo.getSoftVersion(), 58, 0, 2);
		CommonUtils.copyBytes(prodata, rcuInfo.getHwVversion(), 60, 0, 2);

		String dstip = "";
		try {
			dstip = InetAddress.getByAddress(IpAddr).getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtils.LOGE(TAG, "当前目的ip:" + dstip);
		GlobalVars.setDevid(CommonUtils.printHexString(rcuInfo.getDevUnitID()));
		GlobalVars.setDevpass(new String(rcuInfo.getDevUnitPass()));
		GlobalVars.setDstip(dstip);

		for (int i = 0; i < mRcuInfos.size(); i++) {
			if (CommonUtils.printHexString(rcuInfo.getDevUnitID()).equals(
					mRcuInfos.get(i).getDevUnitID())
					&& Arrays.equals(rcuInfo.getDevUnitPass(), mRcuInfos.get(i)
							.getDevUnitPass())) {
				// Toast.makeText(this, "当前联网模块已存在，请重新输入", 0).show();
				mRcuInfos.get(i).setName(rcuInfo.getName());
				saveInfo(mRcuInfos);
				break;
			}
		}
	}

	public void saveInfo(List<RcuInfo> datas) {
		Gson gson = new Gson();

		String str = gson.toJson(datas);

		SharedPreferences sharedPreferences = getSharedPreferences("profile",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("list", str);
		editor.commit();
	}

	private void replaceFragment(Fragment newFragment) {

		FragmentTransaction trasection = getFragmentManager()
				.beginTransaction();

		trasection.replace(R.id.menuFragment, newFragment);
		// trasection.addToBackStack(null);
		trasection.commit();
	}

	public void saveObject(String name, int flag) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = this.openFileOutput(name, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			if (flag == -1) {
				oos.writeObject(mWareDevs);
			} else if (flag == 0) {
				oos.writeObject(mAircondDataset);
			} else if (flag == 1) {
				oos.writeObject(mTvs);
			} else if (flag == 2) {
				oos.writeObject(msetBoxs);
			} else if (flag == 3) {
				oos.writeObject(mLightDataset);
			} else if (flag == 4) {
				oos.writeObject(mCurtainDataset);
			} else if (flag == 6) {
				oos.writeObject(mValueDataset);
			} else if (flag == 10) {
				oos.writeObject(mSceneEvents);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 这里是保存文件产生异常
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// fos流关闭异常
					e.printStackTrace();
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					// oos流关闭异常
					e.printStackTrace();
				}
			}
		}
	}

	private Object getObject(String name) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = this.openFileInput(name);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			// 这里是读取文件产生异常
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// fis流关闭异常
					e.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					// ois流关闭异常
					e.printStackTrace();
				}
			}
		}
		// 读取产生异常，返回null
		return null;
	}

	public void setSelectedFragment(BackHandledFragment selectedFragment) {
		this.mBackHandedFragment = selectedFragment;
	}

	@Override
	public void onBackPressed() {
		if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
			if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
				super.onBackPressed();
			} else {
				getSupportFragmentManager().popBackStack();
			}
		}
	}

	@Override
	public void setTitle(String str) {
		// TODO Auto-generated method stub
		setTextForRightBtn(str);
	}

	public void setHandler(Handler handler) {
		myHandler = handler;
	}
}
