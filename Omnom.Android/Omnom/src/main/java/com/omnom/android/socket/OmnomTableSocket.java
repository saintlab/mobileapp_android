package com.omnom.android.socket;

import android.content.Context;

import com.omnom.android.restaurateur.model.table.TableDataResponse;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OmnomTableSocket extends OmnomSocketBase {
	private final String mTableId;

	protected OmnomTableSocket(final Context context, final TableDataResponse table) {
		super(context);
		mTableId = table.getId();
	}

	protected OmnomTableSocket(final Context context, final String tableId) {
		super(context);
		mTableId = tableId;
	}

	@Override
	protected String getRoomId() {
		return mTableId;
	}
}
