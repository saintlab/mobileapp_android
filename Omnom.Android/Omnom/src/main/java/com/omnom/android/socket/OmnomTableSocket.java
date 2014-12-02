package com.omnom.android.socket;

import android.content.Context;

import com.omnom.android.restaurateur.model.table.TableDataResponse;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OmnomTableSocket extends OmnomSocketBase {
	private final TableDataResponse mTable;

	protected OmnomTableSocket(final Context context, final TableDataResponse table) {
		super(context);
		mTable = table;
	}

	@Override
	protected String getRoomId() {
		return mTable.getId();
	}
}
