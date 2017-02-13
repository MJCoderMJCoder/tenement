package com.lzf.tenement;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.fragment.FragmentHome;
import com.lzf.tenement.fragment.FragmentMyself;
import com.lzf.tenement.fragment.FragmentNotification;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.util.UrlString;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public static Activity instance; // ����MainActivity��ʵ��
	private int width;
	private int height;
	private long exitTime = 0;
	private View shadow;
	private TextView home;
	// private TextView repairs;
	private TextView notification;
	private TextView myself;
	private TextView redPacketDetail;
	private RelativeLayout redPacket;
	private FragmentManager fManager;
	private FragmentHome fHome;
	// private RepairsStatusActivity fRepairs;
	private FragmentNotification fNotification;
	private FragmentMyself fMyself;

	private String xiaoquGuid;
	private String FangHaoGuid;
	private String phone;
	private String resp;

	private final int NEW_REDPACKET = 1;
	private final int GET_REDPACKET = 2;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NEW_REDPACKET:
				if (resp.equals("fail")) {
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(resp);
						if (jObject.getBoolean("success")) {
							JSONObject jsonObject = jObject.getJSONObject("data");
							redPacketDetail.setText(jsonObject.getString("HongBaoJianDanMiaoShu"));
							shadow.setVisibility(View.VISIBLE);
							redPacket.setVisibility(View.VISIBLE);

							// ��Ч����ϵͳ����
							Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
							// long[] pattern = { 100, 400, 100, 400 }; // ֹͣ ����
							// ֹͣ ����
							vibrator.vibrate(2000); // ������
							Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_rotate);
							redPacket.startAnimation(shake);
						} else {
							System.out.println("��ȡ�º��������ʧ��");
						}
					} catch (JSONException e) {
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case GET_REDPACKET:
				if (resp.equals("fail")) {
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(resp);
						if (jObject.getBoolean("success")) {
							shadow.setVisibility(View.GONE);
							redPacket.setVisibility(View.GONE);
							redPacket.clearAnimation();
							System.out.println("shadow.getVisibility()��" + shadow.getVisibility());
							System.out.println("redPacket.getVisibility()��" + redPacket.getVisibility());
							Intent redPacket = new Intent(MainActivity.this, RedPacketActivity.class);
							startActivity(redPacket);
						} else {
							Toast.makeText(MainActivity.this, "��ȡ���ʧ��", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
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
		setContentView(R.layout.activity_main);

		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		xiaoquGuid = sp.getString("xiaoquGuid", "");
		FangHaoGuid = sp.getString("FangHaoGuid", "");
		phone = sp.getString("phone", "");
		((TextView) findViewById(R.id.textTitle)).setText(sp.getString("xiaoquName", "�ƴ�Ƽ�"));

		instance = this;

		// ��̬��ȡ�ֻ���Ļ�Ŀ�ȣ������ݸ�FragmentHome
		WindowManager wManager = this.getWindowManager();
		width = wManager.getDefaultDisplay().getWidth();
		height = wManager.getDefaultDisplay().getHeight(); // ������:px��

		// Ϊ����Ϻ��������ӵĸ��ǹ���
		shadow = findViewById(R.id.shadow);

		// ������涯̬��ʾ
		redPacket = (RelativeLayout) findViewById(R.id.redPacket);
		RelativeLayout.LayoutParams rpParams = new RelativeLayout.LayoutParams(width - 200, height - 500);
		rpParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		redPacket.setLayoutParams(rpParams);
		RelativeLayout rpdRL = (RelativeLayout) findViewById(R.id.rpdRL);
		RelativeLayout.LayoutParams rpdRLParams = new RelativeLayout.LayoutParams((width - 200) * 230 / 305,
				(height - 500) * 100 / 380);
		rpdRLParams.setMargins(0, (height - 500) * 50 / 380, 0, 0);
		rpdRLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rpdRL.setLayoutParams(rpdRLParams);
		redPacketDetail = (TextView) findViewById(R.id.redPacketDetail);
		findViewById(R.id.getRedPacket).setOnClickListener(this);
		findViewById(R.id.shadow).setOnClickListener(this);

		// ��ʼ���ײ��˵���
		home = (TextView) findViewById(R.id.home);
		// repairs = (TextView) findViewById(R.id.repairs);
		notification = (TextView) findViewById(R.id.notification);
		myself = (TextView) findViewById(R.id.myself);
		home.setOnClickListener(this);
		// repairs.setOnClickListener(this);
		notification.setOnClickListener(this);
		myself.setOnClickListener(this);

		fManager = getFragmentManager();
		home.performClick();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.getRedPacket) {
			new Thread() {
				public void run() {
					resp = GetData.getHtml(UrlString.LiuPeng + "/LingQuHongBao?xiaoquGuid=" + xiaoquGuid
							+ "&FangHaoGuid=" + FangHaoGuid + "&phone=" + phone);
					System.out.println("��ȡ�º����" + resp);
					handler.sendEmptyMessage(GET_REDPACKET);
				}
			}.start();
		} else if (v.getId() == R.id.shadow) {
		} else {
			FragmentTransaction fTransaction = fManager.beginTransaction();
			hideAllFragment(fTransaction);
			switch (v.getId()) {
			case R.id.home:
				noSelect();
				home.setSelected(true);
				// ����̬��ȡ���ֻ���Ļ�Ŀ�ȴ��ݸ�FragmentHome
				fHome = new FragmentHome(width);
				fTransaction.replace(R.id.centerContent, fHome);
				break;

			// case R.id.repairs:
			// noSelect();
			// repairs.setSelected(true);
			// fRepairs = new FragmentRepairs();
			// fTransaction.replace(R.id.centerContent, fRepairs);
			// break;
			case R.id.notification:
				noSelect();
				notification.setSelected(true);
				fNotification = new FragmentNotification();
				fTransaction.replace(R.id.centerContent, fNotification);
				break;
			case R.id.myself:
				noSelect();
				myself.setSelected(true);
				fMyself = new FragmentMyself();
				fTransaction.replace(R.id.centerContent, fMyself);
				break;
			default:
				break;
			}
			fTransaction.commit();

			new Thread() {
				public void run() {
					System.out.println("===========�º��===========");
					resp = GetData.getHtml(UrlString.LiuPeng + "/HuoQuHongBaoMoney?xiaoquGuid=" + xiaoquGuid
							+ "&FangHaoGuid=" + FangHaoGuid + "&phone=" + phone);
					handler.sendEmptyMessage(NEW_REDPACKET);
				}
			}.start();
		}
	}

	// ��������Fragment
	protected void hideAllFragment(FragmentTransaction fragmentTransaction) {
		if (fHome != null)
			fragmentTransaction.hide(fHome);
		if (fNotification != null)
			fragmentTransaction.hide(fNotification);
		if (fMyself != null)
			fragmentTransaction.hide(fMyself);
		// if (fRepairs != null)
		// fragmentTransaction.hide(fRepairs);
	}

	// ������TextView��״̬��Ϊδѡ��
	protected void noSelect() {
		home.setSelected(false);
		// repairs.setSelected(false);
		notification.setSelected(false);
		myself.setSelected(false);
	}

	@Override
	public void onBackPressed() {
		if (fManager.getBackStackEntryCount() == 0) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				super.onBackPressed();
			}
		} else {
			fManager.popBackStack();
		}
	}
}
