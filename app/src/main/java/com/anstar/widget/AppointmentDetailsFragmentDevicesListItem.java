package com.anstar.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anstar.fieldwork.R;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.models.list.TrapList;

import java.util.ArrayList;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentDevicesListItem extends LinearLayout{
    private final Context mContext;
    private final TextView mTextTotal;
    private final TextView mTextScanned;
    private final TextView mTextUnscanned;
    private int mAppointmentId;

    public void setOnListItemInteractionListener(OnListItemInteractionListener listener) {
        mListener = listener;
    }

    private OnListItemInteractionListener mListener;

    public interface OnListItemInteractionListener {
        void onButtonAddClick(int appointmentId);
    }

    public AppointmentDetailsFragmentDevicesListItem(Context context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_devices_list_item, this);

        mTextTotal = (TextView) findViewById(R.id.textViewTotal);
        mTextScanned = (TextView) findViewById(R.id.textViewScanned);
        mTextUnscanned = (TextView) findViewById(R.id.textViewUnscanned);

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
        mAppointmentId = appointmentId;

        AppointmentInfo appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
                appointmentId);
        ServiceLocationsInfo serviceLocationInfo = ServiceLocationsList
                .Instance().getServiceLocationById(
                        appointmentInfo.service_location_id);

        TrapList.Instance().LoadCheckedANDunCheckedTraps(appointmentId,
                appointmentInfo.customer_id, serviceLocationInfo.id);
        ArrayList<TrapScanningInfo> m_traps = TrapList.Instance().getAllTrapsByCustomerId(
                appointmentInfo.customer_id, serviceLocationInfo.id);

        int scanned = 0;
        for (TrapScanningInfo trap : m_traps) {
            scanned += (trap.isChecked ? 1 : 0);
        }
        mTextTotal.setText("" + m_traps.size());
        mTextScanned.setText("" + scanned);
        mTextUnscanned.setText("" + (m_traps.size() - scanned));
    }
}
