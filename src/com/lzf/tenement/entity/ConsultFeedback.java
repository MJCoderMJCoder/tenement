package com.lzf.tenement.entity;

import java.io.Serializable;
import java.util.List;

public class ConsultFeedback implements Serializable {
	private String id;
	private String MiaoShu;
	private String AddDate;
	private String ischakan;
	private List<String> tp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMiaoShu() {
		return MiaoShu;
	}

	public void setMiaoShu(String miaoShu) {
		MiaoShu = miaoShu;
	}

	public String getAddDate() {
		return AddDate;
	}

	public void setAddDate(String addDate) {
		AddDate = addDate;
	}

	public String getIschakan() {
		return ischakan;
	}

	public void setIschakan(String ischakan) {
		this.ischakan = ischakan;
	}

	public List<String> getTp() {
		return tp;
	}

	public void setTp(List<String> tp) {
		this.tp = tp;
	}

	public ConsultFeedback(String id, String miaoShu, String addDate, String ischakan, List<String> tp) {
		super();
		this.id = id;
		MiaoShu = miaoShu;
		AddDate = addDate;
		this.ischakan = ischakan;
		this.tp = tp;
	}
}
