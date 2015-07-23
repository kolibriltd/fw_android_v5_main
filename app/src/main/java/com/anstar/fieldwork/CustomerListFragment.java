package com.anstar.fieldwork;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.SectionListAdapter;
import com.anstar.common.SectionListAdapter.IndexPath;
import com.anstar.common.SectionListAdapter.SectionListAdapterAdapterDelegate;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateCustomerDelegate;
import com.anstar.models.list.CustomerList;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerListFragment extends Fragment implements
		ModelDelegate<CustomerInfo>, SectionListAdapterAdapterDelegate {

	private SectionListAdapter adapter = null;
	private ListView lstCustomer;
	private ArrayList<CustomerInfo> m_list;
	private TextView txtsectionmain;
	private EditText edtSearch;
	private LayoutInflater mInflater;
	private HashMap<Integer, ArrayList<CustomerInfo>> m_hashSetContacts = new HashMap<Integer, ArrayList<CustomerInfo>>();
	private String[] abc = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z" };
	ActionBar action = null;
	boolean FromAddAppointment = false;
	private BaseLoader mBaseLoader;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_customer_list, container, false);

		mInflater = inflater;

		edtSearch = (EditText) v.findViewById(R.id.edtSearch);
		lstCustomer = (ListView) v.findViewById(R.id.lstCustomer);
		txtsectionmain = (TextView) v.findViewById(R.id.txtsectionmain);

		edtSearch.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_list = CustomerList.Instance().getAllCustomer();
					generateHashList();
					adapter = new SectionListAdapter();
					adapter.delegate = CustomerListFragment.this;
					lstCustomer.setAdapter(adapter);
				} else {
					ArrayList<CustomerInfo> temp = new ArrayList<CustomerInfo>();
					if (m_list != null) {
						for (CustomerInfo c : m_list) {
							if (c.setCustomerName().toString().toLowerCase()
									.contains(text.toLowerCase())) {
								temp.add(c);
							}
						}
						generateHashList();
						m_list = temp;
						adapter = new SectionListAdapter();
						adapter.delegate = CustomerListFragment.this;
						lstCustomer.setAdapter(adapter);
					}
				}
			}
		});

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBaseLoader = new BaseLoader(getActivity());

		Bundle b = getArguments();
		if (b != null) {
			if (b.containsKey("FromAddAppointment"))
				FromAddAppointment = b.getBoolean("FromAddAppointment");
		}

		setHasOptionsMenu(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_customer_list);
		try {
			mBaseLoader.showProgress();
			CustomerList.Instance().loadLocal(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		txtsectionmain.setText("A");
	}

	@Override
	public void ModelLoaded(ArrayList<CustomerInfo> list) {
		mBaseLoader.hideProgress();
		if (list != null) {
			m_list = list;
			generateHashList();
			adapter = new SectionListAdapter();
			adapter.delegate = CustomerListFragment.this;
			lstCustomer.setAdapter(adapter);
			txtsectionmain.setVisibility(View.VISIBLE);
		} else {
			Toast.makeText(getActivity(),
					"No customer downloaded yet", Toast.LENGTH_LONG).show();
			txtsectionmain.setVisibility(View.GONE);
		}
	}

	@Override
	public void ModelLoadFailedWithError(String error) {
		mBaseLoader.hideProgress();
		Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
	}

	@Override
	public int sectionCount() {
		return 27;
	}

	@Override
	public int rowsInSection(int section) {
		int cnt = 0;
		if (m_hashSetContacts.containsKey(section + 65)) {
			cnt = m_hashSetContacts.get(section + 65).size();
		}
		return cnt;
	}

	@Override
	public View viewForRowAtIndexPath(IndexPath path) {
		View view = mInflater.inflate(R.layout.customer_item, null);
		final CustomerInfo info = getCustomerBySectionAndRow(path.section,
				path.row);
		TextView textname = (TextView) view.findViewById(R.id.txtCustomerName);
		final RelativeLayout rl = (RelativeLayout) view
				.findViewById(R.id.rlCustomer);
		view.setTag(info);

		String name = info.setCustomerName();
		textname.setText(name);
		rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (FromAddAppointment) {
					loadCustomer(info);
				} else {
					Intent i = new Intent(getActivity(),
							CustomerDetailsActivity.class);
					i.putExtra("customer_id", info.id);
					startActivity(i);
				}
			}
		});
		View v = lstCustomer.getChildAt(0);
		if (v != null) {
			final CustomerInfo cust = (CustomerInfo) v.getTag();
			if (cust != null) {
				// String n = cust.setCustomerName();
				String n = "";
				if (cust.customer_type.equalsIgnoreCase("Commercial")) {
					n = cust.name;
				} else {
					n = cust.last_name;
				}
				txtsectionmain.setText(String.valueOf(n.charAt(0))
						.toUpperCase());
			}
		}
		return view;
	}

	@Override
	public View viewForHeaderInSection(int section) {
		View view = mInflater.inflate(R.layout.section_header, null);
		TextView textsection = (TextView) view.findViewById(R.id.txtsection);
		if (section == 0) {
			textsection.setText("A");
			textsection.setVisibility(View.GONE);
		} else {
			int j = section + 65;
			char ch = (char) j;
			textsection.setText(String.valueOf(ch));

		}
		return view;
	}

	@Override
	public void itemSelectedAtIndexPath(IndexPath path) {
		// TODO Auto-generated method stub

	}

	private CustomerInfo getCustomerBySectionAndRow(int section, int row) {
		int charval = section + 65;
		return m_hashSetContacts.get(charval).get(row);
	}

	private void generateHashList() {
		m_hashSetContacts = new HashMap<Integer, ArrayList<CustomerInfo>>();
		// for (int section = 0; section < 27; section++)
		int i = 1;
		ArrayList<CustomerInfo> otherCharList = new ArrayList<CustomerInfo>();
		// Utils.LogInfo("SECTION : " + String.valueOf(section));
		for (CustomerInfo c : m_list) {
			int charval;
			// String name = c.setCustomerName();
			String name = "";

			if (c.customer_type.equalsIgnoreCase("Commercial")) {
				name = c.name;
			} else {
				name = c.last_name;
			}
			// String name = c.last_name;
			// try{
			if (name != null) {
				if (name.length() > 0) {
					charval = (int) name.toUpperCase().charAt(0);
					if (charval < 65 && charval > 90) {
//						otherCharList.add(c);
//						 m_hashSetContacts.put(55, otherCharList);
						 if (m_hashSetContacts.containsKey(35)) {
								m_hashSetContacts.get(35).add(c);
							} else {
								otherCharList.add(c);
								m_hashSetContacts.put(35, otherCharList);
							}
					} else {
						if (m_hashSetContacts.containsKey(charval)) {
							m_hashSetContacts.get(charval).add(c);
						} else {
							ArrayList<CustomerInfo> temp = new ArrayList<CustomerInfo>();
							temp.add(c);
							m_hashSetContacts.put(charval, temp);
						}
					}
				} else {
					charval = 0;
					if (m_hashSetContacts.containsKey(0)) {
						m_hashSetContacts.get(0).add(c);
					} else {
						ArrayList<CustomerInfo> temp = new ArrayList<CustomerInfo>();
						temp.add(c);
						m_hashSetContacts.put(0, temp);
					}
				}
			}
			// }catch (Exception e) {
			// //e.printStackTrace();
			// Utils.LogInfo("Error for customer :::::::::::::::::::::::: "+c.id
			// +"  "+c.first_name);
			// }
		}
//		 m_hashSetContacts.put(55, otherCharList);
	}

	public void loadCustomer(CustomerInfo customerinfo) {

		if (!customerinfo.isAllreadyLoded) {
			if (NetworkConnectivity.isConnected()) {
				mBaseLoader.showProgress();
				customerinfo.RetriveData(new UpdateCustomerDelegate() {

					@Override
					public void UpdateSuccessFully(CustomerInfo info) {
						mBaseLoader.hideProgress();
						Intent i = new Intent();
						i.putExtra("customer_id", info.id);
						getActivity().setResult(Activity.RESULT_OK, i);
// Oleg !!!!!!!!!!!!						finish();
					}

					@Override
					public void UpdateFail(String ErrorMessage) {
						mBaseLoader.hideProgress();
						Toast.makeText(getActivity(),
								"Please try again.", Toast.LENGTH_LONG).show();
					}
				});
			} else {
				Toast.makeText(getActivity(),
						"Please check your internet connection.",
						Toast.LENGTH_LONG).show();
// Oleg !!!!!!!!!!!!						finish();
			}
		} else {
			Intent i = new Intent();
			i.putExtra("customer_id", customerinfo.id);
			getActivity().setResult(Activity.RESULT_OK, i);
// Oleg !!!!!!!!!!!!						finish();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu,inflater);

		inflater.inflate(R.menu.add_pest_menu, menu);
	}

	//

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnAddPest:
			SharedPreferences setting = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			boolean auto = setting.getBoolean("ISAUTOMODE", true);
			if (!auto) {
				Toast.makeText(
						getActivity(),
						"You have to on Auto Sync mode from settings to add customer",
						Toast.LENGTH_LONG).show();
				break;
			}
			if (!NetworkConnectivity.isConnected()) {
				Toast.makeText(getActivity(),
						"You need internet connection to add Customer",
						Toast.LENGTH_LONG).show();
			} else {
				Intent i = new Intent(getActivity(),
						AddCustomerActivity.class);
				startActivity(i);
			}
			return true;
			// case R.id.btnAppointments:
			// return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
