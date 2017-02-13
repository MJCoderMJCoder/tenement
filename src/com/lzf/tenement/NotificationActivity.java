package com.lzf.tenement;

import com.lzf.tenement.entity.Notification;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NotificationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);

		Notification notify = (Notification) getIntent().getSerializableExtra("notify");

		TextView title = (TextView) findViewById(R.id.title);
		TextView time = (TextView) findViewById(R.id.time);
		TextView content = (TextView) findViewById(R.id.content);
		TextView company = (TextView) findViewById(R.id.company);

		title.setText(notify.getBiaoTi());
		time.setText(notify.getShiJian());
		content.setText(notify.getNeiRong());
		company.setText(notify.getFaBuWuYe());
	}

	/**
	 * ·µ»Ø¼ü
	 */
	public void back(View view) {
		onBackPressed();
	}

	/**
	 * ·µ»Ø¼ü
	 */
	@Override
	public void onBackPressed() {
		this.finish();
	}
}
