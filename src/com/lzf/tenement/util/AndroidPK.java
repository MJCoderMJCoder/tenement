package com.lzf.tenement.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

/**
 * 安卓设备唯一标识
 */
public class AndroidPK {

	private static String streamToStr(FileInputStream input) {
		StringBuilder sb = new StringBuilder("");
		try {
			byte[] temp = new byte[1024];
			int len = 0;
			// 读取文件内容
			while ((len = input.read(temp)) > 0) {
				sb.append(new String(temp, 0, len));
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().trim();
	}

	// 内存中的安卓设备唯一标识
	public static String getAndroidInnerPK(Context context) {
		String uuid = null;
		try {
			FileInputStream input = context.openFileInput("Android.PK");
			uuid = streamToStr(input);
		} catch (FileNotFoundException e) {
			uuid = "FileNotFound";
		}
		return uuid;
	}

	// 外存中的安卓设备唯一标识
	public static String getAndroidOuterPK(Context context) {
		String uuid = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String filename = null;
			try {
				filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/Android/Android.PK";
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			FileInputStream input = null;
			try {
				input = new FileInputStream(filename);
				uuid = streamToStr(input);
			} catch (FileNotFoundException e) {
				uuid = "FileNotFound";
			}
		} else {
			uuid = "SdNotFound";
		}
		return uuid;
	}

	// 内存中的安卓设备唯一标识
	public static void setAndroidInnerPK(Context context, String uuid) {
		try {
			FileOutputStream output = context.openFileOutput("Android.PK", Context.MODE_PRIVATE);
			output.write(uuid.getBytes()); // 将String字符串以字节流的形式写入到输出流中
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 外存中的安卓设备唯一标识
	public static void setAndroidOuterPK(Context context, String uuid) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String filename;
			try {
				filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/Android/Android.PK";
				FileOutputStream output = new FileOutputStream(filename);
				output.write(uuid.getBytes()); // 将String字符串以字节流的形式写入到输出流中
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
