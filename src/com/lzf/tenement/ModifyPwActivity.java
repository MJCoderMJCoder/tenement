package com.lzf.tenement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyPwActivity extends Activity {

	private Map<String, String> params = new HashMap<String, String>();

	private ProgressDialog progress = null;
	private EditText phone;

	private String server;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (progress != null) {
				progress.dismiss();
			}
			if (server.equals("fail")) {
				Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jObject = new JSONObject(server);
					if (jObject.getBoolean("success")) {
						Builder builder = new Builder(ModifyPwActivity.this);
						AlertDialog aDialog = builder.setIcon(R.drawable.success).setTitle("修改成功，请牢记。")
								.setPositiveButton("返回", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										onBackPressed();
									}
								}).create();
						aDialog.show(); // 显示对话框
					} else {
						if (jObject.getString("error").contains("获取数据失败")) {
							phone.requestFocus();
							Toast.makeText(getApplicationContext(), "当前（旧）密码与您输入的新密码相同，不需要修改。", Toast.LENGTH_SHORT)
									.show();
						} else if (jObject.getString("error").contains("密码错误")) {
							phone.requestFocus();
							Toast.makeText(getApplicationContext(), "当前（旧）密码错误，请重新输入您的当前（旧）密码。", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifypw);

		SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		params.put("xiaoquGuid", sp.getString("xiaoquGuid", ""));
		params.put("FangHaoGuid", sp.getString("FangHaoGuid", ""));
		params.put("phone", sp.getString("phone", ""));

		phone = (EditText) findViewById(R.id.phone);
		final EditText captcha = (EditText) findViewById(R.id.captcha);
		final EditText password = (EditText) findViewById(R.id.password);

		findViewById(R.id.modifyPW).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String oldPw = phone.getText().toString().trim();
				String newPw = captcha.getText().toString().trim();
				String NewPw = password.getText().toString().trim();
				if (oldPw.equals("") || oldPw == null) {
					phone.requestFocus();
					Toast.makeText(ModifyPwActivity.this, "请输入您的当前（旧）密码。", Toast.LENGTH_SHORT).show();
				} else if (newPw.equals("") || newPw == null) {
					captcha.requestFocus();
					Toast.makeText(ModifyPwActivity.this, "请输入您的新密码。", Toast.LENGTH_SHORT).show();
				} else if (!NewPw.equals(newPw)) {
					password.requestFocus();
					Toast.makeText(ModifyPwActivity.this, "两次输入的新密码不一致。", Toast.LENGTH_SHORT).show();
				} else {
					progress = ProgressDialog.show(ModifyPwActivity.this, null, "正在修改...");
					params.put("JiuMiMa", oldPw);
					params.put("XinMiMa", newPw);
					new Thread() {
						public void run() {
							server = OKHttp.uploadFiles(UrlString.LiuPeng + "/XiuGaiMiMa", params,
									new HashMap<String, File>());
							handler.sendEmptyMessage(6003);
						}
					}.start();
				}

			}
		});

	}

	public void back(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
