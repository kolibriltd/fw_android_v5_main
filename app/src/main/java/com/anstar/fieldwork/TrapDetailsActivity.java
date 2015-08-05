package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.Generics;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.BaitConditionsInfo;
import com.anstar.models.InspectionInfo;
import com.anstar.models.InspectionMaterial;
import com.anstar.models.InspectionPest;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsage.UpdateMUInfoDelegate;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.TrapConditionsInfo;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.TrapTypesInfo;
import com.anstar.models.list.BaitConditionsList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.InspectionPestsList;
import com.anstar.models.list.MaterialUsagesRecordsList;
import com.anstar.models.list.TrapConditionsList;
import com.anstar.models.list.TrapList;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrapDetailsActivity extends AppCompatActivity implements
		OnClickListener {

	int appointment_id;
	int cust_id;
	private TextView txtBarcode, txtLocation, txtBuilding, txtFloor,
			txtTrapType, txtTrapNumber, txtMessage, txtExceptionCount;
	private RelativeLayout rlEvidence, rlCaptured, rlMaterial;
	private Button btnSaveTrapData, btnClean;
	ImageView imgClean;
	boolean isClean = false;

	private AppointmentInfo appointmentInfo = null;
	//private ActionBar action = null;
	private String barcode;

	private TrapScanningInfo trapscan_info = null;
	private InspectionInfo inspection = null;
	private String evidence = "";
	private boolean isEdit = false;
	final int EVIDENCE_CODE = 1;
	final int CAPTURE_REQUEST_ID = 2;
	final int MATERIAL_REQUEST_ID = 3;
	boolean isFromUnChecked = false;
	LinearLayout llContainer;
	ListView lstMaterial;
	ArrayList<InspectionPest> m_inspectionPests = new ArrayList<InspectionPest>();
	ArrayList<InspectionMaterial> m_inspection_material = new ArrayList<InspectionMaterial>();
	boolean isCleanInspection = false;
	ToggleButton tgbRemoved;
	Spinner spnBaitCondition, spnTrapCondition;
	EditText edtException;
	boolean isRemoved;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trap_details);

		// info = MainActivity.appointmentInfo;
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
			cust_id = b.getInt("CUST_ID");
			barcode = b.getString("BARCODE");
			if (b.containsKey("isFromUnChecked")) {
				isFromUnChecked = b.getBoolean("isFromUnChecked");
			}
		}
		if (appointment_id == 0) {
			appointment_id = Const.app_id;
		}
		if (cust_id == 0) {
			cust_id = Const.customer_id;
		}
		if (barcode.length() == 0) {
			barcode = Const.BarCode;
		}
