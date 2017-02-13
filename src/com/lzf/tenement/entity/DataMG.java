package com.lzf.tenement.entity;

/**
 * ±¨ÐÞÀà±ð
 */
public class DataMG {
	private int Did;
	private String Dname;
	private int DBelongedTo;

	public int getDid() {
		return Did;
	}

	public void setDid(int did) {
		Did = did;
	}

	public String getDname() {
		return Dname;
	}

	public void setDname(String dname) {
		Dname = dname;
	}

	public int getDBelongedTo() {
		return DBelongedTo;
	}

	public void setDBelongedTo(int dBelongedTo) {
		DBelongedTo = dBelongedTo;
	}

	public DataMG(int did, String dname, int dBelongedTo) {
		super();
		Did = did;
		Dname = dname;
		DBelongedTo = dBelongedTo;
	}

	public DataMG() {
		super();
	}

}
