package com.anstar.fieldwork;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.LineItemsList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.print.BasePrint;
import com.anstar.print.MsgDialog;
import com.anstar.print.MsgHandle;
import com.anstar.print.PdfPrint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment implements ModelDelegate<AppointmentInfo> {
    private static int APPOINTMENT_DETAIL = 1;
    TextView appointments;
    Date m_currentDate;
    ArrayList<AppointmentInfo> m_appointments = null;
    private ListView lstAppointment;
    AppointmentAdapter m_adapter = null;
    private BaseLoader mBaseLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        lstAppointment = (ListView) rootView.findViewById(R.id.listView);
        appointments = (TextView) rootView.findViewById(R.id.textView9);
        appointments.setPaintFlags(appointments.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).getSupportActionBar().setTitle("Calendar");
                ((DashboardActivity) getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AppointmentListFragment())
                        .commit();
            }
        });


        return rootView;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseLoader = new BaseLoader(getActivity());
        m_currentDate = new Date();

        m_appointments = new ArrayList<AppointmentInfo>();

        NotificationCenter.Instance().addObserver(HomeFragment.this,
                "refresh", "hideshowRefresh", null);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mBaseLoader.showProgress("Please wait...");
            AppointmentModelList.Instance().load(this);
        } catch (Exception e) {
            mBaseLoader.hideProgress();
            e.printStackTrace();
        }
    }

    public class AppointmentAdapter extends BaseAdapter {
        ArrayList<AppointmentInfo> m_list = new ArrayList<AppointmentInfo>();

        public AppointmentAdapter(ArrayList<AppointmentInfo> list) {
            m_list = list;
        }

        @Override
        public int getCount() {
            return m_list.size();
        }

        @Override
        public Object getItem(int position) {
            return m_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View rowView = convertView;
            holder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater li = getActivity().getLayoutInflater();
                rowView = li.inflate(R.layout.appointment_item, null);
                rowView.setTag(holder);
                holder.txtAppointment = (TextView) rowView
                        .findViewById(R.id.txtAppontmentTitle);
                holder.txtServiceLocationName = (TextView) rowView
                        .findViewById(R.id.txtAppontmentType);
                holder.txtStatus = (TextView) rowView
                        .findViewById(R.id.txtStatus);

                holder.txtStartEndTime = (TextView) rowView
                        .findViewById(R.id.txtStartEndTime);
                holder.txtLineItemName = (TextView) rowView
                        .findViewById(R.id.txtLineItemName);
                holder.chkConfirmed = (CheckBox) rowView
                        .findViewById(R.id.chkConfirmed);

                holder.rl = (RelativeLayout) rowView
                        .findViewById(R.id.rlappointment);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            final AppointmentInfo appointment = m_list.get(position);
            CustomerInfo customerinfo = CustomerList.Instance()
                    .getCustomerById(appointment.customer_id);
            LineItemsInfo lineInfo = LineItemsList.Instance()
                    .getFirstLineByWoId(appointment.id);
            String[] temp = appointment.starts_at.split("T");
            String[] temp2 = temp[1].split("-");
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
            Date date = null;
            Date endDate = null;
            String time = "";
            String[] temp1 = appointment.ends_at.split("T");
            String[] temp12 = temp1[1].split("-");

            String endtime = "";
            try {
                date = sdf.parse(temp2[0]);
                endDate = sdf.parse(temp12[0]);
                SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                time = sdf1.format(date);
                endtime = sdf1.format(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ServiceLocationsInfo service = ServiceLocationsList.Instance()
                    .getServiceLocationById(appointment.service_location_id);
            if (service != null) {

                holder.txtServiceLocationName.setText(service.name);
            }
            if (appointment.confirmed) {
                holder.chkConfirmed.setVisibility(View.VISIBLE);
            }
            String name = "";
            if (customerinfo != null) {
                if (customerinfo.customer_type.equalsIgnoreCase("Commercial")) {
                    name = customerinfo.name;
                } else {
                    name = customerinfo.name_prefix + " "
                            + customerinfo.first_name + " "
                            + customerinfo.last_name;
                }
                // holder.txtAppointmentType.setText(time + " "
                // + customerinfo.customer_type);
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please refresh your data.", Toast.LENGTH_LONG).show();
            }
            if (lineInfo != null) {
                // holder.txtAppointmentType.setText(time + " " +
                // lineInfo.name);
                holder.txtLineItemName.setText(lineInfo.name);
            }
            holder.txtStartEndTime.setText(time + "  " + endtime);
            if (name.contains("null")) {
                name = name.replace("null", "");
            }
            holder.txtAppointment.setText(name);
            // holder.txtStatus.setText(appointment.status);
            if (appointment.status.equalsIgnoreCase("complete")) {
                holder.txtStatus
                        .setBackgroundResource(R.drawable.status_background);
                holder.txtStatus.setText("C");
            } else if (appointment.status.equalsIgnoreCase("missed")
                    || appointment.status
                    .equalsIgnoreCase("Missed Appointment")) {
                holder.txtStatus
                        .setBackgroundResource(R.drawable.status_missed);
                // holder.txtStatus.setText("Missed");
                holder.txtStatus.setText("M");
            } else if (appointment.status.equalsIgnoreCase("scheduled")) {
                holder.txtStatus
                        .setBackgroundResource(R.drawable.status_yellow);
                holder.txtStatus.setText("S");
            }
            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkConnectivity.isConnected()) {
                        mBaseLoader.showProgress();
                        appointment.RetriveData(new UpdateInfoDelegate() {
                            @Override
                            public void UpdateSuccessFully(ServiceResponse res) {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mBaseLoader.hideProgress();
                                Intent i = new Intent(getActivity(),
                                        AppointmentDetailsActivity.class);
                                i.putExtra(Const.Appointment_Id, appointment.id);
                                Const.app_id = appointment.id;
                                startActivityForResult(i, APPOINTMENT_DETAIL);
                            }

                            @Override
                            public void UpdateFail(String ErrorMessage) {
                                mBaseLoader.hideProgress();
                                Intent i = new Intent(getActivity(),
                                        AppointmentDetailsActivity.class);
                                i.putExtra(Const.Appointment_Id, appointment.id);
                                Const.app_id = appointment.id;
                                startActivityForResult(i, APPOINTMENT_DETAIL);
                            }
                        });
                    } else {
                        Intent i = new Intent(getActivity(),
                                AppointmentDetailsActivity.class);
                        i.putExtra(Const.Appointment_Id, appointment.id);
                        Const.app_id = appointment.id;
                        startActivityForResult(i, APPOINTMENT_DETAIL);
                    }
                }
            });

            return rowView;
        }
    }

    public static class ViewHolder {
        TextView txtAppointment;
        TextView txtServiceLocationName, txtStartEndTime, txtLineItemName;
        TextView txtStatus;
        CheckBox chkConfirmed;
        RelativeLayout rl;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APPOINTMENT_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                String path = data.getStringExtra("printpath");
                if (path.length() > 0)
                    print(path);
            }
        }
    }

    @Override
    public void ModelLoaded(ArrayList<AppointmentInfo> list) {
        mBaseLoader.hideProgress();
        Utils.LogInfo("finish refresh...................");
        bindData();
        // loadAppointmentCustomer(list);
    }

    @Override
    public void ModelLoadFailedWithError(String error) {
        mBaseLoader.hideProgress();
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    private void bindData() {
        m_appointments = AppointmentModelList.Instance().getAppointmentBydate(m_currentDate, false);
        if (m_appointments.size() > 0) {
            m_adapter = new AppointmentAdapter(m_appointments);
            lstAppointment.setAdapter(m_adapter);
        } else {
//            lstAppointment.setVisibility(View.INVISIBLE);
        }
    }

    // public static BluetoothAdapter bluetoothAdapter;
    protected BasePrint myPrint = null;
    protected MsgHandle mHandle;
    protected MsgDialog mDialog;

    public void print(final String path) {
        mDialog = new MsgDialog(getActivity());
        mHandle = new MsgHandle(getActivity(), mDialog);
        myPrint = new PdfPrint(getActivity(), mHandle, mDialog);

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        myPrint.setBluetoothAdapter(bluetoothAdapter);
        ((PdfPrint) myPrint).setFiles(path);
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int pages = ((PdfPrint) myPrint).getPdfPages(path);
                    ((PdfPrint) myPrint).setPrintPage(1, pages);
                    myPrint.print();
                }
            });

        } catch (Exception e) {
            Utils.LogException(e);
        }
    }

    protected BluetoothAdapter getBluetoothAdapter() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            final Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }
        return bluetoothAdapter;
    }
}
