package com.anstar.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anstar.fieldwork.R;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.ServiceLocationsList;

/**
 * Created by oleg on 13.08.15.
 */
public class AppointmentDetailsFragmentListHeader extends LinearLayout{
    private final Context mContext;
    private final TextView mStatus;
    private final TextView mFinishedAtTime;
    private final TextView mDuration;
    private final TextView mCustomerName;
    private final TextView mAddress;
    private final TextView mStartsAtTime;

    public AppointmentDetailsFragmentListHeader(Context context) {
        super(context);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_appointment_details_list_header, this);

        mStartsAtTime = (TextView) findViewById(R.id.textViewStartsAtTime);
        mFinishedAtTime = (TextView) findViewById(R.id.textViewFinishedAtTime);
        mDuration = (TextView) findViewById(R.id.textViewDuration);
        mCustomerName = (TextView) findViewById(R.id.textViewName);
        mAddress = (TextView) findViewById(R.id.textViewAddress);
        mStatus = (TextView) findViewById(R.id.textViewStatus);
    }

    public void init(int appointmentId) {

        AppointmentInfo appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
                appointmentId);
        CustomerInfo customerInfo = CustomerList.Instance().getCustomerById(
                appointmentInfo.customer_id);
        ServiceLocationsInfo serviceLocationInfo = ServiceLocationsList.Instance()
                .getServiceLocationById(appointmentInfo.service_location_id);

        mStartsAtTime.setText(appointmentInfo.started_at_time);
        mFinishedAtTime.setText(appointmentInfo.finished_at_time);
        mCustomerName.setText(customerInfo.name);
        int hours = appointmentInfo.duration / 60;
        int minutes = appointmentInfo.duration % 60;
        mDuration.setText(String.format("%02d:%02d", hours, minutes));
        mAddress.setText(serviceLocationInfo.street + ", " +
                serviceLocationInfo.city + ", " +
                serviceLocationInfo.state + ", " +
                serviceLocationInfo.zip);
        if (appointmentInfo.status.equals("Missed Appointment")) {
            mStatus.setBackgroundResource(R.color.marck_app_miss);
        } else if (appointmentInfo.status.equals("Complete")) {
            mStatus.setBackgroundResource(R.color.marck_app_comp);
        } else if (appointmentInfo.status.equals("Scheduled")) {
            mStatus.setBackgroundResource(R.color.marck_app_sched);
        }

    }
}
