package com.omnom.android.restaurateur.model.table;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class DemoTableResult extends ResponseBase {
	@Expose
	public DemoTableData[] data;
}
