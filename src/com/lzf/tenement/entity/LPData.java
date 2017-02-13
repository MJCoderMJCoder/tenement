package com.lzf.tenement.entity;

/**
 * Â¥ÅÌÐÅÏ¢
 */
public class LPData {
	private String id; // ¶þÎ¬Âë
	private String mxid;
	private String lpName; // ÌïÉ­°²Äþ½Ö×¡Õ¬Â¥
	private String dtName; // ÌïÉ­°²Äþ½Ö×¡Õ¬Â¥1ºÅÂ¥

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
