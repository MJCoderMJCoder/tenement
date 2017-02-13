package com.lzf.tenement;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.R;
import com.lzf.tenement.fragment.FragmentPay0;
import com.lzf.tenement.fragment.FragmentPay1;
import com.lzf.tenement.fragment.FragmentPay2;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PayActivity extends Activity {

	private FragmentPay0 fPay0;
	private FragmentPay1 fPay1;
	private FragmentPay2 fPay2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);

		Intent intent = getIntent();
		((TextView) findViewById(R.id.textTitle)).setText(intent.getStringExtra("title"));

		FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
		try {
			JSONObject jsonObject = new JSONObject(intent.getStringExtra("jsonObject"));
			if (jsonObject.getBoolean("success")) {
				hideAllFragmen(fTransaction);
				fPay1 = new FragmentPay1(jsonObject.getJSONObject("data"));
				fTransaction.replace(R.id.payContent, fPay1);
			} else {
				hideAllFragmen(fTransaction);
				fPay2 = new FragmentPay2();
				fTransaction.replace(R.id.payContent, fPay2);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		fTransaction.commit();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	public void back(View view) {
		onBackPressed();
	}

	private void hideAllFragmen(FragmentTransaction fTransaction) {
		System.out.println(fTransaction + "£º\t0:" + fPay0 + "£º\t1:" + fPay1 + "£º\t2:" + fPay2);
		if (fPay0 != null) {
			fTransaction.hide(fPay0);
		}
		if (fPay1 != null) {
			fTransaction.hide(fPay1);
		}
		if (fPay2 != null) {
			fTransaction.hide(fPay2);
		}
	}
}
