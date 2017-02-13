package com.lzf.tenement;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class JoinUsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_us);

		ImageView image = (ImageView) findViewById(R.id.imageView);

		// 动态获取手机屏幕的宽度，并传递给FragmentHome
		WindowManager wManager = this.getWindowManager();
		int width = wManager.getDefaultDisplay().getWidth(); // （像素:px）
		int height = (int) (width * (552 / 414));
		Bitmap bitmapRaw = BitmapFactory.decodeResource(getResources(), R.drawable.company);
		Bitmap bitmapOk = Bitmap.createScaledBitmap(bitmapRaw, width, height, true);
		image.setImageBitmap(bitmapOk);

	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	public void back(View view) {
		this.finish();
	}
}
