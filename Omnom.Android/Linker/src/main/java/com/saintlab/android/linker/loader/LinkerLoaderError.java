package com.saintlab.android.linker.loader;

import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.utils.StringUtils;
import com.saintlab.android.linker.R;

/**
 * Created by Ch3D on 16.10.2014.
 */
public class LinkerLoaderError extends LoaderError {
	public static final LoaderError NO_CONNECTION_BIND =
			new LinkerLoaderError(R.drawable.ic_no_connection, R.string.error_unknown_server_error, R.string.bind_table, 0);

	public static final LoaderError MAINTENANCE_DISABLED =
			new LinkerLoaderError(R.drawable.ic_maintenance_disabled, R.string.error_maintenance_mode_off, R.string.try_once_again,
			                      R.drawable.ic_repeat_small);

	public LinkerLoaderError(int drawableResId, int errResId, int btnResId, int btnDrawableId) {
		super(StringUtils.EMPTY_STRING, drawableResId, errResId, btnResId, btnDrawableId);
	}
}
