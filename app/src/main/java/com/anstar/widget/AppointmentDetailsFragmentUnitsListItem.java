package com.anstar.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.anstar.fieldwork.R;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ServiceLocationsInfo;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentUnitsListItem extends LinearLayout{
    private final Context mContext;
    private final LinearLayout mTable;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {
        void onButtonAddClick();
    }

    public AppointmentDetailsFragmentUnitsListItem(Context context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_units_list_item, this);

        mTable = (LinearLayout) findViewById(R.id.linearLayoutList);
        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onButtonAddClick();
                }
            }
        });
    }

    public void init(AppointmentInfo appointmentInfo,
                            CustomerInfo customerInfo,
                            ServiceLocationsInfo serviceLocationInfo) {

/*
        TextView text = new TextView(mContext);
        text.setText("1");
        text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
        text.setTextColor(getResources().getColorStateList(android.R.color.black));

        mTable.addView(text);
*/
    }
}
