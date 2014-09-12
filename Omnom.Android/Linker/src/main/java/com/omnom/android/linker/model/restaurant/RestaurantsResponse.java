package com.omnom.android.linker.model.restaurant;

import com.google.gson.annotations.Expose;
import com.omnom.android.linker.model.ResponseBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class RestaurantsResponse extends ResponseBase {
	@Expose
	private List<Restaurant> items = new ArrayList<Restaurant>();

	@Expose
	private Integer total;

	@Expose
	private Integer offset;

	@Expose
	private Integer limit;

	public List<Restaurant> getItems() {
		return items;
	}

	public void setItems(List<Restaurant> items) {
		this.items = items;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
