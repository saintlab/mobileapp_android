package com.omnom.android.restaurateur.model.order;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrderTips {
	@Expose
	private Integer threshold;
	@Expose
	private List<TipsValue> values = new ArrayList<TipsValue>();

	public Integer getThreshold() {
		return threshold;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

	public List<TipsValue> getValues() {
		return values;
	}

	public void setValues(List<TipsValue> values) {
		this.values = values;
	}

}
