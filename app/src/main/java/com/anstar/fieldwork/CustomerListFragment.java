package com.anstar.fieldwork;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.NetworkConnectivity;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.list.CustomerList;

import java.util.ArrayList;
import java.util.Comparator;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CustomerListFragment extends Fragment implements
        ModelDelegate<CustomerInfo> {

    private CustomerListAdapter mCustomerListAdapter;
    private StickyListHeadersListView mCustomerList;
    private ArrayList<ViewDataItem> mFullItemsList = new ArrayList<ViewDataItem>();
    private ArrayList<ViewDataItem> mFilteredItemsList = new ArrayList<ViewDataItem>();
    private EditText mEdtSearch;
    private BaseLoader mBaseLoader;

    // Container Activity must implement this interface
    public interface OnCustomerListSelectedListener {
        public void onCustomerItemSelected(CustomerInfo item);
    }

    private OnCustomerListSelectedListener mOnCustomerListSelectedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customer_list, container, false);

        mEdtSearch = (EditText) v.findViewById(R.id.edtSearch);
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                showFilteredList(s);
            }

        });
        mCustomerListAdapter = new CustomerListAdapter(getActivity());
        mCustomerList = (StickyListHeadersListView) v.findViewById(R.id.lstCustomer);
        mCustomerList.setAdapter(mCustomerListAdapter);
        mCustomerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnCustomerListSelectedListener.onCustomerItemSelected(mFilteredItemsList.get(position).customerInfo);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseLoader = new BaseLoader(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_customer_list);
        try {
            mBaseLoader.showProgress();
            CustomerList.Instance().loadLocal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ModelLoaded(ArrayList<CustomerInfo> list) {
        mBaseLoader.hideProgress();
        if (list != null) {
            loadList(list);
            showFilteredList("");
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mOnCustomerListSelectedListener = (OnCustomerListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCustomerListSelectedListener");
        }
    }

    private void loadList(ArrayList<CustomerInfo> customerList) {

        mFullItemsList.clear();
        for (CustomerInfo c : customerList) {
            String name = "";

            if (c.customer_type.equalsIgnoreCase("Commercial")) {
                name = c.name;
            } else {
                name = c.name_prefix + " " + c.first_name
                        + " " + c.last_name;
            }
            if (name != null) {
                mFullItemsList.add(new ViewDataItem(name.trim(), c));
            }
        }
        IgnoreCaseComparator icc = new IgnoreCaseComparator();
        java.util.Collections.sort(mFullItemsList, icc);
    }

    private void showFilteredList(CharSequence subStr) {
        String subStrUpper = subStr.toString().toUpperCase();
        mFilteredItemsList.clear();
        for (ViewDataItem c : mFullItemsList) {
            if (c.customerName.toUpperCase().contains(subStrUpper)) {
                mFilteredItemsList.add(c);
            }
        }
        mCustomerListAdapter.notifyDataSetChanged();
    }

/*
    public void loadCustomer(CustomerInfo customerinfo) {

        if (!customerinfo.isAllreadyLoded) {
            if (NetworkConnectivity.isConnected()) {
                mBaseLoader.showProgress();
                customerinfo.RetriveData(new UpdateCustomerDelegate() {

                    @Override
                    public void UpdateSuccessFully(CustomerInfo info) {
                        mBaseLoader.hideProgress();
                        Intent i = new Intent();
                        i.putExtra("customer_id", info.id);
                        getActivity().setResult(Activity.RESULT_OK, i);
// Oleg !!!!!!!!!!!!						finish();
                    }

                    @Override
                    public void UpdateFail(String ErrorMessage) {
                        mBaseLoader.hideProgress();
                        Toast.makeText(getActivity(),
                                "Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(),
                        "Please check your internet connection.",
                        Toast.LENGTH_LONG).show();
// Oleg !!!!!!!!!!!!						finish();
            }
        } else {
            Intent i = new Intent();
            i.putExtra("customer_id", customerinfo.id);
            getActivity().setResult(Activity.RESULT_OK, i);
// Oleg !!!!!!!!!!!!						finish();
        }
    }
*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.add_pest_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnAddPest:
                SharedPreferences setting = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                boolean auto = setting.getBoolean("ISAUTOMODE", true);
                if (!auto) {
                    Toast.makeText(
                            getActivity(),
                            "You have to on Auto Sync mode from settings to add customer",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                if (!NetworkConnectivity.isConnected()) {
                    Toast.makeText(getActivity(),
                            "You need internet connection to add Customer",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getActivity(),
                            AddCustomerActivity.class);
                    startActivity(i);
                }
                return true;
            // case R.id.btnAppointments:
            // return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CustomerListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;

        public CustomerListAdapter(Context context) {

            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            return mFilteredItemsList.size();
        }

        @Override
        public Object getItem(int position) {

            return mFilteredItemsList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.customer_item, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.txtCustomerName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(mFilteredItemsList.get(position).customerName);

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.customer_items_header, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            String headerText = "" + mFilteredItemsList.get(position).customerName.subSequence(0, 1).charAt(0);
            holder.text.setText(headerText);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return mFilteredItemsList.get(position).customerName.subSequence(0, 1).charAt(0);
        }

        class HeaderViewHolder {
            TextView text;
        }

        class ViewHolder {
            TextView text;
        }

    }

    class ViewDataItem {
        public final String customerName;
        public final CustomerInfo customerInfo;

        public ViewDataItem(String name, CustomerInfo info) {
            customerName = name;
            customerInfo = info;
        }
    }

    class IgnoreCaseComparator implements Comparator<ViewDataItem> {
        public int compare(ViewDataItem strA, ViewDataItem strB) {
            return strA.customerName.compareToIgnoreCase(strB.customerName);
        }
    }
}
