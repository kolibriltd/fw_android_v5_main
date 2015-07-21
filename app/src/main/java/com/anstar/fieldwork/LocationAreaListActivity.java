package com.anstar.fieldwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.LocationInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.TempLocation;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.LocationInfoList;
import com.anstar.models.list.ServiceLocationsList;

import java.util.ArrayList;

public class LocationAreaListActivity extends BaseActivity implements
		OnClickListener, ModelDelegate<LocationInfo> {

	private ListView lstLocationAreaList;
	int appointment_id;
	private EditText edtSearch;
	private ImageView imgCancel;
	// MyAppointmentAdapter m_adapter;
	private LocationAreaAdapter m_adapter = null;
	private ArrayList<LocationAreaInfo> m_locationareas = null;
	ActionBar action = null;
	private int location_type_id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conditions_list);
		action = getSupportActionBar();
		// action.setTitle("Location Areas");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Location Areas</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		lstLocationAreaList = (ListView) findViewById(R.id.lstMain);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		imgCancel = (ImageView) findViewById(R.id.imgCancel);
		imgCancel.setOnClickListener(this);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_adapter = new LocationAreaAdapter(m_locationareas);
					lstLocationAreaList.setAdapter(m_adapter);
				}
				ArrayList<LocationAreaInfo> temp = new ArrayList<LocationAreaInfo>();
				for (LocationAreaInfo c : m_locationareas) {
					if (c.name.toLowerCase().contains(text.toLowerCase())) {
						temp.add(c);
					}
				}
				m_adapter = new LocationAreaAdapter(temp);
				lstLocationAreaList.setAdapter(m_adapter);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			LocationInfoList.Instance().load(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try {
		// showProgress();
		// LocationInfo.Instance().load(this);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public class LocationAreaAdapter extends BaseAdapter {
		ArrayList<LocationAreaInfo> m_list = new ArrayList<LocationAreaInfo>();

		public LocationAreaAdapter(ArrayList<LocationAreaInfo> list) {
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
				LayoutInflater li = getLayoutInflater();
				rowView = li.inflate(R.layout.main_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final LocationAreaInfo area = m_list.get(position);
			holder.main_item_text.setText(area.name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (area.id < 0) {
						try {
							LocationInfoList.Instance().load(LocationAreaListActivity.this);
							Toast.makeText(getApplicationContext(),
									"Please select again, data was not proper",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						TempLocation.AddArea(area.name, area.id);
						Intent i = new Intent();
						i.putExtra("isFromLocation", true);
						setResult(Activity.RESULT_OK, i);
						// startActivity(i);
						finish();
					}
				}
			});
			return rowView;
		}
	}

	public static class ViewHolder {
		TextView main_item_text;
		RelativeLayout rl_main_list_item;
	}

	@Override
	public void onClick(View v) {
		if (v == imgCancel) {
			edtSearch.setText("");
		}
	}

	@Override
	public void ModelLoaded(ArrayList<LocationInfo> list) {
		hideProgress();
		// m_locationareas = list;
		bindData();
	}

	@Override
	public void ModelLoadFailedWithError(String error) {
		hideProgress();
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
	}

	private void bindData() {
		m_locationareas = new ArrayList<LocationAreaInfo>();
		AppointmentInfo appointment = AppointmentModelList.Instance()
				.getAppointmentById(appointment_id);
		int ser_id = appointment.service_location_id;
		ServiceLocationsInfo ser_info = ServiceLocationsList.Instance()
				.getServiceLocationById(ser_id);
		if (ser_info != null) {
			location_type_id = ser_info.location_type_id;
		}
		m_locationareas = LocationAreaInfo
				.getLocationAreaByType(location_type_id);
		if (m_locationareas.size() > 0) {
			m_locationareas = Utils.Instance().sortLocationCollections(
					m_locationareas);
			m_adapter = new LocationAreaAdapter(m_locationareas);
			lstLocationAreaList.setAdapter(m_adapter);
		} else {
			Toast.makeText(getApplicationContext(), "No Location areas",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.add_pest_menu, menu);
		// if(!NetworkConnectivity.isConnected()){
		// menu.findItem(R.id.btnAddPest).setVisible(false);
		// }
		return super.onCreateOptionsMenu(menu);
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnAddPest:
			Intent i = new Intent(LocationAreaListActivity.this,
					AddLocationActivity.class);
			i.putExtra("location_type_id", location_type_id);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
