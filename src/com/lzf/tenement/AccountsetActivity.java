package com.lzf.tenement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AccountsetActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accountset);

		// �˺���Ϣ
		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		((TextView) findViewById(R.id.accountText1)).setText(sp.getString("xingming", "�ƴ�Ƽ�"));
		((TextView) findViewById(R.id.phoneText)).setText(sp.getString("phone", "fdkj@123??"));

	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	/**
	 * ���ؼ�
	 */
	public void back(View view) {
		this.finish();
	}

}
