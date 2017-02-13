package com.lzf.tenement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;
import com.lzf.tenement.entity.ConsultFeedback;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackDetailActivity extends Activity {

	private byte[] image1;
	private byte[] image2;
	private byte[] image3;
	private List<File> files = new ArrayList<File>();

	private String tempImg;
	private String feedback;
	private int j; // 临时变量（在连续加载图片的时候）

	private final int FEEDBACK = 100;
	private final int IMAGE1 = 101;
	private final int IMAGE2 = 102;
	private final int IMAGE3 = 103;

	private RelativeLayout feedbackTime;
	private TextView feedbackContent;
	private ImageView imageView1;
	private ImageView imageView2;
	private ImageView imageView3;
	private ProgressDialog progress;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FEEDBACK:
				if (progress != null) {
					progress.dismiss();
				}
				if (feedback.equals("fail")) {
					Toast.makeText(FeedbackDetailActivity.this, "获取反馈信息失败，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(feedback);
						if (jObject.getBoolean("success")) {
							JSONObject feedbackTemp = jObject.getJSONObject("data");
							feedbackTime.setVisibility(View.VISIBLE);
							feedbackContent.setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.feedbackTimeValue))
									.setText(feedbackTemp.getString("FanKuiTime"));
							feedbackContent.setText(feedbackTemp.getString("FanKuiNeiRong"));
						} else {
							Toast.makeText(FeedbackDetailActivity.this, "服务端Error：6003。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON数据解析异常：" + e.getMessage());
					}
				}
				break;
			case IMAGE1:
				if (image1 != null) {
					File file;
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						File dir = new File(Environment.getExternalStorageDirectory(), "tempGallery");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file = new File(dir, System.currentTimeMillis() + ".jpg");
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							FileOutputStream fOutputStream = new FileOutputStream(file);
							fOutputStream.write(image1);
							fOutputStream.flush();
							fOutputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						String dirTemp = null;
						StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
						Class<?>[] paramClasses = {};
						Method getVolumePathsMethod;
						try {
							getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
							getVolumePathsMethod.setAccessible(true);
							Object[] params = {};
							Object invoke = getVolumePathsMethod.invoke(storageManager, params);
							for (int i = 0; i < ((String[]) invoke).length; i++) {
								if (!((String[]) invoke)[i]
										.equals(Environment.getExternalStorageDirectory().toString())) {
									dirTemp = ((String[]) invoke)[i];
								}
							}
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						File dir = new File(dirTemp, "tempGallery");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file = new File(dir, System.currentTimeMillis() + ".jpg");
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							FileOutputStream fOutputStream = new FileOutputStream(file);
							fOutputStream.write(image1);
							fOutputStream.flush();
							fOutputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					files.add(file);
					ImageLoader.getInstance(3, Type.LIFO).loadImage(file.getAbsolutePath(), imageView1);
					imageView1.setVisibility(View.VISIBLE);
					findViewById(R.id.placeholder1).setVisibility(View.GONE);
				} else {
					Toast.makeText(FeedbackDetailActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				}
				break;
			case IMAGE2:
				if (image2 != null) {
					File file;
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						File dir = new File(Environment.getExternalStorageDirectory(), "tempGallery");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file = new File(dir, System.currentTimeMillis() + ".jpg");
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							FileOutputStream fOutputStream = new FileOutputStream(file);
							fOutputStream.write(image2);
							fOutputStream.flush();
							fOutputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						String dirTemp = null;
						StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
						Class<?>[] paramClasses = {};
						Method getVolumePathsMethod;
						try {
							getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
							getVolumePathsMethod.setAccessible(true);
							Object[] params = {};
							Object invoke = getVolumePathsMethod.invoke(storageManager, params);
							for (int i = 0; i < ((String[]) invoke).length; i++) {
								if (!((String[]) invoke)[i]
										.equals(Environment.getExternalStorageDirectory().toString())) {
									dirTemp = ((String[]) invoke)[i];
								}
							}
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						File dir = new File(dirTemp, "tempGallery");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file = new File(dir, System.currentTimeMillis() + ".jpg");
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							FileOutputStream fOutputStream = new FileOutputStream(file);
							fOutputStream.write(image2);
							fOutputStream.flush();
							fOutputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					files.add(file);
					ImageLoader.getInstance(3, Type.LIFO).loadImage(file.getAbsolutePath(), imageView2);
					imageView2.setVisibility(View.VISIBLE);
					findViewById(R.id.placeholder2).setVisibility(View.GONE);
				} else {
					Toast.makeText(FeedbackDetailActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				}
				break;
			case IMAGE3:
				if (image3 != null) {
					File file;
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						File dir = new File(Environment.getExternalStorageDirectory(), "tempGallery");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file = new File(dir, System.currentTimeMillis() + ".jpg");
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							FileOutputStream fOutputStream = new FileOutputStream(file);
							fOutputStream.write(image3);
							fOutputStream.flush();
							fOutputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						String dirTemp = null;
						StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
						Class<?>[] paramClasses = {};
						Method getVolumePathsMethod;
						try {
							getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
							getVolumePathsMethod.setAccessible(true);
							Object[] params = {};
							Object invoke = getVolumePathsMethod.invoke(storageManager, params);
							for (int i = 0; i < ((String[]) invoke).length; i++) {
								if (!((String[]) invoke)[i]
										.equals(Environment.getExternalStorageDirectory().toString())) {
									dirTemp = ((String[]) invoke)[i];
								}
							}
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						File dir = new File(dirTemp, "tempGallery");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file = new File(dir, System.currentTimeMillis() + ".jpg");
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							FileOutputStream fOutputStream = new FileOutputStream(file);
							fOutputStream.write(image3);
							fOutputStream.flush();
							fOutputStream.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					files.add(file);
					ImageLoader.getInstance(3, Type.LIFO).loadImage(file.getAbsolutePath(), imageView3);
					imageView3.setVisibility(View.VISIBLE);
					findViewById(R.id.placeholder3).setVisibility(View.GONE);
				} else {
					Toast.makeText(FeedbackDetailActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				Toast.makeText(FeedbackDetailActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_detail);

		imageView1 = (ImageView) findViewById(R.id.img1);
		imageView2 = (ImageView) findViewById(R.id.img2);
		imageView3 = (ImageView) findViewById(R.id.img3);

		imageView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setDataAndType(Uri.fromFile(files.get(0)), "image/*");
				// startActivity(intent);
				Intent intent = new Intent(FeedbackDetailActivity.this, GalleryImageActivity.class);
				intent.putExtra("files", ((Serializable) files));
				startActivity(intent);
			}
		});
		imageView2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setDataAndType(Uri.fromFile(files.get(1)), "image/*");
				// startActivity(intent);
				Intent intent = new Intent(FeedbackDetailActivity.this, GalleryImageActivity.class);
				intent.putExtra("files", ((Serializable) files));
				startActivity(intent);
			}
		});
		imageView3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setDataAndType(Uri.fromFile(files.get(2)), "image/*");
				// startActivity(intent);
				Intent intent = new Intent(FeedbackDetailActivity.this, GalleryImageActivity.class);
				intent.putExtra("files", ((Serializable) files));
				startActivity(intent);
			}
		});

		// 初始化用户所咨询的问题的数据
		final ConsultFeedback cFeedback = (ConsultFeedback) getIntent().getSerializableExtra("ConsultFeedback");
		TextView dateValue = (TextView) findViewById(R.id.dateValue);
		TextView detail = (TextView) findViewById(R.id.detail);
		dateValue.setText(cFeedback.getAddDate());
		detail.setText(cFeedback.getMiaoShu());
		new Thread() {
			public void run() {
				List<String> temp = cFeedback.getTp();
				for (j = 0; j < temp.size(); j++) {
					tempImg = temp.get(j);
					if (j == 0) {
						image1 = GetData.getImage(tempImg);
						handler.sendEmptyMessage(IMAGE1);
					} else if (j == 1) {
						image2 = GetData.getImage(tempImg);
						handler.sendEmptyMessage(IMAGE2);
					} else if (j == 2) {
						image3 = GetData.getImage(tempImg);
						handler.sendEmptyMessage(IMAGE3);
					}
				}
			}
		}.start();

		// 反馈信息控制器
		feedbackTime = (RelativeLayout) findViewById(R.id.feedbackTime);
		feedbackContent = (TextView) findViewById(R.id.feedbackContent);
		if (cFeedback.getIschakan().contains("未")) {
			feedbackTime.setVisibility(View.GONE);
			feedbackContent.setVisibility(View.GONE);
		} else {
			new Thread() {
				public void run() {
					progress = ProgressDialog.show(FeedbackDetailActivity.this, "正在加载反馈信息...", null, true, false);
					feedback = GetData.getHtml(UrlString.LiuPeng + "/ZhiXunXiangXi?id=" + cFeedback.getId());
					handler.sendEmptyMessage(FEEDBACK);
				}
			}.start();
		}

	}

	public void back(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if (files.size() > 0) {
			for (File file : files) {
				file.delete();
			}
		}
		this.finish();
	}
}
