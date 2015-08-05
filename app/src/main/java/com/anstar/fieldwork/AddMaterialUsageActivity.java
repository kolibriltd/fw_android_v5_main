package com.anstar.fieldwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.Generics;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.ApplicationDeviceTypeInfo;
import com.anstar.models.ApplicationMethodInfo;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.DeviceTypesInfo;
import com.anstar.models.DilutionInfo;
import com.anstar.models.InspectionMaterial;
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsage.UpdateMUInfoDelegate;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.MaterialUsageTargetPestInfo;
import com.anstar.models.MeasurementInfo;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.TempLocation;
import com.anstar.models.list.ApplicationDeviceTypeList;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.DeviceTypesList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.MaterialUsagesList;
import com.anstar.models.list.MaterialUsagesRecordsList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.ServiceLocationsList;

import java.util.ArrayList;
import java.util.List;

public class AddMaterialUsageActivity extends AppCompatActivity {
	private Button btnSave;
	private Spinner spnDilutionRate, spnMeasurement, spnApplicationMethod,
			spnDeviceType;
	private EditText edtQuantity, edtLotNumber;
	TextView txtMaterialName;
	//ActionBar action = null;
	int materialId = 0;
	int appointment_id = 0, edit_usage_id = 0;
	MaterialInfo material = null;
	private MaterialUsage m_materialUsage;
	ArrayList<MeasurementInfo> m_measurements = null;
	ArrayList<DilutionInfo> m_Dilutions = null;
	ArrayList<LocationAreaInfo> m_Locations = null;
	ArrayList<ApplicationDeviceTypeInfo> m_ApplicationDevice = null;
	ArrayList<ApplicationMethodInfo> m_Applicationmethods = null;
	ArrayList<DeviceTypesInfo> m_DeviceTypes = null;
	RelativeLayout rladdLocation, rlAddTargetPest;
	ArrayList<TempLocation> m_loc = null;
	ArrayList<MaterialUsageTargetPestInfo> m_material_targets = null;
	boolean isFromLocation = false, isEdit = false, isMaterialTarget = false;;
	ListView lstLocations, lstTargetPest;
	private static int LOCATION_REQUEST_ID = 1;
	private static int TARGET_REQUEST_ID = 2;
	int location_type_id = 0;
	boolean isFromTrapMaterial = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_material_usage);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("isFromLocation")) {
				isFromLocation = b.getBoolean("isFromLocation");
			}
			if (b.containsKey("isMaterialTarget")) {
				isMaterialTarget = b.getBoolean("isMaterialTarget");
			}
			if (b.containsKey("isEdit")) {
				isEdit = b.getBoolean("isEdit");
				edit_usage_id = b.getInt("usage_id");
			}
			if (b.containsKey("isFromTrapMaterial")) {
				isFromTrapMaterial = b.getBoolean("isFromTrapMaterial");
			}
		}
		if (isEdit) {
			if (edit_usage_id == 0) {
				finish();
				return;
			}
		}
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// int custid = Const.customer_id;
		// locations = new ArrayList<String>();
		materialId = Const.material_id;
		appointment_id = Const.app_id;
