package com.omnom.android.restaurateur.model.order;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class TipsValue {
	@Expose
	private Integer amount;

	@Expose
	private Integer percent;

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}
}
