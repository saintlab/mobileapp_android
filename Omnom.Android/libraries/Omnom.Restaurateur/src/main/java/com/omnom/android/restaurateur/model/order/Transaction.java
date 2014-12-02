package com.omnom.android.restaurateur.model.order;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class Transaction {
	@Expose
	private int amount;

	@Expose
	private int tip;

	public int getTip() {
		return tip;
	}

	public void setTip(final int tip) {
		this.tip = tip;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}
}
