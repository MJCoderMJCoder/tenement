package com.lzf.tenement.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.NotificationActivity;
import com.lzf.tenement.R;
import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.entity.Notification;
import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.util.UrlString;

import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class FragmentNotification extends Fragment {

	private int page;
	private int totalCount;
	private List<Notification> notifies;
	private ListView notificationLV;
	private View footerView;

	private ReusableAdapter<Notification> adapter;
	private String server; // ����˷��صı����б�
	private ProgressDialog progress;

	private final int QUERY = 0;
	private final int LOADING = 1;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case QUERY:
				if (progress != null) {
					progress.dismiss();
				}
				if (server.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(server);
						if (jObject.getBoolean("success")) {
							notifies = new ArrayList<Notification>();
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							notifies.removeAll(notifies);
							if (jArray.length() <= 0) {
								Toast.makeText(getActivity(), "��������", Toast.LENGTH_SHORT).show();
							} else {
								for (int i = 0; i < jArray.length(); i++) {
									JSONObject jData = jArray.getJSONObject(i);
									notifies.add(new Notification(jData.getString("id"), jData.getString("BiaoTi"),
											jData.getString("ShiJian"), jData.getString("NeiRong"),
											jData.getString("FaBuRen"), jData.getString("FaBuWuYe")));
								}
							}
							showListView(notifies);
						} else {
							Toast.makeText(getActivity(), "��ȡ����ʧ�ܡ�", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case LOADING:
				if (server.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(server);
						if (jObject.getBoolean("success")) {
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							System.out.println(jArray.length());
							for (int i = 0; i < jArray.length(); i++) {
								JSONObject jData = jArray.getJSONObject(i);
								adapter.add(new Notification(jData.getString("id"), jData.getString("BiaoTi"),
										jData.getString("ShiJian"), jData.getString("NeiRong"),
										jData.getString("FaBuRen"), jData.getString("FaBuWuYe")));
							}
						} else {
							Toast.makeText(getActivity(), "��ȡ����ʧ�ܡ�", Toast.LENGTH_SHORT).show();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notification, container, false);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_PRIVATE);
		final String xingming = sp.getString("xingming", "�ƴ�Ƽ�");
		final String xiaoquGuid = sp.getString("xiaoquGuid", "�ƴ�Ƽ�");
		final String phone = sp.getString("phone", "fdkj@123??");

		page = 0;

		loading(true, UrlString.LiuPeng + "/TongZiList?xm=" + xingming + "&dh=" + phone + "&xiaoquGuid=" + xiaoquGuid);

		notificationLV = (ListView) view.findViewById(R.id.listNotification);

		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading, null, false);
		notificationLV.addFooterView(footerView);

		notificationLV.setOnScrollListener(new OnScrollListener() {

			/** ��¼��һ��Item����ֵ */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ���������ײ�ʱ
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					if (totalCount > notifies.size()) {
						loading(false, UrlString.LiuPeng + "/TongZiList?xm=" + xingming + "&dh=" + phone
								+ "&xiaoquGuid=" + xiaoquGuid);
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
				} else if (totalCount > notifies.size()) {
					footerView.setVisibility(View.VISIBLE);
				} else {
					footerView.setVisibility(View.GONE);
				}
			}
		});
		return view;
	}

	private void loading(final boolean flag, final String url) {
		page++;
		if (flag) {
			progress = ProgressDialog.show(getActivity(), null, "���ڼ���...", true, false);
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

	private void showListView(final List<Notification> data) {
		adapter = new ReusableAdapter<Notification>(data, R.layout.item_notification) {
			@Override
			public void bindView(ViewHolder holder, Notification obj) {
				holder.setText(R.id.textDate, obj.getShiJian());
				holder.setText(R.id.title, obj.getBiaoTi());
				holder.setText(R.id.textHint, obj.getNeiRong());
			}

		};
		notificationLV.setAdapter(adapter);
		notificationLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, final View arg1, int arg2, long arg3) {
				Intent intent = new Intent(getActivity(), NotificationActivity.class);
				intent.putExtra("notify", data.get(arg2));
				startActivity(intent);
			}
		});
	}
}
