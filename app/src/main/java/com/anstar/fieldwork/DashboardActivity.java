package com.anstar.fieldwork;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.Const;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.dialog.ConfirmDialog;
import com.anstar.models.Account;
import com.anstar.models.CustomerContactInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ServiceLocationContactInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.WorkHistroyInfo;
import com.anstar.print.BasePrint;
import com.anstar.print.MsgDialog;
import com.anstar.print.MsgHandle;
import com.anstar.print.PdfPrint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements
        ConfirmDialog.OnConfirmDialogListener, CustomerListFragment.OnCustomerItemSelectedListener,
        ServiceLocationListFragment.OnServiceLocationItemSelectedListener,
        CustomerContactListFragment.OnCustomerContactItemSelectedListener,
        CustomerDetailsFragment.OnCustomerDetailsItemSelectedListener,
        HomeFragment.OnHomeItemSelectedListener,
        ServiceLocationDetailFragment.OnServiceLocationDetailItemSelectedListener,
        ServiceLocationContactsFragment.OnServiceLocationContactslItemSelectedListener,
        WorkHistoryListFragment.OnWorkHistoryListSelectedListener,
        CaptureSignatureFragment.CaptureSignatureFragmentListener {

    private static int APPOINTMENT_DETAIL = 1;

    final private String ITEM_ICON = "item_icon";
    final private String ITEM_TEXT = "item_text";

    final private String ROOT_FRAGMENT_NAME = "drawer_top";

    // Navigation drawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleAdapter mListAdapter;
    private String[] mDrawerTitles;
    private int[] mDrawerValues;

    ActionBar action = null;

    private FragmentManager.OnBackStackChangedListener
            mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            syncActionBarArrowState();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        action = getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.setDisplayShowHomeEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.root_screen_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.root_screen_list);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.app_name,
                R.string.app_name) {

            public void onDrawerClosed(View view) {
                syncActionBarArrowState();
            }

            public void onDrawerOpened(View drawerView) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        };
        //mDrawerToggle.setDrawerIndicatorEnabled(true);
        List<HashMap<String, String>> mList = new ArrayList<HashMap<String, String>>();
        mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
        String[] drawerIcons = getResources().getStringArray(R.array.drawer_icons);
        mDrawerValues = getResources().getIntArray(R.array.drawer_values);

        for (int i = 0; i < mDrawerTitles.length; i++) {

            HashMap<String, String> hm = new HashMap<String, String>();

            int id_icon = getResources().getIdentifier(drawerIcons[i], "drawable", this.getPackageName());
            hm.put(ITEM_ICON, Integer.toString(id_icon));
            hm.put(ITEM_TEXT, mDrawerTitles[i]);

            mList.add(hm);
        }

        View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
        mDrawerList.addHeaderView(header, null, false);
