package com.anstar.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anstar.fieldwork.R;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.list.AppointmentModelList;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentNotesListItem extends LinearLayout{
    private final Activity mContext;
    private final TextView mTextViewPublicEmpty;
    private final TextView mTextViewPrivateEmpty;
    private AppointmentInfo mAppointmentInfo;
    private int mAppointmentId;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {

        void onPublicNotesClick(int appointmentId);
        void onPrivateNotesClick(int appointmentId);
    }

    public AppointmentDetailsFragmentNotesListItem(Activity context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_notes_list_item, this);

        mTextViewPublicEmpty = (TextView) findViewById(R.id.textViewPublicNotesEmpty);
/*
        mTextViewPublicEmpty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPublicNotesClick(mAppointmentId);
                }
            }
        });
*/
        mTextViewPrivateEmpty = (TextView) findViewById(R.id.textViewPrivateNotesEmpty);
/*
        mTextViewPrivateEmpty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPrivateNotesClick(mAppointmentId);
                }
            }
        });
*/

        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    showLoginDialog(mContext);
                }
            }
        });
    }

    public void init(int appointmentId) {

        mAppointmentId = appointmentId;
        mAppointmentInfo = AppointmentModelList.Instance().getAppointmentById(appointmentId);

        mTextViewPublicEmpty.setText(mAppointmentInfo.notes);
        mTextViewPrivateEmpty.setText(mAppointmentInfo.private_notes);
    }

    private void showLoginDialog(Activity context) {
        // layout and inflater
        LayoutInflater inflater = context.getLayoutInflater();
        View content = inflater.inflate(R.layout.dialog_notes_choice, null);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setView(content);

        Button button = (Button) content.findViewById(R.id.buttonPublicNotes);

        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing, just allow dialog to close
            }
        });

        final AlertDialog alertDialog = dialog.create();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onPublicNotesClick(mAppointmentId);
                alertDialog.dismiss();
            }
        });

        button = (Button) content.findViewById(R.id.buttonPrivateNotes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onPrivateNotesClick(mAppointmentId);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
