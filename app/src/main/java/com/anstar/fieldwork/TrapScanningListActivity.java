package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.common.AlphanumComparator;
import com.anstar.common.Const;
import com.anstar.common.SegmentedRadioGroup;
import com.anstar.common.Utils;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.models.list.TrapList;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import java.util.ArrayList;
import java.util.Collections;

public class TrapScanningListActivity extends AppCompatActivity implements
		OnClickListener {

	// private Button btnUnchecked, btnChecked;
	private ListView lstTraps;
	int appointment_id = 0;
	private TrapScanningAdapter m_adapter = null;
	private ArrayList<TrapScanningInfo> m_traps = null;
	private ActionBar action = null;

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private Button btnScan;
	private ImageView imgCancel;
	private EditText edtSearch;
	private ArrayList<TrapScanningInfo> m_list;

	private SegmentedRadioGroup segmentText;
	private AppointmentInfo appointmentInfo = null;
	boolean isFromUnChecked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trap_scanning_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		// btnUnchecked = (Button) findViewById(R.id.btnUnchecked);
		// btnChecked = (Button) findViewById(R.id.btnChecked);
		lstTraps = (ListView) findViewById(R.id.lstAppointment_Related);
		btnScan = (Button) findViewById(R.id.btnScan);
		imgCancel = (ImageView) findViewById(R.id.imgCancel);
		edtSearch = (EditText) findViewById(R.id.edtSearch);
		btnScan.setOnClickListener(this);
		// btnUnchecked.setOnClickListener(this);
		// btnChecked.setOnClickListener(this);
		segmentText = (SegmentedRadioGroup) findViewById(R.id.segment_test);
		m_traps = new ArrayList<TrapScanningInfo>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
		}
		if (appointment_id == 0) {
			appointment_id = Const.app_id;
		}
		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);

		// comment this when data comes
		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String text = edtSearch.getText().toString();
				if (text.length() <= 0) {
					m_adapter = new TrapScanningAdapter(m_list);
					lstTraps.setAdapter(m_adapter);
				} else {
					ArrayList<TrapScanningInfo> temp = new ArrayList<TrapScanningInfo>();
					for (TrapScanningInfo c : m_list) {
						if (c.number.startsWith(text.toString()) || c.barcode.startsWith(text.toString())) {
							temp.add(c);
						}
					}
					m_adapter = new TrapScanningAdapter(temp);
					lstTraps.setAdapter(m_adapter);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		segmentText.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (group == segmentText) {
					if (checkedId == R.id.button_unChecked) {
						loadViewForUnChecked();

					} else if (checkedId == R.id.button_Checked) {
						LoadViewForChecked();
					}
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		ServiceLocationsInfo serviceLocationInfo = ServiceLocationsList
				.Instance().getServiceLocationById(
						appointmentInfo.service_location_id);

		TrapList.Instance().LoadCheckedANDunCheckedTraps(appointment_id,
				appointmentInfo.customer_id, serviceLocationInfo.id);
		m_traps = TrapList.Instance().getAllTrapsByCustomerId(
				appointmentInfo.customer_id, serviceLocationInfo.id);
		Collections.sort(m_traps, new AlphanumComparator());
		segmentText.check(R.id.button_unChecked);
		segmentText.check(R.id.button_Checked);
		segmentText.check(R.id.button_unChecked);
		// try {
		// showProgress();
		// list.load(this, appointment_id);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public class TrapScanningAdapter extends BaseAdapter {
		ArrayList<TrapScanningInfo> m_list = new ArrayList<TrapScanningInfo>();

		public TrapScanningAdapter(ArrayList<TrapScanningInfo> list) {
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
				rowView = li.inflate(R.layout.trap_scan_list_item, null);
				rowView.setTag(holder);
				holder.main_item_text = (TextView) rowView
						.findViewById(R.id.main_item_text);
				holder.txtTrapId = (TextView) rowView
						.findViewById(R.id.txtTrapId);
				holder.txtLocation = (TextView) rowView
						.findViewById(R.id.txtTrapLocation);
				holder.txtFreqValue = (TextView) rowView
						.findViewById(R.id.txtFreqvalue);

				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}

			final TrapScanningInfo traps = m_list.get(position);
			// PestsTypeInfo pestinfo = PestTypeList.Instance().getPestById(
			// targetPest.pest_type_id);
			// holder.main_item_text.setTextSize(14);
			// holder.main_item_text.setPadding(5, 5, 5, 5);
			holder.main_item_text.setText(traps.barcode);
			if (traps.number != null && traps.number.length() > 0
					&& !traps.number.equalsIgnoreCase("null")) {
				holder.txtTrapId.setText("ID# " + traps.number);
			} else {
				holder.txtTrapId.setText("ID# 00");
			}
			if (traps.location_details != null
					&& traps.location_details.length() > 0
					&& !traps.location_details.equalsIgnoreCase("null")) {
				holder.txtLocation.setVisibility(View.VISIBLE);
				holder.txtLocation.setText(traps.location_details);
			} else {
				holder.txtLocation.setVisibility(View.GONE);
			}
			if (traps.service_frequency != null
					&& traps.service_frequency.length() > 0
					&& !traps.service_frequency.equalsIgnoreCase("null")) {
				holder.txtFreqValue.setVisibility(View.INVISIBLE);
				holder.txtFreqValue.setText(traps.service_frequency);
			} else {
				holder.txtFreqValue.setVisibility(View.GONE);
			}

			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// alertToDelete(targetPest.id);
					Const.BarCode = traps.barcode;
					Intent i = new Intent(TrapScanningListActivity.this,
							NewTrapDetailsActivity.class);
					i.putExtra("BARCODE", traps.barcode);
					i.putExtra("CUST_ID", appointmentInfo.customer_id);
					i.putExtra(Const.Appointment_Id, appointment_id);
					i.putExtra("isFromUnChecked", isFromUnChecked);
					startActivity(i);
				}
			});
			return rowView;
		}
	}

	public static class ViewHolder {
		TextView main_item_text, txtTrapId, txtLocation, txtFreqValue;
		RelativeLayout rl_main_list_item;
	}

	public void loadViewForUnChecked() {
		m_adapter = null;
		m_list = new ArrayList<TrapScanningInfo>();
		for (TrapScanningInfo trap : m_traps) {
			if (!trap.isChecked) {
				m_list.add(trap);
			}
		}
		m_adapter = new TrapScanningAdapter(m_list);
		lstTraps.setAdapter(m_adapter);
		isFromUnChecked = true;

	}

	public void LoadViewForChecked() {
		m_adapter = null;
		m_list = new ArrayList<TrapScanningInfo>();
		for (TrapScanningInfo trap : m_traps) {
			if (trap.isChecked) {
				m_list.add(trap);
			}
		}
		m_adapter = new TrapScanningAdapter(m_list);
		lstTraps.setAdapter(m_adapter);
		isFromUnChecked = false;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(btnScan)) {
			launchScanner();
			// initiateBarCodeReader(TrapScanningListActivity.this);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ZBAR_SCANNER_REQUEST:
		case ZBAR_QR_SCANNER_REQUEST:
			if (resultCode == RESULT_OK) {
				try {
					final MediaPlayer mp1 = MediaPlayer.create(
							getBaseContext(), R.raw.barcodebeep);
					mp1.start();
					// m_scanResult = IntentIntegrator.parseActivityResult(
					// requestCode, resultCode, data);
					String msg = "";
					if (data != null) {
						msg = data.getStringExtra(ZBarConstants.SCAN_RESULT);
						// msg = data.getStringExtra(ZBarConstants.SCAN_RESULT);
						Const.BarCode = msg;
						TrapScanningInfo info = TrapList.Instance()
								.getTrapByBarcodeNdCustomerId(msg,
										appointmentInfo.customer_id);

						if (info == null) {
							Intent i = new Intent(
									TrapScanningListActivity.this,
									AddTrapsActivity.class);
							i.putExtra(Const.Appointment_Id, appointment_id);
							i.putExtra("BARCODE", msg);
							i.putExtra("CUST_ID", appointmentInfo.customer_id);
							startActivity(i);
						} else {
							for (TrapScanningInfo trap : m_traps) {
								if (trap.isChecked) {
									if (trap.barcode.equalsIgnoreCase(msg)) {
										String message = "That device has already been inspected.";
										AlertDialog.Builder alt_bld = new AlertDialog.Builder(
												TrapScanningListActivity.this);

										alt_bld.setMessage(message)
												.setCancelable(false)
												.setPositiveButton(
														"Ok",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int id) {

																dialog.cancel();
															}
														});

										AlertDialog alert = alt_bld.create();
										alert.setTitle("Alert");
										alert.show();
										return;
									}
								}
							}
							Intent i = new Intent(
									TrapScanningListActivity.this,
									NewTrapDetailsActivity.class);
							i.putExtra(Const.Appointment_Id, appointment_id);
							i.putExtra("BARCODE", msg);
							i.putExtra("CUST_ID", appointmentInfo.customer_id);

							startActivity(i);
						}

						/*
						 * if (info != null) { Intent i = new Intent(
						 * TrapScanningListActivity.this,
						 * NewTrapDetailsActivity.class);
						 * i.putExtra(Const.Appointment_Id, appointment_id);
						 * i.putExtra("BARCODE", msg); i.putExtra("CUST_ID",
						 * appointmentInfo.customer_id);
						 * 
						 * startActivity(i); } else { Intent i = new Intent(
						 * TrapScanningListActivity.this,
						 * AddTrapsActivity.class);
						 * i.putExtra(Const.Appointment_Id, appointment_id);
						 * i.putExtra("BARCODE", msg); i.putExtra("CUST_ID",
						 * appointmentInfo.customer_id); startActivity(i); }
						 */
					}
				} catch (Exception e) {
					Utils.LogException(e);
				}
				// Toast.makeText(
				// this,
				// "Scan Result = "
				// + data.getStringExtra(ZBarConstants.SCAN_RESULT),
				// Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	public void launchScanner() {
		if (isCameraAvailable()) {
			Intent intent = new Intent(TrapScanningListActivity.this,
					ZBarScannerActivity.class);
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else {
			Toast.makeText(this, "Rear Facing Camera Unavailable",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
}