/*
        View footer = getLayoutInflater().inflate(R.layout.drawer_footer, null);
        mDrawerList.addFooterView(footer, null, false);
        TextView logOut = (TextView) footer.findViewById(R.id.textViewLogOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
*/

        String[] from = {ITEM_ICON, ITEM_TEXT};
        int[] to = {R.id.item_drawer_list_image, R.id.item_drawer_list_text};

        mListAdapter = new SimpleAdapter(action.getThemedContext(), mList, R.layout.drawer_list, from, to);
        mDrawerList.setAdapter(mListAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                useItem(position);
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);

        // Select Home fragment
        useItem(1);

        NotificationCenter.Instance().addObserver(DashboardActivity.this,
                "hidedash", "hideprogressdialog", null);
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
        super.onDestroy();
    }

    private void syncActionBarArrowState() {
        int backStackEntryCount =
                getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Sets fragment as main view
     * @param position navigation drawer's ListView's cursor position. Based on 1
     */
    private void useItem(int position) {

        switch (mDrawerValues[position - 1]) {
            case 0:
                // Home
                closeAllFragments();
                replaceAnimatedFragment(new HomeFragment());
                break;
            case 1:
                // Calendar
                closeAllFragments();
                replaceAnimatedFragment(new AppointmentListFragment());
                break;
            case 2:
                // Customers
                closeAllFragments();
                replaceAnimatedFragment(new CustomerListFragment());
                break;
            case 3:
                // Settings
                closeAllFragments();
                replaceAnimatedFragment(new SettingFragment());
                break;
            case 4:
                // Log out
                if (NetworkConnectivity.isConnected()) {
                    ConfirmDialog.newInstance("Confirm exit from account?").show(getSupportFragmentManager(), "log_out_dialog");
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Signout needs internet connection", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Only handle with DrawerToggle if the drawer indicator is enabled.
        if (mDrawerToggle.isDrawerIndicatorEnabled() &&
                mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.btnLogout:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // handle up navigation
            return true;
        } else {
            return super.onSupportNavigateUp();
        }
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

    public void print(final String path) {
        MsgDialog mDialog = new MsgDialog(this);
        MsgHandle mHandle = new MsgHandle(this, mDialog);
        final BasePrint myPrint = new PdfPrint(this, mHandle, mDialog);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myPrint.setBluetoothAdapter(bluetoothAdapter);
        ((PdfPrint) myPrint).setFiles(path);
        try {
            this.runOnUiThread(new Runnable() {
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

    private void closeAllFragments() {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void logOut() {
        Account account = Account.getUser();
        try {
            account.isLogin = false;
            account.save();
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        UserInfo.Instance().ClearDB();
        Intent i = new Intent(DashboardActivity.this,
                LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDialogConfirm(String tag) {
        if (tag.equals("log_out_dialog")) {
            logOut();
        }
    }

    @Override
    public void onDialogCancel(String tag) {

    }

    private void addAnimatedFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_animation_enter, R.anim.fragment_animation_exit,
                R.anim.fragment_animation_pop_enter, R.anim.fragment_animation_pop_exit);
        if (tag == null) {
            transaction.replace(R.id.container, fragment);
        } else {
            transaction.replace(R.id.container, fragment, tag);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void addAnimatedFragment(Fragment fragment) {
        addAnimatedFragment(fragment, null);
    }

    private void replaceAnimatedFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_animation_pop_enter, R.anim.fragment_animation_pop_exit,
                R.anim.fragment_animation_enter, R.anim.fragment_animation_exit);
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onCustomerItemSelected(CustomerInfo item) {
        CustomerDetailsFragment fragment = new CustomerDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("customer_id", item.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onServiceLocationItemSelected(ServiceLocationsInfo item) {
        ServiceLocationDetailFragment fragment = new ServiceLocationDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SLID", item.id);
        bundle.putInt("cid", item.customer_id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onCustomerContactItemSelected(CustomerContactInfo item) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("CONTACT_ID", item.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onCustomerDetailsContactsSelected(CustomerInfo item) {
        CustomerContactListFragment fragment = new CustomerContactListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("CID", item.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onCustomerDetailsServiceLocationsSelected(CustomerInfo item) {
        ServiceLocationListFragment fragment = new ServiceLocationListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("CID", item.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onCustomerDetailsEditSelected(CustomerInfo item) {
        if (NetworkConnectivity.isConnected()) {
            Intent i = new Intent(this, AddCustomerActivity.class);
            i.putExtra("customer_id", item.id);
            startActivity(i);
            //finish();
        } else {
            Toast.makeText(this,
                    "Edit action needs internet connection", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onHomeAppointmentsSelected() {
        closeAllFragments();
        replaceAnimatedFragment(new AppointmentListFragment());
    }

    @Override
    public void onHomeCustomersSelected() {
        closeAllFragments();
        replaceAnimatedFragment(new CustomerListFragment());
    }

    @Override
    public void onHomeAppointmentsListItemSelected(int item) {
/*
        Intent i = new Intent(this, AppointmentDetailsActivity.class);
        i.putExtra(Const.Appointment_Id, item);
//        Const.app_id = item.id;
        startActivityForResult(i, APPOINTMENT_DETAIL);
*/

        Fragment fragment = new AppointmentDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.Appointment_Id, item);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onHomeCustomersListItemSelected(int item) {
        CustomerDetailsFragment fragment = new CustomerDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("customer_id", item);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onServiceLocationDetailContactsSelected(ServiceLocationsInfo servicelocation_info) {
/*
        Intent i = new Intent(getActivity(),
                ServiceLocationContactsActivity.class);
        i.putExtra("SILD", service_loc_id);
        startActivity(i);
*/

        ServiceLocationContactsFragment fragment = new ServiceLocationContactsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SILD", servicelocation_info.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onServiceLocationDetailWorkHistorySelected(ServiceLocationsInfo servicelocation_info) {
/*
        Intent i = new Intent(getActivity(),
                WorkHistoryListActivity.class);
        i.putExtra("sid", service_loc_id);
        i.putExtra("cid", cid);
        startActivity(i);
*/

        WorkHistoryListFragment fragment = new WorkHistoryListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sid", servicelocation_info.id);
        bundle.putInt("cid", servicelocation_info.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onServiceLocationContactsListItemSelected(ServiceLocationContactInfo service) {
/*
        Intent i = new Intent(getActivity(),
                ContactDetailActivity.class);
        i.putExtra("SERVICE_CONTACT_ID", service.id);
        startActivity(i);
*/

        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SERVICE_CONTACT_ID", service.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onWorkHistoryListItemSelected(WorkHistroyInfo history) {
/*
        Intent i = new Intent(getActivity(),
                WorkHistoryDetailActivity.class);
        i.putExtra("whid", history.id);
        startActivity(i);
*/

        WorkHistoryDetailFragment fragment = new WorkHistoryDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("whid", history.id);
        fragment.setArguments(bundle);
        addAnimatedFragment(fragment);
    }

    @Override
    public void onCaptureSignatureSaved() {
/*
        Toast.makeText(this,
                "Your signature is successfully saved...",
                Toast.LENGTH_LONG).show();
        Intent i = new Intent(CaptureSignatureFragment.this,
                SignatureActivity.class);
        startActivity(i);
        finish();
*/
/*
        Fragment fragment = new SignatureFragment();
        replaceAnimatedFragment(fragment);
*/
        onBackPressed();
    }

    @Override
    public void onCaptureSignatureCancelClicked() {
/*
        Intent i = new Intent(CaptureSignatureFragment.this,
                SignatureActivity.class);
        startActivity(i);
        finish();
*/
/*
        Fragment fragment = new SignatureFragment();
        replaceAnimatedFragment(fragment);
*/
        onBackPressed();
    }
}
