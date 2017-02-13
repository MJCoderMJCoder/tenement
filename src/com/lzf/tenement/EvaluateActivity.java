package com.lzf.tenement;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.gallery.imgloder.GalleryActivity;
import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;
import com.lzf.tenement.entity.DataMG;
import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EvaluateActivity extends Activity {

	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private LinearLayout image;
	private RelativeLayout RL1;
	private RelativeLayout RL2;
	private RelativeLayout RL3;
	private RelativeLayout placeholder1;
	private RelativeLayout placeholder2;
	private RelativeLayout placeholder3;
	private EditText detailET;
	private ProgressDialog progress = null;

	private File currentImageFile;
	private Map<String, File> files = new HashMap<String, File>();
	private Map<String, String> params = new HashMap<String, String>();

	private final int PHOTO_SD = 1; // 拍照标识
	private final int GALLERY = 2; // 图库标识
	private final int CUSTOM_GALLERY = 3; // 图库标识
	private final int SUBMIT = 4; // 提交标识
	private String submit; // 提交后服务器返回的提示信息

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUBMIT:
				if (progress != null) {
					progress.dismiss();
				}
				if (submit.equals("fail")) {
					Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jsonObject = new JSONObject(submit);
						if (jsonObject.getBoolean("success")) {
							Builder builder = new Builder(EvaluateActivity.this);
							AlertDialog aDialog = builder.setIcon(R.drawable.success).setTitle("评价成功")
									.setCancelable(false)
									.setPositiveButton("返回上一页", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create();
							aDialog.show(); // 显示对话框
						} else {
							Toast.makeText(getApplicationContext(), "提交失败，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), submit, Toast.LENGTH_SHORT).show();
					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate);

		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		// params.put("xiaoquGuid", sp.getString("xiaoquGuid", ""));
		// params.put("FangHaoGuid", sp.getString("FangHaoGuid", ""));
		// params.put("phone", sp.getString("phone", ""));

		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		RL1 = (RelativeLayout) findViewById(R.id.RL1);
		RL2 = (RelativeLayout) findViewById(R.id.RL2);
		RL3 = (RelativeLayout) findViewById(R.id.RL3);
		placeholder1 = (RelativeLayout) findViewById(R.id.placeholder1);
		placeholder2 = (RelativeLayout) findViewById(R.id.placeholder2);
		placeholder3 = (RelativeLayout) findViewById(R.id.placeholder3);
		detailET = (EditText) findViewById(R.id.detailET);

		// 添加图片
		image = (LinearLayout) findViewById(R.id.image);
		findViewById(R.id.addImg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (img1.getDrawable() != null && img2.getDrawable() != null && img3.getDrawable() != null) {
					Toast.makeText(getApplicationContext(), "添加照片最多三张", Toast.LENGTH_SHORT).show();
				} else {
					image.setVisibility(View.VISIBLE);
				}
			}
		});

		// 删除图片
		RL1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				img1.setImageDrawable(null);
				RL1.setVisibility(View.GONE);
				placeholder1.setVisibility(View.INVISIBLE);
				files.remove("1");
			}
		});
		RL2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				img2.setImageDrawable(null);
				RL2.setVisibility(View.GONE);
				placeholder2.setVisibility(View.INVISIBLE);
				files.remove("2");
			}
		});
		RL3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				img3.setImageDrawable(null);
				RL3.setVisibility(View.GONE);
				placeholder3.setVisibility(View.INVISIBLE);
				files.remove("3");
			}
		});

		// 拍照
		findViewById(R.id.photo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					File dir = new File(Environment.getExternalStorageDirectory(), "photograph");
					if (!dir.exists()) {
						dir.mkdirs();
					}
					currentImageFile = new File(dir, System.currentTimeMillis() + ".jpg");
					if (!currentImageFile.exists()) {
						try {
							currentImageFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
					startActivityForResult(it, PHOTO_SD);
					image.setVisibility(View.GONE);
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
							if (!((String[]) invoke)[i].equals(Environment.getExternalStorageDirectory().toString())) {
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
					System.out.println("The default memory：" + dirTemp);
					File dir = new File(dirTemp, "photograph");
					if (!dir.exists()) {
						dir.mkdirs();
					}
					currentImageFile = new File(dir, System.currentTimeMillis() + ".jpg");
					if (!currentImageFile.exists()) {
						try {
							currentImageFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
					startActivityForResult(it, PHOTO_SD);
					image.setVisibility(View.GONE);
				}
			}
		});

		// 从相册选择
		findViewById(R.id.gallery).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 安卓机没问题（单兵设备不可以）
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				if ((intent.resolveActivity(getPackageManager()) != null)) {
					startActivityForResult(intent, GALLERY);
				} else {
					Intent intentTemp = new Intent(EvaluateActivity.this, GalleryActivity.class);
					startActivityForResult(intentTemp, CUSTOM_GALLERY);
				}
				image.setVisibility(View.GONE);
			}
		});

		// 取消
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				image.setVisibility(View.GONE);
			}
		});

		// 提交
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String wenTiContent = detailET.getText().toString().trim();
				if (wenTiContent == null || wenTiContent.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入您的评价。", Toast.LENGTH_SHORT).show();
				} else if (files.size() < 1) {
					Toast.makeText(getApplicationContext(), "请上传至少一张图片", Toast.LENGTH_SHORT).show();
				} else {
					// progress = ProgressDialog.show(EvaluateActivity.this,
					// null, "正在提交...");
					params.put("MiaoShu", wenTiContent);
					new Thread() {
						public void run() {
							// submit = OKHttp.submit(UrlString.LiuPeng +
							// "/ZhiXunWenTiAction", params, files);
							// handler.sendEmptyMessage(SUBMIT);
						}
					}.start();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PHOTO_SD:
				String currentImagePath = currentImageFile.getAbsolutePath();
				if (img1.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(currentImagePath, img1);
					RL1.setVisibility(View.VISIBLE);
					placeholder1.setVisibility(View.GONE);
					files.put("1", currentImageFile);
				} else if (img2.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(currentImagePath, img2);
					RL2.setVisibility(View.VISIBLE);
					placeholder2.setVisibility(View.GONE);
					files.put("2", currentImageFile);
				} else if (img3.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(currentImagePath, img3);
					RL3.setVisibility(View.VISIBLE);
					placeholder3.setVisibility(View.GONE);
					files.put("3", currentImageFile);
				}
				break;
			case GALLERY:
				Uri selectedImage = data.getData(); // 获取系统返回的照片的Uri
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);// 从系统表中查询指定Uri对应的照片
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex); // 获取照片路径
				System.out.println(picturePath);
				cursor.close();
				if (img1.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(picturePath, img1);
					RL1.setVisibility(View.VISIBLE);
					placeholder1.setVisibility(View.GONE);
					files.put("1", new File(picturePath));
				} else if (img2.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(picturePath, img2);
					RL2.setVisibility(View.VISIBLE);
					placeholder2.setVisibility(View.GONE);
					files.put("2", new File(picturePath));
				} else if (img3.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(picturePath, img3);
					RL3.setVisibility(View.VISIBLE);
					placeholder3.setVisibility(View.GONE);
					files.put("3", new File(picturePath));
				}
				break;
			case CUSTOM_GALLERY:
				String imgPath = data.getStringExtra("imgPath");
				if (img1.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(imgPath, img1);
					RL1.setVisibility(View.VISIBLE);
					placeholder1.setVisibility(View.GONE);
					files.put("1", new File(imgPath));
				} else if (img2.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(imgPath, img2);
					RL2.setVisibility(View.VISIBLE);
					placeholder2.setVisibility(View.GONE);
					files.put("2", new File(imgPath));
				} else if (img3.getDrawable() == null) {
					ImageLoader.getInstance(3, Type.LIFO).loadImage(imgPath, img3);
					RL3.setVisibility(View.VISIBLE);
					placeholder3.setVisibility(View.GONE);
					files.put("3", new File(imgPath));
				}
				break;
			default:
				break;
			}
		}
	}

	public void back(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
