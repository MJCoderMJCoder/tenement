package com.lzf.tenement;

import java.util.ArrayList;
import java.util.List;

import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.adapter.ReusableAdapter.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class EvaluateManageActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate_manage);

		final String distinguish = getIntent().getStringExtra("distinguish");

		TextView title = (TextView) findViewById(R.id.textTitle);
		title.setText(distinguish);
		if (distinguish.contains("����")) {
			List<String> data = new ArrayList<String>();
			data.add("�꿨");
			data.add("��ʿ��");
			data.add("������");
			data.add("�꿨");
			data.add("��ʿ��");
			data.add("������");
			ListView listView = (ListView) findViewById(R.id.listEvaluate);
			ReusableAdapter<String> adapter = new ReusableAdapter<String>(data, R.layout.item_evaluate_manage) {

				@Override
				public void bindView(ViewHolder holder, String obj) {
					holder.setText(R.id.repairTYPE, obj);
					holder.setText(R.id.noFeedback, "����ͳ��");
				}
			};
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent evaluateSatisfaction = new Intent(EvaluateManageActivity.this,
							EvaluateSatisfactionActivity.class);
					evaluateSatisfaction.putExtra("distinguish", distinguish);
					startActivity(evaluateSatisfaction);
				}
			});
		} else if (distinguish.contains("�¼�")) {
			List<String> data = new ArrayList<String>();
			data.add("�꿨");
			data.add("��ʿ��");
			data.add("������");
			data.add("�꿨");
			data.add("��ʿ��");
			data.add("������");
			ListView listView = (ListView) findViewById(R.id.listEvaluate);
			ReusableAdapter<String> adapter = new ReusableAdapter<String>(data, R.layout.item_evaluate_manage) {

				@Override
				public void bindView(ViewHolder holder, String obj) {
					holder.setText(R.id.repairTYPE, obj);
					holder.setText(R.id.noFeedback, "�¼�ͳ��");
				}
			};
			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Intent eventStatistics = new Intent(EvaluateManageActivity.this,
							EvaluateSatisfactionActivity.class);
					eventStatistics.putExtra("distinguish", distinguish);
					startActivity(eventStatistics);
				}
			});
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
