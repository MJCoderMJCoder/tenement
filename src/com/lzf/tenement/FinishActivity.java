package com.lzf.tenement;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class FinishActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);

	}

	/**
	 * ���ؼ�
	 */
	public void back(View view) {
		onBackPressed();
	}

	/**
	 * ���ؼ�
	 */
	@Override
	public void onBackPressed() {
		this.finish();
	}
}
