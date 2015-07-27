package com.anstar.fieldwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.models.MaterialUsageTargetPestInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.list.PestTypeList;

import java.util.ArrayList;

public class PestTypeListActivity extends AppCompatActivity implements
		OnClickListener, ModelDelegate<PestsTypeInfo> {

	private ListView lstPestList;
	int appointment_id;
	private EditText edtSearch;
	private ImageView imgCancel;
	// MyAppointmentAdapter m_adapter;
	private TargetPestAdapter m_adapter = null;
	private ArrayList<PestsTypeInfo> m_pesttypes = null;
	//ActionBar action = null;
	boolean isFromCapture = false;
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pest_type_list);
/*
		action = getSupportActionBar();
		// action.setTitle("Pest Types");
		action.setTitle(Html
				.fromHtml("<font color='"
						+ getString(R.string.header_text_color)
						+ "'>Pest Types</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		mBaseLoader = new BaseLoader(this);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		lstPestList = (ListView) findViewById(R.id.lstMain);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		imgCancel = (ImageView) findViewById(R.id.imgCancel);

		imgCancel.setOnClickListener(this);

		m_pesttypes = new ArrayList<PestsTypeInfo>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
			if (b.containsKey("isFromCapture")) {
				isFromCapture = b.getBoolean("isFromCapture");
			}
		}
		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_adapter = new TargetPestAdapter(m_pesttypes);
					lstPestList.setAdapter(m_adapter);
				}
				ArrayList<PestsTypeInfo> temp = new ArrayList<PestsTypeInfo>();
				for (PestsTypeInfo c : m_pesttypes) {
					if (c.name.toLowerCase().contains(text.toLowerCase())) {
						temp.add(c);
					}
				}
				m_adapter = new TargetPestAdapter(temp);
				lstPestList.setAdapter(m_adapter);

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
			mBaseLoader.showProgress();
			PestTypeList.Instance().load(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class TargetPestAdapter extends BaseAdapter {
		ArrayList<PestsTypeInfo> m_list = new ArrayList<PestsTypeInfo>();

		public TargetPestAdapter(ArrayList<PestsTypeInfo> list) {
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

			final PestsTypeInfo pesttype = m_list.get(position);
			holder.main_item_text.setText(pesttype.name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Intent i = new Intent(PestTypeListActivity.this,
					// AppointmentDetails.class);
					// i.putExtra("id", pesttype.id);
					// startActivity(i);
					if (pesttype.id < 0) {
						try {
							PestTypeList.Instance().load(PestTypeListActivity.this);
							Toast.makeText(getApplicationContext(),
									"Please select again, data was not proper",
									Toast.LENGTH_LONG).show();
							return;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (isFromCapture) {
						Intent i = new Intent();
						i.putExtra("pest_id", pesttype.id);
						setResult(RESULT_OK, i);
						finish();
					} else {
						// showProgress();
						try {
							// TargetPestInfo tp_info = FieldworkApplication
							// .Connection().newEntity(
							// TargetPestInfo.class);
							// tp_info.id = Utils.getRandomInt();
							// tp_info.pest_type_id = pesttype.id;
							// tp_info.AppointmentId = appointment_id;
							// tp_info.save();// flow changes
							ArrayList<MaterialUsageTargetPestInfo> m_pest = MaterialUsageTargetPestInfo
									.getAll();
							MaterialUsageTargetPestInfo temp = new MaterialUsageTargetPestInfo();
							temp.pest_type_id = pesttype.id;
							if (!m_pest.contains(temp)) {
								MaterialUsageTargetPestInfo info = FieldworkApplication
										.Connection()
										.newEntity(
												MaterialUsageTargetPestInfo.class);
								info.pest_type_id = pesttype.id;
								info.save();
							}
							Intent i = new Intent();
							i.putExtra("isMaterialTarget", true);
							setResult(Activity.RESULT_OK, i);
							finish();
							// tp_info.AddTargetPests(new UpdateInfoDelegate() {
							//
							// @Override
							// public void UpdateSuccessFully(
							// ServiceResponse res) {
							// if (!res.isError()) {
							// TargetPestList
							// .Instance()
							// .refreshTragetPests(
							// appointment_id,
							// new UpdateInfoDelegate() {
							//
							// @Override
							// public void UpdateSuccessFully(
							// ServiceResponse res) {
							// hideProgress();
							// finish();
							// }
							//
							// @Override
							// public void UpdateFail(
							// String ErrorMessage) {
							// hideProgress();
							// finish();
							// }
							// });
							// } else {
							// hideProgress();
							// finish();
							// }
							//
							// }
							//
							// @Override
							// public void UpdateFail(String ErrorMessage) {
							// hideProgress();
							// // Toast.makeText(getApplicationContext(),
							// // ErrorMessage, 1).show();
							// finish();
							// }
							// });
						} catch (ActiveRecordException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
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

	@Override
	public void ModelLoaded(ArrayList<PestsTypeInfo> list) {
		mBaseLoader.hideProgress();
		if (list != null) {
			m_pesttypes = Utils.Instance().sortPestCollections(list);
			bindData();
		} else {

			Toast.makeText(getApplicationContext(), "No Pest types added",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void ModelLoadFailedWithError(String error) {
		mBaseLoader.hideProgress();
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
	}

	private void bindData() {
		if (m_pesttypes.size() > 0) {
			m_adapter = new TargetPestAdapter(m_pesttypes);
			lstPestList.setAdapter(m_adapter);
		} else {
			Toast.makeText(getApplicationContext(), "No Pest types added",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.fragment_customer_list, menu);
//		if(!NetworkConnectivity.isConnected()){
//			menu.findItem(R.id.btnAddPest).setVisible(false);
//		}
		return super.onCreateOptionsMenu(menu);
	}

	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btnAddPest:
			Intent i = new Intent(PestTypeListActivity.this,
					AddPestTypeActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void alertToAdd(final PestsTypeInfo pest) {
		String message = "Are you sure to add this Pest type?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				PestTypeListActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// call();

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
