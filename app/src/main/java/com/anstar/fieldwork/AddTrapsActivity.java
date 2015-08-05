package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.anstar.dialog.ProgressDialog;
import com.anstar.common.Const;
import com.anstar.common.Generics;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.TrapScanningInfo;
import com.anstar.models.TrapTypesInfo;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.TrapTypesList;

import java.util.ArrayList;

public class AddTrapsActivity extends AppCompatActivity {
	private Button btnSave;
	private EditText edtBarcode, edtBuilding, edtFloor, edtLocation, edtNumber;
	Spinner spnTrapType;
	String barcode = "";
	int customer_id = 0, appointment_id = 0;
	ArrayList<TrapTypesInfo> m_trap_types = null;
	AppointmentInfo appointmentInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_trap);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey(Const.Appointment_Id)) {
				appointment_id = b.getInt(Const.Appointment_Id);
			}
			if (b.containsKey("BARCODE")) {
				barcode = b.getString("BARCODE");
			}
			if (b.containsKey("CUST_ID")) {
				customer_id = b.getInt("CUST_ID");
			}
		}
/*
		ActionBar action = getSupportActionBar();
		action.setTitle(Html
				.fromHtml("<font color='"
						+ getString(R.string.header_text_color)
						+ "'>Add Device</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		btnSave = (Button) findViewById(R.id.btnSaveTraps);
		edtBarcode = (EditText) findViewById(R.id.edtBarcode);
		edtBuilding = (EditText) findViewById(R.id.edtBuilding);
		edtFloor = (EditText) findViewById(R.id.edtFloor);
		edtNumber = (EditText) findViewById(R.id.edtNumber);
		spnTrapType = (Spinner) findViewById(R.id.spnTrap_type);
		edtLocation = (EditText) findViewById(R.id.edtLocation);
		edtBarcode.setText(barcode);
		m_trap_types = new ArrayList<TrapTypesInfo>();
		m_trap_types = TrapTypesList.Instance().getTrapTypesList();
		loadvalues();

		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String building = "";
				String floor = "";
				String location = "";
				String number = "";
				if (edtBuilding.getText().toString().trim().length() > 0) {
					building = edtBuilding.getText().toString();
				}
				if (edtFloor.getText().toString().trim().length() > 0) {
					floor = edtFloor.getText().toString();
				}
				if (edtLocation.getText().toString().trim().length() > 0) {
					location = edtLocation.getText().toString();
				}
				if (edtNumber.getText().toString().trim().length() > 0) {
					number = edtNumber.getText().toString();
				}
				int trap_type_id;
				if (spnTrapType.getSelectedItem().toString()
						.equalsIgnoreCase("Trap Type")) {
					trap_type_id = 0;
				} else {
					trap_type_id = TrapTypesList.Instance()
							.getTrapTypesInfoIdByname(
									spnTrapType.getSelectedItem().toString());
				}
                ProgressDialog.showProgress(AddTrapsActivity.this);

				TrapScanningInfo.AddTraps(number, trap_type_id, customer_id,
						barcode, building, floor, location,
						appointmentInfo.service_location_id,
						new UpdateInfoDelegate() {

							@Override
							public void UpdateSuccessFully(ServiceResponse res) {
                                ProgressDialog.hideProgress();
								Intent i = new Intent(AddTrapsActivity.this,
										NewTrapDetailsActivity.class);
								i.putExtra(Const.Appointment_Id, appointment_id);
								i.putExtra("BARCODE", barcode);
								i.putExtra("CUST_ID", Const.customer_id);
								startActivity(i);
								finish();
							}

							@Override
							public void UpdateFail(String ErrorMessage) {
                                ProgressDialog.hideProgress();
								Toast.makeText(getApplicationContext(),
										ErrorMessage, Toast.LENGTH_LONG).show();
							}
						});

			}
		});
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

	public void loadvalues() {
		setHint();
		if (m_trap_types.size() > 0) {
			setSpinnerValues("name", m_trap_types, TrapTypesInfo.class,
					spnTrapType);
		}
	}

	public void setHint() {
		TrapTypesInfo d = new TrapTypesInfo();
		d.name = "Trap Type";
		if (m_trap_types == null)
			m_trap_types = new ArrayList<TrapTypesInfo>();
		m_trap_types.add(0, d);
	}
}