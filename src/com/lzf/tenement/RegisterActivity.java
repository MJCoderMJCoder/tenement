package com.lzf.tenement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.qrcode.CaptureActivity;
import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.entity.House;
import com.lzf.tenement.entity.LPData;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.http.PostData;
import com.lzf.tenement.util.FormatMatch;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private ProgressDialog progress;
	private TextView community; // С�������ù��ܣ����û��ֶ�ѡ��С������
	private TextView qrcode; // С�������ɨ���ά�룩
	private TextView selectHouse; // ���ţ�ѡ�񷿺ţ�
	private TextView getCaptacha; // ��ȡ��֤��
	private EditText nameET; // ��������ռ�
	private EditText phoneET; // �ֻ�������ؼ�
	private EditText captchaET; // ��֤������ؼ�
	private EditText pwET; // ��������ؼ�
	private AlertDialog alertDialog; // ϵͳ��ʾ�Ի���
	private String loupan; // ����¥����Ϣ
	private String xqId; // �Ѿ�ѡ���¥�̣�С����ID
	private String xqName; // �Ѿ�ѡ���¥�̣�С��������
	private String house; // ���з�����Ϣ
	private String fhId; // �Ѿ�ѡ��ķ���ID
	private String fhName; // �Ѿ�ѡ��ķ�����Ϣ
	private String username; // ����
	private String phone; // �ֻ���
	private String androidPK; // ��׿�豸Ψһ��ʶ
	private String ShuRuyzm; // �ͻ����������֤��
	private String pw; // ����
	private String captcha = ""; // ����˷��͵���֤��
	private String resp; // ���ע�ᰴťʱ������˷��ص�����
	HashMap<String, Object> result = null; // ��֤�뷵��ֵ��ԭ������ֵ��
	private int timer; // ��ʱ��
	private final int SELECT_COMMUNITY = 0; // ѡ��С��
	private final int SELECT_HOUSE = 1; // ѡ�񷿺�
	private final int SCAN_COMMUNITY = 6003; // ɨ���־
	private final int CAPTACHA = 4; // ��֤���־
	private final int REGISTER = 2; // ע���¼�
	private final int FAILED_GETDATA = 3; // ��ȡ����ʧ��
	private final int DEFAULT = -5; // �����쳣ʱ�ı�ʶ
	private List<LPData> lpDatas; // ¥����Ϣ
	private List<House> houses = null; // ������Ϣ

	private boolean scrolling = false; // ѡ�񷿺ŵĹ��ֱ�־
	private WheelView room; // �������
	private WheelView unit; // ��Ԫ����
	private WheelView build; // ��Ԫ����
	private WheelView floor; // ¥�����
	private RelativeLayout threeLevel; // ѡ�񷿼���ܿ���

	private List<String> buildesData; // ¥¥����׼��
	private String buildes[];
	private List<String> unitesData; // ��Ԫ����׼��
	private String unites[][];
	private List<String> flooresData; // ¥������׼��
	private String floores[][][];
	private List<String> roomsData; // ��������׼��
	private String rooms[][][][];

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SELECT_COMMUNITY:
				if (progress != null) {
					progress.dismiss();
				}
				if (loupan.equals("fail")) {
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					lpDatas = new ArrayList<LPData>();
					try {
						JSONArray jsonArray = new JSONArray(loupan);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = (JSONObject) jsonArray.get(i);
							LPData person = new LPData(jsonObject.getString("id"), jsonObject.getString("mxid"),
									jsonObject.getString("lpName"), jsonObject.getString("dtName"));
							lpDatas.add(person);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					initPopWindow(community, lpDatas);
				}
				break;
			case SELECT_HOUSE:
				qrcode.setText(xqName);
				houses = new ArrayList<House>();
				if (house.equals("fail")) {
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jsonObject = new JSONObject(house);
						if (jsonObject.getBoolean("success")) {
							JSONArray data = jsonObject.getJSONArray("data");
							for (int i = 0; i < data.length(); i++) {
								JSONObject dong = data.getJSONObject(i); // С��1��¥
								String dongText = dong.getString("text");
								String dongValue = dong.getString("value");
								JSONArray dongDY = dong.getJSONArray("children");
								for (int j = 0; j < dongDY.length(); j++) {
									JSONObject danyuan = dongDY.getJSONObject(j); // 1��Ԫ
									String danyuanText = danyuan.getString("text");
									JSONArray danyuanC = danyuan.getJSONArray("children");
									for (int k = 0; k < danyuanC.length(); k++) {
										JSONObject ceng = danyuanC.getJSONObject(k); // 3��
										String cengText = ceng.getString("text");
										JSONArray cengFJ = ceng.getJSONArray("children");
										for (int l = 0; l < cengFJ.length(); l++) {
											JSONObject fangjian = cengFJ.getJSONObject(l); // ����
											String fangjianText = fangjian.getString("text");
											String fangjianValue = fangjian.getString("value");
											House hTemp = new House(dongText + danyuanText + cengText + fangjianText,
													fangjianValue, dongText, dongValue);
											houses.add(hTemp);
										}
									}
								}
							}
						} else {
							Toast.makeText(RegisterActivity.this, "��ȡ����ʧ��", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSon���ݽ����쳣��" + e.getMessage());
					}

					// ����׼��
					buildesData = new ArrayList<String>();
					for (House hs : houses) {
						String fh = hs.getFh();
						String temp = fh.substring(0, fh.indexOf("¥"));
						if (!buildesData.contains(temp)) {
							buildesData.add(temp);
						}
					}
					unitesData = new ArrayList<String>();
					for (House hs : houses) {
						String fh = hs.getFh();
						String temp = fh.substring(0, fh.indexOf("��Ԫ"));
						if (!unitesData.contains(temp)) {
							unitesData.add(temp);
						}
					}
					flooresData = new ArrayList<String>();
					for (House hs : houses) {
						String fh = hs.getFh();
						String temp = fh.substring(0, fh.indexOf("��"));
						if (!flooresData.contains(temp)) {
							flooresData.add(temp);
						}
					}
					roomsData = new ArrayList<String>();
					for (House hs : houses) {
						roomsData.add(hs.getFh());
					}
				}
				initWheel();
				if (progress != null) {
					progress.dismiss();
				}
				selectHouse.setAlpha(1.0f);
				break;

			case CAPTACHA:
				if (timer == 0) {
					getCaptacha.setText("��ȡ��֤��");
				} else {
					getCaptacha.setText(timer + " s");
				}
				break;

			case REGISTER:
				if (resp.equals("fail")) {
					Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jsonObject = new JSONObject(resp);
						if (jsonObject.getBoolean("success")) {
							alertDialog = new AlertDialog.Builder(RegisterActivity.this).setIcon(R.drawable.ic_launcher)
									.setTitle("ϵͳ��ʾ").setMessage("ע��ɹ�����ֱ�ӵ�¼")
									.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											alertDialog.dismiss();
										}
									}).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create(); // ����AlertDialog����
							alertDialog.show(); // ��ʾ�Ի���
						} else { // registered=�Ѿ�ע����ģ�ϵͳ�ڴ��ڸ��û������������ظ�ע�ᣩ
							alertDialog = new AlertDialog.Builder(RegisterActivity.this).setIcon(R.drawable.ic_launcher)
									.setTitle("ϵͳ��ʾ").setMessage("���û��Ѵ��ڣ���ֱ�ӵ�¼")
									.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											alertDialog.dismiss();
										}
									}).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create(); // ����AlertDialog����
							alertDialog.show(); // ��ʾ�Ի���
						}
					} catch (JSONException e) {
						System.out.println("JSon���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case FAILED_GETDATA:
				if (progress != null) {
					progress.dismiss();
				}
				Toast.makeText(getApplicationContext(), "��ȡ����ʧ�ܡ���ϵͳ�޷������ö�ά������ݡ�", Toast.LENGTH_SHORT).show();
				break;
			default:
				if (progress != null) {
					progress.dismiss();
				}
				Toast.makeText(getApplicationContext(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		androidPK = getIntent().getStringExtra("androidPK");
		nameET = (EditText) findViewById(R.id.nameET);
		phoneET = (EditText) findViewById(R.id.phoneET);
		captchaET = (EditText) findViewById(R.id.captchaET);
		pwET = (EditText) findViewById(R.id.pwET);

		qrcode = (TextView) findViewById(R.id.QRcode);
		community = (TextView) findViewById(R.id.communityHint1);

		/*
		 * ѡ��С�������湦�ܣ���ά��ɨ�衣��
		 */
		qrcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegisterActivity.this, CaptureActivity.class);
				startActivityForResult(intent, SCAN_COMMUNITY);
			}
		});

		// ѡ�񷿺�
		threeLevel = (RelativeLayout) findViewById(R.id.threeLevel);
		selectHouse = (TextView) findViewById(R.id.selectHouse);
		selectHouse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (selectHouse.getAlpha() == 1.0f) {
					threeLevel.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(getApplicationContext(), "����ɨ���ά�롾ѡ��С����", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// ��ȡ��֤��
		getCaptacha = (TextView) findViewById(R.id.getCaptacha);
		getCaptacha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (getCaptacha.getText().equals("��ȡ��֤��")) {
					timer = 60;
					phone = phoneET.getText().toString().trim();
					if (FormatMatch.phone(phone)) {
						handler.sendEmptyMessage(CAPTACHA);
						new Thread() {
							public void run() {
								captcha = String.valueOf(Math.random()).substring(3, 9);

								// result = Captcha.getCaptcha(captcha, phone);
								// // ��֤�뷵��ֵ��ԭ������ֵ��
								GetData.getHtml(UrlString.LiuPeng + "/PhoneYZM?phone=" + phone + "&yzm=" + captcha);
								System.out.println(UrlString.LiuPeng + "/PhoneYZM?phone=" + phone + "&yzm=" + captcha);
								while (timer > 0) {
									handler.sendEmptyMessage(CAPTACHA);
									timer--;
									try {
										Thread.sleep(999);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

								}
							}
						}.start();
					} else {
						Toast.makeText(getApplicationContext(), "��������ȷ���ֻ���", Toast.LENGTH_SHORT).show();
						phoneET.requestFocus();
					}
				}
			}
		});

		// �����˺ţ�������¼
		findViewById(R.id.loginHint).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	/**
	 * ����ɨ�赽������
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK && requestCode == SCAN_COMMUNITY) {
			Bundle bundle = intent.getExtras();
			xqId = bundle.getString("result").trim();
			if (xqId != null) {
				progress = ProgressDialog.show(RegisterActivity.this, null, "���ݽ�����", true, false);
				new Thread() {
					public void run() {
						// String jsonTemp = GetData.getHtml(UrlString.url +
						// "/loupan?id=" + scanResult.trim());
						String jsonTemp = GetData.getHtml(UrlString.LiuPeng + "/LPBasicDataModel?result=" + xqId);
						if (jsonTemp.equals("fail")) {
							handler.sendEmptyMessage(DEFAULT);
						} else {
							try {
								// JSONArray jsonArray = new
								// JSONArray(jsonTemp);
								// for (int i = 0; i < jsonArray.length(); i++)
								// {
								// JSONObject jsonObject = (JSONObject)
								// jsonArray.get(i);
								JSONObject jsonObject = new JSONObject(jsonTemp);
								if (jsonObject.getBoolean("success")) {
									// xqId = jsonObject.getString("id");
									xqName = jsonObject.getString("data").trim();
									house = GetData.getHtml(UrlString.LiuPeng + "/HouseDetailDataList?result=" + xqId);
									handler.sendEmptyMessage(SELECT_HOUSE);
								} else {
									handler.sendEmptyMessage(FAILED_GETDATA);
								}
								// }
							} catch (JSONException e) {
								System.out.println("JSON���ݽ����쳣��" + e.getMessage());
							}
						}
					}
				}.start();
			}
		}
	}

	// ������
	protected void initPopWindow(TextView v, List<LPData> list) {
		View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.popup_window, null, false);
		ListView lView = (ListView) view.findViewById(R.id.popupList);
		ReusableAdapter<LPData> adapter = new ReusableAdapter<LPData>(list, R.layout.item_popup) {
			@Override
			public void bindView(ViewHolder holder, LPData obj) {
				holder.setText(R.id.textPopup, obj.getLpName()); // obj.getDtName()
			}
		};
		lView.setAdapter(adapter);

		final PopupWindow popWindow = new PopupWindow(view, (int) (community.getWidth() * 3.5),
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
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				new Thread() {
					public void run() {
						LPData tempL = lpDatas.get(position);
						xqId = tempL.getId();
						xqName = tempL.getLpName();
						house = GetData.getHtml(UrlString.url + "/loupan?mxid=" + tempL.getMxid());
						handler.sendEmptyMessage(SELECT_HOUSE);
					}
				}.start();
				popWindow.dismiss();
			}
		});
	}

	/**
	 * ��ʼ��ѡ�񷿺ŵ���������
	 */
	private void initWheel() {
		// ¥
		int lengthB = buildesData.size();
		buildes = new String[lengthB];
		for (int i = 0; i < lengthB; i++) {
			buildes[i] = buildesData.get(i) + "¥";
		}
		build = (WheelView) findViewById(R.id.build);
		ArrayWheelAdapter<String> adapterB = new ArrayWheelAdapter<String>(RegisterActivity.this, buildes);
		adapterB.setTextSize(16);
		build.setViewAdapter(adapterB);

		// ��Ԫ
		unites = new String[buildes.length][];
		for (int i = 0; i < buildes.length; i++) {
			int k = 0;
			for (int j = 0; j < unitesData.size(); j++) {
				String temp = unitesData.get(j);
				if (buildes[i].equals(temp.substring(0, temp.indexOf("¥")) + "¥")) {
					k++;
				}
			}
			unites[i] = new String[k];
		}
		for (int i = 0; i < buildes.length; i++) {
			int k = 0;
			for (int j = 0; j < unitesData.size(); j++) {
				String temp = unitesData.get(j);
				if (buildes[i].equals(temp.substring(0, temp.indexOf("¥")) + "¥")) {
					unites[i][k] = temp.substring(temp.indexOf("¥") + 1) + "��Ԫ";
					k++;
				}
			}
		}
		unit = (WheelView) findViewById(R.id.unit);

		// ¥������
		build.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateUnites(unit, unites, newValue);
				}
			}
		});
		build.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateUnites(unit, unites, build.getCurrentItem());
			}
		});

		// ¥��
		floores = new String[buildes.length][][];
		for (int i = 0; i < buildes.length; i++) {
			floores[i] = new String[(unites[i].length)][];
			for (int j = 0; j < unites[i].length; j++) {
				int t = 0;
				for (int k = 0; k < flooresData.size(); k++) {
					String temp = flooresData.get(k);
					if ((buildes[i] + unites[i][j]).equals(temp.substring(0, temp.indexOf("��Ԫ")) + "��Ԫ")) {
						t++;
					}
				}
				floores[i][j] = new String[t];
			}
		}
		for (int i = 0; i < buildes.length; i++) {
			for (int j = 0; j < unites[i].length; j++) {
				int t = 0;
				for (int k = 0; k < flooresData.size(); k++) {
					String temp = flooresData.get(k);
					if ((buildes[i] + unites[i][j]).equals(temp.substring(0, temp.indexOf("��Ԫ")) + "��Ԫ")) {
						floores[i][j][t] = temp.substring(temp.indexOf("��Ԫ") + 2) + "��";
						t++;
					}
				}
			}
		}
		floor = (WheelView) findViewById(R.id.floor);

		// ��Ԫ������
		unit.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateFloores(floor, floores, build.getCurrentItem(), unit.getCurrentItem());
				}
			}
		});
		unit.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateFloores(floor, floores, build.getCurrentItem(), unit.getCurrentItem());
			}
		});

		// ����
		room = (WheelView) findViewById(R.id.room);
		rooms = new String[buildes.length][][][];
		for (int i = 0; i < buildes.length; i++) {
			rooms[i] = new String[(unites[i].length)][][];
			for (int j = 0; j < unites[i].length; j++) {
				rooms[i][j] = new String[floores[i][j].length][];
				for (int k = 0; k < floores[i][j].length; k++) {
					int t = 0;
					for (int z = 0; z < roomsData.size(); z++) {
						String temp = roomsData.get(z);
						if ((buildes[i] + unites[i][j] + floores[i][j][k])
								.equals(temp.substring(0, temp.indexOf("��")) + "��")) {
							t++;
						}
					}
					rooms[i][j][k] = new String[t];
				}
			}
		}
		for (int i = 0; i < buildes.length; i++) {
			for (int j = 0; j < unites[i].length; j++) {
				for (int k = 0; k < floores[i][j].length; k++) {
					int t = 0;
					for (int z = 0; z < roomsData.size(); z++) {
						String temp = roomsData.get(z);
						if ((buildes[i] + unites[i][j] + floores[i][j][k])
								.equals(temp.substring(0, temp.indexOf("��")) + "��")) {
							rooms[i][j][k][t] = temp.substring(temp.indexOf("��") + 1);
							t++;
						}
					}
				}
			}
		}

		// ¥�������
		floor.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateRooms(room, rooms, build.getCurrentItem(), unit.getCurrentItem(), floor.getCurrentItem());
				}
			}
		});
		floor.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateRooms(room, rooms, build.getCurrentItem(), unit.getCurrentItem(), floor.getCurrentItem());
			}
		});

		build.scroll(buildes.length, 500);
		build.setCurrentItem(buildes.length / 2); // ������ں���
	}

	/**
	 * Updates the room wheel
	 */
	private void updateRooms(WheelView room, String rooms[][][][], int index1, int index2, int index3) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, rooms[index1][index2][index3]);
		adapter.setTextSize(16);
		room.setViewAdapter(adapter);
		room.setCurrentItem(rooms[index1][index2][index3].length / 2); // ������ں���
	}

	/**
	 * Updates the floor wheel
	 */
	private void updateFloores(WheelView floor, String floores[][][], int index1, int index2) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, floores[index1][index2]);
		adapter.setTextSize(16);
		floor.setViewAdapter(adapter);
		floor.setCurrentItem(floores[index1][index2].length / 2); // ������ں���
		updateRooms(room, rooms, build.getCurrentItem(), unit.getCurrentItem(), floor.getCurrentItem());
	}

	/**
	 * Updates the unit wheel
	 */
	private void updateUnites(WheelView unit, String unites[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, unites[index]);
		adapter.setTextSize(16);
		unit.setViewAdapter(adapter);
		unit.setCurrentItem(unites[index].length / 2); // ������ں���
		updateFloores(floor, floores, build.getCurrentItem(), unit.getCurrentItem());
	}

	/**
	 * ȡ����������
	 */
	public void cancel(View view) {
		fhName = buildes[build.getCurrentItem()] + unites[build.getCurrentItem()][unit.getCurrentItem()]
				+ floores[build.getCurrentItem()][unit.getCurrentItem()][floor.getCurrentItem()]
				+ rooms[build.getCurrentItem()][unit.getCurrentItem()][floor.getCurrentItem()][room.getCurrentItem()];
		selectHouse.setText(fhName);
		threeLevel.setVisibility(View.GONE);
		for (House house : houses) {
			if (house.getFh().equals(fhName)) {
				fhId = house.getFwmxID();
				break;
			}
		}
		// new Thread() {
		// public void run() {
		// String fh = fhName.substring(0, 1) + fhName.substring(3, 5) +
		// fhName.substring(6, 8);
		// System.out.println(fh);
		// fhId = GetData.getHtml(UrlString.url + "/loupan?fh=" + fh);
		// }
		// }.start();
	}

	/**
	 * ���ؼ�
	 */
	public void back(View view) {
		this.finish();
	}

	/**
	 * ���ؼ�
	 */
	@Override
	public void onBackPressed() {
		this.finish();
	}

	public void register(View view) {
		username = nameET.getText().toString().trim();
		ShuRuyzm = captchaET.getText().toString().trim();
		pw = pwET.getText().toString().trim();
		phone = phoneET.getText().toString().trim();
		// System.out.println("xqId��" + xqId + "��fhId��" + fhId + "��phone��" +
		// phone + "��ShuRuyzm��" + ShuRuyzm + "��pw��" + pw
		// + "��androidPK��" + androidPK + "��username��" + username + "��xqName��" +
		// xqName + "��fhName��" + fhName
		// + "��captcha��" + captcha);
		if (xqName == null || xqName.equals("")) {
			qrcode.requestFocus();
			Toast.makeText(getApplicationContext(), "����ɨ���ά�롾ѡ��С����", Toast.LENGTH_SHORT).show();
		} else if (fhName == null || fhName.equals("")) {
			selectHouse.requestFocus();
			Toast.makeText(getApplicationContext(), "����ѡ�񷿺�", Toast.LENGTH_SHORT).show();
		} else if (username == null || username.equals("")) {
			nameET.requestFocus();
			Toast.makeText(getApplicationContext(), "����������", Toast.LENGTH_SHORT).show();
		} else if (!(FormatMatch.phone(phone))) {
			Toast.makeText(getApplicationContext(), "��������ȷ���ֻ���", Toast.LENGTH_SHORT).show();
			phoneET.requestFocus();
		} else if (ShuRuyzm == null || ShuRuyzm.equals("") || !(ShuRuyzm.equals(captcha))) {
			captchaET.requestFocus();
			Toast.makeText(getApplicationContext(), "��������ȷ����֤��", Toast.LENGTH_SHORT).show();
		} else if (pw == null || pw.equals("")) {
			pwET.requestFocus();
			Toast.makeText(getApplicationContext(), "����������", Toast.LENGTH_SHORT).show();
		} else {
			new Thread() {
				public void run() {
					resp = PostData.submit(UrlString.LiuPeng + "/Reg", xqId, fhId, phone, ShuRuyzm, pw, androidPK, "",
							username, xqName, fhName, "1", "2", "", "", "", "");
					handler.sendEmptyMessage(REGISTER);
				}
			}.start();
		}
	}
}
