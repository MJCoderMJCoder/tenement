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
	private TextView community; // 小区（备用功能，供用户手动选择小区。）
	private TextView qrcode; // 小区（点击扫描二维码）
	private TextView selectHouse; // 房号（选择房号）
	private TextView getCaptacha; // 获取验证码
	private EditText nameET; // 姓名输入空间
	private EditText phoneET; // 手机号输入控件
	private EditText captchaET; // 验证码输入控件
	private EditText pwET; // 密码输入控件
	private AlertDialog alertDialog; // 系统提示对话框
	private String loupan; // 所有楼盘信息
	private String xqId; // 已经选择的楼盘（小区）ID
	private String xqName; // 已经选择的楼盘（小区）名称
	private String house; // 所有房屋信息
	private String fhId; // 已经选择的房号ID
	private String fhName; // 已经选择的房号信息
	private String username; // 姓名
	private String phone; // 手机号
	private String androidPK; // 安卓设备唯一标识
	private String ShuRuyzm; // 客户端输入的验证码
	private String pw; // 密码
	private String captcha = ""; // 服务端发送的验证码
	private String resp; // 点击注册按钮时，服务端返回的内容
	HashMap<String, Object> result = null; // 验证码返回值（原生返回值）
	private int timer; // 计时器
	private final int SELECT_COMMUNITY = 0; // 选择小区
	private final int SELECT_HOUSE = 1; // 选择房号
	private final int SCAN_COMMUNITY = 6003; // 扫描标志
	private final int CAPTACHA = 4; // 验证码标志
	private final int REGISTER = 2; // 注册事件
	private final int FAILED_GETDATA = 3; // 获取数据失败
	private final int DEFAULT = -5; // 特殊异常时的标识
	private List<LPData> lpDatas; // 楼盘信息
	private List<House> houses = null; // 房号信息

	private boolean scrolling = false; // 选择房号的滚轮标志
	private WheelView room; // 房间滚轮
	private WheelView unit; // 单元滚轮
	private WheelView build; // 单元滚轮
	private WheelView floor; // 楼层滚轮
	private RelativeLayout threeLevel; // 选择房间的总开关

	private List<String> buildesData; // 楼楼数据准备
	private String buildes[];
	private List<String> unitesData; // 单元数据准备
	private String unites[][];
	private List<String> flooresData; // 楼层数据准备
	private String floores[][][];
	private List<String> roomsData; // 房间数据准备
	private String rooms[][][][];

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SELECT_COMMUNITY:
				if (progress != null) {
					progress.dismiss();
				}
				if (loupan.equals("fail")) {
					Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jsonObject = new JSONObject(house);
						if (jsonObject.getBoolean("success")) {
							JSONArray data = jsonObject.getJSONArray("data");
							for (int i = 0; i < data.length(); i++) {
								JSONObject dong = data.getJSONObject(i); // 小区1号楼
								String dongText = dong.getString("text");
								String dongValue = dong.getString("value");
								JSONArray dongDY = dong.getJSONArray("children");
								for (int j = 0; j < dongDY.length(); j++) {
									JSONObject danyuan = dongDY.getJSONObject(j); // 1单元
									String danyuanText = danyuan.getString("text");
									JSONArray danyuanC = danyuan.getJSONArray("children");
									for (int k = 0; k < danyuanC.length(); k++) {
										JSONObject ceng = danyuanC.getJSONObject(k); // 3层
										String cengText = ceng.getString("text");
										JSONArray cengFJ = ceng.getJSONArray("children");
										for (int l = 0; l < cengFJ.length(); l++) {
											JSONObject fangjian = cengFJ.getJSONObject(l); // 房间
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
							Toast.makeText(RegisterActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSon数据解析异常：" + e.getMessage());
					}

					// 数据准备
					buildesData = new ArrayList<String>();
					for (House hs : houses) {
						String fh = hs.getFh();
						String temp = fh.substring(0, fh.indexOf("楼"));
						if (!buildesData.contains(temp)) {
							buildesData.add(temp);
						}
					}
					unitesData = new ArrayList<String>();
					for (House hs : houses) {
						String fh = hs.getFh();
						String temp = fh.substring(0, fh.indexOf("单元"));
						if (!unitesData.contains(temp)) {
							unitesData.add(temp);
						}
					}
					flooresData = new ArrayList<String>();
					for (House hs : houses) {
						String fh = hs.getFh();
						String temp = fh.substring(0, fh.indexOf("层"));
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
					getCaptacha.setText("获取验证码");
				} else {
					getCaptacha.setText(timer + " s");
				}
				break;

			case REGISTER:
				if (resp.equals("fail")) {
					Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jsonObject = new JSONObject(resp);
						if (jsonObject.getBoolean("success")) {
							alertDialog = new AlertDialog.Builder(RegisterActivity.this).setIcon(R.drawable.ic_launcher)
									.setTitle("系统提示").setMessage("注册成功，请直接登录")
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											alertDialog.dismiss();
										}
									}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create(); // 创建AlertDialog对象
							alertDialog.show(); // 显示对话框
						} else { // registered=已经注册过的，系统内存在该用户。（不可以重复注册）
							alertDialog = new AlertDialog.Builder(RegisterActivity.this).setIcon(R.drawable.ic_launcher)
									.setTitle("系统提示").setMessage("该用户已存在，请直接登录")
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											alertDialog.dismiss();
										}
									}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											onBackPressed();
										}
									}).create(); // 创建AlertDialog对象
							alertDialog.show(); // 显示对话框
						}
					} catch (JSONException e) {
						System.out.println("JSon数据解析异常：" + e.getMessage());
					}
				}
				break;
			case FAILED_GETDATA:
				if (progress != null) {
					progress.dismiss();
				}
				Toast.makeText(getApplicationContext(), "获取数据失败。【系统无法解析该二维码的数据】", Toast.LENGTH_SHORT).show();
				break;
			default:
				if (progress != null) {
					progress.dismiss();
				}
				Toast.makeText(getApplicationContext(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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
		 * 选择小区（常规功能，二维码扫描。）
		 */
		qrcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegisterActivity.this, CaptureActivity.class);
				startActivityForResult(intent, SCAN_COMMUNITY);
			}
		});

		// 选择房号
		threeLevel = (RelativeLayout) findViewById(R.id.threeLevel);
		selectHouse = (TextView) findViewById(R.id.selectHouse);
		selectHouse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (selectHouse.getAlpha() == 1.0f) {
					threeLevel.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(getApplicationContext(), "请先扫描二维码【选择小区】", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// 获取验证码
		getCaptacha = (TextView) findViewById(R.id.getCaptacha);
		getCaptacha.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (getCaptacha.getText().equals("获取验证码")) {
					timer = 60;
					phone = phoneET.getText().toString().trim();
					if (FormatMatch.phone(phone)) {
						handler.sendEmptyMessage(CAPTACHA);
						new Thread() {
							public void run() {
								captcha = String.valueOf(Math.random()).substring(3, 9);

								// result = Captcha.getCaptcha(captcha, phone);
								// // 验证码返回值（原生返回值）
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
						Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
						phoneET.requestFocus();
					}
				}
			}
		});

		// 已有账号，立即登录
		findViewById(R.id.loginHint).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	/**
	 * 接收扫描到的内容
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK && requestCode == SCAN_COMMUNITY) {
			Bundle bundle = intent.getExtras();
			xqId = bundle.getString("result").trim();
			if (xqId != null) {
				progress = ProgressDialog.show(RegisterActivity.this, null, "数据解析中", true, false);
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
								System.out.println("JSON数据解析异常：" + e.getMessage());
							}
						}
					}
				}.start();
			}
		}
	}

	// 弹出框
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
	 * 初始化选择房号的三级联动
	 */
	private void initWheel() {
		// 楼
		int lengthB = buildesData.size();
		buildes = new String[lengthB];
		for (int i = 0; i < lengthB; i++) {
			buildes[i] = buildesData.get(i) + "楼";
		}
		build = (WheelView) findViewById(R.id.build);
		ArrayWheelAdapter<String> adapterB = new ArrayWheelAdapter<String>(RegisterActivity.this, buildes);
		adapterB.setTextSize(16);
		build.setViewAdapter(adapterB);

		// 单元
		unites = new String[buildes.length][];
		for (int i = 0; i < buildes.length; i++) {
			int k = 0;
			for (int j = 0; j < unitesData.size(); j++) {
				String temp = unitesData.get(j);
				if (buildes[i].equals(temp.substring(0, temp.indexOf("楼")) + "楼")) {
					k++;
				}
			}
			unites[i] = new String[k];
		}
		for (int i = 0; i < buildes.length; i++) {
			int k = 0;
			for (int j = 0; j < unitesData.size(); j++) {
				String temp = unitesData.get(j);
				if (buildes[i].equals(temp.substring(0, temp.indexOf("楼")) + "楼")) {
					unites[i][k] = temp.substring(temp.indexOf("楼") + 1) + "单元";
					k++;
				}
			}
		}
		unit = (WheelView) findViewById(R.id.unit);

		// 楼监听器
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

		// 楼层
		floores = new String[buildes.length][][];
		for (int i = 0; i < buildes.length; i++) {
			floores[i] = new String[(unites[i].length)][];
			for (int j = 0; j < unites[i].length; j++) {
				int t = 0;
				for (int k = 0; k < flooresData.size(); k++) {
					String temp = flooresData.get(k);
					if ((buildes[i] + unites[i][j]).equals(temp.substring(0, temp.indexOf("单元")) + "单元")) {
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
					if ((buildes[i] + unites[i][j]).equals(temp.substring(0, temp.indexOf("单元")) + "单元")) {
						floores[i][j][t] = temp.substring(temp.indexOf("单元") + 2) + "层";
						t++;
					}
				}
			}
		}
		floor = (WheelView) findViewById(R.id.floor);

		// 单元监听器
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

		// 房间
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
								.equals(temp.substring(0, temp.indexOf("层")) + "层")) {
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
								.equals(temp.substring(0, temp.indexOf("层")) + "层")) {
							rooms[i][j][k][t] = temp.substring(temp.indexOf("层") + 1);
							t++;
						}
					}
				}
			}
		}

		// 楼层监听器
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
		build.setCurrentItem(buildes.length / 2); // 必须放在后面
	}

	/**
	 * Updates the room wheel
	 */
	private void updateRooms(WheelView room, String rooms[][][][], int index1, int index2, int index3) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, rooms[index1][index2][index3]);
		adapter.setTextSize(16);
		room.setViewAdapter(adapter);
		room.setCurrentItem(rooms[index1][index2][index3].length / 2); // 必须放在后面
	}

	/**
	 * Updates the floor wheel
	 */
	private void updateFloores(WheelView floor, String floores[][][], int index1, int index2) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, floores[index1][index2]);
		adapter.setTextSize(16);
		floor.setViewAdapter(adapter);
		floor.setCurrentItem(floores[index1][index2].length / 2); // 必须放在后面
		updateRooms(room, rooms, build.getCurrentItem(), unit.getCurrentItem(), floor.getCurrentItem());
	}

	/**
	 * Updates the unit wheel
	 */
	private void updateUnites(WheelView unit, String unites[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this, unites[index]);
		adapter.setTextSize(16);
		unit.setViewAdapter(adapter);
		unit.setCurrentItem(unites[index].length / 2); // 必须放在后面
		updateFloores(floor, floores, build.getCurrentItem(), unit.getCurrentItem());
	}

	/**
	 * 取消三级联动
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
	 * 返回键
	 */
	public void back(View view) {
		this.finish();
	}

	/**
	 * 返回键
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
		// System.out.println("xqId：" + xqId + "，fhId：" + fhId + "，phone：" +
		// phone + "，ShuRuyzm：" + ShuRuyzm + "，pw：" + pw
		// + "，androidPK：" + androidPK + "，username：" + username + "，xqName：" +
		// xqName + "，fhName：" + fhName
		// + "，captcha：" + captcha);
		if (xqName == null || xqName.equals("")) {
			qrcode.requestFocus();
			Toast.makeText(getApplicationContext(), "请先扫描二维码【选择小区】", Toast.LENGTH_SHORT).show();
		} else if (fhName == null || fhName.equals("")) {
			selectHouse.requestFocus();
			Toast.makeText(getApplicationContext(), "请先选择房号", Toast.LENGTH_SHORT).show();
		} else if (username == null || username.equals("")) {
			nameET.requestFocus();
			Toast.makeText(getApplicationContext(), "请输入姓名", Toast.LENGTH_SHORT).show();
		} else if (!(FormatMatch.phone(phone))) {
			Toast.makeText(getApplicationContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
			phoneET.requestFocus();
		} else if (ShuRuyzm == null || ShuRuyzm.equals("") || !(ShuRuyzm.equals(captcha))) {
			captchaET.requestFocus();
			Toast.makeText(getApplicationContext(), "请输入正确的验证码", Toast.LENGTH_SHORT).show();
		} else if (pw == null || pw.equals("")) {
			pwET.requestFocus();
			Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
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
