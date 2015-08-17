package com.anstar.fieldwork;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.RelativeLayout;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

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
        CaptureSignatureFragment.CaptureSignatureFragmentListener,
        IntroductionFragment.OnIntroductionFragmentInteractionListener,
        LoginFragment.OnLoginFragmentInteractionListener,
        AppointmentDetails2Fragment.OnFragmentInteractionListener,
        AppointmentListFragment.OnFragmentInteractionListener {

    private static final String FRAGMENT_TAG_INTRODUCTION = "tag_introduction";
    private static final String FRAGMENT_TAG_LOGIN = "tag_login";
    private static final String FRAGMENT_TAG_SPLASH = "tag_splash";
    private static final String FRAGMENT_TAG_HOME = "tag_home";
    private static final String FRAGMENT_TAG_APPOINTMENT = "tag_appointment";
    private static final String FRAGMENT_TAG_CUSTOMER_LIST = "tag_customer_list";
    private static final String FRAGMENT_TAG_SETTINGS = "tag_settings";
    private static final String FRAGMENT_TAG_APPOINTMENT_DETAIL = "tag_appointment_detail";

    private static final int ACTIVITY_APPOINTMENT_DETAIL = 1;
    private static final int ACTIVITY_CAPTURE_SIGNATURE = 2;
    private static final int ACTIVITY_ADD_LINE_ITEM = 3;
    private static final int ACTIVITY_LINE_ITEMS = 4;
    private static final int ACTIVITY_ADD_NOTES = 5;
    private static final int ACTIVITY_MATERIAL_USAGE_LIST = 6;
    private static final int ACTIVITY_ADD_PHOTOS = 7;
    private static final int ACTIVITY_PDF_FORMS = 8;

    private static int SPLASH_TIME_OUT = 3000;

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
    private FloatingActionsMenu mFloatingActionsMenu;

    private FragmentManager.OnBackStackChangedListener
            mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            syncActionBarArrowState();
        }
    };
    private RelativeLayout mShadow;

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
                mFloatingActionsMenu.collapse();
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

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(mOnBackStackChangedListener);

        NotificationCenter.Instance().addObserver(DashboardActivity.this,
                "hidedash", "hideprogressdialog", null);

        if (isLogin()) {
            replaceFadeAnimatedFragment(new AppointmentListFragment(), FRAGMENT_TAG_APPOINTMENT);
        } else {
            findViewById(R.id.toolbar).setVisibility(View.GONE);
            mDrawerLayout.
                    setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            if (fm.findFragmentByTag(FRAGMENT_TAG_SPLASH) == null) {
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.container, new SplashFragment(), FRAGMENT_TAG_SPLASH);
                transaction.commit();
            }

            if (fm.findFragmentByTag(FRAGMENT_TAG_INTRODUCTION) == null &&
                    fm.findFragmentByTag(FRAGMENT_TAG_LOGIN) == null) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        replaceFadeAnimatedFragment(new IntroductionFragment(), FRAGMENT_TAG_INTRODUCTION);
                    }
                }, SPLASH_TIME_OUT);
            }
        }
        mShadow = (RelativeLayout) findViewById(R.id.shadow);
        mShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatingActionsMenu.collapse();
            }
        });
        mFloatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_actions_menu);
        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {

                //mFloatingActionsMenu.setEnabled(false);
                mShadow.clearAnimation();
                mShadow.setAlpha(0.0f);
                mShadow.setVisibility(View.VISIBLE);
                mShadow.animate()
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mShadow.setVisibility(View.VISIBLE);
                                //mFloatingActionsMenu.setEnabled(true);
                            }
                        });
            }

            @Override
            public void onMenuCollapsed() {

                //mFloatingActionsMenu.setEnabled(false);
                mShadow.clearAnimation();
                mShadow.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mShadow.setVisibility(View.GONE);
                                //mFloatingActionsMenu.setEnabled(true);
                            }
                        });
            }
        });

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab_action_add_notes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "1", Toast.LENGTH_LONG).show();
                mFloatingActionsMenu.collapse();
            }
        });
        button = (FloatingActionButton) findViewById(R.id.fab_action_add_chemical);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardActivity.this, "2", Toast.LENGTH_LONG).show();
            }
        });
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

    public boolean isLogin() {
        Account account = Account.getUser();
        if (account != null) {
            if (account.isLogin) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Sets fragment as main view
     *
     * @param position navigation drawer's ListView's cursor position. Based on 1
     */
    private void useItem(int position) {

        switch (mDrawerValues[position - 1]) {
            case 0:
                // Home
                closeAllFragments();
                replaceAnimatedFragment(new HomeFragment(), FRAGMENT_TAG_HOME);
                break;
            case 1:
                // Calendar
                closeAllFragments();
                replaceAnimatedFragment(new AppointmentListFragment(), FRAGMENT_TAG_APPOINTMENT);
                break;
            case 2:
                // Customers
                closeAllFragments();
                replaceAnimatedFragment(new CustomerListFragment(), FRAGMENT_TAG_CUSTOMER_LIST);
                break;
            case 3:
                // Settings
                closeAllFragments();
                replaceAnimatedFragment(new SettingFragment(), FRAGMENT_TAG_SETTINGS);
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
        getMenuInflater().inflate(R.menu.activity_dashboard, menu);
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
        if (mFloatingActionsMenu.isExpanded()) {
            mFloatingActionsMenu.collapse();
            mShadow.setVisibility(View.GONE);
            return;
        }
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
        if (requestCode == ACTIVITY_APPOINTMENT_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                String path = data.getStringExtra("printpath");
                if (path.length() > 0)
                    print(path);
            }
        } else if (requestCode == ACTIVITY_CAPTURE_SIGNATURE) {
            if (resultCode == Activity.RESULT_OK) {
                AppointmentDetails2Fragment fragment = (AppointmentDetails2Fragment)
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APPOINTMENT_DETAIL);
                if (fragment != null) {
                    fragment.refresh();
                }
            }
        } else if (requestCode == ACTIVITY_ADD_LINE_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                AppointmentDetails2Fragment fragment = (AppointmentDetails2Fragment)
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APPOINTMENT_DETAIL);
                if (fragment != null) {
                    fragment.refresh();
                }
            }
        } else if (requestCode == ACTIVITY_LINE_ITEMS) {
            if (resultCode == Activity.RESULT_OK) {
                AppointmentDetails2Fragment fragment = (AppointmentDetails2Fragment)
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APPOINTMENT_DETAIL);
                if (fragment != null) {
                    fragment.refresh();
                }
            }
        } else if (requestCode == ACTIVITY_ADD_NOTES) {
            if (resultCode == Activity.RESULT_OK) {
                AppointmentDetails2Fragment fragment = (AppointmentDetails2Fragment)
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APPOINTMENT_DETAIL);
                if (fragment != null) {
                    fragment.refresh();
                }
            }
        } else if (requestCode == ACTIVITY_MATERIAL_USAGE_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                AppointmentDetails2Fragment fragment = (AppointmentDetails2Fragment)
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APPOINTMENT_DETAIL);
                if (fragment != null) {
                    fragment.refresh();
                }
            }
        } else if (requestCode == ACTIVITY_ADD_PHOTOS) {
            if (resultCode == Activity.RESULT_OK) {
                AppointmentDetails2Fragment fragment = (AppointmentDetails2Fragment)
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APPOINTMENT_DETAIL);
                if (fragment != null) {
                    fragment.refresh();
                }
            }
        } else if (requestCode == ACTIVITY_PDF_FORMS) {
            if (resultCode == Activity.RESULT_OK) {
                AppointmentDetails2Fragment fragment = (AppointmentDetails2Fragment)
                        getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APPOINTMENT_DETAIL);
                if (fragment != null) {
                    fragment.refresh();
                }
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
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
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

    private void replaceAnimatedFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(tag) == null) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.anim.fragment_animation_pop_enter, R.anim.fragment_animation_pop_exit,
                    R.anim.fragment_animation_enter, R.anim.fragment_animation_exit);
            transaction.replace(R.id.container, fragment, tag);
            transaction.commit();
        }
    }

    private void replaceFadeAnimatedFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(tag) == null) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in,
                    R.anim.fade_out, R.anim.fade_in);
            transaction.replace(R.id.container, fragment, tag);
            transaction.commit();
        }
    }

    @Override
    public void onCustomerItemSelected(CustomerInfo item) {
        Fragment fragment = new CustomerDetailsFragment();
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
        replaceAnimatedFragment(new AppointmentListFragment(), FRAGMENT_TAG_APPOINTMENT);
    }

    @Override
    public void onHomeCustomersSelected() {
        closeAllFragments();
        replaceAnimatedFragment(new CustomerListFragment(), FRAGMENT_TAG_CUSTOMER_LIST);
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

    @Override
    public void onButtonLoginClick() {
        replaceFadeAnimatedFragment(new LoginFragment(), FRAGMENT_TAG_LOGIN);
    }

    @Override
    public void onLoginDidSuccess() {
        findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        ((DrawerLayout) findViewById(R.id.root_screen_drawer_layout)).
                setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        replaceFadeAnimatedFragment(new AppointmentListFragment(), FRAGMENT_TAG_APPOINTMENT);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHomeAppointmentsListItemSelected(String appointment_Id, int item) {

        addAppointmentDetailsFragment(appointment_Id, item);
    }

    @Override
    public void onAppointmentListItemClick(String appointment_id, int id) {

        addAppointmentDetailsFragment(appointment_id, id);
    }

    private void addAppointmentDetailsFragment(String appointment_id, int id) {

        AppointmentDetails2Fragment aif = new AppointmentDetails2Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt(appointment_id, id);
        aif.setArguments(bundle);
        addAnimatedFragment(aif, FRAGMENT_TAG_APPOINTMENT_DETAIL);
    }

    @Override
    public void onAppointmentDetailsFragmentPaused() {
        mFloatingActionsMenu.setVisibility(View.GONE);
    }

    @Override
    public void onAppointmentDetailsFragmentResumed() {
        mFloatingActionsMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAppointmentDetailsFragmentSignatureTechnicianClick(int appointmentId) {
        // mViewTechSign.clearTech();
        Intent i = new Intent(DashboardActivity.this,
                CaptureSignatureActivity.class);
        i.putExtra("tech", Const.Technitian);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_CAPTURE_SIGNATURE);
    }

    @Override
    public void onAppointmentDetailsFragmentSignatureCustomerClick(int appointmentId) {
        // mViewCustSign.clearCustomer();
        Intent i = new Intent(DashboardActivity.this,
                CaptureSignatureActivity.class);
        i.putExtra("cutomer", Const.Customer);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_CAPTURE_SIGNATURE);
    }

    @Override
    public void onAppointmentDetailsFragmentListItemsButtonEditClick(int appointmentId) {
        Intent i = new Intent(DashboardActivity.this,
                LineItemsActivity.class);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_LINE_ITEMS);
    }

    @Override
    public void onAppointmentDetailsFragmentListItemsLineItemClick(int appointmentId, int position) {
        Intent i = new Intent(DashboardActivity.this,
                LineItemsActivity.class);
        i.putExtra("isedit", true);
        i.putExtra("position", position);
        i.putExtra("isFromDetails", true);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_ADD_LINE_ITEM);
    }

    @Override
    public void onAppointmentDetailsFragmentListItemsButtonPayNowClick(int appointmentId) {

    }

    @Override
    public void onAppointmentDetailsFragmentNotesListItemPrivateNotesClick(int appointmentId) {
        Intent i = new Intent(DashboardActivity.this,
                AddNotesActivity.class);
        i.putExtra(Const.Appointment_Id, appointmentId);
        i.putExtra("note", "private");
        startActivityForResult(i, ACTIVITY_ADD_NOTES);
    }

    @Override
    public void onAppointmentDetailsFragmentNotesListItemPublicNotesClick(int appointmentId) {
        Intent i = new Intent(DashboardActivity.this,
                AddNotesActivity.class);
        i.putExtra(Const.Appointment_Id, appointmentId);
        i.putExtra("note", "public");
        startActivityForResult(i, ACTIVITY_ADD_NOTES);
    }

    @Override
    public void onAppointmentDetailsFragmentChemicalUseListItemButtonAddClick(int appointmentId) {
        Intent i = new Intent(DashboardActivity.this,
                MaterialUsageListActivity.class);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_MATERIAL_USAGE_LIST);
    }

    @Override
    public void onAppointmentDetailsFragmentPhotosListItemAddPhoto(int appointmentId) {
        Intent i = new Intent(DashboardActivity.this,
                AddPhotosActivity.class);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_ADD_PHOTOS);
    }

    @Override
    public void onAppointmentDetailsFragmentPhotosListItemEditPhoto(int appointmentId, int id) {
        Intent i = new Intent(DashboardActivity.this,
                AddPhotosActivity.class);
        i.putExtra("photoid", id);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_ADD_PHOTOS);
    }

    @Override
    public void onAppointmentDetailsFragmentPDFFormsListItemButtonAddClick(int appointmentId) {
        Intent i = new Intent(DashboardActivity.this,
                PdfFormsActivity.class);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_PDF_FORMS);
    }

    @Override
    public void AppointmentDetailsFragmentDevicesListItemButtonAddClick(int appointmentId) {
        Intent i = new Intent(DashboardActivity.this,
                TrapScanningListActivity.class);
        i.putExtra(Const.Appointment_Id, appointmentId);
        startActivityForResult(i, ACTIVITY_PDF_FORMS);
    }
}