/*
		action = getSupportActionBar();
		// action.setTitle("Add Material Usage");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Add Material Usage</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		m_Dilutions = DilutionRatesList.Instance().getDilutionList();
		m_measurements = new MeasurementInfo().getMeasurementList();
		m_Applicationmethods = new ApplicationMethodInfo()
				.getApplicationMethodList();
		m_DeviceTypes = DeviceTypesList.Instance().getDeviceTypesList();
		m_Locations = LocationAreaInfo.getAllLocationArea();
		m_ApplicationDevice = ApplicationDeviceTypeList.Instance()
				.getAppDeviceList();
		rladdLocation = (RelativeLayout) findViewById(R.id.rlAddLocation);
		rlAddTargetPest = (RelativeLayout) findViewById(R.id.rlAddTargetPest);
		txtMaterialName = (TextView) findViewById(R.id.txtMaterialName);
		lstLocations = (ListView) findViewById(R.id.lstLocations);
		lstTargetPest = (ListView) findViewById(R.id.lstTargetpest);
		btnSave = (Button) findViewById(R.id.btnSave);
		edtQuantity = (EditText) findViewById(R.id.edtQuantity);
		spnDilutionRate = (Spinner) findViewById(R.id.spnDilutionRate);
		spnMeasurement = (Spinner) findViewById(R.id.spnMeasurement);
		spnApplicationMethod = (Spinner) findViewById(R.id.spnApplicationMethod);
		spnDeviceType = (Spinner) findViewById(R.id.spnDeviceType);
		edtLotNumber = (EditText) findViewById(R.id.edtLotNumber);
		edtQuantity.setFocusable(false);
		edtLotNumber.setFocusable(false);

		// int custid = Const.customer_id;
		// CustomerInfo customer =
		// CustomerList.Instance().getCustomerById(custid);
		AppointmentInfo appointment = AppointmentModelList.Instance()
				.getAppointmentById(appointment_id);
		if (appointment != null) {

			int ser_id = appointment.service_location_id;
			ServiceLocationsInfo ser_info = ServiceLocationsList.Instance()
					.getServiceLocationById(ser_id);
			if (ser_info != null) {
				if (ser_info.location_type_id == 0) {
					rladdLocation.setVisibility(View.GONE);
				}
			}
		}
		edtQuantity.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.setFocusableInTouchMode(true);
				return false;
			}
		});
		edtLotNumber.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.setFocusableInTouchMode(true);
				return false;
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SaveMaterialUsages();
			}
		});

		rladdLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(AddMaterialUsageActivity.this,
						LocationAreaListActivity.class);
				i.putExtra(Const.Appointment_Id, appointment_id);
				startActivityForResult(i, LOCATION_REQUEST_ID);
			}
		});
		rlAddTargetPest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// i.putExtra(Const.Appointment_Id, appointment_id);
				// Intent i = new Intent(AddMaterialUsageActivity.this,
				// MaterialUsageTargetListActivity.class);
				// i.putExtra(Const.Appointment_Id, appointment_id);
				// if (isEdit) {
				// i.putExtra("record_id", edit_usage_id);
				// }
				// startActivityForResult(i, TARGET_REQUEST_ID);
				Intent i = new Intent(AddMaterialUsageActivity.this,
						PestTypeListActivity.class);
				i.putExtra(Const.Appointment_Id, appointment_id);
				if (isEdit) {
					i.putExtra("record_id", edit_usage_id);
				}
				i.putExtra("isFROMMATERIAL", true);
				startActivityForResult(i, TARGET_REQUEST_ID);
			}
		});
		loadvalues();
		if (isEdit) {
			action.setTitle("Edit Material Usages");
			m_materialUsage = MaterialUsage.getMaterialUsageById(edit_usage_id);
			LoadValueIsFromEdit();
		} else {
			m_materialUsage = new MaterialUsage();
			m_materialUsage.material_id = materialId;
			material = MaterialInfo.getMaterialById(materialId);
			txtMaterialName.setText(material.name);
			if (material.default_dilution_rate_id != null) {
				int dil_id = 0;
				for (int i = 0; i < m_Dilutions.size(); i++) {
					if (String.valueOf(m_Dilutions.get(i).id).equalsIgnoreCase(
							String.valueOf(material.default_dilution_rate_id))) {
						dil_id = i;
						break;
					}
				}
				spnDilutionRate.setSelection(dil_id);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LOCATION_REQUEST_ID) {
			if (resultCode == RESULT_OK) {
				m_loc = TempLocation.getAll();
				bindLocation(m_loc);
			}
		}
		if (requestCode == TARGET_REQUEST_ID) {
			if (resultCode == RESULT_OK) {
				m_material_targets = MaterialUsageTargetPestInfo.getAll();
				bindMaterialTarget(m_material_targets);
				// m_loc = TempLocation.getAll();
				// bindLocation(m_loc);
			}
		}
	}

	public void loadvalues() {
		setHint();
		if (m_Dilutions != null && m_Dilutions.size() > 0) {
			setSpinnerValues("name", m_Dilutions, DilutionInfo.class,
					spnDilutionRate);
		}
		if (m_Locations != null && m_Locations.size() > 0) {
			List<String> lst = new ArrayList<String>();
			try {
				lst = Generics.getStringList("name", m_Locations,
						LocationAreaInfo.class);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		if (m_measurements != null && m_measurements.size() > 0) {
			setSpinnerValues("name", m_measurements, MeasurementInfo.class,
					spnMeasurement);
		}
		if (m_Applicationmethods != null && m_Applicationmethods.size() > 0) {
			setSpinnerValues("name", m_Applicationmethods,
					ApplicationMethodInfo.class, spnApplicationMethod);
		}
		if (m_DeviceTypes != null && m_DeviceTypes.size() > 0) {

			setSpinnerValues("name", m_DeviceTypes, DeviceTypesInfo.class,
					spnDeviceType);
		}
		if (isFromLocation) {
			m_loc = TempLocation.getAll();
			bindLocation(m_loc);
		}
		if (isMaterialTarget) {
			m_material_targets = MaterialUsageTargetPestInfo.getAll();
			bindMaterialTarget(m_material_targets);
		}
	}

	@Override
	protected void onDestroy() {
		TempLocation.ClearDB();
		MaterialUsageTargetPestInfo.ClearDB();
		super.onDestroy();
	}

	public void setSpinnerValues(String propertyName, ArrayList mainList,
			Class type, Spinner spn) {
		ArrayList<String> lst = new ArrayList<String>();
		try {
			lst = Generics.getStringList(propertyName, mainList, type);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
				R.layout.spinner_item, lst);
		spn.setAdapter(adp);
	}

	public void setHint() {
		DilutionInfo d = new DilutionInfo();
		d.name = "Dilution Rate";
		if (m_Dilutions == null)
			m_Dilutions = new ArrayList<DilutionInfo>();
		m_Dilutions.add(0, d);

		LocationAreaInfo l = new LocationAreaInfo();
		l.name = "Location";
		if (m_Locations == null)
			m_Locations = new ArrayList<LocationAreaInfo>();
		m_Locations.add(0, l);

		MeasurementInfo m = new MeasurementInfo();
		m.name = "Measurements";
		if (m_measurements == null)
			m_measurements = new ArrayList<MeasurementInfo>();
		m_measurements.add(0, m);

		ApplicationMethodInfo a = new ApplicationMethodInfo();
		a.name = "Application Method";
		if (m_Applicationmethods == null)
			m_Applicationmethods = new ArrayList<ApplicationMethodInfo>();
		m_Applicationmethods.add(0, a);

		DeviceTypesInfo d1 = new DeviceTypesInfo();
		d1.name = "Device";
		if (m_DeviceTypes == null)
			m_DeviceTypes = new ArrayList<DeviceTypesInfo>();
		m_DeviceTypes.add(0, d1);
	}

	public void SaveMaterialUsages() {
		String msg = "";
		boolean flag = true;
		if (spnDilutionRate.getSelectedItem().toString()
				.equalsIgnoreCase("Dilution Rate")) {
			msg = "Please insert dilution rate";
			flag = false;
		} else if (edtQuantity.getText().toString().length() < 1) {
			msg = "Please insert Quantity";
			flag = false;
		} else if (spnMeasurement.getSelectedItem().toString()
				.equalsIgnoreCase("Measurements")) {
			msg = "Please insert Measurements";
			flag = false;
		}
		// else if (spnApplicationMethod.getSelectedItem().toString()
		// .equalsIgnoreCase("Application Methods")) {
		// msg = "Please insert Application Methods";
		// flag = false;
		// }
		if (edtQuantity.getText().toString().length() > 0) {
			float qnt = Float.parseFloat(edtQuantity.getText().toString());
			if (qnt <= 0) {
				msg += "\nPlease insert valid Quantity";
				flag = false;
			}
		}
		// else if (spnDeviceType.getSelectedItem().toString()
		// .equalsIgnoreCase("Device")) {
		// msg = "Please insert Application Device";
		// flag = false;
		// }
		if (!flag) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
					.show();
		} else {
			ProgressDialog.showProgress(this);

			List<String> list = new ArrayList<String>();
			ArrayList<Integer> locationIds = new ArrayList<Integer>();
			ArrayList<MaterialUsageRecords> m_records = new ArrayList<MaterialUsageRecords>();
			MaterialUsageRecords records = null;
			ArrayList<String> ids = new ArrayList<String>();
			ArrayList<MaterialUsageTargetPestInfo> mt_list = MaterialUsageTargetPestInfo
					.getAll();
			String pest_ids = "";
			if (mt_list != null && mt_list.size() > 0) {
				for (MaterialUsageTargetPestInfo mt : mt_list) {
					ids.add(String.valueOf(mt.pest_type_id));
				}
				pest_ids = Utils.Instance().join(ids, ",");
			}
			String devicetype = spnDeviceType.getSelectedItem().toString()
					.equalsIgnoreCase("Device") ? "" : spnDeviceType
					.getSelectedItem().toString();
			String appmethod = spnApplicationMethod.getSelectedItem()
					.toString().equalsIgnoreCase("Application Method") ? ""
					: spnApplicationMethod.getSelectedItem().toString();
			if (m_loc != null && m_loc.size() > 0) {
				for (TempLocation loc : m_loc) {
					list.add(loc.location);
				}
				String s = Utils.Instance().join(list, ",");
				String[] locations = s.split(",");
				int location_count = m_loc.size();
				double devided_qty = Utils.ConvertToDouble(edtQuantity
						.getText().toString()) / location_count;

				// for (int i = 0; i < locations.length; i++) {
				// locationIds.add(LocationAreaInfo
				// .getLocationIdByname(locations[i]
				// .trim()));
				// }
				for (TempLocation loc : m_loc) {
					locationIds.add(loc.location_id);
				}
				for (int i = 0; i < locationIds.size(); i++) {
					records = new MaterialUsageRecords();
					records.dilution_rate_id = DilutionRatesList.Instance()
							.getDilutionIdByname(
									spnDilutionRate.getSelectedItem()
											.toString());
					records.location_area_id = locationIds.get(i);
					records.amount = String.valueOf(devided_qty);
					records.measurement = spnMeasurement.getSelectedItem()
							.toString();
					records.application_method = appmethod;
					records.device = devicetype;
					records.application_device_type_id = DeviceTypesList
							.Instance().getDeviceIdByname(
									spnDeviceType.getSelectedItem().toString());
					records.application_method_id = ApplicationMethodInfo
							.getMethodIdByname(spnApplicationMethod
									.getSelectedItem().toString());
					records.lot_number = edtLotNumber.getText().toString();
					records.Pest_ids = pest_ids;
					m_records.add(records);
				}
			} else {
				records = new MaterialUsageRecords();
				records.dilution_rate_id = DilutionRatesList.Instance()
						.getDilutionIdByname(
								spnDilutionRate.getSelectedItem().toString());
				records.location_area_id = 0;
				records.amount = edtQuantity.getText().toString();
				records.measurement = spnMeasurement.getSelectedItem()
						.toString();
				records.application_method = appmethod;
				records.application_method_id = ApplicationMethodInfo
						.getMethodIdByname(spnApplicationMethod
								.getSelectedItem().toString());
				records.device = devicetype;
				records.application_device_type_id = DeviceTypesList.Instance()
						.getDeviceIdByname(
								spnDeviceType.getSelectedItem().toString());
				records.lot_number = edtLotNumber.getText().toString();
				records.Pest_ids = pest_ids;
				m_records.add(records);
			}

			final ArrayList<MaterialUsageRecords> m_records1 = new ArrayList<MaterialUsageRecords>();
			m_records1.addAll(m_records);
			if (!isFromTrapMaterial) {
				if (isEdit) {
					MaterialUsage.DeleteMaterialUsageRecord(m_records.get(0),
							appointment_id, m_materialUsage,
							new UpdateMUInfoDelegate() {
								@Override
								public void UpdateSuccessFully(
										ServiceResponse res) {
									if (!res.isError()) {
										MaterialUsage.AddMaterialUsageRecords(
												isFromTrapMaterial,
												appointment_id, m_records1,
												m_materialUsage,
												new UpdateMUInfoDelegate() {

													@Override
													public void UpdateSuccessFully(
															ServiceResponse res) {
														if (!res.isError()) {
															if (NetworkConnectivity
																	.isConnected()) {
																MaterialUsagesList
																		.Instance()
																		.refreshMaterialUsage(
																				appointment_id,
																				new UpdateInfoDelegate() {

																					@Override
																					public void UpdateSuccessFully(
																							ServiceResponse res) {
																						gotoback();
																					}

																					@Override
																					public void UpdateFail(
																							String ErrorMessage) {
																						gotoback();
																					}
																				});
															} else {
																gotoback();
															}

														} else {
															gotoback();
														}

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
								}

								@Override
								public void UpdateFail(String ErrorMessage) {
									ProgressDialog.hideProgress();
									Toast.makeText(getApplicationContext(),
											ErrorMessage, Toast.LENGTH_LONG)
											.show();
								}
							});
				} else {
					MaterialUsage.AddMaterialUsageRecords(isFromTrapMaterial,
							appointment_id, m_records, m_materialUsage,
							new UpdateMUInfoDelegate() {

								@Override
								public void UpdateSuccessFully(
										ServiceResponse res) {
									if (!res.isError()) {
										if (NetworkConnectivity.isConnected()) {
											MaterialUsagesList
													.Instance()
													.refreshMaterialUsage(
															appointment_id,
															new UpdateInfoDelegate() {

																@Override
																public void UpdateSuccessFully(
																		ServiceResponse res) {
																	gotoback();
																}

																@Override
																public void UpdateFail(
																		String ErrorMessage) {
																	gotoback();
																}
															});
										} else {
											gotoback();
										}

									} else {
										gotoback();
									}
								}

								@Override
								public void UpdateFail(String ErrorMessage) {
									ProgressDialog.hideProgress();
									Toast.makeText(getApplicationContext(),
											ErrorMessage, Toast.LENGTH_LONG)
											.show();
								}
							});
				}
			} else {
				MaterialUsage.AddMaterialUsageRecordsForTrapOnly(
						isFromTrapMaterial, appointment_id, m_records,
						m_materialUsage, new UpdateMUInfoDelegate() {

							@Override
							public void UpdateSuccessFully(ServiceResponse res) {
								// gotoback();
								InspectionMaterial.AddMaterial(res.StatusCode);
								Intent i = new Intent();
								// i.putExtra("isInspectionMaterial",
								// true);
								setResult(Activity.RESULT_OK, i);
								finish();
							}

							@Override
							public void UpdateFail(String ErrorMessage) {

								ProgressDialog.hideProgress();
							}
						});

			}

		}

	}

	public void gotoback() {
		ProgressDialog.hideProgress();
		if (isFromTrapMaterial) {
			Intent i = new Intent(AddMaterialUsageActivity.this,
					TrapMaterialUsageActivity.class);
			i.putExtra(Const.Appointment_Id, appointment_id);
			startActivity(i);
			finish();

		} else {
			// Intent i = new Intent(AddMaterialUsageActivity.this,
			// MaterialUsageListActivity.class);
			// i.putExtra(Const.Appointment_Id, appointment_id);
			// startActivity(i);
			Intent i = new Intent();
			setResult(Activity.RESULT_OK, i);
			finish();
		}
	}

	public void bindLocation(ArrayList<TempLocation> m_arr) {
		ArrayList<TempLocation> tl = new ArrayList<TempLocation>();
		for (TempLocation tempLocation : m_arr) {
			if (tempLocation.location_id != 0)
				tl.add(tempLocation);
		}
		LocationAdapter adapter = new LocationAdapter(tl);
		lstLocations.setAdapter(adapter);
		Utils.setListViewHeightBasedOnChildren(lstLocations);
	}

	public void bindMaterialTarget(ArrayList<MaterialUsageTargetPestInfo> m_arr) {
		TargetPestAdapter adapter = new TargetPestAdapter(m_arr);
		lstTargetPest.setAdapter(adapter);
		Utils.setListViewHeightBasedOnChildren(lstTargetPest);
	}

	public class LocationAdapter extends BaseAdapter {
		ArrayList<TempLocation> m_list = new ArrayList<TempLocation>();

		public LocationAdapter(ArrayList<TempLocation> list) {
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
				holder.imgCancel = (ImageView) rowView
						.findViewById(R.id.imgCancel);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}
			holder.imgCancel.setVisibility(View.VISIBLE);

			final TempLocation location = m_list.get(position);
			holder.main_item_text.setText(location.location);
			holder.imgCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TempLocation.RemoveArea(location.location,
							location.location_id);
					m_loc = TempLocation.getAll();
					bindLocation(m_loc);
				}
			});
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
			return rowView;
		}
	}

	public class TargetPestAdapter extends BaseAdapter {
		ArrayList<MaterialUsageTargetPestInfo> m_list = new ArrayList<MaterialUsageTargetPestInfo>();

		public TargetPestAdapter(ArrayList<MaterialUsageTargetPestInfo> list) {
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
				holder.imgCancel = (ImageView) rowView
						.findViewById(R.id.imgCancel);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}
			holder.imgCancel.setVisibility(View.VISIBLE);

			final MaterialUsageTargetPestInfo targetPest = m_list.get(position);
			PestsTypeInfo pestinfo = PestTypeList.Instance().getPestById(
					targetPest.pest_type_id);
			if (pestinfo != null)
				holder.main_item_text.setText(pestinfo.name);
			holder.imgCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isEdit) {
						MaterialUsageRecords records = MaterialUsagesRecordsList
								.Instance().getMaterialRecordByUsageId(
										edit_usage_id);
						ArrayList<String> arr_ids = new ArrayList<String>();
						String[] ids = records.Pest_ids.split(",");
						for (String s : ids) {
							if (Utils.ConvertToInt(s) != targetPest.pest_type_id) {
								arr_ids.add(s);
							}
						}
						String temp = Utils.Instance().join(arr_ids, ",");
						records.Pest_ids = "";
						records.Pest_ids = temp;
						try {
							records.save();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}
					}
					MaterialUsageTargetPestInfo
							.RemoveTarget(targetPest.pest_type_id);
					m_material_targets = MaterialUsageTargetPestInfo.getAll();
					bindMaterialTarget(m_material_targets);
				}
			});
			holder.rl_main_list_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
			return rowView;
		}
	}

	public static class ViewHolder {
		TextView main_item_text;
		ImageView imgCancel;
		RelativeLayout rl_main_list_item;
	}

	public void LoadValueIsFromEdit() {

		MaterialUsage usage = MaterialUsage.getMaterialUsageById(edit_usage_id);
		if (usage != null) {

			txtMaterialName.setText(MaterialInfo
					.getMaterialNamebyId(usage.material_id));
			Const.material_id = usage.material_id;
			m_materialUsage.material_id = usage.material_id;
		}
		ArrayList<MaterialUsageRecords> recs = MaterialUsagesRecordsList
				.Instance().getMaterialRecordsByUsageId(edit_usage_id);
		for (MaterialUsageRecords record : recs) {
			String area = LocationAreaInfo
					.getLocationNameById(record.location_area_id);
			TempLocation.AddArea(area, record.location_area_id);
		}
		m_loc = new ArrayList<TempLocation>();
		m_loc = TempLocation.getAll();
		bindLocation(m_loc);

		MaterialUsageRecords records = MaterialUsagesRecordsList.Instance()
				.getMaterialRecordByUsageId(edit_usage_id);
		if (records != null) {
			if (records.Pest_ids != null && records.Pest_ids.length() > 0) {
				m_material_targets = new ArrayList<MaterialUsageTargetPestInfo>();
				String ids[] = records.Pest_ids.split(",");
				for (String s : ids) {
					MaterialUsageTargetPestInfo.AddTargetPest(Utils
							.ConvertToInt(s));
				}
				m_material_targets = MaterialUsageTargetPestInfo.getAll();
				bindMaterialTarget(m_material_targets);
			}
			int locations = 1;
			if (m_loc != null) {
				locations = m_loc.size();
			}
			if (records.amount != null && records.amount.length() > 0
					&& !records.amount.equalsIgnoreCase("null")) {
				edtQuantity.setText(String.valueOf(Float
						.parseFloat(records.amount) * locations));
			} else {
				edtQuantity.setText("0.0");
			}
			edtLotNumber.setText(records.lot_number + "");

			// loadDilutionnSpinner
			int dil_id = 0;

			for (int i = 0; i < m_Dilutions.size(); i++) {
				if (String.valueOf(m_Dilutions.get(i).id).equalsIgnoreCase(
						String.valueOf(records.dilution_rate_id))) {
					dil_id = i;
					break;
				}
			}
			spnDilutionRate.setSelection(dil_id);

			// LoadApplicationMethod
			int A_id = 0;
			for (int i = 0; i < m_Applicationmethods.size(); i++) {
				if (String.valueOf(m_Applicationmethods.get(i).name)
						.equalsIgnoreCase(
								String.valueOf(records.application_method))) {
					A_id = i;
					break;
				}
			}
			spnApplicationMethod.setSelection(A_id);

			// LoadUnitsTypeSpinner
			int u_id = 0;
			for (int i = 0; i < m_measurements.size(); i++) {
				if (m_measurements.get(i).name.equalsIgnoreCase(String
						.valueOf(records.measurement))) {
					u_id = i;
					break;
				}
			}
			spnMeasurement.setSelection(u_id);

			// LoadDeviceType
			int dt_id = 0;
			for (int i = 0; i < m_DeviceTypes.size(); i++) {
				if (m_DeviceTypes.get(i).name.equalsIgnoreCase(records.device)) {
					dt_id = i;
					break;
				}
			}
			spnDeviceType.setSelection(dt_id);
		}
	}

	@Override
	public void onBackPressed() {
		AlertOnBack();
	}

	public void AlertOnBack() {
		String message = "You have not saved this record, would you like to save before proceeding?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				AddMaterialUsageActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SaveMaterialUsages();
								dialog.cancel();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						finish();
					}
				});

		AlertDialog alert = alt_bld.create();
		alert.setTitle("Alert");
		alert.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AlertOnBack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
