package com.lzf.tenement.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.R;
import com.lzf.tenement.RedPacketActivity;
import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.adapter.ReusableAdapter.ViewHolder;
import com.lzf.tenement.entity.RedPacket;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.util.UrlString;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class FragmentOldred extends Fragment {

	private int page = 1;
	private int totalCount;

	private String xiaoquGuid;
	private String FangHaoGuid;
	private String phone;
	private String resp;

	private String money;
	private List<RedPacket> rpList;

	private View footerView;
	private ReusableAdapter<RedPacket> adapter;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (resp.equals("fail")) {
				Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jObject = new JSONObject(resp);
					if (jObject.getBoolean("success")) {
						JSONArray jArray = jObject.getJSONArray("data");
						for (int i = 0; i < jArray.length(); i++) {
							JSONObject redPacket = jArray.getJSONObject(i);
							adapter.add(new RedPacket(redPacket.getString("danwei"), redPacket.getString("shijian"),
									redPacket.getString("Money")));
						}
					} else {
						Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					System.out.println("JSON数据解析异常：" + e.getMessage());
				}
			}
		}
	};

	public FragmentOldred(int totalCount, String money, List<RedPacket> rpList) {
		super();
		this.money = money;
		this.rpList = rpList;
		this.totalCount = totalCount;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_oldred, container, false);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		xiaoquGuid = sp.getString("xiaoquGuid", "");
		FangHaoGuid = sp.getString("FangHaoGuid", "");
		phone = sp.getString("phone", "");

		((TextView) view.findViewById(R.id.money)).setText(money);

		adapter = new ReusableAdapter<RedPacket>(rpList, R.layout.item_redpacket) {

			@Override
			public void bindView(ViewHolder holder, RedPacket obj) {
				holder.setText(R.id.unit, obj.getDanwei());
				holder.setText(R.id.dateTime, obj.getShijian());
				holder.setText(R.id.money, obj.getMoney() + "元");
			}
		};

		ListView lView = (ListView) view.findViewById(R.id.listView1);
		lView.setAdapter(adapter);

		footerView = LayoutInflater.from(getActivity()).inflate(R.layout.loading, null, false);
		lView.addFooterView(footerView);

		lView.setOnScrollListener(new OnScrollListener() {

			/** 记录第一行Item的数值 */
			private int firstVisibleItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当滑动到底部时
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem != 0) {
					if (totalCount > rpList.size()) {
						new Thread() {
							public void run() {
								page++;
								resp = GetData
										.getHtml(UrlString.LiuPeng + "/HuoQuHongBaoMoneyList?xiaoquGuid=" + xiaoquGuid
												+ "&FangHaoGuid=" + FangHaoGuid + "&phone=" + phone + "&start=" + page);
								handler.sendEmptyMessage(6003);
							}
						}.start();
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
				} else if (totalCount > rpList.size()) {
					footerView.setVisibility(View.VISIBLE);
				} else {
					footerView.setVisibility(View.GONE);
				}
			}
		});

		return view;
	}
}
