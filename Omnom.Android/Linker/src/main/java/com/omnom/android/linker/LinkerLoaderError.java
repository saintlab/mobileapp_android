package com.omnom.android.linker;

import com.omnom.android.utils.LoaderError;

/**
 * Created by Ch3D on 16.10.2014.
 */
public class LinkerLoaderError extends LoaderError {
	public static final LoaderError NO_CONNECTION_BIND = new LinkerLoaderError(R.drawable.ic_no_connection,
	                                                                           R.string.error_unknown_server_error,
	                                                                           R.string.bind_table);


	public static final LoaderError MAINTENANCE_DISABLED = new LinkerLoaderError(R.drawable.ic_maintenance_disabled,
	                                                                             R.string.error_maintenance_mode_off,
	                                                                             R.string.try_once_again);

	public LinkerLoaderError(int drawableResId, int errResId, int btnResId) {
		super(drawableResId, errResId, btnResId);
	}
}
