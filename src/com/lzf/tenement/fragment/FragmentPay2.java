package com.lzf.tenement.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.R;
import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.adapter.ReusableAdapter.ViewHolder;
import com.lzf.tenement.entity.TenementFee;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPay2 extends Fragment {

	private String xiaoquGuid;
	private String FangHaoGuid;
	private String resp;
	private List<TenementFee> listData;

	private ListView listView;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (resp.equals("fail")) {
				Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试。", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jObject = new JSONObject(resp);
					if (jObject.getBoolean("success")) {
						listData = new ArrayList<TenementFee>();
						JSONArray jsonArray = jObject.getJSONArray("data");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							listData.add(new TenementFee(jsonObject.getString("id"),
									jsonObject.getString("JiaoFeiNianFen"), jsonObject.getString("JiaoFeiJinE"),
									jsonObject.getString("JiaoFeiGongShi")));
						}
						showListView(listData);
					} else {
						Toast.makeText(getActivity(), "获取数据失败。", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					System.out.println("JSON数据解析异常：" + e.getMessage());
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pay2, container, false);
		TextView owner = (TextView) view.findViewById(R.id.owner);
		listView = (ListView) view.findViewById(R.id.paiedList);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		xiaoquGuid = sp.getString("xiaoquGuid", "");
		FangHaoGuid = sp.getString("FangHaoGuid", "");
		owner.setText(sp.getString("xiaoquName", "") + sp.getString("FangHaoName", ""));

		new Thread() {
			public void run() {
				resp = GetData.getHtml(
						UrlString.LiuPeng + "/WuYeFeiList?XiaoQuGrid=" + xiaoquGuid + "&FangHaoGrid=" + FangHaoGuid);
				handler.sendEmptyMessage(6003);
			}
		}.start();

		return view;
	}

	private void showListView(List<TenementFee> listData) {
		ReusableAdapter<TenementFee> adapter = new ReusableAdapter<TenementFee>(listData, R.layout.item_pay) {
			@Override
			public void bindView(ViewHolder holder, TenementFee obj) {
				holder.setText(R.id.year, obj.getJiaoFeiNianFen() + "年");
				holder.setText(R.id.company, obj.getJiaoFeiGongShi());
				holder.setText(R.id.money, obj.getJiaoFeiJinE() + "元");
			}
		};
		listView.setAdapter(adapter);
	}
}
