package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates.UpdateCustomerDelegate;
import com.anstar.models.PhoneEmailInfo;
import com.anstar.models.PhoneEmailInfo.ContactType;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.ServiceLocationsList;

import java.util.ArrayList;

public class CustomerDetailsActivity extends AppCompatActivity implements
		OnClickListener {

	int customer_id;
	private TextView txtName, txtBAddress, txtBAddress2;
	private RelativeLayout rlContacts, rlServiceLocations;
	private ListView lstContact;

	boolean isFromStarted;
	private CustomerInfo customerinfo = null;
	//ActionBar action = null;
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_details);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

/*
		// imgMap = (ImageView) findViewById(R.id.imgMap);
		action = getSupportActionBar();
		// action.setTitle("Customer Details");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Customer Details</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);
		mBaseLoader = new BaseLoader(this);

		txtBAddress = (TextView) findViewById(R.id.txtBAddress);
		txtBAddress2 = (TextView) findViewById(R.id.txtBAddress2);
		txtName = (TextView) findViewById(R.id.txtCustomerName);
		// txtSDrivingDirections = (TextView)
		// findViewById(R.id.txtSDrivingDirections);
		// txtBDrivingDirections = (TextView)
		// findViewById(R.id.txtBDrivingDirections);
		rlContacts = (RelativeLayout) findViewById(R.id.rlContacts);
		rlServiceLocations = (RelativeLayout) findViewById(R.id.rlServiceLocation);

		lstContact = (ListView) findViewById(R.id.lstBillingContact);
		lstContact.setDivider(null);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			customer_id = b.getInt("customer_id");
		}
		customerinfo = CustomerList.Instance().getCustomerById(customer_id);

		rlContacts.setOnClickListener(this);

		loadCustomer();

		rlContacts.setOnClickListener(this);
		rlServiceLocations.setOnClickListener(this);
	};

	public void loadCustomer() {

		if (!customerinfo.isAllreadyLoded) {
			if (NetworkConnectivity.isConnected()) {
				mBaseLoader.showProgress();
				customerinfo.RetriveData(new UpdateCustomerDelegate() {

					@Override
					public void UpdateSuccessFully(CustomerInfo info) {
						mBaseLoader.hideProgress();
						ArrayList<ServiceLocationsInfo> sinfo = ServiceLocationsList
								.Instance().getServiceLocationByCustId(
										customer_id);
						customerinfo = info;
						LoadValues();
					}

					@Override
					public void UpdateFail(String ErrorMessage) {
						mBaseLoader.hideProgress();

					}
				});
			} else {
				Toast.makeText(getApplicationContext(),
						"Please check your internet connection.",
						Toast.LENGTH_LONG).show();
				finish();
			}
		} else {
			LoadValues();
		}
	}

	@Override
	public void onPause() {
		// SaveData s = new SaveData();
		// s.execute();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		if (v == rlContacts) {
			Intent i = new Intent(CustomerDetailsActivity.this,
					CustomerContactListActivity.class);
			i.putExtra("CID", customer_id);
			startActivity(i);
		} else if (v == rlServiceLocations) {
			Intent i = new Intent(CustomerDetailsActivity.this,
					ServiceLocationListActivity.class);
			i.putExtra("CID", customer_id);
			startActivity(i);
		}
		// if (v == rlSDirections) {
		//
		// StringBuilder sb = new StringBuilder();
		// sb.append(customerinfo.service_suite + " ")
		// .append(customerinfo.service_street + " ")
		// .append(customerinfo.service_street_two + " ")
		// .append(customerinfo.service_city + " ")
		// .append(customerinfo.service_state + " ")
		// .append(customerinfo.service_zip + " ");
		//
		// Address location = Utils
		// .getLocationFromAddress(sb.toString(), this);
		//
		// Location curruntLoc = Utils.getCurrentLocation(this);
		// if (curruntLoc != null && location != null) {
		// String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="
		// + curruntLoc.getLatitude() + ","
		// + curruntLoc.getLongitude() + "&daddr="
		// + location.getLatitude() + ","
		// + location.getLongitude();
		// Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		// startActivity(intent);
		// }
		//
		// } else if (v == rlBDirections) {
		//
		// StringBuilder sb = new StringBuilder();
		// sb.append(customerinfo.billing_suite + " ")
		// .append(customerinfo.billing_street + " ")
		// .append(customerinfo.billing_street_two + " ")
		// .append(customerinfo.billing_city + " ")
		// .append(customerinfo.billing_state + " ")
		// .append(customerinfo.billing_zip + " ");
		//
		// Address location = Utils
		// .getLocationFromAddress(sb.toString(), this);
		//
		// Location curruntLoc = Utils.getCurrentLocation(this);
		// if (curruntLoc != null && location != null) {
		// String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="
		// + curruntLoc.getLatitude() + ","
		// + curruntLoc.getLongitude() + "&daddr="
		// + location.getLatitude() + ","
		// + location.getLongitude();
		// Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		// startActivity(intent);
		// }
		// }

	}

	public void LoadValues() {
		String name = "";
		if (customerinfo.customer_type.equalsIgnoreCase("Commercial")) {
			name = customerinfo.name;
		} else {
			name = customerinfo.name_prefix + " " + customerinfo.first_name
					+ " " + customerinfo.last_name;
		}
		txtName.setText(name);
		// txtSAddress.setText(customerinfo.service_street + " "
		// + customerinfo.service_street_two);
		// StringBuilder sb = new StringBuilder();
		// if (customerinfo.service_city.length() > 0) {
		// sb.append(customerinfo.service_city + ", ");
		// }
		// if (customerinfo.service_state.length() > 0) {
		// sb.append(customerinfo.service_state + " ");
		// }
		// if (customerinfo.service_zip.length() > 0) {
		// sb.append(customerinfo.service_zip);
		// }
		// txtSAddress2.setText(sb.toString());

		txtBAddress.setText(customerinfo.billing_street + " "
				+ customerinfo.billing_street_two);
		StringBuilder sb1 = new StringBuilder();
		if (customerinfo.billing_city.length() > 0) {
			sb1.append(customerinfo.billing_city + ", ");
		}
		if (customerinfo.billing_state.length() > 0) {
			sb1.append(customerinfo.billing_state + " ");
		}
		if (customerinfo.billing_zip.length() > 0) {
			sb1.append(customerinfo.billing_zip);
		}

		txtBAddress2.setText(sb1.toString());

		ArrayList<PhoneEmailInfo> content = new ArrayList<PhoneEmailInfo>();
		PhoneEmailInfo info = null;

		if (customerinfo.billing_phone != null
				&& customerinfo.billing_phone.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = customerinfo.billing_phone_kind;
			info.Value = customerinfo.billing_phone;
			info.Type = ContactType.Phone.toString();
			content.add(info);
		}
		if (customerinfo.billing_phones != null
				&& customerinfo.billing_phones.size() > 0) {
			int i = 0;

			ArrayList<String> temp_contact = new ArrayList<String>();
			for (String s : customerinfo.billing_phones) {
				if (s != null && s.length() > 0) {
					s = s.replace("[", "");
					s = s.replace("]", "");
					if (s.contains(",")) {
						String h[] = s.split(",");
						for (int j = 0; j < h.length; j++) {
							if (!temp_contact.contains(h[j])) {
								temp_contact.add(h[j]);
							}
						}
					} else {
						temp_contact.add(s);
					}
				}
			}
			ArrayList<String> temp_kind = new ArrayList<String>();
			for (String s : customerinfo.billing_phones_kinds) {
				if (s != null && s.length() > 0) {
					s = s.replace("[", "");
					s = s.replace("]", "");
					if (s.contains(",")) {
						String h[] = s.split(",");
						for (int j = 0; j < h.length; j++) {
							if (!temp_contact.contains(h[j])) {
								temp_kind.add(h[j]);
							}
						}
					} else {
						temp_kind.add(s);
					}
				}
			}
			for (String ph : temp_contact) {
				if (ph.length() > 0) {
					info = new PhoneEmailInfo();
					info.Kind = temp_kind.get(i);
					info.Value = ph;
					info.Type = ContactType.Phone.toString();
					content.add(info);
					i++;
				}
			}
		}
		if (customerinfo.invoice_email != null
				&& customerinfo.invoice_email.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = "Email";
			info.Value = customerinfo.invoice_email;
			info.Type = ContactType.Email.toString();
			content.add(info);
		}

		MyContactAdapter adapter = new MyContactAdapter(content);
		lstContact.setAdapter(adapter);
		Utils.setListViewHeightBasedOnChildren(lstContact);
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public class MyContactAdapter extends BaseAdapter {

		ArrayList<PhoneEmailInfo> m_list = new ArrayList<PhoneEmailInfo>();

		public MyContactAdapter(ArrayList<PhoneEmailInfo> temp) {
			m_list = temp;
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
			return m_list.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View rowView = convertView;
			holder = new ViewHolder();
			final PhoneEmailInfo info = m_list.get(position);
			if (rowView == null) {
				LayoutInflater li = getLayoutInflater();
				rowView = li.inflate(R.layout.phone_email_item, null);
				rowView.setTag(holder);
				holder.txtKind = (TextView) rowView.findViewById(R.id.txtKind);
				holder.txtValue = (TextView) rowView
						.findViewById(R.id.txtValue);
				holder.rl = (RelativeLayout) rowView
						.findViewById(R.id.rlContact);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			holder.txtKind.setText(info.Kind);
			holder.txtValue.setText(info.Value);
			holder.rl.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (info.Type.equalsIgnoreCase(ContactType.Phone.toString())) {
						Utils.callPhone(info.Value,
								CustomerDetailsActivity.this);
					}
					if (info.Type.equalsIgnoreCase(ContactType.Email.toString())) {
						Utils.sendEmail(info.Value,
								CustomerDetailsActivity.this);
					}

				}
			});

			return rowView;
		}
	}

	public static class ViewHolder {
		TextView txtKind, txtValue;
		RelativeLayout rl;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnEdit:
			if (NetworkConnectivity.isConnected()) {
				Intent i = new Intent(CustomerDetailsActivity.this,
						AddCustomerActivity.class);
				i.putExtra("customer_id", customer_id);
				startActivity(i);
				//finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"Signout needs internet connection", Toast.LENGTH_LONG)
						.show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
