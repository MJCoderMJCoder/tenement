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

		// 账号信息
		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		((TextView) findViewById(R.id.accountText1)).setText(sp.getString("xingming", "菲达科技"));
		((TextView) findViewById(R.id.phoneText)).setText(sp.getString("phone", "fdkj@123??"));

	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	/**
	 * 返回键
	 */
	public void back(View view) {
		this.finish();
	}

}
