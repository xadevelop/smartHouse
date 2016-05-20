package com.example.smarthouse;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.keyInputListViewAdapter;
import com.pullmi.common.CommonUtils;
import com.pullmi.entity.WareBoardKeyInput;
import com.pullmi.entity.WareDev;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class KeyInputSetActivity extends AdAbstractActivity {

	private LinearLayout createLayout;
	private ListView listview;

	private ArrayList<String> arrayList;
	keyInputListViewAdapter adapter;
	String boardName;
	int boardType;
	byte[] devUnitID;

	private List<WareBoardKeyInput> mBoardKeyInputs;
	private List<WareDev> mWareDevs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		boardName = intent.getExtras().getString("boardName");
		boardType = intent.getExtras().getInt("boardType");
		devUnitID = intent.getExtras().getByteArray("devUnitID");

		initComponent();
	}

	private void initComponent() {

		setTextForTitle(getString(R.string.systemSetting));
		setTextForBackBtn(getString(R.string.app_back));

		createLayout = (LinearLayout) layoutInflater.inflate(R.layout.activity_keyinput_set, null);
		mRootView.addView(createLayout, FF);

		listview = (ListView) findViewById(R.id.key_name_lst);

		arrayList = new ArrayList<String>();

		getListData(boardName);

		adapter = new keyInputListViewAdapter(this, arrayList, devUnitID, boardType);

		listview.setAdapter(adapter);
	}

	private void getListData(String boardname) {

		// 输入模块
		arrayList.clear();
		if (boardType == 1) {

			mBoardKeyInputs = HomeActivity.mBoardKeyInputs;

			if (mBoardKeyInputs == null) {
				return;
			}

			for (int i = 0; i < mBoardKeyInputs.size(); i++) {
				if (boardName.equals(CommonUtils.getGBstr(mBoardKeyInputs.get(i).boardName))) {
					for (int j = 0; j < mBoardKeyInputs.get(i).keyCnt; j++) {
						arrayList.add(CommonUtils.getGBstr(mBoardKeyInputs.get(i).keyName[j]));
					}
				}
			}
		}
		if (boardType == 0) {

			mWareDevs = HomeActivity.mWareDevs;

			if (mWareDevs == null) {
				return;
			}
			for (int i = 0; i < mWareDevs.size(); i++) {
				String devuid = new String(mWareDevs.get(i).canCpuId);
				if (new String(devUnitID).equals(devuid)) {
					arrayList.add(CommonUtils.getGBstr(mWareDevs.get(i).devName));
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn4:
			this.finish();
			break;

		default:
			break;
		}
	}
}
