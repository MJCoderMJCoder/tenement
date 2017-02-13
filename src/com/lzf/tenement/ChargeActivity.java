package com.lzf.tenement;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ChargeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge);
	}

	/**
	 * ·µ»Ø¼ü
	 */
	public void back(View view) {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
