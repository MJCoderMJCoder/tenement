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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;
import com.lzf.tenement.entity.Repair;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RepairActivity extends Activity {

	private String serverDetail;
	private String serverCancel; // 取消报修服务端返回值

	private byte[] image1;
	private byte[] image2;
	private byte[] image3;
	private List<File> files = new ArrayList<File>();

	private String tempImg;
	private int j; // 临时变量（在连续加载图片的时候）

	private final int DETAIL = 100;
	private final int IMAGE1 = 101;
	private final int IMAGE2 = 102;
	private final int IMAGE3 = 103;
	private final int CANCEL = 104;
	private final int FAIL = -4;
	private final int DEFAULT = -5;

	private ImageView imageView1;
	private ImageView imageView2;
	private ImageView imageView3;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CANCEL:
				if (serverCancel.equals("fail")) {
					Toast.makeText(RepairActivity.this, "", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverCancel);
						if (jObject.getBoolean("success")) {
							Builder builder = new Builder(RepairActivity.this);
							AlertDialog aDialog = builder.setIcon(R.drawable.success).setTitle("取消成功")
									.setCancelable(false)
									.setPositiveButton("返回", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create();
							aDialog.show(); // 显示对话框
						} else {
							Toast.makeText(RepairActivity.this, jObject.getString("error"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				break;
			case DETAIL:
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
					Toast.makeText(RepairActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(RepairActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(RepairActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				}
				break;
			case FAIL:
				Toast.makeText(RepairActivity.this, "获取数据失败。", Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(RepairActivity.this, "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repair);

		imageView1 = (ImageView) findViewById(R.id.img1);
		imageView2 = (ImageView) findViewById(R.id.img2);
		imageView3 = (ImageView) findViewById(R.id.img3);

		imageView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setDataAndType(Uri.fromFile(files.get(0)), "image/*");
				// startActivity(intent);
				Intent intent = new Intent(RepairActivity.this, GalleryImageActivity.class);
				intent.putExtra("files", ((Serializable) files));
				intent.putExtra("tag", 0);
				startActivity(intent);
			}
		});
		imageView2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setDataAndType(Uri.fromFile(files.get(1)), "image/*");
				// startActivity(intent);
				Intent intent = new Intent(RepairActivity.this, GalleryImageActivity.class);
				intent.putExtra("files", ((Serializable) files));
				intent.putExtra("tag", 1);
				startActivity(intent);
			}
		});
		imageView3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setDataAndType(Uri.fromFile(files.get(2)), "image/*");
				// startActivity(intent);
				Intent intent = new Intent(RepairActivity.this, GalleryImageActivity.class);
				intent.putExtra("files", ((Serializable) files));
				intent.putExtra("tag", 2);
				startActivity(intent);
			}
		});

		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		final Repair repair = (Repair) getIntent().getSerializableExtra("repair");

		new Thread() {
			public void run() {
				serverDetail = GetData.getHtml(UrlString.LiuPeng + "/WenTiXiangXi?id=" + repair.getId());
				if (serverDetail.equals("fail")) {
					handler.sendEmptyMessage(DEFAULT);
				} else {
					try {
						JSONObject jObject = new JSONObject(serverDetail);
						if (jObject.getBoolean("success")) {
							JSONArray jArray = jObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								final JSONArray images = jData.getJSONArray("tp");
								new Thread() {
									public void run() {
										for (j = 0; j < images.length(); j++) {
											try {
												tempImg = images.getString(j);
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
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}
								}.start();

							}
						} else {
							handler.sendEmptyMessage(FAIL);
						}
					} catch (

					JSONException e) {
						System.out.println("JSON数据解析异常：" + e.getMessage());
					}
				}
				handler.sendEmptyMessage(DETAIL);
			}
		}.start();

		((TextView) findViewById(R.id.nameValue)).setText(sp.getString("xingming", ""));
		((TextView) findViewById(R.id.phoneValue)).setText(sp.getString("phone", ""));
		((TextView) findViewById(R.id.addressValue))
				.setText(sp.getString("xiaoquName", "") + sp.getString("FangHaoName", ""));
		((TextView) findViewById(R.id.repairTYPEValue)).setText(repair.getLxa());
		((TextView) findViewById(R.id.questionTYPEValue)).setText(repair.getLxb());
		((TextView) findViewById(R.id.repairTimeValue)).setText(repair.getShijian());
		((TextView) findViewById(R.id.repairStateValue)).setText(repair.getZt());
		((TextView) findViewById(R.id.detailText)).setText(repair.getContent());

		final Button evaluateBtn = (Button) findViewById(R.id.evaluateBtn);
		// evaluateBtn.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// if ((evaluateBtn.getText().toString()).contains("评价")) {
		// startActivity(new Intent(RepairActivity.this,
		// EvaluateActivity.class));
		// }
		// }
		// });
		if (repair.getZt().equals("未处理")) {
			evaluateBtn.setVisibility(View.VISIBLE);
			evaluateBtn.setText("取消报修");
			evaluateBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					new Thread() {
						public void run() {
							serverCancel = OKHttp.getData(UrlString.LiuPeng + "/WenTiQuXiao?id=" + repair.getId());
							handler.sendEmptyMessage(CANCEL);
						}
					}.start();
				}
			});
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
