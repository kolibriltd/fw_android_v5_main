package com.anstar.fieldwork;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.HomeInfo;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
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
    private ArrayList<CustomerInfo> m_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        lstAppointment = (ListView) rootView.findViewById(R.id.listView);


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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_home);
        try {
            mBaseLoader.showProgress("Please wait...");
            AppointmentModelList.Instance().load(this);

        } catch (Exception e) {
            mBaseLoader.hideProgress();
            e.printStackTrace();
        }
    }

    public class AppointmentAdapter extends BaseAdapter {
        ArrayList<HomeInfo> m_list = new ArrayList<HomeInfo>();

        public AppointmentAdapter(ArrayList<HomeInfo> list) {
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
                rowView = li.inflate(R.layout.home_item, null);
                holder.firsname = (TextView) rowView.findViewById(R.id.cus_firstname);
                holder.lastname = (TextView) rowView.findViewById(R.id.cus_lastname);
                holder.relativeLayout1 = (RelativeLayout) rowView.findViewById(R.id.relativeLayout1);
                holder.customer_item = (LinearLayout) rowView.findViewById(R.id.customer_item);
                holder.customers = (TextView) rowView.findViewById(R.id.textView11);
                holder.name = (TextView) rowView.findViewById(R.id.textView22);
                holder.localname = (TextView) rowView.findViewById(R.id.textView23);
                holder.duration = (TextView) rowView.findViewById(R.id.textView29);
                holder.duration1 = (TextView) rowView.findViewById(R.id.textView69);
                holder.date = (TextView) rowView.findViewById(R.id.textView24);
                holder.relative_layut_2 = (RelativeLayout) rowView.findViewById(R.id.relative_layut_2);
                holder.appointments_item = (RelativeLayout) rowView.findViewById(R.id.appointments_item);
                holder.appointments = (TextView) rowView.findViewById(R.id.textView9);
                holder.appointmens_line = (LinearLayout) rowView.findViewById(R.id.appointmens_line);
                holder.customer_line = (LinearLayout) rowView.findViewById(R.id.customer_line);
                holder.marker_app = (RelativeLayout) rowView.findViewById(R.id.marker_app);
                holder.no_appointments_d = (RelativeLayout) rowView.findViewById(R.id.no_appointments_d);
                rowView.setTag(holder);

            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            final HomeInfo item = m_list.get(position);

            if (item.typeView.equals("appointmets")) {
                holder.appointmens_line.setVisibility(View.VISIBLE);
                holder.customer_line.setVisibility(View.GONE);
                holder.no_appointments_d.setVisibility(View.GONE);
                SimpleDateFormat datformat = new SimpleDateFormat("ss");
                SimpleDateFormat datformatN = new SimpleDateFormat("mm:ss");
                if (item.type_item.equals("layout")) {
                    holder.appointments_item.setVisibility(View.GONE);
                    holder.relative_layut_2.setVisibility(View.VISIBLE);
                    holder.appointments.setPaintFlags(holder.appointments.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    holder.appointments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((DashboardActivity) getActivity()).replaceAnimatedFragment(new AppointmentListFragment());
                        }
                    });

                }
                if (item.type_item.equals("item")) {
                    holder.appointments_item.setVisibility(View.VISIBLE);
                    holder.relative_layut_2.setVisibility(View.GONE);
                    holder.no_appointments_d.setVisibility(View.GONE);
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
                    holder.appointments_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getActivity(),
                                    AppointmentDetailsActivity.class);
                            i.putExtra(Const.Appointment_Id, item.id);
                            Const.app_id = item.id;
                            startActivityForResult(i, APPOINTMENT_DETAIL);
                        }
                    });


                }
                if (item.type_item == "no_app") {
                    holder.appointments_item.setVisibility(View.GONE);
                    holder.relative_layut_2.setVisibility(View.GONE);
                    holder.no_appointments_d.setVisibility(View.VISIBLE);

                }
            }
            if (item.typeView.equals("customers")) {
                holder.appointmens_line.setVisibility(View.GONE);
                holder.customer_line.setVisibility(View.VISIBLE);

                if (item.type_item.equals("layout")) {
                    holder.customer_item.setVisibility(View.GONE);
                    holder.relativeLayout1.setVisibility(View.VISIBLE);
                    holder.customers.setPaintFlags(holder.customers.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    holder.customers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((DashboardActivity) getActivity()).replaceAnimatedFragment(new CustomerListFragment());
                        }
                    });
                }
                if (item.type_item.equals("item")) {
                    holder.customer_item.setVisibility(View.VISIBLE);
                    holder.relativeLayout1.setVisibility(View.GONE);
                    holder.firsname.setText("");
                    holder.lastname.setText(item.name);
                    holder.customer_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            showCustomerDetailsFragment(item.id_customer);
                        }
                    });
                }

            }

            return rowView;
        }
    }

    private void showCustomerDetailsFragment(int id) {
        CustomerDetailsFragment fragment = new CustomerDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("customer_id", id);
        fragment.setArguments(bundle);
        ((DashboardActivity) getActivity()).addAnimatedFragment(fragment);
    }

    public static class ViewHolder {
        TextView firsname;
        TextView lastname;
        RelativeLayout relativeLayout1;
        RelativeLayout marker_app;
        RelativeLayout no_appointments_d;
        LinearLayout customer_item;
        TextView customers;
        TextView localname;
        TextView name;
        TextView duration;
        TextView duration1;
        TextView date;
        RelativeLayout relative_layut_2;
        RelativeLayout appointments_item;
        TextView appointments;
        LinearLayout appointmens_line;
        LinearLayout customer_line;
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
        m_appointments = AppointmentModelList.Instance().getAppointmentBydate(m_currentDate);
        if (m_appointments.size() > 0) {
            ///
        } else {
//            lstAppointment.setVisibility(View.INVISIBLE);
        }
        loadCustomer();
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

    public void loadCustomer() {
        ////CustomerList.Instance().ClearDB();
        //  try {
        CustomerList.Instance().loadLocal(new ModelDelegate<CustomerInfo>() {

            @Override
            public void ModelLoaded(ArrayList<CustomerInfo> list) {
                mBaseLoader.hideProgress();
                if (list != null) {
                    m_list = list;
                    generateHashList();
                    // adapter = new SectionListAdapter();
                    //  adapter.delegate = CustomerListFragment.this;
                    //  lstCustomer.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(),
                            "No customer downloaded yet", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void ModelLoadFailedWithError(String error) {
                mBaseLoader.hideProgress();
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
       /* } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void generateHashList() {
        ArrayList<HomeInfo> m_list_home = new ArrayList<HomeInfo>();
        int i = 0;
        /*ArrayList<CustomerInfo> otherCharList = new ArrayList<CustomerInfo>();
        for (CustomerInfo c : m_list) {
            if (i < 4) {
                String name = "";
                if (c.customer_type.equalsIgnoreCase("Commercial")) {
                    name = c.name;
                } else {
                    name = c.last_name;
                }
                m_list_four.add(c);
            }
            i++;
        }*/
        HomeInfo layout_app = new HomeInfo();
        layout_app.typeView = "appointmets";
        layout_app.type_item = "layout";
        m_list_home.add(layout_app);

        if (m_appointments.size() > 0) {
            for (AppointmentInfo item : m_appointments) {
                HomeInfo item_app = new HomeInfo();
                item_app.typeView = "appointmets";
                item_app.type_item = "item";
                item_app.customer_id = item.customer_id;
                item_app.id = item.id;
                item_app.service_location_id = item.service_location_id;
                item_app.duration = item.duration;
                item_app.started_at_time = item.started_at_time;
                item_app.status = item.status;
                m_list_home.add(item_app);
            }
        } else {
            HomeInfo layout_app_no = new HomeInfo();
            layout_app_no.typeView = "appointmets";
            layout_app_no.type_item = "no_app";
            m_list_home.add(layout_app_no);
        }

        HomeInfo layout_cus = new HomeInfo();
        layout_cus.typeView = "customers";
        layout_cus.type_item = "layout";
        m_list_home.add(layout_cus);

        for (CustomerInfo c : m_list) {
            if (i < 4) {
                HomeInfo item_cus = new HomeInfo();
                String name = "";
                if (c.customer_type.equalsIgnoreCase("Commercial")) {
                    name = c.name;
                } else {
                    name = c.name_prefix + " "
                            + c.first_name + " "
                            + c.last_name;
                }
                item_cus.id_customer = c.id;
                item_cus.typeView = "customers";
                item_cus.type_item = "item";
                item_cus.name = name;
                m_list_home.add(item_cus);
            }
            i++;
        }

        m_adapter = new AppointmentAdapter(m_list_home);
        lstAppointment.setAdapter(m_adapter);
        m_adapter.notifyDataSetChanged();

    }
}
