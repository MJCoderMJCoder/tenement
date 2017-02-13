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

public class RepairsCenterActivity extends Activity implements OnItemClickListener {

	private int width;
	private String resp;
	private String xiaoquGuid;
	private String FangHaoGuid;
	private String phone;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repairs_center);

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
		data.add(new ItemLeft(R.drawable.no_handle, "δ����"));
		data.add(new ItemLeft(R.drawable.feedback, "������"));
		data.add(new ItemLeft(R.drawable.defer_handle, "�ݻ�����"));
		data.add(new ItemLeft(R.drawable.handled, "�Ѵ���"));
		data.add(new ItemLeft(R.drawable.evaluated, "������"));
		data.add(new ItemLeft(R.drawable.canceled, "��ȡ��"));
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
		Intent intent = new Intent(RepairsCenterActivity.this, RepairsStatusActivity.class);
		switch (arg2) {
		case 0:
			intent.putExtra("title", "δ����");
			break;
		case 1:
			intent.putExtra("title", "������");
			break;
		case 2:
			intent.putExtra("title", "�ݻ�����");
			break;
		case 3:
			intent.putExtra("title", "�Ѵ���");
			break;
		case 4:
			intent.putExtra("title", "������");
			break;
		case 5:
			intent.putExtra("title", "��ȡ��");
			break;
		default:
			break;
		}
		startActivity(intent);
	}

	public void back(View view) {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

}
