package com.lzf.tenement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.entity.RedPacket;
import com.lzf.tenement.fragment.FragmentNewred0;
import com.lzf.tenement.fragment.FragmentNewred1;
import com.lzf.tenement.fragment.FragmentNewred2;
import com.lzf.tenement.fragment.FragmentOldred;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RedPacketActivity extends Activity {

	private FragmentOldred fOldred;
	private FragmentNewred0 fNewred0;
	private FragmentNewred1 fNewred1;
	private FragmentNewred2 fNewred2;

	private String xiaoquGuid;
	private String FangHaoGuid;
	private String phone;
	private String resp;

	private final int OLD_RED = 1;
	private final int NEW_RED = 2;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
			hideAllFragment(fTransaction);
			switch (msg.what) {
			case OLD_RED:
				if (resp.equals("fail")) {
					Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(resp);
						if (jObject.getBoolean("success")) {
							int totalCount = jObject.getInt("totalCount");
							String money = jObject.getString("error");
							List<RedPacket> rpList = new ArrayList<RedPacket>();
							JSONArray jArray = jObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject redPacket = jArray.getJSONObject(i);
								rpList.add(new RedPacket(redPacket.getString("danwei"), redPacket.getString("shijian"),
										redPacket.getString("Money")));
							}
							fOldred = new FragmentOldred(totalCount, money, rpList);
							fTransaction.replace(R.id.redpacketContent, fOldred);
						} else {
							fOldred = new FragmentOldred(0, "0.00", new ArrayList<RedPacket>());
							fTransaction.replace(R.id.redpacketContent, fOldred);
							Toast.makeText(RedPacketActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON数据解析异常：" + e.getMessage());
					}
				}
				break;
			case NEW_RED:
				fNewred0 = new FragmentNewred0();
				// fNewred1 = new FragmentNewred1();
				// fNewred2 = new FragmentNewred2();
				fTransaction.replace(R.id.redpacketContent, fNewred0);
				break;
			default:
				break;
			}
			fTransaction.commit();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_red_packet);

		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		xiaoquGuid = sp.getString("xiaoquGuid", "");
		FangHaoGuid = sp.getString("FangHaoGuid", "");
		phone = sp.getString("phone", "");

		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.redMenu);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup rg, int arg1) {

				switch (rg.getCheckedRadioButtonId()) {
				case R.id.oldRed:
					new Thread() {
						public void run() {
							resp = GetData.getHtml(UrlString.LiuPeng + "/HuoQuHongBaoMoneyList?xiaoquGuid=" + xiaoquGuid
									+ "&FangHaoGuid=" + FangHaoGuid + "&phone=" + phone);
							handler.sendEmptyMessage(OLD_RED);
						}
					}.start();
					break;

				case R.id.newRed:
					new Thread() {
						public void run() {
							resp = GetData.getHtml(UrlString.LiuPeng + "/HuoQuHongBaoMoney?xiaoquGuid=" + xiaoquGuid
									+ "&FangHaoGuid=" + FangHaoGuid + "&phone=" + phone);
							System.out.println(resp);
							handler.sendEmptyMessage(NEW_RED);
						}
					}.start();
					break;
				default:
					break;
				}

			}
		});

		findViewById(R.id.newRed).performClick();

	}

	// 隐藏所有Fragment
	protected void hideAllFragment(FragmentTransaction fragmentTransaction) {
		if (fOldred != null)
			fragmentTransaction.hide(fOldred);
		if (fNewred0 != null)
			fragmentTransaction.hide(fNewred0);
		if (fNewred1 != null)
			fragmentTransaction.hide(fNewred1);
		if (fNewred2 != null)
			fragmentTransaction.hide(fNewred2);
	}

	public void back(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
