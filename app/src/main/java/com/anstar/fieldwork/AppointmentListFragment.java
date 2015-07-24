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
    private TextView txtStartedAtDate, divider1;
    private RelativeLayout RlInfo;
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
        RlInfo = (RelativeLayout) v.findViewById(R.id.RlInfo);

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
                holder.name = (TextView) rowView.findViewById(R.id.textView22);
                holder.localname = (TextView) rowView.findViewById(R.id.textView23);
                holder.date = (TextView) rowView.findViewById(R.id.textView24);
                holder.duration = (TextView) rowView.findViewById(R.id.textView29);
                holder.duration1 = (TextView) rowView.findViewById(R.id.textView69);
                holder.appointments_item_clik = (RelativeLayout) rowView.findViewById(R.id.appointments_item_clik);
                holder.marker_app = (RelativeLayout) rowView.findViewById(R.id.marker_app);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            final AppointmentInfo item = m_list.get(position);
            SimpleDateFormat datformat = new SimpleDateFormat("ss");
            SimpleDateFormat datformatN = new SimpleDateFormat("mm:ss");
            CustomerInfo customerinfo = CustomerList.Instance()
                    .getCustomerById(item.customer_id);
            String name = "";
            if (customerinfo != null) {
                if (customerinfo.customer_type.equalsIgnoreCase("Commercial")) {
                    name = customerinfo.name;
                } else {
                    name = customerinfo.name_prefix + " "
                            + customerinfo.first_name + " "
                            + customerinfo.last_name;
                }
                holder.name.setText(name);
            }
            ServiceLocationsInfo service = ServiceLocationsList.Instance().getServiceLocationById(item.service_location_id);
            if (service != null) {

                holder.localname.setText(service.name);
            }
            LineItemsInfo lineInfo = LineItemsList.Instance().getFirstLineByWoId(item.id);
            if (lineInfo != null) {
                holder.date.setText(lineInfo.name);
            }
            String newDate = null;
            try {
                Date dateNO = datformat.parse(item.duration + "");
                newDate = datformatN.format(dateNO);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.duration1.setText(newDate);
            holder.duration.setText(item.started_at_time + ";");
            if (item.status.equals("Missed Appointment")) {
                holder.marker_app.setBackgroundResource(R.color.marck_app_miss);
            } else if (item.status.equals("Complete")) {
                holder.marker_app.setBackgroundResource(R.color.marck_app_comp);
            } else if (item.status.equals("Scheduled")) {
                holder.marker_app.setBackgroundResource(R.color.marck_app_sched);
            }
            holder.appointments_item_clik.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(),
                            AppointmentDetailsActivity.class);
                    i.putExtra(Const.Appointment_Id, item.id);
                    Const.app_id = item.id;
                    startActivityForResult(i, APPOINTMENT_DETAIL);
                }
            });
            return rowView;
        }
    }

    public static class ViewHolder {
        TextView name;
        TextView localname;
        TextView date;
        TextView duration;
        TextView duration1;
        RelativeLayout appointments_item_clik;
        RelativeLayout marker_app;
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
            m_adapter = new AppointmentAdapter(m_appointments);
            lstAppointment.setAdapter(m_adapter);
            RlInfo.setVisibility(View.GONE);
        } else {
            RlInfo.setVisibility(View.VISIBLE);
            lstAppointment.setVisibility(View.GONE);
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
