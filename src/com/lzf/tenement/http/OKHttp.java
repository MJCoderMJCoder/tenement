package com.lzf.tenement.http;

import android.annotation.TargetApi;
import android.os.Build;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttp {

	private static OkHttpClient client = new OkHttpClient();

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String uploadFiles(String url, Map<String, String> params, Map<String, File> files) {
		String message = "fail"; // ���Ӳ������������������������Ժ�����
		MultipartBody.Builder builder = new MultipartBody.Builder();
		// ��������
		builder.setType(MultipartBody.FORM);
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.addFormDataPart(entry.getKey(), entry.getValue());
			}
		}
		if (files != null && files.size() > 0) {
			int i = 0;
			for (Map.Entry<String, File> entry : files.entrySet()) {
				i++;
				File temp = entry.getValue();
				builder.addFormDataPart("images" + i, temp.getName(), RequestBody.create(null, temp));
			}
		}
		RequestBody body = builder.build();
		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = client.newCall(request).execute()) {
			message = response.body().string();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return message;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getData(String url) {
		String message = "fail"; // ���Ӳ������������������������Ժ�����
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			message = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	// @TargetApi(Build.VERSION_CODES.KITKAT)
	// public static String submit(String url, Map<String, String> params) {
	// String message = "fail"; // ���Ӳ������������������������Ժ�����
	// FormBody.Builder builder = new FormBody.Builder();
	// if (params != null && params.size() > 0) {
	// for (Map.Entry<String, String> entry : params.entrySet()) {
	// builder.add(entry.getKey(), entry.getValue());
	// }
	// }
	// RequestBody body = builder.build();
	// Request request = new Request.Builder().url(url).post(body).build();
	// try (Response response = client.newCall(request).execute()) {
	// message = response.body().string();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return message;
	// }
}
