package com.anstar.fieldwork;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anstar.common.Utils;
import com.anstar.models.CustomerContactInfo;
import com.anstar.models.PhoneEmailInfo;
import com.anstar.models.PhoneEmailInfo.ContactType;
import com.anstar.models.ServiceLocationContactInfo;

import java.util.ArrayList;

public class ContactDetailActivity extends AppCompatActivity {

	TextView txtName, txtDescription, txtPhoneKind, txtPhone, txtEmail;
	ListView lstContact;
	int Contact_id = 0;
	int Service_Contact_id = 0;
	ImageView imgCall, imgEmail;
	CustomerContactInfo customerinfo;
	ServiceLocationContactInfo service_customer_info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_detail);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("CONTACT_ID")) {
				Contact_id = b.getInt("CONTACT_ID");
			}
			if (b.containsKey("SERVICE_CONTACT_ID")) {
				Service_Contact_id = b.getInt("SERVICE_CONTACT_ID");
			}
		}
		if (Contact_id != 0) {
			customerinfo = CustomerContactInfo
					.getCustomerContactInfoById(Contact_id);
			service_customer_info = null;
		}
		if (Service_Contact_id != 0) {
			service_customer_info = ServiceLocationContactInfo
					.getServiceLocationContactsById(Service_Contact_id);
			customerinfo = null;
		}
/*
		ActionBar action = getSupportActionBar();
		action = getSupportActionBar();
		// action.setTitle("Add Material");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Contact Detail</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		txtName = (TextView) findViewById(R.id.txtCustomerName);
		txtDescription = (TextView) findViewById(R.id.txtDescription);

		txtPhoneKind = (TextView) findViewById(R.id.txtPhoneKind);
		txtPhone = (TextView) findViewById(R.id.txtPhone);
		txtEmail = (TextView) findViewById(R.id.txtEmail);

		lstContact = (ListView) findViewById(R.id.lstContact);

		imgCall = (ImageView) findViewById(R.id.imgCall);
		imgEmail = (ImageView) findViewById(R.id.imgEmail);

		if (customerinfo != null) {
			txtName.setText(customerinfo.first_name + " "
					+ customerinfo.last_name);
			txtDescription.setText(customerinfo.description);

			txtPhoneKind.setText(customerinfo.phone_kind);
			txtPhone.setText(customerinfo.phone);
			txtEmail.setText(customerinfo.email);
		}
		if (service_customer_info != null) {
			txtName.setText(service_customer_info.first_name + " "
					+ service_customer_info.last_name);
			txtDescription.setText(service_customer_info.description);

			txtPhoneKind.setText(service_customer_info.phone_kind);
			txtPhone.setText(service_customer_info.phone);
			txtEmail.setText(service_customer_info.email);
		}

		imgCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (customerinfo != null) {
					Utils.callPhone(customerinfo.phone,
							ContactDetailActivity.this);
				} else {
					Utils.callPhone(service_customer_info.phone,
							ContactDetailActivity.this);
				}
			}
		});
		imgEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (customerinfo != null) {
					Utils.sendEmail(customerinfo.email,
							ContactDetailActivity.this);
				} else {
					Utils.sendEmail(service_customer_info.email,
							ContactDetailActivity.this);
				}
			}
		});

	}

	public void LoadContacts() {
		if (customerinfo != null) {
			ArrayList<PhoneEmailInfo> content = new ArrayList<PhoneEmailInfo>();
			PhoneEmailInfo info = null;

			if (customerinfo.phone != null && customerinfo.phone.length() > 0) {
				info = new PhoneEmailInfo();
				info.Kind = customerinfo.phone_kind;
				info.Value = customerinfo.phone;
				info.Type = ContactType.Phone.toString();
				content.add(info);
			}
			if (customerinfo.phones != null && customerinfo.phones.size() > 0) {
				int i = 0;

				ArrayList<String> temp_contact = new ArrayList<String>();
				for (String s : customerinfo.phones) {
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
				for (String s : customerinfo.phones_kinds) {
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
			if (customerinfo.email != null && customerinfo.email.length() > 0) {
				info = new PhoneEmailInfo();
				info.Kind = "Email";
				info.Value = customerinfo.email;
				info.Type = ContactType.Email.toString();
				content.add(info);
			}

			MyContactAdapter adapter = new MyContactAdapter(content);
			lstContact.setAdapter(adapter);
			Utils.setListViewHeightBasedOnChildren(lstContact);
		}
		if (service_customer_info != null) {

			ArrayList<PhoneEmailInfo> content = new ArrayList<PhoneEmailInfo>();
			PhoneEmailInfo info = null;

			if (service_customer_info.phone != null
					&& service_customer_info.phone.length() > 0) {
				info = new PhoneEmailInfo();
				info.Kind = customerinfo.phone_kind;
				info.Value = customerinfo.phone;
				info.Type = ContactType.Phone.toString();
				content.add(info);
			}
			if (service_customer_info.phones != null
					&& service_customer_info.phones.size() > 0) {
				int i = 0;

				ArrayList<String> temp_contact = new ArrayList<String>();
				for (String s : service_customer_info.phones) {
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
				for (String s : service_customer_info.phones_kinds) {
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
			if (service_customer_info.email != null
					&& service_customer_info.email.length() > 0) {
				info = new PhoneEmailInfo();
				info.Kind = "Email";
				info.Value = customerinfo.email;
				info.Type = ContactType.Email.toString();
				content.add(info);
			}

			MyContactAdapter adapter = new MyContactAdapter(content);
			lstContact.setAdapter(adapter);
			Utils.setListViewHeightBasedOnChildren(lstContact);

		}
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
						Utils.callPhone(info.Value, ContactDetailActivity.this);
					}
					if (info.Type.equalsIgnoreCase(ContactType.Email.toString())) {
						Utils.sendEmail(info.Value, ContactDetailActivity.this);
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
}
