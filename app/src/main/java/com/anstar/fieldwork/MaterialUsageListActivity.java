package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.Const;
import com.anstar.common.NotificationCenter;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsage.UpdateMUInfoDelegate;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.MaterialUsagesList;
import com.anstar.models.list.MaterialUsagesRecordsList;

import java.util.ArrayList;

public class MaterialUsageListActivity extends BaseActivity implements
		OnClickListener {

	private ListView lstMaterialUsage;
	int appointment_id;
	private MaterialUsageAdapter m_adapter = null;
	ActionBar action = null;
	ArrayList<MaterialUsage> m_list = null;
	TextView txtMessage;
	final int MATERIAL_REQUEST_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appointment_related);
		action = getSupportActionBar();
		// action.setTitle("Material Usage");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Material Usage</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
		txtMessage = (TextView) findViewById(R.id.txtNodata);
		lstMaterialUsage = (ListView) findViewById(R.id.lstAppointment_Related);
		m_list = new ArrayList<MaterialUsage>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}
		NotificationCenter.Instance().addObserver(this,
				MaterialUsagesList.MATERIAL_USAGE_LIST_NOTIFICATION,
				"loadList", null);
	}

	@Override
	public void onResume() {
		super.onResume();
		m_list = MaterialUsagesList.Instance().load(appointment_id);
		bindData();

	}

	@Override
	protected void onDestroy() {
		NotificationCenter.Instance().removeObserver(this);
		super.onDestroy();

	}

	public void loadList() {
		m_list = MaterialUsagesList.Instance().load(appointment_id);
		bindData();
	}

	public class MaterialUsageAdapter extends BaseAdapter {
		ArrayList<MaterialUsage> m_list = new ArrayList<MaterialUsage>();

		public MaterialUsageAdapter(ArrayList<MaterialUsage> list) {
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
				rowView = li.inflate(R.layout.material_usage_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.sub_item_text = (TextView) rowView
						.findViewById(R.id.sub_text);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final MaterialUsage usage = m_list.get(position);
			if (usage != null) {
				holder.main_item_text.setText(MaterialInfo
						.getMaterialNamebyId(usage.material_id));
				final ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
						.Instance().getMaterialRecordsByUsageId(usage.id);
				if (records != null && records.size() > 0) {
					final MaterialUsageRecords record = records.get(0);
					int locations = 1;
					if (records != null) {
						locations = records.size();
					}
					String appmethod = "";
					if (record.application_method != null) {
						appmethod = " , " + record.application_method;
					}

					if (record.amount != null && record.amount.length() > 0
							&& !record.amount.equalsIgnoreCase("null")) {
						holder.sub_item_text.setText(DilutionRatesList
								.Instance().getDilutionNameByid(
										record.dilution_rate_id)
								+ " , "
								+ String.valueOf(Float
										.parseFloat(record.amount) * locations)
								+ " , " + record.measurement + appmethod);
					} else {
						holder.sub_item_text.setText(DilutionRatesList
								.Instance().getDilutionNameByid(
										record.dilution_rate_id)
								+ " , "
								+ "0.0"
								+ " , "
								+ record.measurement
								+ appmethod);
					}
				}
				holder.rl_main_list_item
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (usage.id != 0) {
									Intent i = new Intent(
											MaterialUsageListActivity.this,
											AddMaterialUsageActivity.class);
									i.putExtra("isEdit", true);
									i.putExtra("usage_id", usage.id);
									startActivity(i);
								} else {
									Toast.makeText(
											getApplicationContext(),
											"Please wait for few seconds for data syncing",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
				holder.rl_main_list_item
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								if (records != null && records.size() > 0) {
									alertToDelete(usage, records.get(0));
								} else {
									alertToDelete(usage, null);
								}
								return false;
							}
						});
			}

			return rowView;
		}
	}

	public static class ViewHolder {
		TextView main_item_text, sub_item_text;
		RelativeLayout rl_main_list_item;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MATERIAL_REQUEST_ID:
			if (resultCode == RESULT_OK) {
				m_list = MaterialUsagesList.Instance().load(appointment_id);
				bindData();
			}
			break;
		default:
			break;
		}
	}

	private void bindData() {
		if (m_list.size() > 0) {
			m_adapter = new MaterialUsageAdapter(m_list);
			lstMaterialUsage.setAdapter(m_adapter);
			txtMessage.setVisibility(View.GONE);
		} else {
			m_adapter = new MaterialUsageAdapter(m_list);
			lstMaterialUsage.setAdapter(m_adapter);
			txtMessage.setVisibility(View.VISIBLE);
			txtMessage
					.setText("To add a material to the usage list please touch the plus button in the upper right hand corner");
			Toast.makeText(getApplicationContext(),
					"No Material Usage added yet", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.add_pest_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnAddPest:
			Intent i = new Intent(MaterialUsageListActivity.this,
					MaterialListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivityForResult(i, MATERIAL_REQUEST_ID);
			// finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void alertToDelete(final MaterialUsage info,
			final MaterialUsageRecords record) {
		String message = "Are you sure to delete this MaterialUsage type?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				MaterialUsageListActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// call();
								showProgress();
								MaterialUsage.DeleteMaterialUsageRecord(record,
										appointment_id, info,
										new UpdateMUInfoDelegate() {
											@Override
											public void UpdateSuccessFully(
													ServiceResponse res) {
												hideProgress();
												m_list = MaterialUsagesList
														.Instance().load(
																appointment_id);
												bindData();
											}

											@Override
											public void UpdateFail(
													String ErrorMessage) {
												hideProgress();
												Toast.makeText(
														getApplicationContext(),
														ErrorMessage, Toast.LENGTH_LONG).show();
											}
										});
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = alt_bld.create();
		alert.setTitle("Alert");
		alert.show();
	}

}
