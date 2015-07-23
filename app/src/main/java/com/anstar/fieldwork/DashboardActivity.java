package com.anstar.fieldwork;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.NotificationCenter;
import com.anstar.models.Account;
import com.anstar.models.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    final private String ITEM_ICON = "item_icon";
    final private String ITEM_TEXT = "item_text";

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

        String[] from = {ITEM_ICON, ITEM_TEXT};
        int[] to = {R.id.item_drawer_list_image, R.id.item_drawer_list_text};

        mListAdapter = new SimpleAdapter(action.getThemedContext(), mList, R.layout.drawer_list, from, to);
        mDrawerList.setAdapter(mListAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setFragment(position);
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);

        // Select Home fragment
        setFragment(1);

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

    private void setFragment(int position) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        //action.setTitle(mDrawerTitles[position - 1]);

        switch (mDrawerValues[position - 1]) {
            case 0:
                // Home
                fragment = new HomeFragment();
                break;
            case 1:
                // Calendar
                fragment = new AppointmentListFragment();
                break;
            case 2:
                // Customers
                fragment = new CustomerListFragment();

                break;
            case 3:
                // Settings
                fragment = new SettingFragment();
                break;
        }
        mDrawerLayout.closeDrawers();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, mDrawerTitles[position - 1])
                .commit();
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
                if (NetworkConnectivity.isConnected()) {
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
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Signout needs internet connection", Toast.LENGTH_LONG)
                            .show();
                }
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
}
