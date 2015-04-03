package com.omnom.android.utils.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.utils.R;

public final class DialogUtils {

    private DialogUtils() {
        throw new UnsupportedOperationException("Unable to create an instance of static class");
    }

    public static AlertDialog showDialog(Context context, int msg, int okResId, DialogInterface.OnClickListener okListener,
                                         int cancelResId, DialogInterface.OnClickListener cancelListener) {
        return showDialog(context, context.getString(msg), okResId, okListener, cancelResId, cancelListener);
    }

    public static AlertDialog showDialog(Context context, String msg, int okResId, DialogInterface.OnClickListener okListener,
                                         int cancelResId, DialogInterface.OnClickListener cancelListener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(okResId,
                okListener)
                .setNegativeButton(cancelResId, cancelListener).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showDialog(Context context, int msg, int okResId,
                                         DialogInterface.OnClickListener okListener) {
	    return showDialog(context, context.getString(msg), okResId, okListener);
    }

	public static AlertDialog showDialog(Context context, String msg, int okResId,
	                                     DialogInterface.OnClickListener okListener) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setMessage(msg)
				.setPositiveButton(okResId, okListener)
				.create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
		return alertDialog;
	}

    public static AlertDialog showDialog(Context context, int titleResId, String msg,
                                         int okResId, DialogInterface.OnClickListener okListener,
                                         int cancelResId, DialogInterface.OnClickListener cancelListener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(msg)
                .setPositiveButton(okResId, okListener)
                .setNegativeButton(cancelResId, cancelListener)
                .create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showDeleteDialog(final Context context,
                                               final String title,
                                               final DialogInterface.OnClickListener removeListener) {
        final AlertDialog alertDialog = DialogUtils.showDialog(context, title,
                R.string.delete, removeListener, R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setCanceledOnTouchOutside(true);
        final float btnTextSize = context.getResources().getDimension(R.dimen.font_normal);
        final Button btn1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btn1.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
        final Button btn2 = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn2.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
        TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
        Button removeCardButton = (Button) alertDialog.findViewById(android.R.id.button1);
        removeCardButton.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        Button cancelButton = (Button) alertDialog.findViewById(android.R.id.button2);
        cancelButton.setTextColor(context.getResources().getColor(R.color.cancel_button));

        return alertDialog;
    }

}
