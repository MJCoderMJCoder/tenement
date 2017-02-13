package com.lzf.tenement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.OnlineActivity;
import com.lzf.tenement.R;
import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.bean.ItemLeft;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.util.UrlString;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ConsultActivity extends Activity implements OnItemClickListener {

	private int width;
	private String resp;
	private String xiaoquGuid;
	private String FangHaoGuid;
	private String phone;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (resp.equals("fail")) {
				Toast.makeText(ConsultActivity.this, "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jsonObject = new JSONObject(resp);
					if (jsonObject.getInt("totalCount") > 0) {
						Intent feedback = new Intent(ConsultActivity.this, FeedbackActivity.class);
						feedback.putExtra("resp", resp);
						startActivity(feedback);
					} else {
						Toast.makeText(ConsultActivity.this, "����δ��ѯ������Ŷ��", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					System.out.println("JSON���ݽ����쳣" + e.getMessage());
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consult);

		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		xiaoquGuid = sp.getString("xiaoquGuid", "�ƴ�Ƽ�");
		FangHaoGuid = sp.getString("FangHaoGuid", "�ƴ�Ƽ�");
		phone = sp.getString("phone", "fdkj@123??");

		// ��̬��ȡ�ֻ���Ļ�Ŀ�ȣ������ݸ�FragmentHome
		WindowManager wManager = this.getWindowManager();
		width = wManager.getDefaultDisplay().getWidth();

		// ��̬����ͼƬ�Ŀ�ȡ��߶ȣ�����Ӧ��Ļ��С��
		int height = (int) (width / 1.38);
		Bitmap bitmapRaw = BitmapFactory.decodeResource(getResources(), R.drawable.tiny_img);
		Bitmap bitmapOk = Bitmap.createScaledBitmap(bitmapRaw, width, height, true);
		ImageView iView = (ImageView) findViewById(R.id.imageCenter);
		iView.setImageBitmap(bitmapOk);

		// ���������ItemLeftΪGridView��ItemGrid��Bean��
		GridView gView = (GridView) findViewById(R.id.gridView);
		List<ItemLeft> data = new ArrayList<ItemLeft>();
		data.add(new ItemLeft(R.drawable.consult, "������ѯ"));
		data.add(new ItemLeft(R.drawable.feedback, "��ѯ����"));
		ReusableAdapter<ItemLeft> adapter = new ReusableAdapter<ItemLeft>(data, R.layout.item_grid) {
			@Override
			public void bindView(ViewHolder holder, ItemLeft obj) {
				holder.setImageResource(R.id.icon, obj.getIcon());
				holder.setText(R.id.text, obj.getText());
			}
		};
		gView.setAdapter(adapter);
		gView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg2) {
		case 0:
			Intent online = new Intent(ConsultActivity.this, OnlineActivity.class);
			startActivity(online);
			break;
		case 1:
			new Thread() {
				public void run() {
					resp = GetData.getHtml(UrlString.LiuPeng + "/ZhiXunList?xiaoquGuid=" + xiaoquGuid + "&FangHaoGuid="
							+ FangHaoGuid + "&phone=" + phone);
					handler.sendEmptyMessage(6003);
				}
			}.start();
			break;
		default:
			break;
		}
	}

	public void back(View view) {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

}
