package com.lzf.tenement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.gallery.imgloder.GalleryActivity;
import com.lzf.gallery.utils.ImageLoader;
import com.lzf.gallery.utils.ImageLoader.Type;
import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.entity.DataMG;
import com.lzf.tenement.http.FlowBinary;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.http.UploadFiles;
import com.lzf.tenement.util.RotateMatrix;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonRepairsActivity extends Activity {

	private EditText detailET;
	private TextView categoryText;
	private ImageView img1;
	private ImageView img2;
	private ImageView img3;
	private RelativeLayout RL1;
	private RelativeLayout RL2;
	private RelativeLayout RL3;
	private RelativeLayout placeholder1;
	private RelativeLayout placeholder2;
	private RelativeLayout placeholder3;
	private LinearLayout image;
	private Bitmap bitmap;
	private ProgressDialog progress = null;
	private AlertDialog aDialog = null;

	private String id;
	private String xingming;
	private String phone;
	private String xiaoquName;
	private String xiaoquGuid;
	private String FangHaoName;
	private String FangHaoGuid;
	private String wenTiLeiXing;
	private String wenTiLeiXingIdFu;
	private String wenTiLeiXingId;
	private String wenTiContent;
	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, File> files = new HashMap<String, File>();

	private File currentImageFile;
	private String category; // 类别（服务端返回的类别信息）
	private List<DataMG> categoryEntity; // 类别实体类
	private final int CATEGORY = 0; // 选择类别标识
	private final int PHOTO_SD = 1; // 拍照标识
	private final int GALLERY = 2; // 图库标识
	private final int CUSTOM_GALLERY = 3; // 图库标识
	private final int SUBMIT = 4; // 提交标识
	private String submit; // 提交后服务器返回的提示信息

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CATEGORY:
				if (category.equals("fail")) {
					Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jsonObject = new JSONObject(category);
						if (jsonObject.getBoolean("success")) {
							categoryEntity = new ArrayList<DataMG>();
							JSONArray jArray = jsonObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jObject = (JSONObject) jArray.get(i);
								DataMG dataMG = new DataMG(jObject.getInt("id"), jObject.getString("name"), 1);
								categoryEntity.add(dataMG);
							}
						} else {
							Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					initPopWindow(categoryText, categoryEntity);
				}
				break;
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
							Builder builder = new Builder(PersonRepairsActivity.this);
							aDialog = builder.setIcon(R.drawable.success).setTitle("提交成功").setCancelable(false)
									.setNegativeButton("继续提交", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											Intent intent = new Intent(PersonRepairsActivity.this,
													PersonRepairsActivity.class);
											onBackPressed();
											startActivity(intent);
										}
									}).setPositiveButton("返回", new DialogInterface.OnClickListener() {
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
		setContentView(R.layout.activity_person_repairs);

		SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		id = sp.getString("id", "");
		xingming = sp.getString("xingming", "菲达科技");
		phone = sp.getString("phone", "fdkj@123??");
		xiaoquName = sp.getString("xiaoquName", "菲达科技");
		xiaoquGuid = sp.getString("xiaoquGuid", "菲达科技");
		FangHaoName = sp.getString("FangHaoName", "fdkj@123??");
		FangHaoGuid = sp.getString("FangHaoGuid", "菲达科技");
		((TextView) findViewById(R.id.nickname)).setText(xiaoquName);
		((TextView) findViewById(R.id.account)).setHint(FangHaoName);
		((TextView) findViewById(R.id.nameET)).setHint(xingming);
		((TextView) findViewById(R.id.phoneET)).setHint(phone);

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
		// 选择类别
		categoryText = (TextView) findViewById(R.id.categoryText);
		categoryText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (categoryEntity == null) {
					new Thread() {
						public void run() {
							category = GetData.getHtml(UrlString.LiuPeng + "/HuoQuWeiXiuLeiXing?id=1");
							handler.sendEmptyMessage(CATEGORY);
						}
					}.start();
				} else {
					initPopWindow(categoryText, categoryEntity);
				}
			}
		});

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
						// 在反射调用之前将此对象的 accessible 标志设置为 true，以此来提升反射速度。
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
					Intent intentTemp = new Intent(PersonRepairsActivity.this, GalleryActivity.class);
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
				wenTiContent = detailET.getText().toString().trim();
				if (wenTiLeiXingId == null) {
					Toast.makeText(getApplicationContext(), "请先选择类别", Toast.LENGTH_SHORT).show();
				} else if (wenTiContent == null || wenTiContent.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入详细信息", Toast.LENGTH_SHORT).show();
				} else if (files.size() < 1) {
					Toast.makeText(getApplicationContext(), "请上传至少一张图片", Toast.LENGTH_SHORT).show();
				} else {
					progress = ProgressDialog.show(PersonRepairsActivity.this, null, "正在提交...");

					params.put("Uid", id);
					params.put("XingMing", xingming);
					params.put("DianHua", phone);
					params.put("DiZhi", xiaoquName + FangHaoName);
					params.put("WenTiLeiXing", wenTiLeiXing);
					params.put("WenTiLeiXingIdFu", wenTiLeiXingIdFu);
					params.put("WenTiLeiXingId", wenTiLeiXingId);
					params.put("WenTiContent", wenTiContent);
					params.put("xiaoquGuid", xiaoquGuid);
					params.put("FangHaoGuid", FangHaoGuid);
					// params.put("State", "0");

					new Thread() {
						public void run() {
							submit = OKHttp.uploadFiles(UrlString.LiuPeng + "/WenTiAction", params, files);
							handler.sendEmptyMessage(SUBMIT);
						}
					}.start();
				}
			}
		});
	}

	// 弹出框
	protected void initPopWindow(TextView v, final List<DataMG> list) {
		View view = LayoutInflater.from(PersonRepairsActivity.this).inflate(R.layout.popup_window, null, false);
		ListView lView = (ListView) view.findViewById(R.id.popupList);
		ReusableAdapter<DataMG> adapter = new ReusableAdapter<DataMG>(list, R.layout.item_popup) {
			@Override
			public void bindView(ViewHolder holder, DataMG obj) {
				holder.setText(R.id.textPopup, obj.getDname()); // obj.getDtName()
			}

		};
		lView.setAdapter(adapter);

		final PopupWindow popWindow = new PopupWindow(view, categoryText.getWidth(),
				ViewGroup.LayoutParams.WRAP_CONTENT, true);

		// 这些为了点击非PopupWindow区域，PopupWindow会消失的
		popWindow.setTouchable(true);
		popWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});
		popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000)); // 要为popWindow设置一个背景才有效
		popWindow.showAsDropDown(v, 0, 0);

		lView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				wenTiLeiXing = list.get(position).getDname();
				wenTiLeiXingIdFu = String.valueOf(list.get(position).getDBelongedTo());
				wenTiLeiXingId = String.valueOf(list.get(position).getDid());
				categoryText.setText(wenTiLeiXing);
				popWindow.dismiss();
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
				System.out.println(RotateMatrix.getBitmapDegree(picturePath));
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

	/**
	 * 返回键
	 */
	public void back(View view) {
		onBackPressed();
	}

	/**
	 * 返回键
	 */
	@Override
	public void onBackPressed() {
		if (bitmap != null) {
			bitmap.recycle();
		}
		this.finish();
	}
}
