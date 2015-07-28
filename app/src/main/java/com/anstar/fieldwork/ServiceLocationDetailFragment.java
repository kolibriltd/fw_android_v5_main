package com.anstar.fieldwork;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anstar.common.Utils;
import com.anstar.models.PhoneEmailInfo;
import com.anstar.models.PhoneEmailInfo.ContactType;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.list.ServiceLocationsList;

import java.util.ArrayList;

public class ServiceLocationDetailFragment extends Fragment implements
		OnClickListener {

	int service_loc_id, cid;
	private TextView txtName, txtBAddress, txtBAddress2, txt01;
	private RelativeLayout rlContacts, rlWorkHistory;
	private ListView lstContact;

	boolean isFromStarted;
	private ServiceLocationsInfo servicelocation_info = null;
	private OnServiceLocationDetailItemSelectedListener mOnServiceLocationDetailItemSelectedListener;
	// Container Activity must implement this interface
	public interface OnServiceLocationDetailItemSelectedListener {
		void onServiceLocationDetailContactsSelected(ServiceLocationsInfo servicelocation_info);
		void onServiceLocationDetailWorkHistorySelected(ServiceLocationsInfo servicelocation_info);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_service_location_detail, container, false);

		txtBAddress = (TextView) v.findViewById(R.id.txtBAddress);
		txtBAddress2 = (TextView) v.findViewById(R.id.txtBAddress2);
		txtName = (TextView) v.findViewById(R.id.txtCustomerName);
		txt01 = (TextView) v.findViewById(R.id.txt02);

		rlContacts = (RelativeLayout) v.findViewById(R.id.rlContacts);
		rlWorkHistory = (RelativeLayout) v.findViewById(R.id.rlServiceLocation);

		lstContact = (ListView) v.findViewById(R.id.lstBillingContact);
		lstContact.setDivider(null);

		rlContacts.setOnClickListener(this);
		rlWorkHistory.setOnClickListener(this);
		txt01.setText("Work History");

		LoadValues();

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if (b != null) {
			service_loc_id = b.getInt("SLID");
			cid = b.getInt("cid");
			servicelocation_info = ServiceLocationsList.Instance()
					.getServiceLocationById(service_loc_id);
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_service_location_detail);
	}

	@Override
	public void onClick(View v) {
		if (v == rlContacts) {
/*
			Intent i = new Intent(getActivity(),
					ServiceLocationContactsActivity.class);
			i.putExtra("SILD", service_loc_id);
			startActivity(i);
*/
			mOnServiceLocationDetailItemSelectedListener.onServiceLocationDetailContactsSelected(servicelocation_info);
		}else if (v == rlWorkHistory) {
/*
			Intent i = new Intent(getActivity(),
					WorkHistoryListActivity.class);
			i.putExtra("sid", service_loc_id);
			i.putExtra("cid", cid);
			startActivity(i);
*/
			mOnServiceLocationDetailItemSelectedListener.onServiceLocationDetailWorkHistorySelected(servicelocation_info);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mOnServiceLocationDetailItemSelectedListener = (OnServiceLocationDetailItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnServiceLocationDetailItemSelectedListener");
		}
	}

	public void LoadValues() {
		String name = "";
		name = servicelocation_info.name;
		txtName.setText(name);

		txtBAddress.setText(servicelocation_info.street + " "
				+ servicelocation_info.street_two);
		StringBuilder sb1 = new StringBuilder();
		if (servicelocation_info.city.length() > 0) {
			sb1.append(servicelocation_info.city + ", ");
		}
		if (servicelocation_info.state.length() > 0) {
			sb1.append(servicelocation_info.state + " ");
		}
		if (servicelocation_info.zip.length() > 0) {
			sb1.append(servicelocation_info.zip);
		}

		txtBAddress2.setText(sb1.toString());

		ArrayList<PhoneEmailInfo> content = new ArrayList<PhoneEmailInfo>();
		PhoneEmailInfo info = null;

		if (servicelocation_info.phone != null
				&& servicelocation_info.phone.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = servicelocation_info.phone_kind;
			info.Value = servicelocation_info.phone;
			info.Type = ContactType.Phone.toString();
			content.add(info);
		}
		if (servicelocation_info.phones != null
				&& servicelocation_info.phones.size() > 0) {
			int i = 0;

			ArrayList<String> temp_contact = new ArrayList<String>();
			for (String s : servicelocation_info.phones) {
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
			for (String s : servicelocation_info.phones_kinds) {
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
					if(temp_kind.size() > 0)
						info.Kind = temp_kind.get(i);
					info.Value = ph;
					info.Type = ContactType.Phone.toString();
					content.add(info);
					i++;
				}
			}
		}
		if (servicelocation_info.email != null
				&& servicelocation_info.email.length() > 0) {
			info = new PhoneEmailInfo();
			info.Kind = "Email";
			info.Value = servicelocation_info.email;
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
			holder.rl.setOnClickListener(new OnClickListener() {

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
