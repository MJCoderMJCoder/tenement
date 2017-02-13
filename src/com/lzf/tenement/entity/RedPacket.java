package com.lzf.tenement.entity;

public class RedPacket {

	private String danwei;
	private String shijian;
	private String Money;

	public String getDanwei() {
		return danwei;
	}

	public void setDanwei(String danwei) {
		this.danwei = danwei;
	}

	public String getShijian() {
		return shijian;
	}

	public void setShijian(String shijian) {
		this.shijian = shijian;
	}

	public String getMoney() {
		return Money;
	}

	public void setMoney(String money) {
		Money = money;
	}

	public RedPacket() {
	}

	public RedPacket(String danwei, String shijian, String money) {
		super();
		this.danwei = danwei;
		this.shijian = shijian;
		Money = money;
	}

}
