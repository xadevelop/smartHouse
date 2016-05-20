package com.pullmi.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

	public ProgressDialog createProgressDialog(Context context) {
		return ProgressDialog.show(context, "提示", "加载中,请稍等...");
	}
	
	public AlertDialog createAlertDialog(Context context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
		return new AlertDialog.Builder(context)
		.setTitle("提示")
		.setMessage(message)
		.setPositiveButton("确定", okListener)
		.setNegativeButton("删除", cancelListener)
		.create();
	}

}
