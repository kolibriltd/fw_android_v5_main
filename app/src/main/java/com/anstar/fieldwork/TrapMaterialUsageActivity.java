package com.anstar.fieldwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.Const;
import com.anstar.models.InspectionInfo;
import com.anstar.models.InspectionMaterial;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.MaterialUsagesList;
import com.anstar.models.list.MaterialUsagesRecordsList;

import java.util.ArrayList;

public class TrapMaterialUsageActivity extends BaseActivity {

	private ListView lstMaterialUsage;
	int appointment_id;
	private MaterialUsageAdapter m_adapter = null;
	ActionBar action = null;
	ArrayList<MaterialUsage> m_list = null;
	TextView txtMessage;
	int inspection_id = 0;
	InspectionInfo inspection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appointment_related);
		action = getSupportActionBar();
		// action.setTitle("Material Usage");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Material Usages</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
		txtMessage = (TextView) findViewById(R.id.txtNodata);
		lstMaterialUsage = (ListView) findViewById(R.id.lstAppointment_Related);
		m_list = new ArrayList<MaterialUsage>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey(Const.Appointment_Id)) {
				appointment_id = b.getInt(Const.Appointment_Id);
			}
			if (b.containsKey("Inspection_id")) {
				inspection_id = b.getInt("Inspection_id");
				inspection = InspectionList.Instance().getInspectionById(
						inspection_id);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
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
				final ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
						.Instance().getMaterialRecordsByUsageId(usage.id);
				final MaterialUsageRecords record = records.get(0);
				int locations = 1;
				if (records != null) {
					locations = records.size();
				}

				holder.main_item_text.setText(MaterialInfo
						.getMaterialNamebyId(usage.material_id));
				holder.sub_item_text.setText(DilutionRatesList.Instance()
						.getDilutionNameByid(record.dilution_rate_id)
						+ " , "
						+ String.valueOf(Float.parseFloat(record.amount)
								* locations)
						+ " , "
						+ record.measurement
						+ " , " + record.application_method);
				holder.rl_main_list_item
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (inspection_id != 0) {
									if (inspection != null) {
										if (inspection.Material_ids != null
												&& inspection.Material_ids
														.length() > 0) {
											inspection.Material_ids = inspection.Material_ids
													+ "," + usage.id;
										} else {
											inspection.Material_ids = ""
													+ usage.id;

										}
										try {
											inspection.save();
										} catch (ActiveRecordException e) {
											e.printStackTrace();
										}
									}
								}
								InspectionMaterial.AddMaterial(usage.id);
								Intent i = new Intent();
								i.putExtra("isInspectionMaterial", true);
								setResult(Activity.RESULT_OK, i);
								finish();
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

	private void bindData() {
		if (m_list.size() > 0) {
			m_adapter = new MaterialUsageAdapter(m_list);
			lstMaterialUsage.setAdapter(m_adapter);
			txtMessage.setVisibility(View.GONE);
		} else {
			m_adapter = new MaterialUsageAdapter(m_list);
			lstMaterialUsage.setAdapter(m_adapter);
			txtMessage.setVisibility(View.VISIBLE);
			txtMessage.setText("No Material Usage added yet");
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
			Intent i = new Intent(TrapMaterialUsageActivity.this,
					MaterialListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			i.putExtra("isFromTrapMaterial", true);
			startActivity(i);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
