package com.anstar.fieldwork;

import android.app.Activity;
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

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.Const;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.MaterialUsageTargetPestInfo;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.TargetPestInfo;
import com.anstar.models.list.MaterialUsagesRecordsList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.TargetPestList;

import java.util.ArrayList;

public class MaterialUsageTargetListActivity extends AppCompatActivity implements
		OnClickListener {

	private ListView lstTargetPests;
	int appointment_id;
	private EditText edtSearch;
	private ImageView imgCancel;
	private TargetPestAdapter m_adapter = null;
	private ArrayList<TargetPestInfo> m_target = new ArrayList<TargetPestInfo>();
	//ActionBar action = null;
	TargetPestList target_list;
	int material_usage_id = 0;
	MaterialUsageRecords records = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_usage_target_list);
/*
		action = getSupportActionBar();
		// action.setTitle("Location Areas");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Target Pests</font>"));
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
		lstTargetPests = (ListView) findViewById(R.id.lstMain);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		imgCancel = (ImageView) findViewById(R.id.imgCancel);
		imgCancel.setOnClickListener(this);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
			if (b.containsKey("record_id")) {
				material_usage_id = b.getInt("record_id");
			}
		}
		records = MaterialUsagesRecordsList.Instance()
				.getMaterialRecordByUsageId(material_usage_id);
		target_list = new TargetPestList();
		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_adapter = new TargetPestAdapter(m_target);
					lstTargetPests.setAdapter(m_adapter);
				}
				ArrayList<TargetPestInfo> temp = new ArrayList<TargetPestInfo>();
				for (TargetPestInfo c : m_target) {
					PestsTypeInfo pestinfo = PestTypeList.Instance()
							.getPestById(c.pest_type_id);
					if (pestinfo != null) {
						if (pestinfo.name.toLowerCase().contains(
								text.toLowerCase())) {
							temp.add(c);
						}
					}
				}
				m_adapter = new TargetPestAdapter(temp);
				lstTargetPests.setAdapter(m_adapter);

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
		m_target = target_list.load(appointment_id);
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
			if (pestinfo != null)
				holder.main_item_text.setText(pestinfo.name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (material_usage_id != 0) {
						if (records != null) {
							records.Pest_ids = records.Pest_ids + ","
									+ targetPest.pest_type_id;
							try {
								records.save();
							} catch (ActiveRecordException e) {
								e.printStackTrace();
							}
						}
					}
					MaterialUsageTargetPestInfo
							.AddTargetPest(targetPest.pest_type_id);
					Intent i = new Intent();
					i.putExtra("isMaterialTarget", true);
					setResult(Activity.RESULT_OK, i);
					finish();
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
		if (v == imgCancel) {
			edtSearch.setText("");
		}
	}

	private void bindData() {
		if (m_target.size() > 0) {
			m_adapter = new TargetPestAdapter(m_target);
			lstTargetPests.setAdapter(m_adapter);
		} else {
			Toast.makeText(getApplicationContext(), "No Target Pest",
					Toast.LENGTH_LONG).show();
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
			Intent i = new Intent(MaterialUsageTargetListActivity.this,
					PestTypeListActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}