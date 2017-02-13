package com.lzf.tenement.entity;

public class TenementFee {
	private String id;
	private String JiaoFeiNianFen;
	private String JiaoFeiJinE;
	private String JiaoFeiGongShi;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJiaoFeiNianFen() {
		return JiaoFeiNianFen;
	}

	public void setJiaoFeiNianFen(String jiaoFeiNianFen) {
		JiaoFeiNianFen = jiaoFeiNianFen;
	}

	public String getJiaoFeiJinE() {
		return JiaoFeiJinE;
	}

	public void setJiaoFeiJinE(String jiaoFeiJinE) {
		JiaoFeiJinE = jiaoFeiJinE;
	}

	public String getJiaoFeiGongShi() {
		return JiaoFeiGongShi;
	}

	public void setJiaoFeiGongShi(String jiaoFeiGongShi) {
		JiaoFeiGongShi = jiaoFeiGongShi;
	}

	public TenementFee(String id, String jiaoFeiNianFen, String jiaoFeiJinE, String jiaoFeiGongShi) {
		super();
		this.id = id;
		JiaoFeiNianFen = jiaoFeiNianFen;
		JiaoFeiJinE = jiaoFeiJinE;
		JiaoFeiGongShi = jiaoFeiGongShi;
	}

}
