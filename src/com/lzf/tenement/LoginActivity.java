package com.lzf.tenement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.http.GetData;
import com.lzf.tenement.http.PostData;
import com.lzf.tenement.util.AndroidPK;
import com.lzf.tenement.util.FormatMatch;
import com.lzf.tenement.util.UrlString;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private RelativeLayout captchaRL; // ��֤���ܿ���
	private TextView getCaptacha; // ��ȡ��֤��ؼ�
	private EditText captchaET; // ��֤������ؼ�
	private EditText account;
	private EditText pw;
	private Button register;
	private Button login;
	private int timer; // ��ʱ��
	private final int LOGIN = 0; // �ظ���¼
	private final int LOGIN_VERIFY = 1; // ��¼��֤
	private final int CAPTACHA = 4; // ��֤���־
	HashMap<String, Object> result = null; // ��֤�뷵��ֵ��ԭ������ֵ��
	private String resp;
	private String captcha; // ����˷��͵���֤��
	private String ShuRuyzm; // �ͻ����������֤��
	private String phone; // �ֻ���
	private String password; // ����
	private String androidPK; // ��׿�豸Ψһ��ʶ

	private Handler handler = new Handler() {
		@SuppressLint("NewApi")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOGIN:
				if (resp.equals("fail")) {
					login.setClickable(true);
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(resp);
						if (jObject.getBoolean("success")) {
							if (!jObject.getString("error").equals("��½�ɹ�")) {
								final JSONArray jArray = jObject.getJSONArray("data");
								final String[] xiaoQu = new String[jArray.length()];
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject temp = jArray.getJSONObject(i);
									xiaoQu[i] = temp.getString("fwname");
								}
								Builder builder = new AlertDialog.Builder(LoginActivity.this);
								AlertDialog alert = builder.setTitle("ѡ��Ҫ��ȥ�ķ���").setCancelable(false)
										.setItems(xiaoQu, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												try {
													login(jArray.getJSONObject(which).getString("Uid"));
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										}).create();
								alert.show();
							} else {
								JSONObject jsonObject = jObject.getJSONObject("data");
								SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo",
										Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.putString("id", jsonObject.getString("Uid"));
								editor.putString("xiaoquGuid", jsonObject.getString("xiaoquGuid"));
								editor.putString("FangHaoGuid", jsonObject.getString("FangHaoGuid"));
								editor.putString("phone", jsonObject.getString("phone"));
								editor.putString("ShuRuyzm", jsonObject.getString("ShuRuyzm"));
								editor.putString("password", jsonObject.getString("password"));
								editor.putString("uuid", jsonObject.getString("uuid"));
								editor.putString("WeiXin", jsonObject.getString("WeiXin"));
								editor.putString("xingming", jsonObject.getString("xingming"));
								editor.putString("xiaoquName", jsonObject.getString("xiaoquName"));
								editor.putString("FangHaoName", jsonObject.getString("FangHaoName"));
								editor.putInt("IsIosOrAndroid", jsonObject.getInt("IsIosOrAndroid"));
								editor.putInt("RoleID", jsonObject.getInt("RoleID"));
								editor.putString("token", jsonObject.getString("token"));
								editor.putString("clientid", jsonObject.getString("clientid"));
								editor.putString("appid", jsonObject.getString("appid"));
								editor.putString("appkey", jsonObject.getString("appkey"));
								editor.commit();
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);
								finish();
							}
						} else if (jObject.getInt("data") == 1) {
							login.setClickable(true);
							findViewById(R.id.newPw).setVisibility(View.VISIBLE);
							findViewById(R.id.line4).setVisibility(View.VISIBLE);
							Toast.makeText(getApplicationContext(), "��⵽���������豸�Ͻ��в�����Ϊ�����İ�ȫ����������֤�롣", Toast.LENGTH_SHORT)
									.show();
						} else if (jObject.getInt("data") == 2) {
							login.setClickable(true);
							pw.requestFocus();
							Toast.makeText(getApplicationContext(), "�˺ź����벻��Ӧ", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						login.setClickable(true);
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case LOGIN_VERIFY:
				if (resp.equals("fail")) {
					login.setClickable(true);
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(resp);
						if (jObject.getBoolean("success")) {
							if (!jObject.getString("error").equals("��½�ɹ�")) {
								final JSONArray jArray = jObject.getJSONArray("data");
								final String[] xiaoQu = new String[jArray.length()];
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject temp = jArray.getJSONObject(i);
									xiaoQu[i] = temp.getString("fwname");
								}
								Builder builder = new AlertDialog.Builder(LoginActivity.this);
								AlertDialog alert = builder.setTitle("ѡ��Ҫ��ȥ�ķ���").setCancelable(false)
										.setItems(xiaoQu, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												try {
													login(jArray.getJSONObject(which).getString("Uid"));
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										}).create();
								alert.show();
							} else {
								JSONObject jsonObject = jObject.getJSONObject("data");
								SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo",
										Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.putString("id", jsonObject.getString("Uid"));
								editor.putString("xiaoquGuid", jsonObject.getString("xiaoquGuid"));
								editor.putString("FangHaoGuid", jsonObject.getString("FangHaoGuid"));
								editor.putString("phone", jsonObject.getString("phone"));
								editor.putString("ShuRuyzm", jsonObject.getString("ShuRuyzm"));
								editor.putString("password", jsonObject.getString("password"));
								editor.putString("uuid", jsonObject.getString("uuid"));
								editor.putString("WeiXin", jsonObject.getString("WeiXin"));
								editor.putString("xingming", jsonObject.getString("xingming"));
								editor.putString("xiaoquName", jsonObject.getString("xiaoquName"));
								editor.putString("FangHaoName", jsonObject.getString("FangHaoName"));
								editor.putInt("IsIosOrAndroid", jsonObject.getInt("IsIosOrAndroid"));
								editor.putInt("RoleID", jsonObject.getInt("RoleID"));
								editor.putString("token", jsonObject.getString("token"));
								editor.putString("clientid", jsonObject.getString("clientid"));
								editor.putString("appid", jsonObject.getString("appid"));
								editor.putString("appkey", jsonObject.getString("appkey"));
								editor.commit();
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);
								finish();
							}
						} else if (jObject.getInt("data") == 1) {
							login.setClickable(true);
							findViewById(R.id.newPw).setVisibility(View.VISIBLE);
							Toast.makeText(getApplicationContext(), "��⵽���������豸�Ͻ��в�����Ϊ�����İ�ȫ����������֤�롣", Toast.LENGTH_SHORT)
									.show();
						} else if (jObject.getInt("data") == 2) {
							login.setClickable(true);
							pw.requestFocus();
							Toast.makeText(getApplicationContext(), "�˺ź����벻��Ӧ", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						login.setClickable(true);
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case CAPTACHA:
				if (timer == 0) {
					getCaptacha.setText("��ȡ��֤��");
					// if (!("000000".equals(result.get("statusCode")))) {
					// Toast.makeText(getApplicationContext(), (CharSequence)
					// result.get("statusMsg"),
					// Toast.LENGTH_LONG).show();
					// }
				} else {
					getCaptacha.setText(timer + " s");
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
		setContentView(R.layout.activity_login);
		account = (EditText) findViewById(R.id.account);
		pw = (EditText) findViewById(R.id.pw);
		register = (Button) findViewById(R.id.register);
		login = (Button) findViewById(R.id.login);
		captchaRL = (RelativeLayout) findViewById(R.id.newPw);
		captchaET = (EditText) findViewById(R.id.captchaET);
		androidPK = getAndroidPK();

		findViewById(R.id.forgetPW).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this, ForgetPwActivity.class);
				startActivity(intent);
			}
		});

		// �˺���Ϣ
		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		((EditText) findViewById(R.id.account)).setText(sp.getString("phone", ""));

		// ��ȡ��֤��
		getCaptacha = (TextView) findViewById(R.id.getCaptacha);
		getCaptacha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (getCaptacha.getText().equals("��ȡ��֤��")) {
					timer = 60;
					phone = account.getText().toString().trim();
					if (FormatMatch.phone(phone)) {
						handler.sendEmptyMessage(CAPTACHA);
						new Thread() {
							public void run() {
								captcha = String.valueOf(Math.random()).substring(3, 9);
								// result = Captcha.getCaptcha(captcha, phone);
								GetData.getHtml(UrlString.LiuPeng + "/PhoneYZM?phone=" + phone + "&yzm=" + captcha);
								System.out.println(UrlString.LiuPeng + "/PhoneYZM?phone=" + phone + "&yzm=" + captcha);
								while (timer > 0) {
									handler.sendEmptyMessage(CAPTACHA);
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
						Toast.makeText(getApplicationContext(), "��������ȷ���ֻ���", Toast.LENGTH_SHORT).show();
						account.requestFocus();
					}
				}
			}
		});
	}

	// ��¼�¼�
	public void login(View view) {
		if (captchaRL.getVisibility() == View.GONE) {
			phone = account.getText().toString().trim();
			password = pw.getText().toString().trim();
			if (FormatMatch.phone(phone)) {
				login.setClickable(false);
				new Thread() {
					public void run() {
						resp = PostData.submit(UrlString.LiuPeng + "/LoginAction", phone, password, androidPK, "0");
						System.out.println(resp);
						handler.sendEmptyMessage(LOGIN);
					}
				}.start();
			} else {
				Toast.makeText(getApplicationContext(), "��������ȷ���ֻ���", Toast.LENGTH_SHORT).show();
				account.requestFocus();
			}
		} else if (captchaRL.getVisibility() == View.VISIBLE) {
			phone = account.getText().toString().trim();
			password = pw.getText().toString().trim();
			ShuRuyzm = captchaET.getText().toString().trim();

			if (FormatMatch.phone(phone)) {
				if (ShuRuyzm == null || ShuRuyzm.equals("") || !(ShuRuyzm.equals(captcha))) {
					Toast.makeText(getApplicationContext(), "��������ȷ����֤��", Toast.LENGTH_SHORT).show();
				} else {
					login.setClickable(false);
					new Thread() {

						public void run() {
							resp = PostData.submit(UrlString.LiuPeng + "/LoginAction", phone, password, androidPK,
									ShuRuyzm);
							System.out.println(resp);
							handler.sendEmptyMessage(LOGIN_VERIFY);
						}

					}.start();
				}
			} else {
				Toast.makeText(getApplicationContext(), "��������ȷ���ֻ���", Toast.LENGTH_SHORT).show();
				account.requestFocus();
			}
			// System.out.println(
			// "phone��" + phone + "�� password��" + password + "��uuid��" +
			// androidPK + "��captacha��" + captcha);
		}
	}

	private void login(final String uid) {
		new Thread() {
			public void run() {
				resp = GetData.getHtml(UrlString.LiuPeng + "/LoginAction?uid=" + uid);
				System.out.println(resp);
				handler.sendEmptyMessage(LOGIN_VERIFY);
			}
		}.start();
	}

	// ע���¼�
	public void register(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		intent.putExtra("androidPK", androidPK);
		startActivity(intent);
	}

	// ��ȡ��׿�豸Ψһ��ʶ
	public String getAndroidPK() {
		String uuid = AndroidPK.getAndroidOuterPK(getApplicationContext());
		if (uuid.equals("SdNotFound")) {
			uuid = AndroidPK.getAndroidInnerPK(getApplicationContext());
			if (uuid.equals("FileNotFound")) {
				uuid = UUID.randomUUID().toString();
				AndroidPK.setAndroidInnerPK(getApplicationContext(), uuid);
				return uuid;
			} else {
				return uuid;
			}
		} else {
			if (uuid.equals("FileNotFound")) {
				uuid = AndroidPK.getAndroidInnerPK(getApplicationContext());
				if (uuid.equals("FileNotFound")) {
					uuid = UUID.randomUUID().toString();
					AndroidPK.setAndroidInnerPK(getApplicationContext(), uuid);
					AndroidPK.setAndroidOuterPK(getApplicationContext(), uuid);
					return uuid;
				} else {
					AndroidPK.setAndroidOuterPK(getApplicationContext(), uuid);
					return uuid;
				}
			} else {
				if ((AndroidPK.getAndroidInnerPK(getApplicationContext())).equals("FileNotFound")) {
					AndroidPK.setAndroidInnerPK(getApplicationContext(), uuid);
				}
				return uuid;
			}
		}
	}
}
