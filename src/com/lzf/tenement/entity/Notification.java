package com.lzf.tenement.entity;

import java.io.Serializable;

public class Notification implements Serializable {
	private String id;
	private String BiaoTi;
	private String ShiJian;
	private String NeiRong;
	private String FaBuRen;
	private String FaBuWuYe;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBiaoTi() {
		return BiaoTi;
	}

	public void setBiaoTi(String biaoTi) {
		BiaoTi = biaoTi;
	}

	public String getShiJian() {
		return ShiJian;
	}

	public void setShiJian(String shiJian) {
		ShiJian = shiJian;
	}

	public String getNeiRong() {
		return NeiRong;
	}

	public void setNeiRong(String neiRong) {
		NeiRong = neiRong;
	}

	public String getFaBuRen() {
		return FaBuRen;
	}

	public void setFaBuRen(String faBuRen) {
		FaBuRen = faBuRen;
	}

	public String getFaBuWuYe() {
		return FaBuWuYe;
	}

	public void setFaBuWuYe(String faBuWuYe) {
		FaBuWuYe = faBuWuYe;
	}

	public Notification() {
	}

	public Notification(String id, String biaoTi, String shiJian, String neiRong, String faBuRen, String faBuWuYe) {
		super();
		this.id = id;
		BiaoTi = biaoTi;
		ShiJian = shiJian;
		NeiRong = neiRong;
		FaBuRen = faBuRen;
		FaBuWuYe = faBuWuYe;
	}

}
