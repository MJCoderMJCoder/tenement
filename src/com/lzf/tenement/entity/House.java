package com.lzf.tenement.entity;

public class House {
	private String fh;// 房号：1栋2单元5层04房间==1栋2单元5层04房间
	private String FwmxID; // 主键：F8C7F454-E21F-42EC-8AE2-B5D081FAAE73
	private String dongText; // 1栋
	private String dongValue; // 6C310D2F-2A4E-415A-B046-D433F32002DB

	public String getFh() {
		return fh;
	}

	public void setFh(String fh) {
		this.fh = fh;
	}

	public String getFwmxID() {
		return FwmxID;
	}

	public void setFwmxID(String fwmxID) {
		FwmxID = fwmxID;
	}

	public String getDongText() {
		return dongText;
	}

	public void setDongText(String dongText) {
		this.dongText = dongText;
	}

	public String getDongValue() {
		return dongValue;
	}

	public void setDongValue(String dongValue) {
		this.dongValue = dongValue;
	}

	public House(String fh, String fwmxID, String dongText, String dongValue) {
		super();
		this.fh = fh;
		FwmxID = fwmxID;
		this.dongText = dongText;
		this.dongValue = dongValue;
	}

	public House() {
		super();
	}

	@Override
	public String toString() {
		return "House [fh=" + fh + ", FwmxID=" + FwmxID + ", dongText=" + dongText + ", dongValue=" + dongValue + "]";
	}

}
