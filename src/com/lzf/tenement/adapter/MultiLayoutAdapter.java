package com.lzf.tenement.adapter;

import java.util.List;

import com.lzf.tenement.R;
import com.lzf.tenement.bean.AccountLeft;
import com.lzf.tenement.bean.ItemLeft;
import com.lzf.tenement.bean.ItemLeftFirst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MultiLayoutAdapter extends BaseAdapter {

	// 定义类别标志（区分类别的标志要从0开始算）
	private static final int ACCOUNT_LEFT = 0;
	private static final int ITEM_LEFT = 1;
	private static final int ITEM_LEFT_FIRST = 2;
	private Context context;
	private List<Object> data = null;

	public MultiLayoutAdapter(Context context, List<Object> data) {
		super();
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// 多布局的核心：判断类别
	@Override
	public int getItemViewType(int position) {

		if (data.get(position) instanceof AccountLeft) {
			return ACCOUNT_LEFT;
		} else if (data.get(position) instanceof ItemLeft) {
			return ITEM_LEFT;
		} else if (data.get(position) instanceof ItemLeftFirst) {
			return ITEM_LEFT_FIRST;
		} else {
			return super.getItemViewType(position);
		}
	}

	// 类别数目
	@Override
	public int getViewTypeCount() {
		return 3;
	}

	// 三个不同的ViewHolder
	private static class ViewHolderItem {
		ImageView icon;
		TextView text;
	}

	private static class ViewHolderItemFirst {
		ImageView icon;
		TextView text;
	}

	private static class ViewHolderAccount {
		ImageView portrait;
		TextView nickname;
		TextView account;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolderItem holderItem = null;
		ViewHolderItemFirst holderItemFirst = null;
		ViewHolderAccount holderAccount = null;
		if (convertView == null) {
			switch (type) {
			case ITEM_LEFT_FIRST:
				holderItemFirst = new ViewHolderItemFirst();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_left_first, parent, false);
				holderItemFirst.icon = (ImageView) convertView.findViewById(R.id.icon);
				holderItemFirst.text = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(R.id.Tag_Item_First, holderItemFirst);
				break;
			case ITEM_LEFT:
				holderItem = new ViewHolderItem();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_left, parent, false);
				holderItem.icon = (ImageView) convertView.findViewById(R.id.icon);
				holderItem.text = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(R.id.Tag_Item, holderItem);
				break;
			case ACCOUNT_LEFT:
				holderAccount = new ViewHolderAccount();
				convertView = LayoutInflater.from(context).inflate(R.layout.account_left, parent, false);
				holderAccount.portrait = (ImageView) convertView.findViewById(R.id.portrait);
				holderAccount.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holderAccount.account = (TextView) convertView.findViewById(R.id.account);
				convertView.setTag(R.id.Tag_Account, holderAccount);
				break;
			}
		} else {
			switch (type) {
			case ITEM_LEFT_FIRST:
				holderItemFirst = (ViewHolderItemFirst) convertView.getTag(R.id.Tag_Item_First);
				break;
			case ITEM_LEFT:
				holderItem = (ViewHolderItem) convertView.getTag(R.id.Tag_Item);
				break;
			case ACCOUNT_LEFT:
				holderAccount = (ViewHolderAccount) convertView.getTag(R.id.Tag_Account);
				break;
			}
		}

		Object obj = data.get(position);
		// 设置下控件的值
		switch (type) {
		case ITEM_LEFT_FIRST:
			ItemLeftFirst itemLeftFirst = (ItemLeftFirst) obj;
			if (itemLeftFirst != null) {
				holderItemFirst.icon.setImageResource(itemLeftFirst.getIcon());
				holderItemFirst.text.setText(itemLeftFirst.getText());
			}
			break;
		case ITEM_LEFT:
			ItemLeft itemLeft = (ItemLeft) obj;
			if (itemLeft != null) {
				holderItem.icon.setImageResource(itemLeft.getIcon());
				holderItem.text.setText(itemLeft.getText());
			}
			break;
		case ACCOUNT_LEFT:
			AccountLeft accountLeft = (AccountLeft) obj;
			if (accountLeft != null) {
				holderAccount.portrait.setImageResource(accountLeft.getPortrait());
				holderAccount.nickname.setText(accountLeft.getNickname());
				holderAccount.account.setText(accountLeft.getAccount());
			}
			break;
		}
		return convertView;
	}

}
