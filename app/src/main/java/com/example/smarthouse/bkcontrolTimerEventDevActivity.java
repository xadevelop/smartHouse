package com.example.smarthouse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pullmi.common.CommonUtils;
import com.pullmi.common.DbOperator;
import com.pullmi.entity.UnitNode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class bkcontrolTimerEventDevActivity extends Activity {

	private static final String TAG = "controlTimerEventDevActivity";
	private GridView gridView;
	private int airTag, lightTag, tvTag, curtainTag, lockTag, freshairTag;

	private List<UnitNode> nodes;
	ArrayList<HashMap<String, Object>> lst;

	private byte[] senddata;
	private int sendFlag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.control_dev_gird);

		Intent intent = getIntent();
		String roomName = intent.getStringExtra("roomName");

		nodes = DbOperator.getDbOperator().queryUnitNodeByRoomName(roomName);
		gridView = (GridView) findViewById(R.id.grid);
		lst = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < nodes.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			Object res = R.drawable.yszhfx, text = getString(R.string.e_ware_dev);

			switch (nodes.get(i).devType) {
			case 0: // 空调
				airTag = i;
				res = R.drawable.ac;
				text = getString(R.string.e_ware_airCond);
				break;
			case 1: // 电视
				tvTag = i;
				res = R.drawable.tv_r;
				text = getString(R.string.e_ware_tv);
				break;
			case 3: // 灯光
				lightTag = i;
				res = R.drawable.light;
				text = getString(R.string.e_ware_light);
				break;
			case 4: // 窗帘
				curtainTag = i;
				res = R.drawable.curtain;
				text = getString(R.string.e_ware_curtain);
				break;
			case 5: // 门锁
				lockTag = i;
				res = R.drawable.lock_r;
				text = getString(R.string.e_ware_lock);
				break;
			case 7: // 新风
				freshairTag = i;
				res = R.drawable.ac;
				text = getString(R.string.e_ware_fresh_air);
				break;
			default:
				break;
			}
			map.put("ItemImage", res);
			map.put("ItemText", text);
			lst.add(map);
		}

		final SimpleAdapter saItem = new SimpleAdapter(
				bkcontrolTimerEventDevActivity.this, lst, // 数据源
				R.layout.alarm_grid_item, // xml实现
				new String[] { "ItemImage", "ItemText" }, // 对应map的Key
				new int[] { R.id.alarm_grid_item_iv, R.id.alarm_grid_item_tv }); // 对应R的Id

		// 添加Item到网格中
		gridView.setAdapter(saItem);
		// 添加点击事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int index = arg2;// id是从0开始的，所以需要+1

				// Toast用于向用户显示一些帮助/提示
				// 在本例中arg2=arg3
				@SuppressWarnings("unchecked")
				HashMap<String, Object> item = (HashMap<String, Object>) arg0
						.getItemAtPosition(index);

				// 显示所选Item的ItemText
				String text = (String) item.get("ItemText");
				Toast.makeText(bkcontrolTimerEventDevActivity.this,
						"你按下了选项：" + text, 0).show();
				if (text.equals(getString(R.string.e_ware_light))) {
					// 添加情景事件
					sendFlag = 1;
					Log.e(TAG,
							new String(nodes.get(lightTag).devName)
									+ "------"
									+ new String(nodes.get(lightTag).canCpuId
											+ "------")
									+ new String(nodes.get(lightTag).roomName
											+ "------")
									+ nodes.get(lightTag).devType);
					byte[] timSta = new byte[4];
					timSta[0] = 12;
					timSta[1] = 20;
					timSta[2] = 23;

					byte test1 = 1 << 1;
					byte test2 = 1 << 3;
					byte test3 = 1 << 5;

					timSta[3] = (byte) (test1 | test2 | test3);

					byte[] timEnd = new byte[4];
					timEnd[0] = 22;
					timEnd[1] = 23;
					timEnd[2] = 30;
					timEnd[3] = 1;

					String timeString = "卧室窗帘开";
					int len = 0;
					try {
						len = timeString.getBytes("GBK").length;
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					byte[] timerName = new byte[12];
					int i;
					for (i = 0; i < len; i++) {
						timerName[i] = timeString.getBytes()[i];
					}
					for (; i < 12; i++) {
						timerName[i] = 0;
					}

					senddata = CommonUtils.createTimerEvent(timSta, timEnd,
							timerName, 1, 1, nodes.get(lightTag).canCpuId,
							nodes.get(lightTag).devId, (byte) 1);

				} else if (text.equals(getString(R.string.e_ware_curtain))) {
					sendFlag = 1;
				} else if (text.equals(getString(R.string.e_ware_airCond))) {
					sendFlag = 1;
				}
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				DatagramPacket packet;
				try {
					if (sendFlag == 1) {
						packet = new DatagramPacket(
								senddata,
								senddata.length,
								InetAddress.getByName(CommonUtils.getLocalIp()),
								8300);

						DatagramSocket sockTx = new DatagramSocket();
						sockTx.send(packet);

						sockTx.close();
						sockTx = null;
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
