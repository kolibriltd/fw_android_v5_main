package com.anstar.fieldwork;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

public class ContactDetailFragment extends Fragment {

	TextView txtName, txtDescription, txtPhoneKind, txtPhone, txtEmail;
	ListView lstContact;
	int Contact_id = 0;
	int Service_Contact_id = 0;
	ImageView imgCall, imgEmail;
	CustomerContactInfo customerinfo;
	ServiceLocationContactInfo service_customer_info;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_contact_detail, container, false);

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
		txtName = (TextView) v.findViewById(R.id.txtCustomerName);
		txtDescription = (TextView) v.findViewById(R.id.txtDescription);

		txtPhoneKind = (TextView) v.findViewById(R.id.txtPhoneKind);
		txtPhone = (TextView) v.findViewById(R.id.txtPhone);
		txtEmail = (TextView) v.findViewById(R.id.txtEmail);

		lstContact = (ListView) v.findViewById(R.id.lstContact);

		imgCall = (ImageView) v.findViewById(R.id.imgCall);
		imgEmail = (ImageView) v.findViewById(R.id.imgEmail);

		LoadContacts();

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
							getActivity());
				} else {
					Utils.callPhone(service_customer_info.phone,
							getActivity());
				}
			}
		});
		imgEmail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (customerinfo != null) {
					Utils.sendEmail(customerinfo.email,
							getActivity());
				} else {
					Utils.sendEmail(service_customer_info.email,
							getActivity());
				}
			}
		});

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getArguments();
		if (b != null) {
			if (b.containsKey("CONTACT_ID")) {
				Contact_id = b.getInt("CONTACT_ID");
			}
			if (b.containsKey("SERVICE_CONTACT_ID")) {
				Service_Contact_id = b.getInt("SERVICE_CONTACT_ID");
			}
		}

	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_contact_detail);
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
						Utils.callPhone(info.Value, getActivity());
					}
					if (info.Type.equalsIgnoreCase(ContactType.Email.toString())) {
						Utils.sendEmail(info.Value, getActivity());
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
