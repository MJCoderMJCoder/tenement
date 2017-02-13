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
	private String category; // ��𣨷���˷��ص������Ϣ��
	private List<DataMG> categoryEntity; // ���ʵ����
	private final int CATEGORY = 0; // ѡ������ʶ
	private final int PHOTO_SD = 1; // ���ձ�ʶ
	private final int GALLERY = 2; // ͼ���ʶ
	private final int CUSTOM_GALLERY = 3; // ͼ���ʶ
	private final int SUBMIT = 4; // �ύ��ʶ
	private String submit; // �ύ����������ص���ʾ��Ϣ

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CATEGORY:
				if (category.equals("fail")) {
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(getApplicationContext(), "��ȡ����ʧ��", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jsonObject = new JSONObject(submit);
						if (jsonObject.getBoolean("success")) {
							Builder builder = new Builder(PersonRepairsActivity.this);
							aDialog = builder.setIcon(R.drawable.success).setTitle("�ύ�ɹ�").setCancelable(false)
									.setNegativeButton("�����ύ", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											Intent intent = new Intent(PersonRepairsActivity.this,
													PersonRepairsActivity.class);
											onBackPressed();
											startActivity(intent);
										}
									}).setPositiveButton("����", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create();
							aDialog.show(); // ��ʾ�Ի���
						} else {
							Toast.makeText(getApplicationContext(), "�ύʧ�ܣ��������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
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
		xingming = sp.getString("xingming", "�ƴ�Ƽ�");
		phone = sp.getString("phone", "fdkj@123??");
		xiaoquName = sp.getString("xiaoquName", "�ƴ�Ƽ�");
		xiaoquGuid = sp.getString("xiaoquGuid", "�ƴ�Ƽ�");
		FangHaoName = sp.getString("FangHaoName", "fdkj@123??");
		FangHaoGuid = sp.getString("FangHaoGuid", "�ƴ�Ƽ�");
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

		// ɾ��ͼƬ
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
		// ѡ�����
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

		// ���ͼƬ
		image = (LinearLayout) findViewById(R.id.image);
		findViewById(R.id.addImg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (img1.getDrawable() != null && img2.getDrawable() != null && img3.getDrawable() != null) {
					Toast.makeText(getApplicationContext(), "�����Ƭ�������", Toast.LENGTH_SHORT).show();
				} else {
					image.setVisibility(View.VISIBLE);
				}
			}
		});

		// ����
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
						// �ڷ������֮ǰ���˶���� accessible ��־����Ϊ true���Դ������������ٶȡ�
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
					System.out.println("The default memory��" + dirTemp);
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

		// �����ѡ��
		findViewById(R.id.gallery).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// ��׿��û���⣨�����豸�����ԣ�
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

		// ȡ��
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				image.setVisibility(View.GONE);
			}
		});

		// �ύ
		findViewById(R.id.submit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				wenTiContent = detailET.getText().toString().trim();
				if (wenTiLeiXingId == null) {
					Toast.makeText(getApplicationContext(), "����ѡ�����", Toast.LENGTH_SHORT).show();
				} else if (wenTiContent == null || wenTiContent.equals("")) {
					Toast.makeText(getApplicationContext(), "��������ϸ��Ϣ", Toast.LENGTH_SHORT).show();
				} else if (files.size() < 1) {
					Toast.makeText(getApplicationContext(), "���ϴ�����һ��ͼƬ", Toast.LENGTH_SHORT).show();
				} else {
					progress = ProgressDialog.show(PersonRepairsActivity.this, null, "�����ύ...");

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

	// ������
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

		// ��ЩΪ�˵����PopupWindow����PopupWindow����ʧ��
		popWindow.setTouchable(true);
		popWindow.setTouchInterceptor(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
				// �����������true�Ļ���touch�¼���������
				// ���غ� PopupWindow��onTouchEvent�������ã���������ⲿ�����޷�dismiss
			}
		});
		popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000)); // ҪΪpopWindow����һ����������Ч
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
				Uri selectedImage = data.getData(); // ��ȡϵͳ���ص���Ƭ��Uri
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);// ��ϵͳ���в�ѯָ��Uri��Ӧ����Ƭ
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex); // ��ȡ��Ƭ·��
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
	 * ���ؼ�
	 */
	public void back(View view) {
		onBackPressed();
	}

	/**
	 * ���ؼ�
	 */
	@Override
	public void onBackPressed() {
		if (bitmap != null) {
			bitmap.recycle();
		}
		this.finish();
	}
}
