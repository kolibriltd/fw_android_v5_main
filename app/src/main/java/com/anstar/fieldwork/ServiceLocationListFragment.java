package com.anstar.fieldwork;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.list.ServiceLocationsList;

import java.util.ArrayList;

public class ServiceLocationListFragment extends Fragment {

	private ListView lstServiceLocations;
	private int Customer_id = 0;
	private ArrayList<ServiceLocationsInfo> m_locations;
	private OnServiceLocationItemSelectedListener mOnServiceLocationItemSelectedListener;
	// Container Activity must implement this interface
	public interface OnServiceLocationItemSelectedListener {
		void onServiceLocationItemSelected(ServiceLocationsInfo item);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_service_location_list, container, false);

		lstServiceLocations = (ListView) v.findViewById(R.id.lstServices_list);
		m_locations = ServiceLocationsList.Instance()
				.getServiceLocationByCustId(Customer_id);
		if (m_locations == null) {
			m_locations = new ArrayList<ServiceLocationsInfo>();
		}
		CustomAdapter adapter = new CustomAdapter(m_locations);
		lstServiceLocations.setAdapter(adapter);

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if (b != null) {
			if (b.containsKey("CID")) {
				Customer_id = b.getInt("CID");
			}
/*
			if (b.containsKey("FromAddAppointment")) {
				mFromAddAppointment = b.getBoolean("FromAddAppointment");
			}
*/
		}
		setHasOptionsMenu(true);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_fragment_service_location_list);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mOnServiceLocationItemSelectedListener = (OnServiceLocationItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnServiceLocationItemSelectedListener");
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_service_location_list, menu);
		MenuItem item = menu.findItem(R.id.action_search);
		SearchView sv = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
		//MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		MenuItemCompat.setActionView(item, sv);
		sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				showFilteredList(query);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				showFilteredList(newText);
				return false;
			}
		});
	}

	private void showFilteredList(String text) {
		ArrayList<ServiceLocationsInfo> m_temp = new ArrayList<>();
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
				LayoutInflater li = getActivity().getLayoutInflater();
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
/*
							if (mFromAddAppointment) {
								Intent i = new Intent();
								i.putExtra("service_location_id", service.id);
								getActivity().setResult(Activity.RESULT_OK, i);
//////////!!!!!								finish();
							} else {
								Intent i = new Intent(getActivity(),
										ServiceLocationDetailActivity.class);
								i.putExtra("SLID", service.id);
								i.putExtra("cid", Customer_id);
								startActivity(i);
							}
*/
							mOnServiceLocationItemSelectedListener.onServiceLocationItemSelected(service);
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
