package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.models.CustomerInfo;
import com.anstar.models.PhoneEmailInfo;
import com.anstar.models.PhoneEmailInfo.ContactType;
import com.anstar.models.list.CustomerList;

import java.util.ArrayList;

public class CustomerDetailsFragment extends Fragment implements
		OnClickListener {

	private int customer_id;
	private TextView txtName, txtBAddress, txtBAddress2;
	private RelativeLayout rlContacts, rlServiceLocations;
	private ListView lstContact;

	private CustomerInfo mCustomerInfo = null;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_customer_details, container, false);

		txtBAddress = (TextView) v.findViewById(R.id.txtBAddress);
		txtBAddress2 = (TextView) v.findViewById(R.id.txtBAddress2);
		txtName = (TextView) v.findViewById(R.id.txtCustomerName);
		rlContacts = (RelativeLayout) v.findViewById(R.id.rlContacts);
		rlServiceLocations = (RelativeLayout) v.findViewById(R.id.rlServiceLocation);
		lstContact = (ListView) v.findViewById(R.id.lstBillingContact);

		lstContact.setDivider(null);
        rlContacts.setOnClickListener(this);
        rlContacts.setOnClickListener(this);
        rlServiceLocations.setOnClickListener(this);
        LoadValues();

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        customer_id = getArguments().getInt("customer_id");
        mCustomerInfo = CustomerList.Instance().getCustomerById(customer_id);

        setHasOptionsMenu(true);
	};

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_customer_details);
    }

	@Override
	public void onClick(View v) {
		if (v == rlContacts) {
			CustomerContactListFragment fragment = new CustomerContactListFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("CID", customer_id);
			fragment.setArguments(bundle);
			((DashboardActivity) getActivity()).addAnimatedFragment(fragment);
		} else if (v == rlServiceLocations) {
			ServiceLocationListFragment fragment = new ServiceLocationListFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("CID", customer_id);
			fragment.setArguments(bundle);
			((DashboardActivity) getActivity()).addAnimatedFragment(fragment);
		}
	}

	public void LoadValues() {
		String name = "";
		if (mCustomerInfo.customer_type.equalsIgnoreCase("Commercial")) {
			name = mCustomerInfo.name;
		} else {
			name = mCustomerInfo.name_prefix + " " + mCustomerInfo.first_name
					+ " " + mCustomerInfo.last_name;
		}
		txtName.setText(name);

		txtBAddress.setText(mCustomerInfo.billing_street + " "
				+ mCustomerInfo.billing_street_two);
		StringBuilder sb1 = new StringBuilder();
		if (mCustomerInfo.billing_city.length() > 0) {
			sb1.append(mCustomerInfo.billing_city + ", ");
		}
		if (mCustomerInfo.billing_state.length() > 0) {
			sb1.append(mCustomerInfo.billing_state + " ");
		}
		if (mCustomerInfo.billing_zip.length() > 0) {
			sb1.append(mCustomerInfo.billing_zip);
		}

		txtBAddress2.setText(sb1.toString());

		ArrayList<PhoneEmailInfo> content = new ArrayList<PhoneEmailInfo>();
		PhoneEmailInfo info = null;

		if (mCustomerInfo.billing_phone != null
				&& mCustomerInfo.billing_phone.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = mCustomerInfo.billing_phone_kind;
			info.Value = mCustomerInfo.billing_phone;
			info.Type = ContactType.Phone.toString();
			content.add(info);
		}
		if (mCustomerInfo.billing_phones != null
				&& mCustomerInfo.billing_phones.size() > 0) {
			int i = 0;

			ArrayList<String> temp_contact = new ArrayList<String>();
			for (String s : mCustomerInfo.billing_phones) {
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
			for (String s : mCustomerInfo.billing_phones_kinds) {
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
		if (mCustomerInfo.invoice_email != null
				&& mCustomerInfo.invoice_email.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = "Email";
			info.Value = mCustomerInfo.invoice_email;
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
				LayoutInflater li = getActivity().getLayoutInflater();
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
								getActivity());
					}
					if (info.Type.equalsIgnoreCase(ContactType.Email.toString())) {
						Utils.sendEmail(info.Value,
								getActivity());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_customer_details, menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnEdit:
			if (NetworkConnectivity.isConnected()) {
				Intent i = new Intent(getActivity(),
						AddCustomerActivity.class);
				i.putExtra("customer_id", customer_id);
				startActivity(i);
				//finish();
			} else {
				Toast.makeText(getActivity(),
						"Edit action needs internet connection", Toast.LENGTH_LONG)
						.show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
