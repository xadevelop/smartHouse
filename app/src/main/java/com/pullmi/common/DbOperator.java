package com.pullmi.common;

import com.pullmi.app.GlobalVars;

import android.database.DatabaseUtils;

public class DbOperator {

	private static DatabaseUtil dbUtil;
	
	static {
		dbUtil = DatabaseUtil.getInstance(GlobalVars.getContext());
	}
	
	public static DatabaseUtil getDbOperator() {
		return dbUtil;
	}

}
