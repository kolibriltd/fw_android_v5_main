package com.anstar.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anstar.fieldwork.R;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.list.AppointmentModelList;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentServiceInstructionsListItem extends LinearLayout{
    private final Context mContext;
    private final LinearLayout mTable;
    private AppointmentInfo mAppointmentInfo;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {
        void onButtonAddClick();
    }

    public AppointmentDetailsFragmentServiceInstructionsListItem(Context context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_service_instructions_list_item, this);

        mTable = (LinearLayout) findViewById(R.id.linearLayoutList);
    }

    public void init(int appointmentId) {

        mAppointmentInfo = AppointmentModelList.Instance().getAppointmentById(appointmentId);

        TextView text = new TextView(mContext);
        text.setText(mAppointmentInfo.instructions);
        text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
        text.setTextColor(getResources().getColorStateList(android.R.color.black));

        mTable.addView(text);
    }
}
