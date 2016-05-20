package com.example.smarthouse.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.smarthouse.HomeActivity;
import com.example.smarthouse.R;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.WareDev;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class setFragment extends Fragment {

	private View view;
	private GridView gridView;
	private List<Map<String, Object>> data_list;
	private SimpleAdapter sim_adapter;

	// 图片封装为一个数组
	private int[] icon = { R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher };
	private String[] iconName = { "常用", "房间", "设备", "设置" };
	private List<WareDev> mWareDevs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.fragment_my_set, container, false);
		gridView = (GridView) view.findViewById(R.id.grid);

		// 新建List
		data_list = new ArrayList<Map<String, Object>>();
		// 获取数据
		getData();
		
		sim_adapter = new SimpleAdapter(getActivity(), data_list, // 数据源
				R.layout.alarm_grid_item, // xml实现
				new String[] { "ItemImage", "ItemText" }, // 对应map的Key
				new int[] { R.id.alarm_grid_item_iv, R.id.alarm_grid_item_tv }); // 对应R的Id
		
		gridView.setAdapter(sim_adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				//Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
				if (position == 1) {
					showAllRooms();
				}
			}
		});

		return view;
	}
	
	private void showAllRooms(){
		mWareDevs = HomeActivity.mWareDevs;

		List<WareDev> lst = removeDuplicate(mWareDevs);
		final String[] item = new String[lst.size() + 1];
		for (int i = 0; i < lst.size(); i++) {
			item[i + 1] = CommonUtils.getGBstr(lst.get(i).roomName);
		}
		item[0] = "全部";
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setTitle("选择一个房间");
		// 指定下拉列表的显示数据d
		// 设置一个下拉的列表选择项
		builder.setItems(item, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				HomeActivity.currentRoomName = item[which];
				HomeActivity.myHandler.sendEmptyMessage(1010);
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
				if (devs.get(j).devType == devs.get(i).devType) {
					devs.remove(j);
				}
			}
		}

		return devs;
	}
	
	public List<Map<String, Object>> getData() {
		// cion和iconName的长度是相同的，这里任选其一都可以
		for (int i = 0; i < icon.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", icon[i]);
			map.put("ItemText", iconName[i]);
			data_list.add(map);
		}

		return data_list;
	}
}
