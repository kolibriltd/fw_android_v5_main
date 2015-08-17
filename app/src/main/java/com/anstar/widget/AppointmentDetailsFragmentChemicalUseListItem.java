package com.anstar.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anstar.fieldwork.R;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.list.MaterialUsagesList;

import java.util.ArrayList;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentChemicalUseListItem extends LinearLayout{
    private final Context mContext;
    private final LinearLayout mTable;
    private ArrayList<MaterialUsage> mList;
    private int mAppointmentId;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {
        void onButtonAddClick(int appointmentId);
    }

    public AppointmentDetailsFragmentChemicalUseListItem(Context context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_chemical_use_list_item, this);

        mTable = (LinearLayout) findViewById(R.id.linearLayoutList);
        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onButtonAddClick(mAppointmentId);
                }
            }
        });
    }

    public void init(int appointmentId) {

        mTable.removeAllViews();

        mAppointmentId = appointmentId;
        mList = MaterialUsagesList.Instance().load(appointmentId);

        for (MaterialUsage usage : mList) {
            TextView text = new TextView(mContext);
            text.setText(MaterialInfo.getMaterialNamebyId(usage.material_id));
            text.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
            text.setTextColor(getResources().getColorStateList(android.R.color.black));
            mTable.addView(text);
        }
    }
}
