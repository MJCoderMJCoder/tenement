package com.lzf.tenement.entity;

import java.io.Serializable;

public class Repair implements Serializable {
	private String id;
	private String lxa;
	private String lxb;
	private String content;
	private String state;
	private String shijian;
	private String zt;
	private String fpys;
	private String pj;
	private String pjs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLxa() {
		return lxa;
	}

	public void setLxa(String lxa) {
		this.lxa = lxa;
	}

	public String getLxb() {
		return lxb;
	}

	public void setLxb(String lxb) {
		this.lxb = lxb;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getShijian() {
		return shijian;
	}

	public void setShijian(String shijian) {
		this.shijian = shijian;
	}

	public String getZt() {
		return zt;
	}

	public void setZt(String zt) {
		this.zt = zt;
	}

	public String getFpys() {
		return fpys;
	}

	public void setFpys(String fpys) {
		this.fpys = fpys;
	}

	public String getPj() {
		return pj;
	}

	public void setPj(String pj) {
		this.pj = pj;
	}

	public String getPjs() {
		return pjs;
	}

	public void setPjs(String pjs) {
		this.pjs = pjs;
	}

	public Repair() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Repair(String id, String lxa, String lxb, String content, String state, String shijian, String zt,
			String fpys, String pj, String pjs) {
		super();
		this.id = id;
		this.lxa = lxa;
		this.lxb = lxb;
		this.content = content;
		this.state = state;
		this.shijian = shijian;
		this.zt = zt;
		this.fpys = fpys;
		this.pj = pj;
		this.pjs = pjs;
	}

	@Override
	public String toString() {
		return "Repair [id=" + id + ", lxa=" + lxa + ", lxb=" + lxb + ", content=" + content + ", state=" + state
				+ ", shijian=" + shijian + ", zt=" + zt + ", fpys=" + fpys + ", pj=" + pj + ", pjs=" + pjs + "]";
	}

}
