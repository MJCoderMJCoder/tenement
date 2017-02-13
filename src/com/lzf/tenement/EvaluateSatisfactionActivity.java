package com.lzf.tenement;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EvaluateSatisfactionActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate_satisfaction);

		LinearLayout satisfaction = (LinearLayout) findViewById(R.id.satisfaction);
		TextView hint = (TextView) findViewById(R.id.hint);
		TextView title = (TextView) findViewById(R.id.textTitle);

		String distinguish = getIntent().getStringExtra("distinguish");
		title.setText(distinguish);

		if (distinguish.contains("评价")) {
			hint.setText("满意度");
			satisfaction.setVisibility(View.VISIBLE);
		} else if (distinguish.contains("事件")) {
			hint.setText("处理事件");
			satisfaction.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	public void back(View view) {
		this.finish();
	}
}
