package com.anstar.fieldwork;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.internetbroadcast.SyncHelper;
import com.anstar.models.Account;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.UserInfo;
import com.anstar.models.list.CustomerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements OnClickListener,
		ModelDelegate<PestsTypeInfo> {

	final private String ITEM_ICON = "item_icon";
	final private String ITEM_TEXT = "item_text";

	// Navigation drawer
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private SimpleAdapter mListAdapter;
	private String[] mDrawerTitles;
	private int[] mDrawerValues;

	private Button btnHome, btnAppointments, btnCustomers, btnSettings, btnSyncNow;
	ActionBar action = null;
	SharedPreferences setting;
	static volatile ProgressDialog m_temppd = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		setting = PreferenceManager
				.getDefaultSharedPreferences(DashboardActivity.this);
		m_temppd = new ProgressDialog(this);
		m_temppd.setMessage("Please wait...");

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
		};
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		List<HashMap<String, String>> mList = new ArrayList<HashMap<String,String>>();
		mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
		String[] drawerIcons = getResources().getStringArray(R.array.drawer_icons);
		mDrawerValues = getResources().getIntArray(R.array.drawer_values);

		for (int i = 0; i < mDrawerTitles.length; i++) {

			HashMap<String, String> hm = new HashMap<String,String>();

			int id_icon = getResources().getIdentifier(drawerIcons[i], "drawable", this.getPackageName());
			hm.put(ITEM_ICON, Integer.toString(id_icon));
			hm.put(ITEM_TEXT, mDrawerTitles[i]);

			mList.add(hm);
		}

		View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
		mDrawerList.addHeaderView(header, null, false);

		String[] from = { ITEM_ICON, ITEM_TEXT };
		int[] to = { R.id.item_drawer_list_image , R.id.item_drawer_list_text};

		mListAdapter = new SimpleAdapter(getSupportActionBar().getThemedContext(), mList, R.layout.drawer_list, from, to);
		mDrawerList.setAdapter(mListAdapter);

		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectDrawerItem(position);
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		Fragment fragment = new HomeFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragment != null) {
			getSupportActionBar().setTitle(mDrawerTitles[0]);
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment, "calendar_fragment")
					.commit();
		}

