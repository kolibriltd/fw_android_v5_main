package com.anstar.fieldwork;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
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

public class AppointmentListFragment extends Fragment implements OnClickListener,
        ModelDelegate<AppointmentInfo> {

    private static boolean isRefreshShow = true;
    private static int APPOINTMENT_DETAIL = 1;

    private ImageView btnNext, btnPrev;
    private ListView lstAppointment;
    // MyAppointmentAdapter m_adapter;
    private TextView txtStartedAtDate, txtAppointmentPrice,
            txtAppointmentCount, divider1;
    int pos = 0;
    AppointmentAdapter m_adapter = null;
    ArrayList<AppointmentInfo> m_appointments = null;
    int i = 0;
    int j = 0;
    //ActionBar action = null;
    Date m_currentDate;
    private BaseLoader mBaseLoader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_appointment_list, container, false);

        btnNext = (ImageView) v.findViewById(R.id.btnNext);
        btnPrev = (ImageView) v.findViewById(R.id.btnPrev);
        lstAppointment = (ListView) v.findViewById(R.id.lstAppointment);
        txtStartedAtDate = (TextView) v.findViewById(R.id.txtDate);
        txtAppointmentCount = (TextView) v.findViewById(R.id.txtAppointmentCount);
        txtAppointmentPrice = (TextView) v.findViewById(R.id.txtAppointmentPrice);
        divider1 = (TextView) v.findViewById(R.id.divider1);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseLoader = new BaseLoader(getActivity());

//        action = getSupportActionBar();
        // action.setTitle("Schedule");
//        action.setTitle(Html.fromHtml("<font color='"
//                + getString(R.string.header_text_color) + "'>Schedule</font>"));
//        action.setHomeButtonEnabled(true);
//        action.setDisplayHomeAsUpEnabled(true);
        m_currentDate = new Date();

        m_appointments = new ArrayList<AppointmentInfo>();

        NotificationCenter.Instance().addObserver(AppointmentListFragment.this,
                "refresh", "hideshowRefresh", null);
        // NotificationCenter.Instance().postNotification(
        // "refresh");

        // boolean isonline = NetworkConnectivity.isConnected();
        // Utils.LogInfo(String.valueOf(isonline));
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_appointment_list);
        try {
            mBaseLoader.showProgress("Please wait...");
            AppointmentModelList.Instance().load(this);
        } catch (Exception e) {
            mBaseLoader.hideProgress();
            e.printStackTrace();
        }
    }

