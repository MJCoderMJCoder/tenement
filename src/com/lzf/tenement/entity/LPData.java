package com.lzf.tenement.entity;

/**
 * ¥����Ϣ
 */
public class LPData {
	private String id; // ��ά��
	private String mxid;
	private String lpName; // ��ɭ������סլ¥
	private String dtName; // ��ɭ������סլ¥1��¥

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMxid() {
		return mxid;
	}

	public void setMxid(String mxid) {
		this.mxid = mxid;
	}

	public String getLpName() {
		return lpName;
	}

	public void setLpName(String lpName) {
		this.lpName = lpName;
	}

	public String getDtName() {
		return dtName;
	}

	public void setDtName(String dtName) {
		this.dtName = dtName;
	}

	public LPData() {
		super();
	}

	public LPData(String id, String mxid, String lpName, String dtName) {
		super();
		this.id = id;
		this.mxid = mxid;
		this.lpName = lpName;
		this.dtName = dtName;
	}

	// @Override
	// public String toString() {
	// return "{\"id\":\"" + id + "\",\"mxid\":\"" + mxid + "\",\"lpName\":\"" +
	// lpName + "\",\"dtName\":\"" + dtName
	// + "\"}";
	// }

}
