package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.NotificationCenter;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.TargetPestInfo;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.TargetPestList;

import java.util.ArrayList;

public class TargetPestListActivity extends AppCompatActivity implements
		OnClickListener {

	private ListView lstTargetPest;
	int appointment_id;
	private TargetPestAdapter m_adapter = null;
	private ArrayList<TargetPestInfo> m_targetpests = null;
	ActionBar action = null;
	TargetPestList list = null;
	TextView txtMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_pest_list);
/*
		action = getSupportActionBar();
		// action.setTitle("Target Pests");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Target Pests</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		txtMessage = (TextView) findViewById(R.id.txtNodata);
		lstTargetPest = (ListView) findViewById(R.id.lstAppointment_Related);
		m_targetpests = new ArrayList<TargetPestInfo>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}
		list = new TargetPestList();
		NotificationCenter.Instance().addObserver(this,
				TargetPestList.TARGET_PEST_LIST_NOTIFICATION, "loadList", null);
	}

	@Override
	public void onResume() {
		super.onResume();
		m_targetpests = list.load(appointment_id);
		bindData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NotificationCenter.Instance().removeObserver(this);
	}

	public void loadList() {
		m_targetpests = list.load(appointment_id);
		bindData();
	}

	public class TargetPestAdapter extends BaseAdapter {
		ArrayList<TargetPestInfo> m_list = new ArrayList<TargetPestInfo>();

		public TargetPestAdapter(ArrayList<TargetPestInfo> list) {
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
				rowView = li.inflate(R.layout.main_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final TargetPestInfo targetPest = m_list.get(position);
			PestsTypeInfo pestinfo = PestTypeList.Instance().getPestById(
					targetPest.pest_type_id);
			// holder.main_item_text.setTextSize(14);
			// holder.main_item_text.setPadding(5, 5, 5, 5);
			if (pestinfo != null)
				holder.main_item_text.setText(pestinfo.name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Intent i = new Intent(TargetPestListActivity.this,
					// AppointmentDetails.class);
					// i.putExtra("id", targetPest.id);
					// startActivity(i);
					alertToDelete(targetPest.id);
				}
			});
			return rowView;
		}
	}

	public static class ViewHolder {
		TextView main_item_text;
		RelativeLayout rl_main_list_item;
	}

	@Override
	public void onClick(View v) {

	}

	private void bindData() {
		if (m_targetpests.size() > 0) {
			m_adapter = new TargetPestAdapter(m_targetpests);
			lstTargetPest.setAdapter(m_adapter);
			txtMessage.setVisibility(View.GONE);
		} else {
			m_adapter = new TargetPestAdapter(m_targetpests);
			lstTargetPest.setAdapter(m_adapter);
			txtMessage.setVisibility(View.VISIBLE);
			txtMessage
					.setText("To add a pest to the target pest list please touch the plus button in the upper right hand corner");
			Toast.makeText(getApplicationContext(), "No Target Pest",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.fragment_customer_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnAddPest:
			Intent i = new Intent(TargetPestListActivity.this,
					PestTypeListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void alertToDelete(final int target_id) {
		String message = "Are you sure to delete this Pest type?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				TargetPestListActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// call();
								ProgressDialog.showProgress(TargetPestListActivity.this);
								TargetPestInfo.DeleteTargetPests(target_id,
										new UpdateInfoDelegate() {

											@Override
											public void UpdateSuccessFully(
													ServiceResponse res) {
												// try {
												// FieldworkApplication
												// .Connection()
												// .delete(TargetPestInfo.class);
												// } catch
												// (ActiveRecordException e) {
												// e.printStackTrace();
												// }
												m_targetpests = list
														.load(appointment_id);
												bindData();
												ProgressDialog.hideProgress();
											}

											@Override
											public void UpdateFail(
													String ErrorMessage) {
												ProgressDialog.hideProgress();
												Toast.makeText(
														getApplicationContext(),
														ErrorMessage,
														Toast.LENGTH_LONG)
														.show();
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
