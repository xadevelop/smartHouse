package com.example.smarthouse.hotel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.example.smarthouse.activityAircondControl;
import com.example.smarthouse.activityCurtainControl;
import com.example.smarthouse.activityEditDev;
import com.example.smarthouse.fragment.fragmentControlDev.Callbacks;
import com.pullmi.app.GlobalVars;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.UdpProPkt;
import com.pullmi.entity.WareAirCondDev;
import com.pullmi.entity.WareCurtain;
import com.pullmi.entity.WareLight;
import com.pullmi.utils.LogUtils;

/**
 * Created by maibenben on 2016/4/25.
 */
public class Air_condition extends Fragment {

	GridView mGridview;
	View view;
	List<String> list_zi;
	List<String> list_ying;
	ViewPager mViewPager;
	List<View> mVP_list;
	private int index;
	WareAirCondDev air;
	protected byte[] devBuff;
	private activityHotel mActivity;
	private MyVPAdapter adapter;
	private boolean powerState = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.air_condition_fragment, null);

		inti();// 初始化数据及组件；
		event();// 事件处理；

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		mActivity = (activityHotel) activity;
		mActivity.setHandler(mHandler);
	}

	public Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
				//LogUtils.LOGE("空调控制", "********");
				mVP_list.clear();
				setVPData();
				adapter.notifyDataSetChanged();

				break;
			}
		};
	};

	private void event() {
		adapter = new MyVPAdapter(mVP_list);
		mViewPager.setAdapter(adapter);

		// ViewPager设置适配器；
		mGridview.setAdapter(new MyAdapter(list_zi, list_ying, getActivity()));

		mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView textView = (TextView) view
						.findViewById(R.id.item_textview);

				String name = ((TextView) mVP_list.get(index).findViewById(
						R.id.name)).getText().toString();
				int curTem = Integer.parseInt(((TextView) mVP_list.get(index)
						.findViewById(R.id.set_temperature)).getText()
						.toString());

				for (int i = 0; i < activityHotel.mAircondDataset.size(); i++) {
					if (name.equals(CommonUtils
							.getGBstr(activityHotel.mAircondDataset.get(i).dev.devName))) {
						air = activityHotel.mAircondDataset.get(i);
						break;
					}
				}
				if (air != null) {
					byte[] state = new byte[8];
					state[0] = air.bOnOff;
					state[1] = air.selMode;
					state[2] = air.selTemp;
					state[3] = air.selSpd;
					state[4] = air.selDirect;
					state[5] = air.rev1;
					state[6] = (byte) air.powChn;
					state[7] = 0;

					devBuff = CommonUtils.createWareDevInfo(
							air.dev.getCanCpuId(), air.dev.getDevName(),
							air.dev.getRoomName(), air.dev.getType(),
							air.dev.getDevCtrlType(), air.dev.getDevId(), state);
				}
				int value = 0;
				int selTemp = curTem;
				switch (position) {
				case 0: // 开关机
					if (air.bOnOff == 1) {
						value = (UdpProPkt.E_AIR_MODE.e_air_auto.getValue() << 5)
								| UdpProPkt.E_AIR_CMD.e_air_pwrOff.getValue();
					} else {
						value = (UdpProPkt.E_AIR_MODE.e_air_auto.getValue() << 5)
								| UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue();
					}
					break;
				case 1:// 太冷，温度+
					selTemp = curTem + 1;
					value = (UdpProPkt.E_AIR_MODE.e_air_hot.getValue() << 5)
							| selTemp;
					break;
				case 2:// 太热，温度-
					selTemp = curTem - 1;
					value = (UdpProPkt.E_AIR_MODE.e_air_cool.getValue() << 5)
							| selTemp;
					break;
				case 3:// 扫风模式
					value = (UdpProPkt.E_AIR_MODE.e_air_wind.getValue() << 5)
							| selTemp;
					break;
				case 4:// 制冷模式
					value = (UdpProPkt.E_AIR_MODE.e_air_cool.getValue() << 5)
							| selTemp;
					break;
				case 5:// 制热模式
					value = (UdpProPkt.E_AIR_MODE.e_air_hot.getValue() << 5)
							| selTemp;
					break;
				case 6:// 风速高
					value = (UdpProPkt.E_AIR_MODE.e_air_auto.getValue() << 5)
							| UdpProPkt.E_AIR_CMD.e_air_spdHigh.getValue();
					break;
				case 7:// 风速中
					value = (UdpProPkt.E_AIR_MODE.e_air_auto.getValue() << 5)
							| UdpProPkt.E_AIR_CMD.e_air_spdMid.getValue();
					break;
				case 8:// 风速低
					value = (UdpProPkt.E_AIR_MODE.e_air_auto.getValue() << 5)
							| UdpProPkt.E_AIR_CMD.e_air_spdLow.getValue();
					break;
				default:
					break;
				}
				if (air.bOnOff == 1 || position == 0) {
					GlobalVars.setSenddata(CommonUtils.preSendUdpProPkt(
							GlobalVars.getDstip(),
							CommonUtils.getLocalIp(),
							UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue(),
							0, value, devBuff, devBuff.length));

					CommonUtils.sendMsg();
				}
				//Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
			}
		});

		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						if (positionOffset == 0) {
							Log.i("当前页面ID：", position + "");
							index = position;
						}
					}

					@Override
					public void onPageSelected(int position) {
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});
	}

	/**
	 * viewpager适配器
	 */
	class MyVPAdapter extends PagerAdapter {
		/**
		 * 这个可以不用，在一个类中。
		 */
		private List<View> list_view;
		private int mChildCount;

		public MyVPAdapter(List<View> list_view) {
			this.list_view = list_view;
		}

		@Override
		public int getCount() {
			return list_view.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
			//container.removeView(list_view.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list_view.get(position));
			return list_view.get(position);
		}

		@Override
		public void notifyDataSetChanged() {
			mChildCount = getCount();
			super.notifyDataSetChanged();
		}

		@Override
		public int getItemPosition(Object object) {
			if (mChildCount > 0) {
				mChildCount--;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}
	}

	private void setVPData() {
		List<WareAirCondDev> airs = activityHotel.mAircondDataset;
		if (airs.size() > 0) {
			for (int i = 0; i < airs.size(); i++) {
				View view = LayoutInflater.from(getActivity()).inflate(
						R.layout.vp_fragment, null);
				TextView name = (TextView) view.findViewById(R.id.name);
				TextView set_temperature = (TextView) view
						.findViewById(R.id.set_temperature);
				TextView state = (TextView) view.findViewById(R.id.state);
				TextView wind = (TextView) view.findViewById(R.id.wind);
				
				name.setText(CommonUtils.getGBstr(airs.get(i).dev.devName));
				set_temperature.setText(airs.get(i).selTemp + "");
				if (airs.get(i).bOnOff == 1) {
					state.setText("打开");
				}else {
					state.setText("关闭");
				}
				if (airs.get(i).selSpd == 2) {
					wind.setText("低风");
				}if (airs.get(i).selSpd == 3) {
					wind.setText("中风");
				}if (airs.get(i).selSpd == 4) {
					wind.setText("高风");
				}
				
				mVP_list.add(view);
			}
		}
	}

	private void inti() {
		mGridview = (GridView) view.findViewById(R.id.gridview);
		mViewPager = (ViewPager) view.findViewById(R.id.air_condition_vp);
		mVP_list = new ArrayList<View>();
		setVPData();

		list_zi = new ArrayList<String>();
		list_zi.add("开/关");
		list_zi.add("太冷");
		list_zi.add("太热");
		list_zi.add("扫风");
		list_zi.add("制冷");
		list_zi.add("制热");
		list_zi.add("风速高");
		list_zi.add("风速中");
		list_zi.add("风速低");
		list_ying = new ArrayList<String>();
		list_ying.add("On/Off");
		list_ying.add("℃+");
		list_ying.add("℃-");
		list_ying.add("Fan");
		list_ying.add("Colling");
		list_ying.add("Heating");
		list_ying.add("High-speed");
		list_ying.add("medium-speed");
		list_ying.add("low-speed");

	}
}
