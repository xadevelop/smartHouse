package com.pullmi.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

import com.pullmi.app.GlobalVars;

public class FileUtils {

	public static boolean copyDatabaseFileAssets(String filename) {
		AssetManager assetManager = GlobalVars.getContext().getAssets();

		InputStream ins = null;

		try {
			ins = assetManager.open(filename);
			return copyDatabaseFile(ins, filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyDatabaseFile(InputStream inputStream,
			String fileName) throws IOException {
		File out = new File("/data/data/com.pullmi.shanghai/databases/"
				+ fileName);
		File fileDatabases = new File(
				"/data/data/com.pullmi.shanghai/databases");

		if (!fileDatabases.exists()) {
			fileDatabases.mkdir();
		}

		FileOutputStream fileOutputStream = new FileOutputStream(out);
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				fileOutputStream.write(buf, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			fileOutputStream.close();
			inputStream.close();
		}
		return true;
	}

}