/*
    public void hideshowRefresh(final Boolean flag) {
        Utils.LogInfo("hideshowRefresh ****** " + flag);
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                isRefreshShow = flag;
                invalidateOptionsMenu();
            }
        }));
    }
*/

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
            holder.rl.setOnClickListener(new OnClickListener() {
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
    public void onClick(View v) {
        if (v == btnNext) {
            // if (!AppointmentModelList.Instance().hasNextDateRecords(
            // m_currentDate)) {
            // btnNext.setVisibility(View.GONE);
            // return;
            // }
            m_currentDate = new Date(
                    (m_currentDate.getTime() + (24 * 60 * 60 * 1000)));

            bindData();
            Date today = new Date(new Date().getTime()
                    + (4 * 24 * 60 * 60 * 1000));
            if (Utils.isSameDate(m_currentDate, today)) {
                btnNext.setVisibility(View.GONE);
            }

            btnPrev.setVisibility(View.VISIBLE);
        } else if (v == btnPrev) {
            Date yesterday = new Date(new Date().getTime()
                    - (24 * 60 * 60 * 1000));
            if (Utils.isSameDate(m_currentDate, yesterday)) {
                btnPrev.setVisibility(View.GONE);
                return;
            }
            m_currentDate = new Date(
                    (m_currentDate.getTime() - (24 * 60 * 60 * 1000)));
            bindData();
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void ModelLoaded(ArrayList<AppointmentInfo> list) {
        mBaseLoader.hideProgress();
        Utils.LogInfo("finish refresh...................");
        bindData();
        // loadAppointmentCustomer(list);
    }

    // public void loadAppointmentCustomer(ArrayList<AppointmentInfo> list) {
    // ArrayList<Integer> m_Custids = new ArrayList<Integer>();
    // for (AppointmentInfo a : list) {
    // m_Custids.add(a.customer_id);
    // }
    // for (int i = 0; i < m_Custids.size(); i++) {
    // CustomerInfo info = CustomerList.Instance().getCustomer(
    // m_Custids.get(i));
    //
    // CustomerList.Instance().loadWithId(new UpdateCustomerDelegate() {
    //
    // @Override
    // public void UpdateSuccessFully(CustomerInfo info) {
    // }
    //
    // @Override
    // public void UpdateFail(String ErrorMessage) {
    // }
    // }, info);
    // }
    //
    // }

    @Override
    public void ModelLoadFailedWithError(String error) {
        mBaseLoader.hideProgress();
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    private void bindData() {
        m_appointments = AppointmentModelList.Instance().getAppointmentBydate(m_currentDate);
        if (m_appointments.size() > 0) {
            lstAppointment.setVisibility(View.VISIBLE);
            txtAppointmentCount.setTextSize(28);
            txtAppointmentCount.setText("" + m_appointments.size() + "");
            setPrice(m_appointments);
            m_adapter = new AppointmentAdapter(m_appointments);
            lstAppointment.setAdapter(m_adapter);
            divider1.setVisibility(View.VISIBLE);
        } else {
            txtAppointmentCount.setTextSize(16);
            txtAppointmentCount.setText("No appointments scheduled");
            txtAppointmentPrice.setText("");
            lstAppointment.setVisibility(View.INVISIBLE);
            divider1.setVisibility(View.GONE);
        }
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");
        String date = sdf.format(m_currentDate);

        if (date.endsWith("1") && !date.endsWith("11"))
            sdf = new SimpleDateFormat("EEE, MMM d'st'");
        else if (date.endsWith("2") && !date.endsWith("12"))
            sdf = new SimpleDateFormat("EEE, MMM d'nd'");
        else if (date.endsWith("3") && !date.endsWith("13"))
            sdf = new SimpleDateFormat("EEE, MMM d'rd'");
        else
            sdf = new SimpleDateFormat("EEE, MMM d'th'");

        String yourDate = sdf.format(m_currentDate);

        txtStartedAtDate.setText(yourDate);

        Date yesterday = new Date(new Date().getTime() - (24 * 60 * 60 * 1000));
        if (Utils.isSameDate(m_currentDate, yesterday)) {
            btnPrev.setVisibility(View.GONE);
        } else {
            btnPrev.setVisibility(View.VISIBLE);
        }
    }

    public String getToday() {
        Date d = new Date(System.currentTimeMillis());
        String dt = d.toString();
        return dt;
    }

    public void setPrice(ArrayList<AppointmentInfo> m_list) {
        double total = 0;
        for (AppointmentInfo info : m_list) {
            total += LineItemsList.Instance().getLineItemsPriceByAppt(info.id);
            // total = total + Double.parseDouble(info.price);
        }
        txtAppointmentPrice.setText("$" + String.format("%.02f", total));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);

        inflater.inflate(R.menu.refresh_menu, menu);
        MenuItem item = menu.findItem(R.id.btnRefresh);
        item.setVisible(isRefreshShow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnRefresh:
                if (NetworkConnectivity.isConnected()) {
                    // loadCustomer();
                    AppointmentModelList.Instance().ClearDB();
                    LineItemsList.Instance().ClearDB();

                    try {
                        mBaseLoader.showProgress("Please wait...");
                        AppointmentModelList.Instance().load(this);
                    } catch (Exception e) {
                        mBaseLoader.hideProgress();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(),
                            "Please check your internet connection to refresh", Toast.LENGTH_LONG)
                            .show();
                }
                return true;
            case R.id.btnAddAppointment:
                SharedPreferences setting = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                boolean auto = setting.getBoolean("ISAUTOMODE", true);
                if (!auto) {
                    Toast.makeText(
                            getActivity(),
                            "You have to on Auto Sync mode from settings to add appointment",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                if (!NetworkConnectivity.isConnected()) {
                    Toast.makeText(getActivity(),
                            "You need internet connection to add Appointment",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getActivity(),
                            AddAppointmentActivity.class);
                    startActivity(i);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadCustomer() {
        CustomerList.Instance().ClearDB();
        try {
            CustomerList.Instance().load(new ModelDelegate<CustomerInfo>() {

                @Override
                public void ModelLoaded(ArrayList<CustomerInfo> list) {
                }

                @Override
                public void ModelLoadFailedWithError(String error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
