package com.lzf.tenement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.http.GetData;
import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.util.FormatMatch;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPwActivity extends Activity {

	private int timer; // 计时器
	private String phoneValue;
	private String captchaValue; // 服务端发送的验证码
	private String server;
	private Map<String, String> params = new HashMap<String, String>();

	private TextView getCaptcha;
	private EditText password;

	private final int CAPTCHA = 0;
	private final int SUBMIT = 1;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CAPTCHA:
				if (timer == 0) {
					getCaptcha.setText("获取验证码");
					// if (!("000000".equals(result.get("statusCode")))) {
					// Toast.makeText(getApplicationContext(), (CharSequence)
					// result.get("statusMsg"),
					// Toast.LENGTH_LONG).show();
					// }
				} else {
					getCaptcha.setText(timer + " s");
				}
				break;
			case SUBMIT:
				if (server.equals("fail")) {
					Toast.makeText(ForgetPwActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(server);
						if (jObject.getBoolean("success")) {
							Builder builder = new Builder(ForgetPwActivity.this);
							AlertDialog aDialog = builder.setIcon(R.drawable.success).setTitle("请牢记您的新密码。")
									.setPositiveButton("返回", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create();
							aDialog.show(); // 显示对话框
						} else {
							Toast.makeText(getApplicationContext(), jObject.getString("error"), Toast.LENGTH_SHORT)
									.show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgetpw);

		final EditText phone = (EditText) findViewById(R.id.oldPw);
		final EditText captcha = (EditText) findViewById(R.id.captchaET);
		password = (EditText) findViewById(R.id.newPassword);
		getCaptcha = (TextView) findViewById(R.id.getCaptacha);
		TextView modifyPW = (TextView) findViewById(R.id.modifyPW);

		getCaptcha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (getCaptcha.getText().equals("获取验证码")) {
					timer = 60;
					phoneValue = phone.getText().toString().trim();
					if (FormatMatch.phone(phoneValue)) {
						handler.sendEmptyMessage(CAPTCHA);
						new Thread() {
							public void run() {
								captchaValue = String.valueOf(Math.random()).substring(3, 7);
								// result = Captcha.getCaptcha(captcha, phone);
								GetData.getHtml(
										UrlString.LiuPeng + "/PhoneYZM?phone=" + phoneValue + "&yzm=" + captchaValue);
								System.out.println(
										UrlString.LiuPeng + "/PhoneYZM?phone=" + phoneValue + "&yzm=" + captchaValue);
								while (timer > 0) {
									handler.sendEmptyMessage(CAPTCHA);
									timer--;
									try {
										Thread.sleep(999);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}.start();
					} else {
						phone.requestFocus();
						Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		modifyPW.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				phoneValue = phone.getText().toString().trim();
				String captchaTemp = captcha.getText().toString().trim();
				String passwordValue = password.getText().toString().trim();
				if (FormatMatch.phone(phoneValue)) {
					if (captchaTemp.equals(captchaValue)) {
						if (!passwordValue.equals("") && passwordValue != null) {
							params.put("wjmmshoujihao", phoneValue);
							params.put("wjmmYZM", captchaTemp);
							params.put("wjmmXinMiMa", passwordValue);
							new Thread() {
								public void run() {
									server = OKHttp.uploadFiles(UrlString.LiuPeng + "/WangJiMiMa", params,
											new HashMap<String, File>());
									System.out.println(server);
									handler.sendEmptyMessage(SUBMIT);
								}
							}.start();
						} else {
							Toast.makeText(getApplicationContext(), "请输入您的新密码。", Toast.LENGTH_SHORT).show();
						}
					} else {
						captcha.requestFocus();
						Toast.makeText(getApplicationContext(), "请输入正确的验证码。", Toast.LENGTH_SHORT).show();
					}
				} else {
					phone.requestFocus();
					Toast.makeText(getApplicationContext(), "请输入正确的手机号。", Toast.LENGTH_SHORT).show();
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
