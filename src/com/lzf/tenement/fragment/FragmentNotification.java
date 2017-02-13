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
	private String server; // 服务端返回的报修列表
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
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(server);
						if (jObject.getBoolean("success")) {
							notifies = new ArrayList<Notification>();
							totalCount = jObject.getInt("totalCount");
							JSONArray jArray = jObject.getJSONArray("data");
							notifies.removeAll(notifies);
							if (jArray.length() <= 0) {
								Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(getActivity(), "获取数据失败。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON数据解析异常：" + e.getMessage());
					}
				}
				break;
			case LOADING:
				if (server.equals("fail")) {
					Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(getActivity(), "获取数据失败。", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON数据解析异常：" + e.getMessage());
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
		final String xingming = sp.getString("xingming", "菲达科技");
		final String xiaoquGuid = sp.getString("xiaoquGuid", "菲达科技");
		final String phone = sp.getString("phone", "fdkj@123??");

		page = 0;

		loading(true, UrlString.LiuPeng + "/TongZiList?xm=" + xingming + "&dh=" + phone + "&xiaoquGuid=" + xiaoquGuid);

		notificationLV = (ListView) view.findViewById(R.id.listNotification);

		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading, null, false);
		notificationLV.addFooterView(footerView);

		notificationLV.setOnScrollListener(new OnScrollListener() {

			/** 记录第一行Item的数值 */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当滑动到底部时
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					if (totalCount > notifies.size()) {
						loading(false, UrlString.LiuPeng + "/TongZiList?xm=" + xingming + "&dh=" + phone
								+ "&xiaoquGuid=" + xiaoquGuid);
					}
				}
			}

			/*
			 * firstVisibleItem表示在现时屏幕第一个ListItem(部分显示的ListItem也算)在整个ListView的位置
			 * （下标从0开始）
			 * 
			 * visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数
			 * 
			 * totalItemCount表示ListView的ListItem总数
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				this.firstVisibleItem = firstVisibleItem;
				// System.out.println("firstVisibleItem：" + firstVisibleItem +
				// "；visibleItemCount：" + visibleItemCount
				// + "；totalItemCount：" + totalItemCount);
				// 判断可视Item是否能在当前页面完全显示
				if (visibleItemCount == totalItemCount) {
					// removeFooterView(footerView);
					footerView.setVisibility(View.GONE);// 隐藏底部布局
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
			progress = ProgressDialog.show(getActivity(), null, "正在加载...", true, false);
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
