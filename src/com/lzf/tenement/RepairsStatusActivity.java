package com.lzf.tenement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.entity.Repair;
import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.util.UrlString;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RepairsStatusActivity extends Activity {

	private int page;
	private int totalCount;
	private List<Repair> repairs;
	private ListView repairsList;
	private View footerView;
	private String title; // ״̬����
	private int state; // ״̬�������
	private String tempRepair;
	private Repair repair; // ��������

	private ReusableAdapter<Repair> adapter;
	private String server; // ����˷��صı����б�
	private ProgressDialog progress;
	private RadioButton deal;
	private RadioButton dealed;

	private final int QUERY = 0;
	private final int LOADING = 1;
	private final int UPDATE = 2;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE:
				if (tempRepair.equals("fail")) {
					Toast.makeText(RepairsStatusActivity.this, "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {

					try {
						JSONObject jObject = new JSONObject(tempRepair);
						if (jObject.getBoolean("success")) {
							JSONArray jArray = jObject.getJSONArray("data");
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								System.out.println(repair);
								if (!(jData.getString("zt").trim()).equals(repair.getZt().trim())) {
									adapter.delete(repair);
								}
							}
						} else {
							System.out.println("��ȡ����ʧ�ܡ�");
						}
					} catch (JSONException e) {
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case QUERY:
				if (progress != null) {
					progress.dismiss();
				}
				if (server.equals("fail")) {
					Toast.makeText(RepairsStatusActivity.this, "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(server);
						if (jObject.getBoolean("success")) {
							repairs = new ArrayList<Repair>();
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							repairs.removeAll(repairs);
							if (jArray.length() <= 0) {
								Toast.makeText(RepairsStatusActivity.this, "��������", Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject jData = jArray.getJSONObject(i);
									repairs.add(new Repair(jData.getString("id"), jData.getString("lxa"),
											jData.getString("lxb"), jData.getString("content"),
											jData.getString("State"), jData.getString("shijian"), jData.getString("zt"),
											jData.getString("fpys"), jData.getString("pj"), jData.getString("pjs")));
								}
							}
							showListView(repairs);
						} else {
							Toast.makeText(RepairsStatusActivity.this, "��ȡ����ʧ�ܡ�", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case LOADING:
				if (server.equals("fail")) {
					Toast.makeText(RepairsStatusActivity.this, "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(server);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							System.out.println(jArray.length());
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								adapter.add(new Repair(jData.getString("id"), jData.getString("lxa"),
										jData.getString("lxb"), jData.getString("content"), jData.getString("State"),
										jData.getString("shijian"), jData.getString("zt"), jData.getString("fpys"),
										jData.getString("pj"), jData.getString("pjs")));
							}
						} else {
							Toast.makeText(RepairsStatusActivity.this, "��ȡ����ʧ�ܡ�", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				footerView.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repairs_status);

		TextView textTitle = (TextView) findViewById(R.id.textTitle);
		title = getIntent().getStringExtra("title");
		textTitle.setText(title);

		if (title.equals("δ����")) {
			state = 0;
		} else if (title.equals("������")) {
			state = 1;
		} else if (title.equals("�ݻ�����")) {
			state = 2;
		} else if (title.equals("�Ѵ���")) {
			state = 3;
		} else if (title.equals("������")) {
			state = 4;
		} else if (title.equals("��ȡ��")) {
			state = -1;
		}

		SharedPreferences sp = RepairsStatusActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		final String xingming = sp.getString("xingming", "�ƴ�Ƽ�");
		final String phone = sp.getString("phone", "fdkj@123??");
		final String uid = sp.getString("id", "fdkj@123??");
		final String xiaoquGuid = sp.getString("xiaoquGuid", "fdkj@123??");

		RadioGroup menu = (RadioGroup) findViewById(R.id.repairsMenu);
		deal = (RadioButton) findViewById(R.id.deal);
		dealed = (RadioButton) findViewById(R.id.dealed);
		repairsList = (ListView) findViewById(R.id.listView);

		menu.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup rg, int i) {
				page = 0;
				switch (rg.getCheckedRadioButtonId()) {
				case R.id.deal:
					loading(true, UrlString.LiuPeng + "/WenTiList?lx=1&xm=" + xingming + "&dh=" + phone + "&state="
							+ state + "&FK_Uid=" + uid + "&xiaoquGuid=" + xiaoquGuid);
					break;
				case R.id.dealed:
					loading(true, UrlString.LiuPeng + "/WenTiList?lx=14&xm=" + xingming + "&dh=" + phone + "&state="
							+ state + "&FK_Uid=" + uid + "&xiaoquGuid=" + xiaoquGuid);
					break;
				default:
					break;
				}
			}
		});

		footerView = LayoutInflater.from(RepairsStatusActivity.this).inflate(R.layout.loading, null, false);
		repairsList.addFooterView(footerView);

		repairsList.setOnScrollListener(new OnScrollListener() {

			/** ��¼��һ��Item����ֵ */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ���������ײ�ʱ
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					System.out.println("totalCount��" + totalCount + "��repairs.size()��" + repairs.size());
					if (totalCount > repairs.size()) {
						if (deal.isChecked()) {
							loading(false, UrlString.LiuPeng + "/WenTiList?lx=1&xm=" + xingming + "&dh=" + phone
									+ "&state=" + state);
						} else if (dealed.isChecked()) {
							loading(true, UrlString.LiuPeng + "/WenTiList?lx=14&xm=" + xingming + "&dh=" + phone
									+ "&state=" + state);
						}
					}
				}
			}

			/*
			 * firstVisibleItem��ʾ����ʱ��Ļ��һ��ListItem(������ʾ��ListItemҲ��)������ListView��λ��
			 * ���±��0��ʼ��
			 * 
			 * visibleItemCount��ʾ����ʱ��Ļ���Լ�����ListItem(������ʾ��ListItemҲ��)����
			 * 
			 * totalItemCount��ʾListView��ListItem����
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				this.firstVisibleItem = firstVisibleItem;
				// System.out.println("firstVisibleItem��" + firstVisibleItem +
				// "��visibleItemCount��" + visibleItemCount
				// + "��totalItemCount��" + totalItemCount);
				// �жϿ���Item�Ƿ����ڵ�ǰҳ����ȫ��ʾ
				if (visibleItemCount == totalItemCount) {
					// removeFooterView(footerView);
					footerView.setVisibility(View.GONE);// ���صײ�����
				} else if (totalCount > repairs.size()) {
					footerView.setVisibility(View.VISIBLE);
				} else {
					footerView.setVisibility(View.GONE);
				}
			}
		});

		deal.performClick();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		new Thread() {
			public void run() {
				tempRepair = OKHttp.getData(UrlString.LiuPeng + "/WenTiXiangXi?id=" + repair.getId());
				System.out.println(tempRepair);
				handler.sendEmptyMessage(UPDATE);
			}
		}.start();
	}

	private void loading(final boolean flag, final String url) {
		page++;
		if (flag) {
			progress = ProgressDialog.show(RepairsStatusActivity.this, null, "���ڼ���...", true, false);
		}
		new Thread() {
			public void run() {
				server = OKHttp.getData(url + "&start=" + page);
				if (flag) {
					handler.sendEmptyMessage(QUERY);
				} else {
					handler.sendEmptyMessage(LOADING);
				}
			}
		}.start();
	}

	private void showListView(final List<Repair> data) {
		adapter = new ReusableAdapter<Repair>(repairs, R.layout.item_repair) {

			@Override
			public void bindView(ViewHolder holder, Repair obj) {
				holder.setText(R.id.date, obj.getShijian());
				holder.setText(R.id.repairTYPE, obj.getLxa());
				holder.setText(R.id.repairtype, obj.getLxb());
				holder.setText(R.id.cancelBtn, obj.getZt());
			}
		};
		repairsList.setAdapter(adapter);
		repairsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, final View arg1, int arg2, long arg3) {
				// arg1.setOnClickListener(new OnClickListener() {
				// @Override
				// public void onClick(View arg0) {
				// System.out.println(arg1.toString());
				// }
				// });
				repair = data.get(arg2);
				Intent intent = new Intent(RepairsStatusActivity.this, RepairActivity.class);
				intent.putExtra("repair", repair);
				startActivity(intent);
			}
		});
	}

	public void back(View view) {
		this.finish();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

}
