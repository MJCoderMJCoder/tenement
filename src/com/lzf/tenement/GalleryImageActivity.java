package com.lzf.tenement;

import java.io.File;
import java.util.List;

import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;

public class GalleryImageActivity extends Activity {

	private ViewFlipper viewFlipper; // 翻转视图
	private GestureDetector detector; // 手势识别（识别手势）
	private List<File> files;
	private int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_image);

		/**
		 * ViewFlipper(翻转视图)
		 */
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

		// 动态导入添加子View
		Intent intent = getIntent();
		files = (List<File>) intent.getSerializableExtra("files");
		for (File file : files) {
			viewFlipper.addView(getImageView(file));
		}
		// viewFlipper.startFlipping(); //
		// （必须调用ViewFliper.startFlipping后，才会开始滑动）

		// 实例化SimpleOnGestureListener与GestureDetector对象
		MyGestureListener myGestureListener = new MyGestureListener();
		detector = new GestureDetector(this, myGestureListener);

		findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		findViewById(R.id.rotate).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				i++;
				ImageView imageView = (ImageView) viewFlipper.getCurrentView();
				imageView.setPivotX(imageView.getWidth() / 2);
				imageView.setPivotY(imageView.getHeight() / 2);
				imageView.setRotation(-90 * i);
			}
		});

	}

	// 1.(先会触发“手势触摸事件”)重写onTouchEvent触发MyGestureListener里的方法
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}

	// 2.（然后根据触摸的位移确定手势的移动方向，从而触发“手势滑动事件”）自定义一个GestureListener,这个是View类下的。
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final int MIN_MOVE = 200; // 最小的移动距离

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
			if (e1.getX() - e2.getX() > MIN_MOVE) {
				if (viewFlipper.getDisplayedChild() != (viewFlipper.getChildCount() - 1)) {
					i = 0;
					viewFlipper.setInAnimation(GalleryImageActivity.this, R.anim.right_in);
					viewFlipper.setOutAnimation(GalleryImageActivity.this, R.anim.right_out);
					viewFlipper.showNext();
				}
			} else if (e2.getX() - e1.getX() > MIN_MOVE) {
				if (viewFlipper.getDisplayedChild() != 0) {
					i = 0;
					viewFlipper.setInAnimation(GalleryImageActivity.this, R.anim.left_in);
					viewFlipper.setOutAnimation(GalleryImageActivity.this, R.anim.left_out);
					viewFlipper.showPrevious();
				}
			}
			return true;
		}

	}

	private ImageView getImageView(File file) {
		ImageView img = new ImageView(this);
		img.setScaleType(ScaleType.FIT_XY);
		ImageLoader.getInstance(3, Type.LIFO).loadImage(file.getAbsolutePath(), img);
		return img;
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

}