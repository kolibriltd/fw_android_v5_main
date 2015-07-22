package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.anstar.common.BaseLoader;
import com.anstar.common.Const;
import com.anstar.common.Generics;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.BaitConditionsInfo;
import com.anstar.models.InspectionInfo;
import com.anstar.models.InspectionMaterial;
import com.anstar.models.InspectionPest;
import com.anstar.models.MaterialInfo;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsage.UpdateMUInfoDelegate;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.PestsTypeInfo;
import com.anstar.models.TrapConditionsInfo;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.list.BaitConditionsList;
import com.anstar.models.list.DilutionRatesList;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.InspectionPestsList;
import com.anstar.models.list.MaterialUsagesRecordsList;
import com.anstar.models.list.PestTypeList;
import com.anstar.models.list.TrapConditionsList;
import com.anstar.models.list.TrapList;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewTrapDetailsActivity extends AppCompatActivity implements
		OnClickListener {

	int appointment_id;
	int cust_id;
	private TextView txtTrapNumber, txtMessage, txtBarcode, txtScannedAt;
	private RelativeLayout rlEvidence, rlCaptured, rlMaterial;
	private Button btnSaveTrapData, btnClean;
	ImageView imgClean;
	boolean isClean = false;
	String currentDateTimeString = "";
	// private AppointmentInfo appointmentInfo = null;
	//private ActionBar action = null;
	private String barcode;

	private TrapScanningInfo trapscan_info = null;
	private InspectionInfo inspection = null;
	// private String evidence = "";
	private boolean isEdit = false;
	final int EVIDENCE_CODE = 1;
	final int CAPTURE_REQUEST_ID = 2;
	final int MATERIAL_REQUEST_ID = 3;
	boolean isFromUnChecked = false;
	LinearLayout llContainer, llNotes, llSecondContainert, llisClean;
	ListView lstMaterial, lstCaptures, lstEvidence;
	ArrayList<InspectionPest> m_inspectionPests = new ArrayList<InspectionPest>();
	ArrayList<InspectionMaterial> m_inspection_material = new ArrayList<InspectionMaterial>();
	boolean isCleanInspection = false;
	ToggleButton tgbRemoved;
	Spinner spnBaitCondition, spnTrapCondition;
	EditText edtException, edtNotes;
	boolean isRemoved;
	InspectionInfo temp_Inspection = null;
	ArrayList<InspectionPest> m_temp_inspectionPests = new ArrayList<InspectionPest>();
	boolean isUpdated = false;
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_trap_details);
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
				+ "'>Device Inspection</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		mBaseLoader = new BaseLoader(this);

		lstMaterial = (ListView) findViewById(R.id.lstMaterialUsages);
		lstCaptures = (ListView) findViewById(R.id.lstCaptures);
		lstEvidence = (ListView) findViewById(R.id.lstEvidence);
		btnSaveTrapData = (Button) findViewById(R.id.btnSaveTrapData);
		btnClean = (Button) findViewById(R.id.btnClean);
		rlEvidence = (RelativeLayout) findViewById(R.id.rlEvidence);
		rlCaptured = (RelativeLayout) findViewById(R.id.rlCaptured);
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		llisClean = (LinearLayout) findViewById(R.id.llisClean);

		llSecondContainert = (LinearLayout) findViewById(R.id.llSecondContainer);
		llNotes = (LinearLayout) findViewById(R.id.llNotes);

		rlMaterial = (RelativeLayout) findViewById(R.id.rlMaterialUsage);
		txtTrapNumber = (TextView) findViewById(R.id.txtTrapNumber);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		txtBarcode = (TextView) findViewById(R.id.txtBarcode);
		txtScannedAt = (TextView) findViewById(R.id.txtScannedAt);
		imgClean = (ImageView) findViewById(R.id.imgClean);
		tgbRemoved = (ToggleButton) findViewById(R.id.tgbremoved);
		spnBaitCondition = (Spinner) findViewById(R.id.spnbait_condition_id);
		spnTrapCondition = (Spinner) findViewById(R.id.spntrap_condition_id);
		edtException = (EditText) findViewById(R.id.edtexception);
		edtNotes = (EditText) findViewById(R.id.edtNotes);
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
				inspection.id = Utils.getRandomInt();
				inspection.barcode = barcode;
				inspection.AppointmentId = appointment_id;
				if (isFromUnChecked) {
					inspection.isForUnchecked = true;
				}
				inspection.save();
			} catch (ActiveRecordException e) {
				e.printStackTrace();
			}
		} else {
			isEdit = true;
			m_inspectionPests = InspectionPestsList.Instance()
					.getInspectionPestByInspectionId(inspection.id);
			m_temp_inspectionPests = m_inspectionPests;
			isClean = getIsClean(inspection, m_inspectionPests);
			isCleanInspection = getIsClean(inspection, m_inspectionPests);
			if (isClean) {
				imgClean.setVisibility(View.VISIBLE);
			}
		}

		LoadValues();
		LoadSpinners();
		if (isEdit) {
			try {
				temp_Inspection = FieldworkApplication.Connection().newEntity(
						InspectionInfo.class);
			} catch (ActiveRecordException e) {
				e.printStackTrace();
			}
			temp_Inspection.copyFrom(inspection);
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

			Intent i = new Intent(NewTrapDetailsActivity.this,
					EvidenceActivity.class);
			i.putExtra("Inspection_id", inspection.id);
			startActivityForResult(i, EVIDENCE_CODE);

		} else if (v == rlCaptured) {
			Intent i = new Intent(NewTrapDetailsActivity.this,
					AddCapturedPestActivity.class);
			i.putExtra("BARCODE", inspection.barcode);
			i.putExtra("Inspection_id", inspection.id);

			startActivityForResult(i, CAPTURE_REQUEST_ID);
		} else if (v == btnSaveTrapData) {
			saveInspection();
		} else if (v == btnClean) {
			if (isClean) {
				m_inspectionPests = new ArrayList<InspectionPest>();
				BindPests();
				BindEvidence(new ArrayList<String>());
				imgClean.setVisibility(View.VISIBLE);
				isClean = false;
				llisClean.setVisibility(View.GONE);
			} else {
				imgClean.setVisibility(View.GONE);
				llisClean.setVisibility(View.VISIBLE);
				isClean = true;
			}
			if (isCleanInspection) {
				isCleanInspection = false;
			} else {
				isCleanInspection = true;
			}
		} else if (v == rlMaterial) {
			Intent i = new Intent(NewTrapDetailsActivity.this,
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
				tgbRemoved.setChecked(inspection.removed);
				if (inspection.exception != null
						&& inspection.exception.length() > 0) {
					edtException.setText(inspection.exception);
				}

				if (inspection.scanned_on != null
						&& !inspection.scanned_on.equalsIgnoreCase("null")
						&& inspection.scanned_on.length() > 0) {
					// Display Time Only 2013-12-02T16:39:00-06:00
					if (inspection.scanned_on.contains("T")) {
						String temp[] = inspection.scanned_on.split("T");
						String time[] = temp[1].split("-");
						String scanned_at = Utils.Instance().getFormatedDate(
								time[0], "hh:mm:ss", "hh:mm a");
						txtScannedAt.setText(scanned_at);
					} else {
						// 2015-01-22 12:15 PM
						String temp[] = inspection.scanned_on.split(" ");
						txtScannedAt.setText(temp[1] + " " + temp[2]);
					}
				}
				if (inspection.notes != null && inspection.notes.length() > 0) {
					edtNotes.setText(inspection.notes);
				}
				if (m_inspectionPests != null && m_inspectionPests.size() > 0) {
					BindPests();
				}
				ArrayList<String> evidence_arr = new ArrayList<String>();
				if (inspection.evidence != null
						&& inspection.evidence.length() > 0) {
					if (inspection.evidence.contains(",")) {
						String[] evd = inspection.evidence.split(",");
						for (String s : evd) {
							evidence_arr.add(s);
						}
					} else {
						evidence_arr.add(inspection.evidence);
					}
					BindEvidence(evidence_arr);
				}
			} else {
				currentDateTimeString = DateFormat.getDateTimeInstance()
						.format(new Date());
				currentDateTimeString = Utils.Instance().getFormatedDate(
						currentDateTimeString, "MMM dd, yyyy hh:mm:ss a",
						"yyyy-MM-dd hh:mm a");
				String scanned_at = Utils.Instance().getFormatedDate(
						currentDateTimeString, "yyyy-MM-dd hh:mm a", "hh:mm a");
				txtScannedAt.setText(scanned_at);
			}
		}
	}

	public void BindPests() {
		PestAdapter adp = new PestAdapter(m_inspectionPests);
		lstCaptures.setAdapter(adp);
		Utils.Instance().setListViewHeightBasedOnChildren(lstCaptures);
	}

	public void BindEvidence(ArrayList<String> evidence_arr) {
		EvidenceAdapter evd_adp = new EvidenceAdapter(evidence_arr);
		lstEvidence.setAdapter(evd_adp);
		Utils.Instance().setListViewHeightBasedOnChildren(lstEvidence);
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
		String Notes = "";
		if (edtException.getText().toString().length() > 0) {
			exception = edtException.getText().toString();
		}
		if (edtNotes.getText().toString().length() > 0) {
			Notes = edtNotes.getText().toString();
		}

		if (exception.length() > 200) {
			Toast.makeText(getApplicationContext(),
					"Exception must be less then 200 character.",
					Toast.LENGTH_LONG).show();
			return;
		}
		if (Notes.length() > 500) {
			Toast.makeText(getApplicationContext(),
					"Notes must be less then 500 character.", Toast.LENGTH_LONG)
					.show();
			return;
		}
		inspection.deleteRelatedPestsRecords();
		if (isCleanInspection) {
			m_inspectionPests = new ArrayList<InspectionPest>();
			inspection.evidence = "";
		}

		inspection.trap_number = trapscan_info.number;
		inspection.trap_type_id = trapscan_info.trap_type_id;
		if (!isFromUnChecked) {
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
		}
		// String currentDateTimeString =
		// DateFormat.getDateTimeInstance().format(
		// new Date());
		// currentDateTimeString = Utils.Instance().getFormatedDate(
		// currentDateTimeString, "MMM dd, yyyy hh:mm:ss a",
		// "yyyy-MM-dd hh:mm a");
		inspection.exception = exception;
		inspection.notes = Notes;
		inspection.removed = isRemoved;
		inspection.scanned_on = currentDateTimeString;
		try {
			inspection.save();
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		inspection.isForUnchecked = false;
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
		mBaseLoader.showProgress();

		InspectionInfo.AddInspectionRecordNew(appointment_id, m_list,
				inspection, new UpdateMUInfoDelegate() {
					@Override
					public void UpdateSuccessFully(ServiceResponse res) {
						if (!res.isError()) {
							isUpdated = true;
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

						}
					}

					@Override
					public void UpdateFail(String ErrorMessage) {
						mBaseLoader.hideProgress();
						Toast.makeText(getApplicationContext(), ErrorMessage,
								Toast.LENGTH_LONG).show();
					}
				});
	}

	public void gotoback() {
		mBaseLoader.hideProgress();
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
				ArrayList<String> evidence_arr = new ArrayList<String>();
				if (inspection.evidence != null
						&& inspection.evidence.length() > 0) {
					if (inspection.evidence.contains(",")) {
						String[] evd = inspection.evidence.split(",");
						for (String s : evd) {
							evidence_arr.add(s);
						}
					} else {
						evidence_arr.add(inspection.evidence);
					}
				}

				BindEvidence(evidence_arr);
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
				if (m_inspectionPests != null && m_inspectionPests.size() > 0) {
					BindPests();
				}
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
		ArrayList<String> m_ids = new ArrayList<String>();
		if (m_arr != null && m_arr.size() > 0) {
			for (InspectionMaterial im : m_arr) {
				MaterialUsage usage = MaterialUsage
						.getMaterialUsageById(im.material_id);
				if (usage != null) {
					m_usages.add(usage);
					m_ids.add("" + usage.id);
				}
			}
			String ids_str = Utils.Instance().join(m_ids, ",");
			if (inspection != null) {
				inspection.Material_ids = "";
				inspection.Material_ids = ids_str;
				try {
					inspection.save();
				} catch (ActiveRecordException e) {
					e.printStackTrace();
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
			txtTrapNumber.setText("ID : " + trapscan_info.number);
			if (isClean) {
				m_inspectionPests = new ArrayList<InspectionPest>();
				BindPests();
				BindEvidence(new ArrayList<String>());
				imgClean.setVisibility(View.VISIBLE);
				isClean = false;
				llisClean.setVisibility(View.GONE);
			} else {
				imgClean.setVisibility(View.GONE);
				isClean = true;
			}
			if (isFromUnChecked) {
				llContainer.setVisibility(View.GONE);
				llSecondContainert.setVisibility(View.GONE);
				llNotes.setVisibility(View.VISIBLE);
				txtMessage.setVisibility(View.VISIBLE);
			} else {
				llContainer.setVisibility(View.VISIBLE);
				llSecondContainert.setVisibility(View.VISIBLE);
				llNotes.setVisibility(View.VISIBLE);
				txtMessage.setVisibility(View.GONE);
				edtException.setVisibility(View.GONE);
			}
		}
	}

	public class PestAdapter extends BaseAdapter {
		ArrayList<InspectionPest> m_list = new ArrayList<InspectionPest>();

		public PestAdapter(ArrayList<InspectionPest> list) {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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
			final InspectionPest ins_pest = m_list.get(position);
			if (ins_pest != null) {
				PestsTypeInfo pestinfo = PestTypeList.Instance().getPestById(
						ins_pest.pest_type_id);
				if (pestinfo != null) {
					holder.main_item_text.setText(pestinfo.name + "     "
							+ ins_pest.count);
				}
			}
			holder.rl_main_list_item
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							alertToDeletePest(ins_pest);
						}
					});

			return rowView;
		}
	}

	public class EvidenceAdapter extends BaseAdapter {
		ArrayList<String> m_list = new ArrayList<String>();

		public EvidenceAdapter(ArrayList<String> list) {
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
			final String targetPest = m_list.get(position);
			holder.main_item_text.setText(targetPest);
			holder.rl_main_list_item
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							alertToDeleteEvidense(targetPest);
						}
					});
			return rowView;
		}
	}

	public void alertToDeletePest(final InspectionPest pest) {
		String message = "Are you sure to delete this Pest Record?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				NewTrapDetailsActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// call();
								// try {
								// pest.delete();
								// } catch (ActiveRecordException e) {
								// e.printStackTrace();
								// }
								if (m_inspectionPests.contains(pest)) {
									m_inspectionPests.remove(pest);
								}
								// m_inspectionPests = InspectionPestsList
								// .Instance()
								// .getInspectionPestByInspectionId(
								// inspection.id);
								BindPests();
								dialog.cancel();
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

	public void alertToDeleteEvidense(final String evidence) {
		String message = "Are you sure to delete this Evidence?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				NewTrapDetailsActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// call();
								ArrayList<String> m_temp = new ArrayList<String>();
								if (inspection.evidence.contains(evidence)) {
									if (inspection.evidence.contains(",")) {
										String[] evd = inspection.evidence
												.split(",");
										for (String s : evd) {
											if (!s.equalsIgnoreCase(evidence)) {
												m_temp.add(s);
											}
										}
										inspection.evidence = Utils.Instance()
												.join(m_temp, ",");
										try {
											inspection.save();
										} catch (ActiveRecordException e) {
											e.printStackTrace();
										}
									} else {
										m_temp = new ArrayList<String>();
										inspection.evidence = "";
										try {
											inspection.save();
										} catch (ActiveRecordException e) {
											e.printStackTrace();
										}
									}
									BindEvidence(m_temp);
								}
								dialog.cancel();
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

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		AlertOnBack();
	}

	public void AlertOnBack() {
		String message = "You have not saved this record, would you like to save before proceeding?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				NewTrapDetailsActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								saveInspection();
								dialog.cancel();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (!isUpdated) {
							if (temp_Inspection != null) {
								inspection.copyFrom(temp_Inspection);
								try {
									inspection.save();
								} catch (ActiveRecordException e) {
									e.printStackTrace();
								}
								try {
									InspectionPestsList.Instance().ClearDB(inspection.id);
//									FieldworkApplication.Connection().delete(
//											InspectionPest.class);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (m_temp_inspectionPests.size() > 0) {
									for (InspectionPest ip : m_temp_inspectionPests) {
										try {
											ip.save();
										} catch (ActiveRecordException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
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
