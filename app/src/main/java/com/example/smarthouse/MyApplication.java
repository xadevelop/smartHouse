package com.example.smarthouse;

import java.io.File;

import com.pullmi.app.GlobalVars;
import com.pullmi.common.Constants;
import com.pullmi.common.FileUtils;

import android.app.Application;
import android.os.Handler;

public class MyApplication extends Application {

	public static MyApplication mInstance;
	private Handler handler = null;
	private String devid,devpass;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		GlobalVars.init(this.getApplicationContext());
		GlobalVars.setSn(0);
		
		CrashHandler catchHandler = CrashHandler.getInstance();
		catchHandler.init(getApplicationContext());

		/*if (!new File(Constants.DATABASE_DIRECTORY).exists()) {
			FileUtils.copyDatabaseFileAssets(Constants.DATABASE_NAME);
		}*/

	}
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public String getDevpass() {
		return devpass;
	}

	public void setDevpass(String devpass) {
		this.devpass = devpass;
	}
}
