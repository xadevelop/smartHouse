package com.pullmi.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogUtil {

	public static AlertDialog createAlertDialog(Context context, String title, String message, String postiveButtonTitle) {
		return new AlertDialog
		.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(postiveButtonTitle, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		}).create();
	}
	
	public static AlertDialog createAlertDialog(Context context, String title, String message, String positiveButtonTitle, String negativeButtonTitle, OnClickListener pListener, OnClickListener nListener) {
		return new AlertDialog
		.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(positiveButtonTitle, pListener)
		.setNegativeButton(negativeButtonTitle, nListener).create();
	}
}
