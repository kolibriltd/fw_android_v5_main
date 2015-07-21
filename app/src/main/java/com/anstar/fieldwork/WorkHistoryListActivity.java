package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.WorkHistroyInfo;
import com.anstar.models.list.WorkHistoryList;

import java.util.ArrayList;

public class WorkHistoryListActivity extends BaseActivity {

	private ListView lstWorkHistory;
	private int service_location_id = 0, cid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_history_list);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("sid")) {
				service_location_id = b.getInt("sid");
				cid = b.getInt("cid");
			}
		}
		ActionBar action = getSupportActionBar();
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Work History</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);

		lstWorkHistory = (ListView) findViewById(R.id.lstWorkHistory);

		showProgress();
		if (cid > 0 && service_location_id > 0) {
			WorkHistoryList.Instance().load(
					new ModelDelegate<WorkHistroyInfo>() {
						@Override
						public void ModelLoaded(ArrayList<WorkHistroyInfo> list) {
							hideProgress();
							if (list != null) {
								CustomAdapter adapter = new CustomAdapter(list);
								lstWorkHistory.setAdapter(adapter);
							}
						}

						@Override
						public void ModelLoadFailedWithError(String error) {
							hideProgress();
							Toast.makeText(getApplicationContext(), error,
									Toast.LENGTH_LONG).show();
						}
					}, cid, service_location_id);
		}

	}

	public class CustomAdapter extends BaseAdapter {
		ArrayList<WorkHistroyInfo> m_list = new ArrayList<WorkHistroyInfo>();

		public CustomAdapter(ArrayList<WorkHistroyInfo> list) {
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
				rowView = li.inflate(R.layout.work_history_item, null);
				rowView.setTag(holder);
				holder.txtDate = (TextView) rowView.findViewById(R.id.txtDate);
				holder.txtWorkOrderNumber = (TextView) rowView
						.findViewById(R.id.txtWorkOrderNumber);
				holder.txtNotes = (TextView) rowView
						.findViewById(R.id.txtNotes);
				holder.rl_main_list_history = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_history);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final WorkHistroyInfo history = m_list.get(position);
			if (history != null) {
				holder.txtDate.setText(history.starts_at_date);
				holder.txtWorkOrderNumber.setText(" - WO # "
						+ history.report_number);
				if (history.notes != null && history.notes.length() > 0
						&& !history.notes.contains("null"))
					holder.txtNotes.setText(history.notes);
			}
			holder.rl_main_list_history
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent i = new Intent(WorkHistoryListActivity.this,
									WorkHistoryDetailActivity.class);
							i.putExtra("whid", history.id);
							startActivity(i);
						}
					});
			return rowView;
		}
	}

	private static class ViewHolder {
		TextView txtDate, txtWorkOrderNumber, txtNotes;
		RelativeLayout rl_main_list_history;
	}

}
