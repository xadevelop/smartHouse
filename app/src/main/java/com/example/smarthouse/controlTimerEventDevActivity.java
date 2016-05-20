package com.example.smarthouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.example.adapter.sceneEventsListViewAdapter;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.WareDev;
import com.pullmi.entity.WareSceneDevItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class controlTimerEventDevActivity extends AdAbstractActivity {

	private static final String TAG = "controlTimerEventDevActivity";
	private ListView listview;
	private Button save;

	ArrayList<HashMap<String, Object>> lst;

	private static List<WareDev> mWareDevs;
	String roomName;

	private sceneEventsListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scene_control_dev_gird);

		Intent intent = getIntent();
		roomName = intent.getStringExtra("roomName");

		mWareDevs = HomeActivity.mWareDevs;

		save = (Button) findViewById(R.id.save_scene);
		save.setOnTouchListener(TouchDark);
		save.setOnClickListener(this);

		initView();
		initData();

	}

	private void initView() {
		// TODO Auto-generated method stub
		listview = (ListView) findViewById(R.id.scene_list);
		lst = new ArrayList<HashMap<String, Object>>();
		adapter = new sceneEventsListViewAdapter(controlTimerEventDevActivity.this, lst);
		listview.setAdapter(adapter);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	private void initData() {

		Object text = getString(R.string.e_ware_dev);

		for (int i = 0; i < mWareDevs.size(); i++) {

			int devtype = mWareDevs.get(i).devType;
			if (CommonUtils.getGBstr(mWareDevs.get(i).roomName).equals(roomName)) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				text = CommonUtils.getGBstr(mWareDevs.get(i).devName);
				map.put("ItemType", devtype);
				map.put("ItemText", text);
				lst.add(map);
			}

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.save_scene:
			Iterator iter = adapter.states.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				int key = Integer.parseInt((String) entry.getKey());
				boolean val = (Boolean) entry.getValue();

				String devName = lst.get(key).get("ItemText").toString();
				// LogUtils.LOGE(TAG, "states 状态" + val + "名称　" + devName);
				for (int i = 0, j = 0; i < mWareDevs.size(); i++, j++) {
					if (devName.equals(CommonUtils.getGBstr(mWareDevs.get(i).devName))) {
						WareSceneDevItem item = new WareSceneDevItem();
						item.uid = mWareDevs.get(i).canCpuId;
						item.devID = mWareDevs.get(i).devId;
						item.devType = mWareDevs.get(i).devType;
						if (val) {
							item.bOnOff = 1;
						} else {
							item.bOnOff = 0;
						}
						HomeActivity.mSceneDevs.add(item);
					}
				}

			}
			finish();
			break;

		default:
			break;
		}
	}
}
