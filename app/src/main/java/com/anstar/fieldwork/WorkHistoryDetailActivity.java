package com.anstar.fieldwork;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anstar.common.Utils;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.WorkHistroyInfo;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.WorkHistoryList;

import java.util.ArrayList;

public class WorkHistoryDetailActivity extends BaseActivity implements
		OnClickListener {

	int whid, cid;
	private TextView txtNotes, txtWoNumber, txtDate, txtTime, txtStatus;
	private ListView lstMaterialUsage;

	boolean isFromStarted;
	private WorkHistroyInfo history_info = null;
	ActionBar action = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_history_details);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			whid = b.getInt("whid");
		}
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Work History Details</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);

		txtNotes = (TextView) findViewById(R.id.txtNotes);
		lstMaterialUsage = (ListView) findViewById(R.id.lstMaterialUsage);
		txtWoNumber = (TextView) findViewById(R.id.txtWoNumber);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtStatus = (TextView) findViewById(R.id.txtStatus);

		history_info = WorkHistoryList.Instance().getHistoryById(whid);
		history_info = history_info.getDetails();
		if (history_info != null) {
			LoadValues();
		}
	}

	@Override
	public void onClick(View v) {
	}

	public void LoadValues() {
		txtNotes.setText(history_info.notes == "null" ? "" : history_info.notes);
		txtWoNumber.setText("WO # " + history_info.id);
		txtDate.setText(history_info.starts_at_date);
		txtTime.setText(history_info.starts_at_time.trim() + " - "
				+ history_info.ends_at_time);
		if (history_info.status.equalsIgnoreCase("complete")) {
			txtStatus.setBackgroundResource(R.drawable.status_background);
			txtStatus.setText("C");
		} else if (history_info.status.equalsIgnoreCase("missed")
				|| history_info.status.equalsIgnoreCase("Missed Appointment")) {
			txtStatus.setBackgroundResource(R.drawable.status_missed);
			txtStatus.setText("M");
		} else if (history_info.status.equalsIgnoreCase("scheduled")) {
			txtStatus.setBackgroundResource(R.drawable.status_yellow);
			txtStatus.setText("S");
		}
		if (history_info.m_material_usages != null
				&& history_info.m_material_usages.size() > 0) {
			// ArrayList<MaterialUsageRecords> mrecords =
			// history_info.m_material_usages_records.get(history_info.m_material_usages.get(0).id);
			MaterialUsageAdapter adapter = new MaterialUsageAdapter(
					history_info.m_material_usages);
			lstMaterialUsage.setAdapter(adapter);
			Utils.setListViewHeightBasedOnChildren(lstMaterialUsage);
		}

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

	public class MaterialUsageAdapter extends BaseAdapter {

		ArrayList<MaterialUsage> m_list = new ArrayList<MaterialUsage>();

		public MaterialUsageAdapter(ArrayList<MaterialUsage> temp) {
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
			final MaterialUsage usage = m_list.get(position);
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
			if (usage != null) {
				ArrayList<MaterialUsageRecords> records = history_info.m_material_usages_records
						.get(usage.id);
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
}
