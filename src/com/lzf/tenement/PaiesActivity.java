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
import android.net.Uri;
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

public class PaiesActivity extends Activity implements OnItemClickListener {

	private int width;
	private String resp;
	private String xiaoquGuid;
	private String FangHaoGuid;
	private String phone;

	private String serverResponse;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (serverResponse.equals("fail")) {
				Toast.makeText(PaiesActivity.this, "更多功能，敬请期待。", Toast.LENGTH_SHORT).show();
			} else {
				Intent pay = new Intent(PaiesActivity.this, PayActivity.class);
				switch (msg.what) {
				case 0:
					pay.putExtra("title", "物业费");
					break;
				case 1:
					pay.putExtra("title", "水费");
					break;
				case 2:
					pay.putExtra("title", "燃气费");
					break;
				case 3:
					pay.putExtra("title", "有线电视");
					break;
				default:
					break;
				}
				System.out.println(serverResponse);
				pay.putExtra("jsonObject", serverResponse);
				startActivity(pay);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paies);

		// 动态获取手机屏幕的宽度，并传递给FragmentHome
		WindowManager wManager = this.getWindowManager();
		width = wManager.getDefaultDisplay().getWidth();

		// 动态设置图片的宽度、高度（自适应屏幕大小）
		int height = (int) (width / 1.38);
		Bitmap bitmapRaw = BitmapFactory.decodeResource(getResources(), R.drawable.tiny_img);
		Bitmap bitmapOk = Bitmap.createScaledBitmap(bitmapRaw, width, height, true);
		ImageView iView = (ImageView) findViewById(R.id.imageCenter);
		iView.setImageBitmap(bitmapOk);

		// 在这里借用ItemLeft为GridView的ItemGrid的Bean类
		GridView gView = (GridView) findViewById(R.id.gridView);
		List<ItemLeft> data = new ArrayList<ItemLeft>();
		data.add(new ItemLeft(R.drawable.tenement_pay, "物业费"));
		data.add(new ItemLeft(R.drawable.water_fee, "水费"));
		data.add(new ItemLeft(R.drawable.gas_fee, "燃气费"));
		data.add(new ItemLeft(R.drawable.tv_fee, "有线电视"));
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
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		new Thread() {
			public void run() {
				serverResponse = GetData.getHtml(
						UrlString.LiuPeng + "/HuoQuWuYeWei?xiaoquGuid=" + xiaoquGuid + "&FangHaoGuid=" + FangHaoGuid);
				handler.sendEmptyMessage(arg2);
			}
		}.start();
	}

	public void back(View view) {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

}