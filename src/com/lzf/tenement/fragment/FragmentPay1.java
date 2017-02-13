package com.lzf.tenement.fragment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.R;
import com.lzf.tenement.http.OKHttp;
import com.lzf.tenement.http.PostData;
import com.lzf.tenement.util.UrlString;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPay1 extends Fragment {

	private String money;
	private String company;

	private String resp;

	private Map<String, String> params = new HashMap<String, String>();

	private JSONObject wuYeFei;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (resp.contains("fail")) {
				Toast.makeText(getActivity(), "连接不到服务器，请检查你的网络或稍后重试", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jsonObject = new JSONObject(resp);
					if (jsonObject.getBoolean("success")) {
						Toast.makeText(getActivity(), "物业缴费成功", Toast.LENGTH_SHORT).show();

						getActivity().onBackPressed();
					} else {
						Toast.makeText(getActivity(), "物业缴费过程中发现错误，请重新提交", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	};

	public FragmentPay1(JSONObject wuYeFei) {
		this.wuYeFei = wuYeFei;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pay1, container, false);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		params.put("XiaoQuGrid", sp.getString("xiaoquGuid", ""));
		params.put("FangHaoGrid", sp.getString("FangHaoGuid", ""));
		params.put("Phone", sp.getString("phone", ""));

		TextView tView = (TextView) view.findViewById(R.id.textView1);
		TextView unit = (TextView) view.findViewById(R.id.unitValue);
		TextView address = (TextView) view.findViewById(R.id.addressValue);
		TextView total = (TextView) view.findViewById(R.id.totalValue);

		try {
			money = wuYeFei.getString("Wuyefei");
			company = wuYeFei.getString("Jiaofeidanwei");
			address.setText(wuYeFei.getString("Dizhi"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		tView.setText(money);
		total.setText(money);
		unit.setText(company);

		view.findViewById(R.id.payBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				params.put("JiaoFeiJinE", money);
				params.put("JiaoFeiGongShi", company);
				new Thread() {
					public void run() {
						resp = OKHttp.uploadFiles(UrlString.LiuPeng + "/WuYeFeiAction", params,
								new HashMap<String, File>());
						handler.sendEmptyMessage(6003);
					}
				}.start();

			}
		});
		return view;
	}
}
