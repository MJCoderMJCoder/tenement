package com.lzf.tenement.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lzf.tenement.ChargeActivity;
import com.lzf.tenement.ConsultActivity;
import com.lzf.tenement.EvaluateManageActivity;
import com.lzf.tenement.FinishActivity;
import com.lzf.tenement.JoinUsActivity;
import com.lzf.tenement.PaiesActivity;
import com.lzf.tenement.PayActivity;
import com.lzf.tenement.R;
import com.lzf.tenement.RedPacketActivity;
import com.lzf.tenement.RepairBillActivity;
import com.lzf.tenement.RepairsActivity;
import com.lzf.tenement.RepairsCenterActivity;
import com.lzf.tenement.adapter.ReusableAdapter;
import com.lzf.tenement.bean.ItemLeft;
import com.lzf.tenement.http.GetData;
import com.lzf.tenement.util.UrlString;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentHome extends Fragment implements OnItemClickListener {

	private int width;
	private String serverResponse;

	private String xiaoquGuid;
	private String FangHaoGuid;
	private int RoleID;

	private final int HuoQuWuYeDianHua = 1;
	private final int IsWuYeFei = 2;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HuoQuWuYeDianHua:
				if (serverResponse.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject jObject = new JSONObject(serverResponse);
						if (jObject.getBoolean("success")) {
							Uri uri = Uri.parse("tel:" + jObject.getString("data"));
							Intent intent = new Intent(Intent.ACTION_DIAL, uri);
							startActivity(intent);
						} else {
							Toast.makeText(getActivity(), "��ȡ����ʧ�ܡ�", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						System.out.println("JSON���ݽ����쳣��" + e.getMessage());
					}
				}
				break;
			case IsWuYeFei:
				if (serverResponse.equals("fail")) {
					Toast.makeText(getActivity(), "���Ӳ������������������������Ժ����ԡ�", Toast.LENGTH_SHORT).show();
				} else {
					Intent pay = new Intent(getActivity(), PayActivity.class);
					pay.putExtra("jsonObject", serverResponse);
					startActivity(pay);
				}
				break;
			default:
				break;
			}
		}
	};

	public FragmentHome(int width) {
		this.width = width;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		xiaoquGuid = sp.getString("xiaoquGuid", "");
		FangHaoGuid = sp.getString("FangHaoGuid", "");
		RoleID = sp.getInt("RoleID", 2);

		// ��̬����ͼƬ�Ŀ�ȡ��߶ȣ�����Ӧ��Ļ��С��
		int height = (int) (width / 1.38);

		// ���������ItemLeftΪGridView��ItemGrid��Bean��
		GridView gView = (GridView) view.findViewById(R.id.gridView);
		List<ItemLeft> data = new ArrayList<ItemLeft>();
		if (RoleID == 2) {
			Bitmap bitmapRaw = BitmapFactory.decodeResource(getResources(), R.drawable.tiny_img);
			Bitmap bitmapOk = Bitmap.createScaledBitmap(bitmapRaw, width, height, true);
			ImageView iView = (ImageView) view.findViewById(R.id.imageCenter);
			iView.setImageBitmap(bitmapOk);
			data.add(new ItemLeft(R.drawable.repairs, "����")); // ԭ���Ǹ��˱���
			// data.add(new ItemLeft(R.drawable.public_repairs, "��������"));
			data.add(new ItemLeft(R.drawable.pay, "�ۺϽɷ�")); // ԭ������ҵ�ɷ�
			data.add(new ItemLeft(R.drawable.contact_tenemen, "��ϵ��ҵ"));
			data.add(new ItemLeft(R.drawable.online_advisory, "������ѯ"));
			data.add(new ItemLeft(R.drawable.red_packet, "���"));
		} else {
			Bitmap bitmapRaw = BitmapFactory.decodeResource(getResources(), R.drawable.banner);
			Bitmap bitmapOk = Bitmap.createScaledBitmap(bitmapRaw, width, height, true);
			ImageView iView = (ImageView) view.findViewById(R.id.imageCenter);
			iView.setImageBitmap(bitmapOk);
			if (RoleID == 0) {
				data.add(new ItemLeft(R.drawable.charge, "�շ�")); // ԭ���Ǹ��˱���
				data.add(new ItemLeft(R.drawable.repairs, "����")); // ԭ���Ǹ��˱���
				data.add(new ItemLeft(R.drawable.evaluated, "���۹���"));
				data.add(new ItemLeft(R.drawable.event_statistic, "�¼�ͳ��"));
				data.add(new ItemLeft(R.drawable.join_us, "��������"));
				data.add(new ItemLeft(R.drawable.red_packet, "���"));
			} else if (RoleID == 1) {
				data.add(new ItemLeft(R.drawable.repairs_center, "���޵�")); // ԭ���Ǹ��˱���
				data.add(new ItemLeft(R.drawable.finish, "�����"));
				data.add(new ItemLeft(R.drawable.red_packet, "���"));
			}
		}
		ReusableAdapter<ItemLeft> adapter = new ReusableAdapter<ItemLeft>(data, R.layout.item_grid) {
			@Override
			public void bindView(ViewHolder holder, ItemLeft obj) {
				holder.setImageResource(R.id.icon, obj.getIcon());
				holder.setText(R.id.text, obj.getText());
			}
		};
		gView.setAdapter(adapter);
		gView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (RoleID == 2) {
			switch (arg2) {
			case 0:
				Intent repairs = new Intent(getActivity(), RepairsActivity.class);
				startActivity(repairs);
				break;
			case 1:
				Intent paies = new Intent(getActivity(), PaiesActivity.class);
				startActivity(paies);
				break;
			case 2:
				new Thread() {
					public void run() {
						serverResponse = GetData.getHtml(UrlString.LiuPeng + "/HuoQuWuYeDianHua");
						handler.sendEmptyMessage(HuoQuWuYeDianHua);
					}
				}.start();
				break;
			case 3:
				Intent online = new Intent(getActivity(), ConsultActivity.class);
				startActivity(online);
				break;
			case 4:
				Intent redPacket = new Intent(getActivity(), RedPacketActivity.class);
				startActivity(redPacket);
				break;
			default:
				break;
			}
		} else if (RoleID == 1) {
			switch (arg2) {
			case 0:
				Intent repairBill = new Intent(getActivity(), RepairBillActivity.class);
				startActivity(repairBill);
				break;
			case 1:
				Intent finish = new Intent(getActivity(), FinishActivity.class);
				startActivity(finish);
				break;
			case 2:
				Intent redPacket = new Intent(getActivity(), RedPacketActivity.class);
				startActivity(redPacket);
				break;
			default:
				break;
			}
		} else if (RoleID == 0) {
			switch (arg2) {
			case 0:
				Intent charge = new Intent(getActivity(), ChargeActivity.class);
				startActivity(charge);
				break;
			case 1:
				Intent repairs = new Intent(getActivity(), RepairsCenterActivity.class);
				startActivity(repairs);
				break;
			case 2:
				Intent evaluateManage = new Intent(getActivity(), EvaluateManageActivity.class);
				evaluateManage.putExtra("distinguish", "���۹���");
				startActivity(evaluateManage);
				break;
			case 3:
				// �¼�ͳ����ʱ�������۹����������ʾ
				Intent eventSatisfaction = new Intent(getActivity(), EvaluateManageActivity.class);
				eventSatisfaction.putExtra("distinguish", "�¼�ͳ��");
				startActivity(eventSatisfaction);
				break;
			case 4:
				Intent intent = new Intent(getActivity(), JoinUsActivity.class);
				startActivity(intent);
				break;
			case 5:
				Intent redPacket = new Intent(getActivity(), RedPacketActivity.class);
				startActivity(redPacket);
				break;
			default:
				break;
			}
		}
	}

}
