package com.anstar.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class ConfirmDialog extends DialogFragment {

    public static ConfirmDialog newInstance(String message) {
        ConfirmDialog frag = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }
    
	public interface OnConfirmDialogListener {
		public void onDialogConfirm(String tag);
		public void onDialogCancel(String tag);
	}
   
    public ConfirmDialog() {
    	// Empty constructor required 
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");

        return 	new AlertDialog.Builder(getActivity())
        		.setCancelable(true)
        		.setMessage(message)
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Fragment fragment = getTargetFragment();
								if (fragment != null
										&& fragment instanceof OnConfirmDialogListener) {
									((OnConfirmDialogListener) fragment)
											.onDialogCancel(getTag());
								}
								Activity activity = getActivity();
								if (activity instanceof OnConfirmDialogListener) {
									((OnConfirmDialogListener) activity)
											.onDialogCancel(getTag());
								}
							}
						})
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Fragment fragment = getTargetFragment();
								if (fragment != null
										&& fragment instanceof OnConfirmDialogListener) {
									((OnConfirmDialogListener) fragment)
											.onDialogConfirm(getTag());
								}
								Activity activity = getActivity();
								if (activity instanceof OnConfirmDialogListener) {
									((OnConfirmDialogListener) activity)
											.onDialogConfirm(getTag());
								}
							}
						}).create();
    }
}
