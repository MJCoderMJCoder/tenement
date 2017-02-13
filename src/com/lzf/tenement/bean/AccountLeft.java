package com.lzf.tenement.bean;

/**
 * acount_left布局下的临时前端bean类
 */
public class AccountLeft {
	private int portrait;
	private String nickname;
	private String account;

	public int getPortrait() {
		return portrait;
	}

	public void setPortrait(int portrait) {
		this.portrait = portrait;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public AccountLeft(int portrait, String nickname, String account) {
		super();
		this.portrait = portrait;
		this.nickname = nickname;
		this.account = account;
	}

	public AccountLeft() {
		super();
	}

}
