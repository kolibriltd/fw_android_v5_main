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

import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.Utils;
import com.anstar.models.MaterialInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.list.MaterialList;

import java.util.ArrayList;

public class MaterialListActivity extends AppCompatActivity implements
		OnClickListener, ModelDelegate<MaterialInfo> {

	private ListView lstAppointment;
	int appointment_id;
	private EditText edtSearch;
	private ImageView imgCancel;
	// MyAppointmentAdapter m_adapter;
	private MaterialListAdapter m_adapter = null;
	private ArrayList<MaterialInfo> m_materials = null;
	//ActionBar action = null;
	boolean isFromTrapMaterial = false;
	final int ADD_MATERIAL = 2;
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_list);
/*
		action = getSupportActionBar();
		// action.setTitle("Material List");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Material List</font>"));
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
		lstAppointment = (ListView) findViewById(R.id.lstMain);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		imgCancel = (ImageView) findViewById(R.id.imgCancel);
		imgCancel.setOnClickListener(this);

		m_materials = new ArrayList<MaterialInfo>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
			if (b.containsKey("isFromTrapMaterial")) {
				isFromTrapMaterial = b.getBoolean("isFromTrapMaterial");
			}
		} else {
			appointment_id = Const.app_id;
		}

		try {
			mBaseLoader.showProgress();
			MaterialList.Instance().load(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_adapter = new MaterialListAdapter(m_materials);
					lstAppointment.setAdapter(m_adapter);
				}
				ArrayList<MaterialInfo> temp = new ArrayList<MaterialInfo>();
				for (MaterialInfo c : m_materials) {
					if (c.name.toLowerCase().contains(text.toLowerCase())) {
						temp.add(c);
					}
				}
				m_adapter = new MaterialListAdapter(temp);
				lstAppointment.setAdapter(m_adapter);
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
			MaterialList.Instance().load(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MaterialListAdapter extends BaseAdapter {
		ArrayList<MaterialInfo> m_list = new ArrayList<MaterialInfo>();

		public MaterialListAdapter(ArrayList<MaterialInfo> list) {
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

			final MaterialInfo material = m_list.get(position);
			holder.main_item_text.setText(material.name);
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// alertToAdd(material);
					// TempLocation.ClearDB();
					// MaterialUsageTargetPestInfo.ClearDB();
					if (material.id < 0) {
						try {
							MaterialList.Instance().load(
									MaterialListActivity.this);
							Toast.makeText(getApplicationContext(),
									"Please select again, data was not proper",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Intent i = new Intent(MaterialListActivity.this,
								AddMaterialUsageActivity.class);
						i.putExtra("isFromTrapMaterial", isFromTrapMaterial);
						Const.material_id = material.id;
						startActivityForResult(i, ADD_MATERIAL);
					}
					// finish();
				}
			});
			return rowView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ADD_MATERIAL:
			if (resultCode == RESULT_OK) {
				Intent i = new Intent();
				setResult(Activity.RESULT_OK, i);
				finish();
			}
			break;
		default:
			break;
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
	public void ModelLoaded(ArrayList<MaterialInfo> list) {
		mBaseLoader.hideProgress();
		if (list != null) {
			m_materials = Utils.Instance().sortMaterialCollections(list);
			bindData();
		} else {
			Toast.makeText(getApplicationContext(), "No Material added",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void ModelLoadFailedWithError(String error) {
		mBaseLoader.hideProgress();
		Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
	}

	private void bindData() {
		if (m_materials.size() > 0) {
			m_adapter = new MaterialListAdapter(m_materials);
			lstAppointment.setAdapter(m_adapter);
		} else {
			Toast.makeText(getApplicationContext(), "No Material added",
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
			Intent i = new Intent(MaterialListActivity.this,
					AddMaterialActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void alertToAdd(final MaterialInfo pest) {
		String message = "Are you sure to add this material?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				MaterialListActivity.this);

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
