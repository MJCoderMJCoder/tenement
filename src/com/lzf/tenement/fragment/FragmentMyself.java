package com.lzf.tenement.fragment;

import com.lzf.tenement.LoginActivity;
import com.lzf.tenement.ModifyPwActivity;
import com.lzf.tenement.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.view.ViewGroup;

public class FragmentMyself extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_myself, container, false);

		view.findViewById(R.id.modify).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), ModifyPwActivity.class);
				startActivity(intent);
			}
		});

		// �˳���¼
		view.findViewById(R.id.exit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});

		// ��ʼ���˺�������
		SharedPreferences sp = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		((TextView) (view.findViewById(R.id.nickname))).setText(sp.getString("xingming", "�ƴ�Ƽ�"));
		((TextView) (view.findViewById(R.id.account))).setText(sp.getString("phone", "fdkj@123??"));
		((TextView) (view.findViewById(R.id.communityValue))).setText(sp.getString("xiaoquName", "fdkj@123??"));
		((TextView) (view.findViewById(R.id.addressValue))).setText(sp.getString("FangHaoName", "fdkj@123??"));
		((TextView) (view.findViewById(R.id.versionNo))).setText("" + getVersionCode());
		return view;

	}

	/*
	 * ��ȡ��ǰ����İ汾��
	 */
	private String getVersionCode() {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = getActivity().getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo.versionName;
	}
}
