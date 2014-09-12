package com.omnom.android.linker.model.table;

import com.google.gson.annotations.Expose;
import com.omnom.android.linker.model.ResponseBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 26.08.2014.
 */
public class RestaurantTablesResponse extends ResponseBase {
	@Expose
	private List<TableDataResponse> items = new ArrayList<TableDataResponse>();

	@Expose
	private Integer total;

	@Expose
	private Integer offset;

	@Expose
	private Integer limit;

	public List<TableDataResponse> getItems() {
		return items;
	}

	public void setItems(List<TableDataResponse> items) {
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
