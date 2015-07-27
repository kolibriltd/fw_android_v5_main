package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.list.ServiceLocationsList;

import java.util.ArrayList;

public class ServiceLocationListActivity extends AppCompatActivity {

	private ListView lstServiceLocations;
	private EditText edtSearch;
	private ImageView imgSearch;
	private int Customer_id = 0;
	private ArrayList<ServiceLocationsInfo> m_locations;
	private boolean FromAddAppointment = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_service_location_list);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("CID")) {
				Customer_id = b.getInt("CID");
			}
			if (b.containsKey("FromAddAppointment")) {
				FromAddAppointment = b.getBoolean("FromAddAppointment");
			}
		}
/*
		ActionBar action = getSupportActionBar();
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Service Location</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		lstServiceLocations = (ListView) findViewById(R.id.lstServices_list);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		imgSearch = (ImageView) findViewById(R.id.imgSearchServiceLocation);
		m_locations = ServiceLocationsList.Instance()
				.getServiceLocationByCustId(Customer_id);
		if (m_locations == null) {
			m_locations = new ArrayList<ServiceLocationsInfo>();
		}
		CustomAdapter adapter = new CustomAdapter(m_locations);
		lstServiceLocations.setAdapter(adapter);

		edtSearch.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				ArrayList<ServiceLocationsInfo> m_temp = new ArrayList<ServiceLocationsInfo>();
				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_temp = m_locations;

				} else {
					for (ServiceLocationsInfo c : m_locations) {
						if (c.name.toString().toLowerCase()
								.contains(text.toLowerCase())) {
							m_temp.add(c);
						}
					}
				}
				CustomAdapter adapter = new CustomAdapter(m_temp);
				lstServiceLocations.setAdapter(adapter);
			}
		});
	}

	public class CustomAdapter extends BaseAdapter {
		ArrayList<ServiceLocationsInfo> m_list = new ArrayList<ServiceLocationsInfo>();

		public CustomAdapter(ArrayList<ServiceLocationsInfo> list) {
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
				holder.img = (ImageView) rowView.findViewById(R.id.imgCancel);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			holder.img.setVisibility(View.VISIBLE);
			holder.img.setBackgroundResource(R.drawable.navigation_arrow);
			final ServiceLocationsInfo service = m_list.get(position);
			if (service != null) {
				holder.main_item_text.setText(service.name);
			}
			holder.rl_main_list_item
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (FromAddAppointment) {
								Intent i = new Intent();
								i.putExtra("service_location_id", service.id);
								setResult(RESULT_OK, i);
								finish();
							} else {
								Intent i = new Intent(
										ServiceLocationListActivity.this,
										ServiceLocationDetailActivity.class);
								i.putExtra("SLID", service.id);
								i.putExtra("cid", Customer_id);
								startActivity(i);
							}
						}
					});
			return rowView;
		}
	}

	public static class ViewHolder {
		TextView main_item_text;
		ImageView img;
		RelativeLayout rl_main_list_item;
	}

}
