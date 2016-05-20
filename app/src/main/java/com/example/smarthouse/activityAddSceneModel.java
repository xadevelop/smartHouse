package com.example.smarthouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class activityAddSceneModel extends AdAbstractActivity {

	private LinearLayout createLayout;
	private EditText nameText;
	private Button save,cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		initComponent();
	}
	private void initComponent() {

		setTextForTitle(getString(R.string.new_scene_model));

		createLayout = (LinearLayout) layoutInflater.inflate(
				R.layout.activity_add_scene, null);
		mRootView.addView(createLayout, FF);

		nameText = (EditText) findViewById(R.id.edittext);
		save = (Button)findViewById(R.id.saveEvent);
		cancel = (Button)findViewById(R.id.cancelEvent);
		
		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		save.setOnTouchListener(TouchDark);
		cancel.setOnTouchListener(TouchDark);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.saveEvent:
			Intent mIntent = new Intent();
			String name = nameText.getText().toString();
	        mIntent.putExtra("mode", name);  
	        // 设置结果，并进行传送  
	        int resultCode = 0;
	        this.setResult(resultCode, mIntent);  
	        this.finish();  
			break;
		case R.id.cancelEvent:
			this.finish();
			break;
		default:
			break;
		}
	}
}
