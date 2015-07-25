package com.anstar.fieldwork;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.anstar.common.BaseLoader;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.dialog.ConfirmDialog;
import com.anstar.models.Account;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates;
import com.anstar.models.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements
        CustomerListFragment.OnCustomerListSelectedListener, ConfirmDialog.OnConfirmDialogListener {

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
    private BaseLoader mBaseLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mBaseLoader = new BaseLoader(this);

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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new HomeFragment(), mDrawerTitles[position - 1])
                        .commit();

                //Utils.showAnimatedFragment(this, new HomeFragment(), mDrawerTitles[position - 1], false);
                break;
            case 1:
                // Calendar
                closeAllFragments();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new AppointmentListFragment(), mDrawerTitles[position - 1])
                        .commit();

//                Utils.showAnimatedFragment(this, new AppointmentListFragment(), mDrawerTitles[position - 1], false);
                break;
            case 2:
                // Customers
                closeAllFragments();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new CustomerListFragment(), mDrawerTitles[position - 1])
                        .commit();


                //Utils.showAnimatedFragment(this, new CustomerListFragment(), mDrawerTitles[position - 1], false);
                break;
            case 3:
                // Settings
                closeAllFragments();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new SettingFragment(), mDrawerTitles[position - 1])
                        .commit();

                //Utils.showAnimatedFragment(this, new SettingFragment(), mDrawerTitles[position - 1], false);
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
    public void onCustomerItemSelected(CustomerInfo item) {
        if (item.isAllreadyLoded) {
            showCustomerDetailsFragment(item.id);
        } else {
            if (NetworkConnectivity.isConnected()) {
                mBaseLoader.showProgress();
                item.RetriveData(new ModelDelegates.UpdateCustomerDelegate() {

                    @Override
                    public void UpdateSuccessFully(CustomerInfo info) {
                        mBaseLoader.hideProgress();
                        showCustomerDetailsFragment(info.id);
                    }

                    @Override
                    public void UpdateFail(String ErrorMessage) {
                        mBaseLoader.hideProgress();

                    }
                });
            } else {
                Toast.makeText(this,
                        "Please check your internet connection.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void closeAllFragments() {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void showCustomerDetailsFragment(int id) {
        CustomerDetailsFragment fragment = new CustomerDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("customer_id", id);
        fragment.setArguments(bundle);
        Utils.showAnimatedFragment(this, fragment, "customer_detail", true);
    }

    private void logOut() {
            Account account = Account.getUser();
            account.isLogin = false;
            try {
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
}
