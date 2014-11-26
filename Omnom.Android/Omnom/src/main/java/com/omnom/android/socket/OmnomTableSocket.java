package com.omnom.android.socket;

import android.content.Context;

import com.omnom.android.restaurateur.model.table.TableDataResponse;

import java.net.URISyntaxException;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class OmnomTableSocket extends OmnomSocketBase {
	private final TableDataResponse mTable;

	protected OmnomTableSocket(final Context context, final TableDataResponse table, final String url) throws URISyntaxException {
		super(context, url);
		mTable = table;
	}
}