/*
		action = getSupportActionBar();
		// action.setTitle("Traps Scan Details");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Traps Scan Details</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		lstMaterial = (ListView) findViewById(R.id.lstMaterialUsages);
		btnSaveTrapData = (Button) findViewById(R.id.btnSaveTrapData);
		btnClean = (Button) findViewById(R.id.btnClean);
		rlEvidence = (RelativeLayout) findViewById(R.id.rlEvidence);
		rlCaptured = (RelativeLayout) findViewById(R.id.rlCaptured);
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		rlMaterial = (RelativeLayout) findViewById(R.id.rlMaterialUsage);
		txtBarcode = (TextView) findViewById(R.id.txtBarcode);
		txtLocation = (TextView) findViewById(R.id.txtLocation);
		txtBuilding = (TextView) findViewById(R.id.txtBuilding);
		txtFloor = (TextView) findViewById(R.id.txtFloor);
		txtTrapType = (TextView) findViewById(R.id.txtTrapType);
		txtTrapNumber = (TextView) findViewById(R.id.txtNumber);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		imgClean = (ImageView) findViewById(R.id.imgClean);
		tgbRemoved = (ToggleButton) findViewById(R.id.tgbremoved);
		spnBaitCondition = (Spinner) findViewById(R.id.spnbait_condition_id);
		spnTrapCondition = (Spinner) findViewById(R.id.spntrap_condition_id);
		edtException = (EditText) findViewById(R.id.edtexception);
		txtExceptionCount = (TextView) findViewById(R.id.txtCount);
		btnSaveTrapData.setOnClickListener(this);
		btnClean.setOnClickListener(this);
		rlCaptured.setOnClickListener(this);
		rlEvidence.setOnClickListener(this);
		rlMaterial.setOnClickListener(this);
		trapscan_info = TrapList.Instance().getTrapByBarcodeNdCustomerId(
				barcode, cust_id);
		inspection = InspectionList.Instance().getInspectionByApp_Id(
				appointment_id, barcode);
		if (inspection == null) {
			isEdit = false;
			try {
				inspection = FieldworkApplication.Connection().newEntity(
						InspectionInfo.class);
				inspection.id = -1;
				inspection.barcode = barcode;
				inspection.save();
			} catch (ActiveRecordException e) {
				e.printStackTrace();
			}
		} else {
			isEdit = true;
			m_inspectionPests = InspectionPestsList.Instance()
					.getInspectionPestByInspectionId(inspection.id);
			isClean = getIsClean(inspection, m_inspectionPests);
			isCleanInspection = getIsClean(inspection, m_inspectionPests);
			if (isClean) {
				imgClean.setVisibility(View.VISIBLE);
			}
		}
		LoadValues();
		LoadSpinners();
		if (isEdit) {
			loadMaterial();
		}
		tgbRemoved.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isRemoved = isChecked;
			}
		});
	};

	public boolean getIsClean(InspectionInfo info,
			ArrayList<InspectionPest> m_list) {
		boolean yes = false;
		if (info != null && m_list.size() == 0) {
			if (info.evidence != null) {
				if (info.evidence.length() == 0) {
					yes = true;
				}
			} else {
				yes = true;
			}
		}
		return yes;
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		InspectionMaterial.ClearDB();
	}

	@Override
	public void onClick(View v) {
		if (v == rlEvidence) {

			Intent i = new Intent(TrapDetailsActivity.this,
					EvidenceActivity.class);
			i.putExtra("Inspection_id", inspection.id);
			startActivityForResult(i, EVIDENCE_CODE);

		} else if (v == rlCaptured) {
			Intent i = new Intent(TrapDetailsActivity.this,
					AddCapturedPestActivity.class);
			i.putExtra("BARCODE", inspection.barcode);
			i.putExtra("Inspection_id", inspection.id);

			startActivityForResult(i, CAPTURE_REQUEST_ID);
		} else if (v == btnSaveTrapData) {
			saveInspection();
		} else if (v == btnClean) {
			if (isClean) {
				imgClean.setVisibility(View.VISIBLE);
				isClean = false;
			} else {
				imgClean.setVisibility(View.GONE);
				isClean = true;
			}
			if (isCleanInspection) {
				isCleanInspection = false;
			} else {
				isCleanInspection = true;
			}
		} else if (v == rlMaterial) {
			Intent i = new Intent(TrapDetailsActivity.this,
					MaterialListActivity.class);
			// i.putExtra("BARCODE", inspection.barcode);
			// i.putExtra("Inspection_id", inspection.id);
			i.putExtra(Const.Appointment_Id, appointment_id);
			i.putExtra("isFromTrapMaterial", true);
			startActivityForResult(i, MATERIAL_REQUEST_ID);
		}
	}

	public void LoadSpinners() {
		ArrayList<BaitConditionsInfo> m_baits = new ArrayList<BaitConditionsInfo>();
		m_baits = BaitConditionsList.Instance().getBaitConditionsList();
		ArrayList<TrapConditionsInfo> m_trapconditions = new ArrayList<TrapConditionsInfo>();
		m_trapconditions = TrapConditionsList.Instance()
				.getTrapConditionsList();
		if (!isFromUnChecked) {
			BaitConditionsInfo d = new BaitConditionsInfo();
			d.name = "Bait Conditions";
			m_baits.add(0, d);
			setSpinnerValues("name", m_baits, BaitConditionsInfo.class,
					spnBaitCondition);
			TrapConditionsInfo tci = new TrapConditionsInfo();
			tci.name = "Trap Conditions";
			m_trapconditions.add(0, tci);
			setSpinnerValues("name", m_trapconditions,
					TrapConditionsInfo.class, spnTrapCondition);
			if (isEdit) {
				int b = 0;
				for (int i = 0; i < m_baits.size(); i++) {
					if (inspection.bait_condition_id == m_baits.get(i).id) {
						b = i;
						break;
					}
				}
				spnBaitCondition.setSelection(b);

				int t = 0;
				for (int i = 0; i < m_trapconditions.size(); i++) {
					if (inspection.trap_condition_id == m_trapconditions.get(i).id) {
						t = i;
						break;
					}
				}
				spnTrapCondition.setSelection(t);
				tgbRemoved.setSelected(inspection.removed);
				if (inspection.exception != null
						&& inspection.exception.length() > 0) {
					edtException.setText(inspection.exception);
					txtExceptionCount.setText(""
							+ inspection.exception.length() + " / 200");
				} else {
					txtExceptionCount.setText("0/ 200");
				}
			}
		}
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

	public void saveInspection() {
		String exception = "";
		if (edtException.getText().toString().length() > 0) {
			exception = edtException.getText().toString();
		}
		if (exception.length() > 200) {
			Toast.makeText(getApplicationContext(),
					"Exception must be less then 200 character.",
					Toast.LENGTH_LONG).show();
			return;
		}
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(
				new Date());
		inspection.deleteRelatedPestsRecords();
		if (isCleanInspection) {
			m_inspectionPests = new ArrayList<InspectionPest>();
			inspection.evidence = "";
		}

		inspection.trap_number = trapscan_info.number;
		inspection.trap_type_id = trapscan_info.trap_type_id;
		int bait = 0, trap = 0;
		if (!spnBaitCondition.getSelectedItem().toString()
				.equalsIgnoreCase("Bait Conditions")) {
			bait = BaitConditionsInfo.getIdByName(spnBaitCondition
					.getSelectedItem().toString());
		}
		if (!spnTrapCondition.getSelectedItem().toString()
				.equalsIgnoreCase("Trap Conditions")) {
			trap = TrapConditionsInfo.getIdByName(spnTrapCondition
					.getSelectedItem().toString());
		}
		inspection.bait_condition_id = bait;
		inspection.trap_condition_id = trap;
		currentDateTimeString = Utils.Instance().getFormatedDate(
				currentDateTimeString, "MMM dd, yyyy hh:mm:ss a",
				"yyyy-MM-dd hh:mm a");
		inspection.exception = exception;
		inspection.removed = isRemoved;
		inspection.scanned_on = currentDateTimeString;
		try {
			inspection.save();
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}

		AddTraps(m_inspectionPests, inspection);
		// if (m_list.size() == 1) {
		// if (m_list.get(0).count == 0 && m_list.get(0).pest_type_id == 0) {
		// m_list = new ArrayList<InspectionPest>();
		// AddTraps(m_list, inspection);
		// } else {
		// AddTraps(m_list, inspection);
		// }
		// } else {
		// }
	}

	public void AddTraps(ArrayList<InspectionPest> m_list,
			InspectionInfo inspection) {
		ProgressDialog.showProgress(this);
		InspectionInfo.AddInspectionRecord(appointment_id, m_list, inspection,
				new UpdateMUInfoDelegate() {
					@Override
					public void UpdateSuccessFully(ServiceResponse res) {
						if (!res.isError()) {
							if (NetworkConnectivity.isConnected()) {
								InspectionList.Instance()
										.refreshInspectionList(appointment_id,
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
                            ProgressDialog.hideProgress();
						}
					}

					@Override
					public void UpdateFail(String ErrorMessage) {
						ProgressDialog.hideProgress();
						Toast.makeText(getApplicationContext(), ErrorMessage,
								Toast.LENGTH_LONG).show();
					}
				});
	}

	public void gotoback() {
        ProgressDialog.hideProgress();
		TrapScanningInfo trap = TrapList.Instance()
				.getTrapByBarcodeNdCustomerId(barcode, cust_id);
		trap.isChecked = true;
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case EVIDENCE_CODE:
			if (resultCode == RESULT_OK) {
				inspection.evidence = data.getStringExtra("evidance");
				// Toast.makeText(getApplicationContext(), evidence,
				// Toast.LENGTH_LONG).show();
			}
			break;
		case CAPTURE_REQUEST_ID:
			if (resultCode == RESULT_OK) {
				String key = data.getStringExtra("KEY");
				@SuppressWarnings("unchecked")
				ArrayList<InspectionPest> arr = (ArrayList<InspectionPest>) ((FieldworkApplication) getApplication())
						.getStoredObject(key);
				((FieldworkApplication) getApplication())
						.removeStoredObject(key);
				m_inspectionPests = arr;
			}
			break;
		case MATERIAL_REQUEST_ID:
			if (resultCode == RESULT_OK) {
				m_inspection_material = InspectionMaterial.getAll();
				bindMaterial(m_inspection_material);
			}
			break;
		default:
			break;
		}
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
				holder.imgCancel = (ImageView) rowView
						.findViewById(R.id.imgCancel);
				holder.rl_main_list_item = (RelativeLayout) rowView
						.findViewById(R.id.rl_main_list_item);
			} else {
				holder = (ViewHolder) rowView.getTag();
			}
			holder.imgCancel.setVisibility(View.GONE);
			final MaterialUsage usage = m_list.get(position);
			if (usage != null) {
				holder.imgCancel.setVisibility(View.VISIBLE);
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
				holder.imgCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// if (isEdit) {
						ArrayList<String> arr_ids = new ArrayList<String>();
						if (inspection.Material_ids != null
								&& inspection.Material_ids.length() > 0) {
							if (inspection.Material_ids.contains(",")) {
								String[] ids = inspection.Material_ids
										.split(",");
								for (String s : ids) {
									if (Utils.ConvertToInt(s) != usage.id) {
										arr_ids.add(s);
									}
								}
							} else {
								if (Utils.ConvertToInt(inspection.Material_ids) != usage.id) {
									arr_ids.add(inspection.Material_ids);
								}
							}
						}
						String temp = Utils.Instance().join(arr_ids, ",");
						inspection.Material_ids = "";
						inspection.Material_ids = temp;
						try {
							inspection.save();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}
						// }
						InspectionMaterial.RemoveMaterial(usage.id);
						m_inspection_material = InspectionMaterial.getAll();
						bindMaterial(m_inspection_material);
					}
				});
			}
			return rowView;
		}
	}

	public static class ViewHolder {
		TextView main_item_text, sub_item_text;
		ImageView imgCancel;
		RelativeLayout rl_main_list_item;
	}

	public void bindMaterial(ArrayList<InspectionMaterial> m_arr) {
		ArrayList<MaterialUsage> m_usages = new ArrayList<MaterialUsage>();
		if (m_arr != null && m_arr.size() > 0) {
			for (InspectionMaterial im : m_arr) {
				MaterialUsage usage = MaterialUsage
						.getMaterialUsageById(im.material_id);
				if (usage != null) {
					m_usages.add(usage);
				}
			}
		}

		MaterialUsageAdapter adapter = new MaterialUsageAdapter(m_usages);
		lstMaterial.setAdapter(adapter);
		Utils.setListViewHeightBasedOnChildren(lstMaterial);
	}

	public void loadMaterial() {
		if (inspection != null) {
			if (inspection.Material_ids != null
					&& inspection.Material_ids.length() > 0) {
				m_inspection_material = new ArrayList<InspectionMaterial>();
				String ids[] = inspection.Material_ids.split(",");
				for (String s : ids) {
					InspectionMaterial.AddMaterial(Utils.ConvertToInt(s));
				}
				m_inspection_material = InspectionMaterial.getAll();
				bindMaterial(m_inspection_material);
			}
		}
	}

	public void LoadValues() {
		if (trapscan_info != null) {
			txtBarcode.setText(trapscan_info.barcode);
			txtLocation.setText(trapscan_info.location_details);
			txtBuilding.setText(trapscan_info.building);
			txtFloor.setText(trapscan_info.floor);
			txtTrapNumber.setText(trapscan_info.number);
			txtTrapType.setText(TrapTypesInfo
					.getNameById(trapscan_info.trap_type_id));
			if (isClean) {
				imgClean.setVisibility(View.VISIBLE);
				isClean = false;
			} else {
				imgClean.setVisibility(View.GONE);
				isClean = true;
			}
			if (isFromUnChecked) {
				llContainer.setVisibility(View.GONE);
				txtMessage.setVisibility(View.VISIBLE);
			} else {
				llContainer.setVisibility(View.VISIBLE);
				txtMessage.setVisibility(View.GONE);
			}
		}
	}
}
