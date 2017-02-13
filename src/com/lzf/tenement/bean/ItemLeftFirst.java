package com.lzf.tenement.bean;

/**
 * item_left布局下的临时前端bean类
 */
public class ItemLeftFirst {
	private int icon;
	private String text;

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ItemLeftFirst(int icon, String text) {
		super();
		this.icon = icon;
		this.text = text;
	}

	public ItemLeftFirst() {
		super();
	}

}
