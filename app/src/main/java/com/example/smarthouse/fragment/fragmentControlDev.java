package com.example.smarthouse.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.smarthouse.ActivitySetBoxControl;
import com.example.smarthouse.ActivityTvControl;
import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.MyApplication;
import com.example.smarthouse.R;
import com.example.smarthouse.activityAircondControl;
import com.example.smarthouse.activityCurtainControl;
import com.example.smarthouse.activityFreshAirControl;
import com.example.smarthouse.activityLightTuneControl;
import com.example.smarthouse.activityLockControl;
import com.example.smarthouse.activityEditDev;
import com.example.smarthouse.valueControlView;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareFreshAir;
import com.pullmi.entity.WareLight;
import com.pullmi.entity.WareLock;
import com.pullmi.entity.WareSetBox;
import com.pullmi.entity.WareTv;
import com.pullmi.entity.WareValve;
import com.pullmi.utils.LogUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class fragmentControlDev extends BackHandledFragment {

	private static final String TAG = "controlDevFragment";
	private GridView gridView;
	private ProgressBar progress;
	private View view;

	private boolean hadIntercept = false;
	private ArrayList<HashMap<String, Object>> lst;
	private SimpleAdapter saItem;
	MyApplication myApp;
	private static final int MSG_UPDATA_DEVS = 1000;
	private static final int MSG_UPDATE_LIGHT = 1001;
	private static final int MSG_UPDATE_CURTAIN = 1002;
	private static final int MSG_UPDATE_AIRCOND = 1003;
	private static final int MSG_UPDATE_LOCKS = 1005;
	private static final int MSG_UPDATE_VALUE = 1006;
	private static final int MSG_UPDATE_FRESHAIR = 1007;
	private static final int MSG_UPDATE_TV = 1008;
	private static final int MSG_UPDATE_SETBOX = 1009;
	private static final int MSG_UPDATA_DEVS_BYROOM = 1010;
	private static final int MSG_REFRSH_INFO = 2100;

	private MyLightAdapter mLightAdapter;
	private MyCurtainAdapter mCurtainAdapter;
	private MyLockAdapter mLockAdapter;
	private MyValveAdapter mValueAdapter;
	private MyAirCondAdapter mAirCondAdapter;
	private MyFreshairAdapter mFreshairAdapter;
	private MyTvAdapter mTvAdapter;
	private MySetBoxAdapter mSetBoxAdapter;
	private BaseAdapter currentAdapter;

	private static List<WareLight> mLightDataset;
	private static List<WareCurtain> mCurtainDataset;
	private static List<WareLock> mLockDataset;
	private static List<WareValve> mValueDataset;
	private static List<WareAirCondDev> mAircondDataset;
	private static List<WareFreshAir> mFreshairDataset;
	private static List<WareTv> mTvs;
	private static List<WareSetBox> mSetBoxs;
	private static List<WareDev> mWareDevs;
	Callbacks mcallbacks;

	private int[] devTypeIcon = { R.drawable.air_r, R.drawable.tv_r,
			R.drawable.led_r, R.drawable.curtain_r, R.drawable.lock_r,
			R.drawable.value_r, R.drawable.freshair_r, R.drawable.setbox_r };

	private String[] devTypeIconName = new String[8];
	private int[] devIcon = { R.drawable.air_open, R.drawable.air_close,
			R.drawable.light_on, R.drawable.light_off, R.drawable.curtain_open,
			R.drawable.curtain_close, R.drawable.valve_open,
			R.drawable.valve_close, R.drawable.on_enable,
			R.drawable.off_disable };
	private HomeActivity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mLightAdapter = new MyLightAdapter();
		mCurtainAdapter = new MyCurtainAdapter();
		mLockAdapter = new MyLockAdapter();
		mValueAdapter = new MyValveAdapter();
		mAirCondAdapter = new MyAirCondAdapter();
		mFreshairAdapter = new MyFreshairAdapter();
		mTvAdapter = new MyTvAdapter();
		mSetBoxAdapter = new MySetBoxAdapter();

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		mcallbacks = (Callbacks) activity;
		mActivity = (HomeActivity) activity;
		mActivity.setHandler(mHandler);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater
				.inflate(R.layout.fragment_control_dev, container, false);

		progress = (ProgressBar) view.findViewById(R.id.control_progress);
		progress.setVisibility(view.GONE);
		gridView = (GridView) view.findViewById(R.id.grid);
		gridView.setVisibility(view.VISIBLE);

		devTypeIconName[0] = getString(R.string.e_ware_airCond);
		devTypeIconName[1] = getString(R.string.e_ware_tv);
		devTypeIconName[2] = getString(R.string.e_ware_light);
		devTypeIconName[3] = getString(R.string.e_ware_curtain);
		devTypeIconName[4] = getString(R.string.e_ware_lock);
		devTypeIconName[5] = getString(R.string.e_ware_value);
		devTypeIconName[6] = getString(R.string.e_ware_fresh_air);
		devTypeIconName[7] = getString(R.string.e_ware_tvup);

		lst = new ArrayList<HashMap<String, Object>>();
		saItem = new SimpleAdapter(getActivity(), lst, // 数据源
				R.layout.alarm_grid_item, // xml实现
				new String[] { "ItemImage", "ItemText" }, // 对应map的Key
				new int[] { R.id.alarm_grid_item_iv, R.id.alarm_grid_item_tv }); // 对应R的Id
		// 添加点击事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				int index = arg2;// id是从0开始的

				// Toast用于向用户显示一些帮助/提示
				// 在本例中arg2=arg3
				@SuppressWarnings("unchecked")
				HashMap<String, Object> item = (HashMap<String, Object>) arg0
						.getItemAtPosition(index);

				if (item == null) {
					return;
				}
				// 显示所选Item的ItemText
				String text = (String) item.get("ItemText");
				// Toast.makeText(getActivity(), "你按下了选项：" + text, 0).show();
				hadIntercept = false;
				if (text.equals(getString(R.string.e_ware_light))) {
					// 获取灯光节点设备，显示
					mHandler.sendEmptyMessage(MSG_UPDATE_LIGHT);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_curtain))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_CURTAIN);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_lock))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_LOCKS);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_airCond))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_AIRCOND);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_value))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_VALUE);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_fresh_air))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_FRESHAIR);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_tv))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_TV);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_tvup))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_SETBOX);
					gridView.setClickable(false);
				} else if (text.equals(getString(R.string.e_ware_lock))) {
					mHandler.sendEmptyMessage(MSG_UPDATE_LOCKS);
					gridView.setClickable(false);
				}
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (currentAdapter == null) {
			mWareDevs = HomeActivity.mWareDevs;
			LogUtils.LOGE(TAG, mWareDevs.size() + "");
			if (mWareDevs.size() > 0) {
				gridView.setVisibility(view.VISIBLE);
				progress.setVisibility(view.GONE);
				lst.clear();
				showDevsByType();
				saItem = new SimpleAdapter(getActivity(), lst, // 数据源
						R.layout.alarm_grid_item, // xml实现
						new String[] { "ItemImage", "ItemText" }, // 对应map的Key
						new int[] { R.id.alarm_grid_item_iv,
								R.id.alarm_grid_item_tv }); // 对应R的Id
				gridView.setAdapter(saItem);
				saItem.notifyDataSetChanged();
			}
			return;
		}
		if (currentAdapter == mLightAdapter) {
			mHandler.sendEmptyMessage(MSG_UPDATE_LIGHT);
		} else if (currentAdapter == mCurtainAdapter) {
			mHandler.sendEmptyMessage(MSG_UPDATE_CURTAIN);
		} else if (currentAdapter == mAirCondAdapter) {
			mHandler.sendEmptyMessage(MSG_UPDATE_AIRCOND);
		} else if (currentAdapter == mFreshairAdapter) {
			mHandler.sendEmptyMessage(MSG_UPDATE_FRESHAIR);
		} else if (currentAdapter == mTvAdapter) {
			mHandler.sendEmptyMessage(MSG_UPDATE_TV);
		} else if (currentAdapter == mSetBoxAdapter) {
			mHandler.sendEmptyMessage(MSG_UPDATE_SETBOX);
		} else if (currentAdapter == mLockAdapter) {
			mHandler.sendEmptyMessage(MSG_UPDATE_LOCKS);
		}
	}

	public Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRSH_INFO:
				if (currentAdapter == null) {
					gridView.setVisibility(view.VISIBLE);
					progress.setVisibility(view.GONE);

					lst.clear();
					showDevsByType();

					gridView.setAdapter(saItem);
					saItem.notifyDataSetChanged();
				}
				break;
			case MSG_UPDATA_DEVS:
				if (currentAdapter == mLightAdapter) {
					LogUtils.LOGE(TAG, "更新灯状态");
					mLightDataset = HomeActivity.mLightDataset;
					if (mLightDataset == null) {
						break;
					}
					List<WareLight> mDevForRooms = new ArrayList<WareLight>();
					if (!HomeActivity.currentRoomName.equals("全部")) {
						for (int i = 0; i < mLightDataset.size(); i++) {
							if (CommonUtils.getGBstr(
									mLightDataset.get(i).dev.roomName).equals(
									HomeActivity.currentRoomName)) {
								mDevForRooms.add(mLightDataset.get(i));
							}
						}
						mLightDataset = mDevForRooms;
					}
					mLightAdapter.notifyDataSetChanged();
				} else if (currentAdapter == mTvAdapter) {
					mTvs = HomeActivity.mTvs;
					if (mTvs == null) {
						break;
					}
					List<WareTv> mTvForRooms = new ArrayList<WareTv>();
					if (!HomeActivity.currentRoomName.equals("全部")) {
						for (int i = 0; i < mTvs.size(); i++) {
							if (CommonUtils.getGBstr(mTvs.get(i).dev.roomName)
									.equals(HomeActivity.currentRoomName)) {
								mTvForRooms.add(mTvs.get(i));
							}
						}
						mTvs = mTvForRooms;
					}
					mTvAdapter.notifyDataSetChanged();
				} else if (currentAdapter == mSetBoxAdapter) {
					mSetBoxs = HomeActivity.msetBoxs;
					if (mSetBoxs == null) {
						break;
					}
					List<WareSetBox> mSetBoxForRooms = new ArrayList<WareSetBox>();
					if (!HomeActivity.currentRoomName.equals("全部")) {
						for (int i = 0; i < mSetBoxs.size(); i++) {
							if (CommonUtils.getGBstr(
									mSetBoxs.get(i).dev.roomName).equals(
									HomeActivity.currentRoomName)) {
								mSetBoxForRooms.add(mSetBoxs.get(i));
							}
						}
						mSetBoxs = mSetBoxForRooms;
					}
					mSetBoxAdapter.notifyDataSetChanged();
				} else if (currentAdapter == mAirCondAdapter) {
					mAircondDataset = HomeActivity.mAircondDataset;
					if (mAircondDataset == null) {
						break;
					}
					List<WareAirCondDev> mAircondForRooms = new ArrayList<WareAirCondDev>();
					if (!HomeActivity.currentRoomName.equals("全部")) {
						for (int i = 0; i < mAircondDataset.size(); i++) {
							if (CommonUtils.getGBstr(
									mAircondDataset.get(i).dev.roomName)
									.equals(HomeActivity.currentRoomName)) {
								mAircondForRooms.add(mAircondDataset.get(i));
							}
						}
						mAircondDataset = mAircondForRooms;
					}
					mAirCondAdapter.notifyDataSetChanged();
				} else if (currentAdapter == mCurtainAdapter) {
					mCurtainDataset = HomeActivity.mCurtainDataset;
					if (mCurtainDataset == null) {
						break;
					}
					List<WareCurtain> mCurtainForRooms = new ArrayList<WareCurtain>();
					if (!HomeActivity.currentRoomName.equals("全部")) {
						for (int i = 0; i < mCurtainDataset.size(); i++) {
							if (CommonUtils.getGBstr(
									mCurtainDataset.get(i).dev.roomName)
									.equals(HomeActivity.currentRoomName)) {
								mCurtainForRooms.add(mCurtainDataset.get(i));
							}
						}
						mCurtainDataset = mCurtainForRooms;
					}
					mCurtainAdapter.notifyDataSetChanged();
				} else if (currentAdapter == mLockAdapter) {
					mLockDataset = HomeActivity.mLockDataset;
					if (mLockDataset == null) {
						break;
					}
					List<WareLock> mLockForRooms = new ArrayList<WareLock>();
					if (!HomeActivity.currentRoomName.equals("全部")) {
						for (int i = 0; i < mLockDataset.size(); i++) {
							if (CommonUtils.getGBstr(
									mCurtainDataset.get(i).dev.roomName)
									.equals(HomeActivity.currentRoomName)) {
								mLockForRooms.add(mLockDataset.get(i));
							}
						}
						mLockDataset = mLockForRooms;
					}
					mCurtainAdapter.notifyDataSetChanged();
				} else {
					if (HomeActivity.mWareDevs.size() > 0) {
						showDevsByType();
						gridView.setAdapter(saItem);
						saItem.notifyDataSetChanged();
					}
				}
				break;
			case MSG_UPDATE_LIGHT:
				mLightDataset = HomeActivity.mLightDataset;
				if (mLightDataset == null) {
					break;
				}
				List<WareLight> mDevForRooms = new ArrayList<WareLight>();
				if (!HomeActivity.currentRoomName.equals("全部")) {
					for (int i = 0; i < mLightDataset.size(); i++) {
						if (CommonUtils.getGBstr(
								mLightDataset.get(i).dev.roomName).equals(
								HomeActivity.currentRoomName)) {
							mDevForRooms.add(mLightDataset.get(i));
						}
					}
					mLightDataset = mDevForRooms;
				}

				gridView.setAdapter(mLightAdapter);
				mLightAdapter.notifyDataSetChanged();
				currentAdapter = mLightAdapter;
				break;
			case MSG_UPDATE_CURTAIN:
				mCurtainDataset = HomeActivity.mCurtainDataset;
				if (mCurtainDataset == null) {
					break;
				}
				List<WareCurtain> mCurtainForRooms = new ArrayList<WareCurtain>();
				if (!HomeActivity.currentRoomName.equals("全部")) {
					for (int i = 0; i < mCurtainDataset.size(); i++) {
						if (CommonUtils.getGBstr(
								mCurtainDataset.get(i).dev.roomName).equals(
								HomeActivity.currentRoomName)) {
							mCurtainForRooms.add(mCurtainDataset.get(i));
						}
					}
					mCurtainDataset = mCurtainForRooms;
				}
				gridView.setAdapter(mCurtainAdapter);
				mCurtainAdapter.notifyDataSetChanged();
				currentAdapter = mCurtainAdapter;
				break;
			case MSG_UPDATE_AIRCOND:
				mAircondDataset = HomeActivity.mAircondDataset;
				if (mAircondDataset == null) {
					break;
				}
				List<WareAirCondDev> mAircondForRooms = new ArrayList<WareAirCondDev>();
				if (!HomeActivity.currentRoomName.equals("全部")) {
					for (int i = 0; i < mAircondDataset.size(); i++) {
						if (CommonUtils.getGBstr(
								mAircondDataset.get(i).dev.roomName).equals(
								HomeActivity.currentRoomName)) {
							mAircondForRooms.add(mAircondDataset.get(i));
						}
					}
					mAircondDataset = mAircondForRooms;
				}
				gridView.setAdapter(mAirCondAdapter);
				mAirCondAdapter.notifyDataSetChanged();
				currentAdapter = mAirCondAdapter;
				break;
			case MSG_UPDATE_TV:
				mTvs = HomeActivity.mTvs;
				if (mTvs == null) {
					break;
				}
				List<WareTv> mTvForRooms = new ArrayList<WareTv>();
				if (!HomeActivity.currentRoomName.equals("全部")) {
					for (int i = 0; i < mTvs.size(); i++) {
						if (CommonUtils.getGBstr(mTvs.get(i).dev.roomName)
								.equals(HomeActivity.currentRoomName)) {
							mTvForRooms.add(mTvs.get(i));
						}
					}
					mTvs = mTvForRooms;
				}
				gridView.setAdapter(mTvAdapter);
				mTvAdapter.notifyDataSetChanged();
				currentAdapter = mTvAdapter;
				break;
			case MSG_UPDATE_SETBOX:
				mSetBoxs = HomeActivity.msetBoxs;
				if (mSetBoxs == null) {
					break;
				}
				List<WareSetBox> mSetBoxForRooms = new ArrayList<WareSetBox>();
				if (!HomeActivity.currentRoomName.equals("全部")) {
					for (int i = 0; i < mSetBoxs.size(); i++) {
						if (CommonUtils.getGBstr(mSetBoxs.get(i).dev.roomName)
								.equals(HomeActivity.currentRoomName)) {
							mSetBoxForRooms.add(mSetBoxs.get(i));
						}
					}
					mSetBoxs = mSetBoxForRooms;
				}
				gridView.setAdapter(mSetBoxAdapter);
				mSetBoxAdapter.notifyDataSetChanged();
				currentAdapter = mSetBoxAdapter;
				break;
			case MSG_UPDATE_LOCKS:
				mLockDataset = HomeActivity.mLockDataset;
				if (mLockDataset == null) {
					break;
				}
				List<WareLock> mLockForRooms = new ArrayList<WareLock>();
				if (!HomeActivity.currentRoomName.equals("全部")) {
					for (int i = 0; i < mLockDataset.size(); i++) {
						if (CommonUtils.getGBstr(mSetBoxs.get(i).dev.roomName)
								.equals(HomeActivity.currentRoomName)) {
							mLockForRooms.add(mLockDataset.get(i));
						}
					}
					mLockDataset = mLockForRooms;
				}
				gridView.setAdapter(mLockAdapter);
				mLockAdapter.notifyDataSetChanged();
				currentAdapter = mLockAdapter;
				break;
			case MSG_UPDATE_VALUE:
				mValueDataset = HomeActivity.mValueDataset;
				if (mValueDataset == null) {
					break;
				}
				List<WareValve> mValueForRooms = new ArrayList<WareValve>();
				if (!HomeActivity.currentRoomName.equals("全部")) {
					for (int i = 0; i < mLockDataset.size(); i++) {
						if (CommonUtils.getGBstr(mSetBoxs.get(i).dev.roomName)
								.equals(HomeActivity.currentRoomName)) {
							mValueForRooms.add(mValueDataset.get(i));
						}
					}
					mValueDataset = mValueForRooms;
				}
				gridView.setAdapter(mValueAdapter);
				mValueAdapter.notifyDataSetChanged();
				currentAdapter = mValueAdapter;
				break;
			case MSG_UPDATE_FRESHAIR:
				mFreshairDataset = HomeActivity.mFreshairDataset;
				if (mFreshairDataset == null) {
					break;
				}
				List<WareFreshAir> mFreshairForRooms = new ArrayList<WareFreshAir>();
				if (!HomeActivity.currentRoomName.equals("")) {
					for (int i = 0; i < mFreshairDataset.size(); i++) {
						if (CommonUtils.getGBstr(
								mFreshairDataset.get(i).dev.roomName).equals(
								HomeActivity.currentRoomName)) {
							mFreshairForRooms.add(mFreshairDataset.get(i));
						}
					}
					mFreshairDataset = mFreshairForRooms;
				}
				gridView.setAdapter(mFreshairAdapter);
				mFreshairAdapter.notifyDataSetChanged();
				currentAdapter = mFreshairAdapter;
				break;
			default:
				break;
			}
		};
	};

	private void showDevsByRoom() {
		lst.clear();

		List<WareDev> list = HomeActivity.mWareDevs;
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String roomString = CommonUtils.getGBstr(list.get(i).roomName);
			LogUtils.LOGE(TAG, "设备房间" + roomString);
			if (HomeActivity.currentRoomName.equals(roomString)) {
				Object res = "", text = "";
				LogUtils.LOGE(TAG, "设备类型" + list.get(i).devType);
				switch (list.get(i).devType) {
				case 0: // 空调
					for (WareAirCondDev air : HomeActivity.mAircondDataset) {
						if (air.dev.devId == list.get(i).devId) {
							if (air.bOnOff == 1) {
								res = R.drawable.air_open;
							} else {
								res = R.drawable.air_close;
							}
						}
					}
					text = CommonUtils.getGBstr(list.get(i).devName);
					break;
				case 1: // 电视
					text = CommonUtils.getGBstr(list.get(i).devName);
					break;
				case 2: // 机顶盒
					text = CommonUtils.getGBstr(list.get(i).devName);
					break;
				case 3: // 灯光
					for (WareLight light : HomeActivity.mLightDataset) {
						if (light.dev.devId == list.get(i).devId) {
							if (light.bOnOff == 1) {
								res = R.drawable.light_on;
							} else {
								res = R.drawable.light_off;
							}
						}
					}
					text = CommonUtils.getGBstr(list.get(i).devName);
					break;
				case 4: // 窗帘
					for (WareCurtain curtain : HomeActivity.mCurtainDataset) {
						if (curtain.dev.devId == list.get(i).devId) {
							if (curtain.bOnOff == 1) {
								res = R.drawable.curtain_open;
							} else {
								res = R.drawable.curtain_close;
							}
						}
					}
					text = CommonUtils.getGBstr(list.get(i).devName);
					break;
				case 5: // 门锁
				case 6: // 阀门
				case 7: // 新风
				default:
					break;
				}
				if (!text.equals("")) {
					map.put("ItemImage", res);
					map.put("ItemText", text);
					lst.add(map);
				}
			}
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.on_enable);
		map.put("ItemText", "全开");
		lst.add(map);

		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("ItemImage", R.drawable.off_disable);
		map1.put("ItemText", "全关");
		lst.add(map1);
	}

	private void showDevsByType() {

		gridView.setVisibility(view.VISIBLE);
		progress.setVisibility(view.GONE);

		lst.clear();
		mWareDevs = HomeActivity.mWareDevs;

		List<WareDev> list = removeDuplicate(mWareDevs);
		Object res = "", text = "";

		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// LogUtils.LOGE(TAG, "设备类型" + list.get(i).devType);
			switch (list.get(i).devType) {
			case 0: // 空调
				res = devTypeIcon[0];
				text = devTypeIconName[0];
				break;
			case 1: // 电视
				res = devTypeIcon[1];
				text = devTypeIconName[1];
				break;
			case 2: // 机顶盒
				res = devTypeIcon[7];
				text = devTypeIconName[7];
				break;
			case 3: // 灯光
				res = devTypeIcon[2];
				text = devTypeIconName[2];
				break;
			case 4: // 窗帘
				res = devTypeIcon[3];
				text = devTypeIconName[3];
				break;
			case 5: // 门锁
				res = devTypeIcon[4];
				text = devTypeIconName[4];
				break;
			case 6: // 阀门
				res = devTypeIcon[5];
				text = devTypeIconName[5];
				break;
			case 7: // 新风
				res = devTypeIcon[6];
				text = devTypeIconName[6];
				break;
			default:
				break;
			}
			map.put("ItemImage", res);
			map.put("ItemText", text);
			lst.add(map);
		}
	}

	public List<WareDev> removeDuplicate(List<WareDev> list) {
		List<WareDev> devs = new ArrayList<WareDev>();

		for (int i = 0; i < list.size(); i++) {
			devs.add(list.get(i));
		}

		for (int i = 0; i < devs.size() - 1; i++) {
			for (int j = devs.size() - 1; j > i; j--) {
				if (devs.get(j).devType == devs.get(i).devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}

	private class MyLightAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mLightDataset != null) {
				return mLightDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			WareDev dev;
			int onoff;
			int lmvalue;
			int tuneEn;

			ImageView lightIv;
			TextView lightName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.lightIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.lightName = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareLight currentLight = mLightDataset.get(position);
			viewHolder.dev = currentLight.dev;
			viewHolder.onoff = currentLight.bOnOff;
			viewHolder.lmvalue = currentLight.lmVal;
			viewHolder.tuneEn = currentLight.bTuneEn;

			if (currentLight.bOnOff == 1) {
				viewHolder.lightIv.setImageResource(R.drawable.light_on);
			} else {
				viewHolder.lightIv.setImageResource(R.drawable.light_off);
			}

			viewHolder.lightName.setText(CommonUtils
					.getGBstr(currentLight.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (viewHolder.tuneEn == 1) {
						Intent intent = new Intent();
						intent.setClass(getActivity(),
								activityLightTuneControl.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("light", currentLight);
						intent.putExtras(bundle);
						startActivity(intent);
					} else {
						if (viewHolder.onoff == 0) {
							if (currentLight != null) {
								CommonUtils.controlLight(currentLight, 0);
							}
						} else {
							if (currentLight != null) {
								CommonUtils.controlLight(currentLight, 1);
							}
						}
					}
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "light");
					bundle.putSerializable("light", currentLight);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});

			return convertView;
		}
	}

	private class MyTvAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mTvs != null) {
				return mTvs.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			ImageView tvIv;
			TextView tvName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.tvName = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareTv currentTv = mTvs.get(position);
			viewHolder.tvIv.setImageResource(R.drawable.ic_tv);
			viewHolder.tvName.setText(CommonUtils
					.getGBstr(currentTv.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), ActivityTvControl.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("tv", currentTv);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "tv");
					bundle.putSerializable("tv", currentTv);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});

			return convertView;
		}
	}

	private class MySetBoxAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mSetBoxs != null) {
				return mSetBoxs.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			ImageView setBoxIv;
			TextView setBoxName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.setBoxIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.setBoxName = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareSetBox currentSetBox = mSetBoxs.get(position);

			viewHolder.setBoxIv.setImageResource(R.drawable.ic_stb);

			viewHolder.setBoxName.setText(CommonUtils
					.getGBstr(currentSetBox.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), ActivitySetBoxControl.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("setBox", currentSetBox);
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "setBox");
					bundle.putSerializable("setBox", currentSetBox);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});

			return convertView;
		}
	}

	private class MyCurtainAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mCurtainDataset != null) {
				return mCurtainDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			WareDev dev;
			int onoff;

			ImageView curtainIv;
			TextView curtainName;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.curtainIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.curtainName = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareCurtain currentCurtain = mCurtainDataset.get(position);
			viewHolder.dev = currentCurtain.dev;
			viewHolder.onoff = currentCurtain.bOnOff;

			if (currentCurtain.bOnOff == 1) {
				viewHolder.curtainIv.setImageResource(R.drawable.curtain_open);
			} else {
				viewHolder.curtainIv.setImageResource(R.drawable.curtain_close);
			}

			viewHolder.curtainName.setText(CommonUtils
					.getGBstr(currentCurtain.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), activityCurtainControl.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("curtain", currentCurtain);
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "curtain");
					bundle.putSerializable("curtain", currentCurtain);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});

			return convertView;
		}

	}

	private class MyLockAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mLockDataset != null) {
				return mLockDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			ImageView lockIv;
			TextView lockTv;

			WareDev dev;
			int onoff;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.lockIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.lockTv = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareLock currentLock = mLockDataset.get(position);
			viewHolder.dev = currentLock.dev;
			viewHolder.onoff = currentLock.bOnOff;

			if (currentLock.bOnOff == 1) {
				viewHolder.lockIv.setImageResource(R.drawable.lock_close);
			} else {
				viewHolder.lockIv.setImageResource(R.drawable.lock_open);
			}

			viewHolder.lockTv.setText(CommonUtils
					.getGBstr(currentLock.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), activityLockControl.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("lock", currentLock);
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "lock");
					bundle.putSerializable("lock", currentLock);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});

			return convertView;
		}

	}

	private class MyValveAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mValueDataset != null) {
				return mValueDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			ImageView valveIv;
			TextView valveTv;

			WareDev dev;
			int onoff;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.valveIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.valveTv = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareValve currentValve = mValueDataset.get(position);
			viewHolder.dev = currentValve.dev;
			viewHolder.onoff = currentValve.bOnOff;

			if (viewHolder.onoff == 0) {
				viewHolder.valveIv.setImageResource(R.drawable.valve_close);
			} else {
				viewHolder.valveIv.setImageResource(R.drawable.valve_open);
			}

			viewHolder.valveTv.setText(CommonUtils
					.getGBstr(currentValve.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), valueControlView.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("value", currentValve);
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "value");
					bundle.putSerializable("value", currentValve);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});

			return convertView;
		}
	}

	private class MyAirCondAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mAircondDataset != null) {
				return mAircondDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			ImageView airCondIv;
			TextView airCondTv;

			WareDev dev;
			int onoff;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.airCondIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.airCondTv = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareAirCondDev currentairCond = mAircondDataset.get(position);

			viewHolder.dev = currentairCond.dev;
			viewHolder.onoff = currentairCond.bOnOff;

			if (currentairCond.bOnOff == 1) {
				viewHolder.airCondIv.setImageResource(R.drawable.air_close);
			} else {
				viewHolder.airCondIv.setImageResource(R.drawable.air_open);
			}

			viewHolder.airCondTv.setText(CommonUtils
					.getGBstr(currentairCond.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), activityAircondControl.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "aircond");
					bundle.putSerializable("aircond", currentairCond);
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "aircond");
					bundle.putSerializable("aircond", currentairCond);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});

			return convertView;
		}
	}

	private class MyFreshairAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mFreshairDataset != null) {
				return mFreshairDataset.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		private class ViewHolder {
			ImageView freshairIv;
			TextView freshairTv;

			WareDev dev;
			int onoff;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.alarm_grid_item, null);
				viewHolder = new ViewHolder();
				viewHolder.freshairIv = (ImageView) convertView
						.findViewById(R.id.alarm_grid_item_iv);
				viewHolder.freshairTv = (TextView) convertView
						.findViewById(R.id.alarm_grid_item_tv);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			final WareFreshAir currentfreshair = mFreshairDataset.get(position);

			viewHolder.dev = currentfreshair.dev;
			viewHolder.onoff = currentfreshair.bOnOff;

			if (currentfreshair.bOnOff == 1) {
				viewHolder.freshairIv.setImageResource(R.drawable.on_enable);
			} else {
				viewHolder.freshairIv.setImageResource(R.drawable.off_disable);
			}

			viewHolder.freshairTv.setText(CommonUtils
					.getGBstr(currentfreshair.dev.devName));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent();
					intent.setClass(getActivity(),
							activityFreshAirControl.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "freshair");
					bundle.putSerializable("freshair", currentfreshair);
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(), activityEditDev.class);
					Bundle bundle = new Bundle();
					bundle.putString("dev", "freshair");
					bundle.putSerializable("freshair", currentfreshair);
					intent.putExtras(bundle);
					startActivity(intent);

					return false;
				}
			});
			return convertView;
		}
	}

	private long exitTime = 0;

	@Override
	public boolean onBackPressed() {

		if (hadIntercept) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT)
						.show();
				// 将系统当前的时间赋值给exitTime
				exitTime = System.currentTimeMillis();
			} else {
				HomeActivity parentActivity = (HomeActivity) getActivity();
				parentActivity.saveObject("dev.dat", -1);
				parentActivity.saveObject("airs.dat", 0);
				parentActivity.saveObject("tvs.dat", 1);
				parentActivity.saveObject("boxs.dat", 2);
				parentActivity.saveObject("lights.dat", 3);
				parentActivity.saveObject("curtains.dat", 4);
				parentActivity.saveObject("values.dat", 6);

				getActivity().finish();
				System.exit(0);
			}
		} else {
			hadIntercept = true;
			if (saItem != null) {
				showDevsByType();
				gridView.setAdapter(saItem);
				saItem.notifyDataSetChanged();
			}
			currentAdapter = null;
			HomeActivity.currentRoomName = "全部";
			mcallbacks.setTitle(HomeActivity.currentRoomName);
			gridView.setClickable(true);
		}

		return true;
	}

	public interface Callbacks {
		void setTitle(String str);
	};

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();

		mcallbacks = null;
	}
}
