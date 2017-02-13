package com.lzf.tenement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.adapter.ReusableAdapter.ViewHolder;
import com.lzf.tenement.entity.ConsultFeedback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FeedbackActivity extends Activity {

	private List<ConsultFeedback> list = new ArrayList<ConsultFeedback>();

	private int totalCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		ListView listView = (ListView) findViewById(R.id.listView);

		try {
			JSONObject jObject = new JSONObject(getIntent().getStringExtra("resp"));
			totalCount = jObject.getInt("totalCount");
			JSONArray jArray = jObject.getJSONArray("data");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject temp = jArray.getJSONObject(i);

				JSONArray tpArray = temp.getJSONArray("tp");
				List<String> tp = new ArrayList<String>();
				for (int j = 0; j < tpArray.length(); j++) {
					tp.add(tpArray.getString(j));
				}
				System.out.println(tp);
				list.add(new ConsultFeedback(temp.getString("id"), temp.getString("MiaoShu"), temp.getString("AddDate"),
						temp.getString("ischakan"), tp));
			}
		} catch (JSONException e) {
			System.out.println("JSON数据解析异常：" + e.getMessage());
		}

		ReusableAdapter<ConsultFeedback> adapter = new ReusableAdapter<ConsultFeedback>(list, R.layout.item_feedback) {

			@SuppressLint("ResourceAsColor")
			@Override
			public void bindView(ViewHolder holder, ConsultFeedback obj) {
				holder.setText(R.id.date, obj.getAddDate());
				holder.setText(R.id.repairTYPE, obj.getMiaoShu());
				if (obj.getIschakan().contains("未")) {
					holder.setVisibility(R.id.yesFeedback, View.GONE);
					holder.setVisibility(R.id.noFeedback, View.VISIBLE);
				} else {
					holder.setVisibility(R.id.noFeedback, View.GONE);
					holder.setVisibility(R.id.yesFeedback, View.VISIBLE);
				}
			}
		};
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(FeedbackActivity.this, FeedbackDetailActivity.class);
				intent.putExtra("ConsultFeedback", list.get(arg2));
				startActivity(intent);
			}
		});
	}

	public void back(View view) {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

}
