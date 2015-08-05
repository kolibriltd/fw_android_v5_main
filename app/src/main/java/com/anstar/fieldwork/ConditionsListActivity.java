package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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

import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.models.AppointmentConditionsInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.list.AppointmentConditionsList;

import java.util.ArrayList;

public class ConditionsListActivity extends AppCompatActivity implements
		OnClickListener, ModelDelegate<AppointmentConditionsInfo> {

	private ListView lstConditions;
	int appointment_id;
	private EditText edtSearch;
	private ImageView imgCancel;
	// MyAppointmentAdapter m_adapter;
	private ConListAdapter m_adapter = null;
	private ArrayList<AppointmentConditionsInfo> m_conditions = null;
	//ActionBar action = null;
	private RelativeLayout RlSubHeader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conditions_list);
/*
		action = getSupportActionBar();
		// action.setTitle("Material List");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Conditions List</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		lstConditions = (ListView) findViewById(R.id.lstMain);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		imgCancel = (ImageView) findViewById(R.id.imgCancel);
		RlSubHeader = (RelativeLayout) findViewById(R.id.RlSubHeader);
		RlSubHeader.setVisibility(View.GONE);
		imgCancel.setOnClickListener(this);

		m_conditions = new ArrayList<AppointmentConditionsInfo>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		} else {
			appointment_id = Const.app_id;
		}
		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_adapter = new ConListAdapter(m_conditions);
					lstConditions.setAdapter(m_adapter);
				}
				ArrayList<AppointmentConditionsInfo> temp = new ArrayList<AppointmentConditionsInfo>();
				for (AppointmentConditionsInfo c : m_conditions) {
					if (c.name.toLowerCase().contains(text.toLowerCase())) {
						temp.add(c);
					}
				}
				m_adapter = new ConListAdapter(temp);
				lstConditions.setAdapter(m_adapter);
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
			ProgressDialog.showProgress(this);
			AppointmentConditionsList.Instance().load(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class ConListAdapter extends BaseAdapter {
		ArrayList<AppointmentConditionsInfo> adapterlist = new ArrayList<AppointmentConditionsInfo>();

		public ConListAdapter(ArrayList<AppointmentConditionsInfo> list) {
			adapterlist = list;
		}

		@Override
		public int getCount() {
			return adapterlist.size();
		}

		@Override
		public Object getItem(int position) {
			return adapterlist.get(position);
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
				rowView = li.inflate(R.layout.recomendation_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.imgTik = (ImageView) rowView.findViewById(R.id.imgTik);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final AppointmentConditionsInfo con = adapterlist.get(position);
			holder.main_item_text.setText(con.name);
			final ViewHolder vh = holder;
			if (AddNotesActivity.conids.contains(con.id+"")) {
				vh.imgTik.setVisibility(View.VISIBLE);
			} else {
				vh.imgTik.setVisibility(View.GONE);
			}
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (AddNotesActivity.conids.contains(con.id+"")) {
						AddNotesActivity.conids.remove(con.id+"");
						vh.imgTik.setVisibility(View.GONE);
					} else {
						AddNotesActivity.conids.add(con.id+"");
						vh.imgTik.setVisibility(View.VISIBLE);
					}
				}
			});
			return rowView;
		}
	}

	private static class ViewHolder {
		TextView main_item_text;
		ImageView imgTik;
		RelativeLayout rl_main_list_item;
	}

	@Override
	public void onClick(View v) {
		if (v == imgCancel) {
			edtSearch.setText("");
		}
	}

	@Override
	public void ModelLoaded(ArrayList<AppointmentConditionsInfo> list) {
		ProgressDialog.hideProgress();
		if (list != null) {
			m_conditions = list;
			// m_recomendations =
			// Utils.Instance().sortMaterialCollections(list);
			bindData();
		} else {
			Toast.makeText(getApplicationContext(), "No Conditions added",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void ModelLoadFailedWithError(String error) {
		ProgressDialog.hideProgress();
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
	}

	private void bindData() {
		if (m_conditions.size() > 0) {
			m_adapter = new ConListAdapter(m_conditions);
			lstConditions.setAdapter(m_adapter);
		} else {
			Toast.makeText(getApplicationContext(), "No Conditions added",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.rec_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnDone:
			 Intent intent=new Intent();  
			setResult(RESULT_OK,intent);
			finish();
			return true;
		case android.R.id.home:
			 Intent i=new Intent();  
			setResult(RESULT_OK,i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
