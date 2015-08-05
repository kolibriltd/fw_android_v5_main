package com.anstar.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.anstar.fieldwork.R;

/**
 * Created by oleg on 19.07.15.
 */
public class ProgressDialog extends Dialog {
    private final Context mContext;
    private static ProgressDialog mProgressDialog = null;

    public ProgressDialog(Activity context) {
        super(context, R.style.TransparentProgressDialog);
        mContext = context;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progressdialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progressdialog);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    void setMessage(String message) {
        TextView msg = (TextView) findViewById(R.id.textViewMessage);
        msg.setText(message);
    }

    public static void showProgress(Activity context) {
        showProgress(context, "Please wait...");
    }

    public static void showProgress(Activity context, String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mProgressDialog.setMessage(msg);
    }

    public static void hideProgress() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }
}
