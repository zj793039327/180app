package com.example.neo.app.commons.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * IgnitedDialogs
 * 
 * @author zj
 * @since 2014-8-10 1.0
 */
public class IgnitedDialogs {
	  /**
     * Builds a new AlertDialog to display a simple message
     * 
     * @param context
     * @param title
     * @param message
     * @param iconId
     * @return
     */
    public static AlertDialog.Builder newMessageDialog(final Context context, String title,
            String message, int iconId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(android.R.string.ok), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(title);
        builder.setMessage(message);
		builder.setIcon(iconId);

        return builder;
    }
}