/*

        btnAppointments = (Button) findViewById(R.id.btnAppointments);
        btnCustomers = (Button) findViewById(R.id.btnCustomers);
        btnSettings = (Button) findViewById(R.id.btnSetting);
        btnSyncNow = (Button) findViewById(R.id.btnSyncNow);

        btnAppointments.setOnClickListener(this);
		btnCustomers.setOnClickListener(this);
		btnSettings.setOnClickListener(this);
		btnSyncNow.setOnClickListener(this);

*/
		try {
			UserInfo.Instance().load(new ModelDelegate<UserInfo>() {
				@Override
				public void ModelLoaded(ArrayList<UserInfo> list) {
					if (list != null) {
						UserInfo user = list.get(0);
						if (user.hide_customer_details) {
							// btnCustomers.setVisibility(View.GONE);
						} else {
							// btnCustomers.setVisibility(View.VISIBLE);
						}
					}
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationCenter.Instance().addObserver(DashboardActivity.this,
				"hidedash", "hideprogressdialog", null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
				if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
					mDrawerLayout.closeDrawers();
				} else {
					mDrawerLayout.openDrawer(Gravity.LEFT);
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
			mDrawerLayout.closeDrawers();
		} else {
			super.onBackPressed();
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

	private void selectDrawerItem(int position) {
		Fragment fragment = null;
		FragmentManager fragmentManager = getSupportFragmentManager();

		getSupportActionBar().setTitle(mDrawerTitles[position - 1]);

		switch (mDrawerValues[position - 1]) {
			case 0:
				// Home
				fragment = new HomeFragment();
				break;
			case 1:
				// Calendar
				fragment = new AppointmentListFragment();
				//startActivity(new Intent(DashboardActivity.this,
				//        AppointmentList.class));
				break;
			case 2:
				// Customers
				Account a = Account.getUser();
				if (a != null) {
					if (!a.isCustomerLoded) {
						// AlertToloadCustomer();
						downloadCustomer();
					} else {
						if (CustomerList.Instance().getAllCustomer().size() == 0) {
							downloadCustomer();
							a.isCustomerLoded = false;
							try {
								a.save();
							} catch (ActiveRecordException e) {
								e.printStackTrace();
							}

						} else {
//Oleg !!!!                            startActivity(new Intent(DashboardActivity.this,
//                                    CustomerListActivity.class));
						}
					}
					fragment = new CustomerListFragment();
				}

				break;
			case 3:
				// Settings
				fragment = new SettingFragment();
//                startActivity(new Intent(DashboardActivity.this,
//                        SettingActivity.class));
				break;
		}
		mDrawerLayout.closeDrawers();

		if (fragment != null) {
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment, "calendar_fragment")
					.commit();
		}
	}

    /*private String getIconId(String) {
        int id_icon = getResources().getIdentifier(menuItemResIcon[i], "drawable", this.getPackageName());
    }*/

	protected void SettingshowProgress() {
		if (m_temppd != null) {
			Utils.LogInfo("Show Progress-->>");
			m_temppd.show();
		}
		// else {
		// m_pd = new ProgressDialog(this);
		// m_pd.setMessage("Please wait...");
		// m_pd.show();
		// }
	}

	protected void SettingshowProgress(String msg) {
		if (m_temppd != null) {
			Utils.LogInfo("Show Progress-->>");
			m_temppd.setCancelable(false);
			m_temppd.setMessage(msg);
			m_temppd.show();
		}
		// else {
		// m_pd = new ProgressDialog(this);
		// m_pd.setCancelable(false);
		// m_pd.setMessage(msg);
		// m_pd.show();
		// }
	}

	protected void SettinghideProgress() {
		if (m_temppd != null) {
			Utils.LogInfo("Hide Progress-->>");
			m_temppd.dismiss();

			// m_pd = null;
		}
	}

	private boolean isnet = false;

	public void hideprogressdialog() {
		// runOnUiThread(new Thread(new Runnable() {
		// @Override
		// public void run() {
		// Utils.LogInfo("in dash hide progress function **********&&&&&&&&");
		// hideProgress();
		// }
		// }));
		stopprogress.sendMessage(stopprogress.obtainMessage());
		stopprogress.sendMessage(stopprogress.obtainMessage());
		stopprogress.sendMessage(stopprogress.obtainMessage());
		stopprogress.sendMessage(stopprogress.obtainMessage());

	}

	private Handler stopprogress = new Handler() {
		@Override
		public void handleMessage(Message message) {
			SettinghideProgress();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		boolean isOffline = setting.getBoolean("ISOFFLINE", false);
		if (isOffline == false) {
//			btnSyncNow.setVisibility(View.GONE);
		} else {
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mMobile = connManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (!mWifi.isAvailable() && !mMobile.isAvailable()) {
				isnet = false;
				btnSyncNow.setVisibility(View.VISIBLE);
				btnSyncNow.setBackgroundColor(Color.parseColor("#bdbdbd"));
			} else {
				isnet = true;
				btnSyncNow.setVisibility(View.VISIBLE);
				btnSyncNow.setBackgroundColor(Color.parseColor("#7EBA00"));
			}
		}
	}

	// @Override
	// public void appointmentListLoaded(AppointmentList list) {
	// if(pd != null){
	// pd.dismiss();
	// }
	// startActivity(new Intent(DashboardActivity.this,
	// AppointmentActivity.class));
	//
	// }
	//
	// @Override
	// public void appointmentListLoadUnsuccess(String error) {
	// if(pd != null){
	// pd.dismiss();
	// }
	// Toast.makeText(DashboardActivity.this, error, Toast.LENGTH_LONG).show();
	// }

	@Override
	public void onClick(View v) {
		if (v == btnAppointments) {
			startActivity(new Intent(DashboardActivity.this,
					AppointmentListFragment.class));
			// AppointmentList.Instance().loadFromService(this);
		} else if (v == btnCustomers) {
			Account a = Account.getUser();
			if (a != null) {
				if (!a.isCustomerLoded) {
					// AlertToloadCustomer();
					downloadCustomer();
				} else {
					if (CustomerList.Instance().getAllCustomer().size() == 0) {
						downloadCustomer();
						a.isCustomerLoded = false;
						try {
							a.save();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}

					} else {
						startActivity(new Intent(DashboardActivity.this,
								CustomerListFragment.class));
					}
				}
			}

		} else if (v == btnSettings) {
			startActivity(new Intent(DashboardActivity.this,
					SettingFragment.class));
		} else if (v == btnSyncNow) {
			if (isnet = false) {
				Toast.makeText(
						getApplicationContext(),
						"Syncing is disabled until you have a working connection",
						Toast.LENGTH_LONG).show();
			} else {
				Sync();
			}
		}
	}

	public void Sync() {
		if (NetworkConnectivity.isConnectedwithoutmode()) {
			SettingshowProgress("Please wait while syncing data to server");
			SyncHelper.Instance().startSyncing();
		} else {
			Toast.makeText(getApplicationContext(),
					"Data will be synced when internet connected",
					Toast.LENGTH_LONG).show();
		}
	}

	public void AlertToloadCustomer() {
		String message = "Hey, this is going to take a few minutes while we sync your customer database. We suggest that you do this over WIFI";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				DashboardActivity.this);
		alt_bld.setMessage(message)
				.setCancelable(true)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								downloadCustomer();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						startActivity(new Intent(DashboardActivity.this,
								CustomerListFragment.class));
						dialog.cancel();
					}
				});

		AlertDialog alert = alt_bld.create();
		alert.setTitle("Alert");
		alert.show();
	}

	private void downloadCustomer() {
		if (NetworkConnectivity.isConnected()) {
			SettingshowProgress("Syncing customer database");
			try {
				CustomerList.Instance().refreshCustomerList(
						new ModelDelegate<CustomerInfo>() {

							@Override
							public void ModelLoaded(ArrayList<CustomerInfo> list) {
								Account info = Account.getUser();
								info.LastModifiedCustomerData = String
										.valueOf(System.currentTimeMillis());
								info.isCustomerLoded = true;
								try {
									info.save();
								} catch (ActiveRecordException e) {
									e.printStackTrace();
								}
								startActivity(new Intent(
										DashboardActivity.this,
										CustomerListFragment.class));
								SettinghideProgress();
							}

							@Override
							public void ModelLoadFailedWithError(String error) {
								Toast.makeText(DashboardActivity.this, error,
										Toast.LENGTH_LONG).show();
								SettinghideProgress();
							}
						});
			} catch (Exception e) {
				SettinghideProgress();
				e.printStackTrace();
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"Syncing customer data needs internet connection.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.logout_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public void ModelLoaded(ArrayList<PestsTypeInfo> list) {
	}

	@Override
	public void ModelLoadFailedWithError(String error) {

	}

}
