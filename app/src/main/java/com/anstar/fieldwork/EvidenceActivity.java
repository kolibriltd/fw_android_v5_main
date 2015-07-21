package com.anstar.fieldwork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.anstar.common.Const;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.InspectionInfo;
import com.anstar.models.list.InspectionList;

public class EvidenceActivity extends AppCompatActivity implements OnClickListener {

	int appointment_id;
	int inspection_id = 0;
	private CheckBox chkMouse, chkOther, chkRat;
	private Button btnSave;
	AppointmentInfo appointmentInfo = null;
	boolean isMouse = false, isOther = false,
			isRat = false;
	InspectionInfo info = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evidence);

		// info = MainActivity.appointmentInfo;
		Bundle b = getIntent().getExtras();
		if (b != null) {
			appointment_id = b.getInt(Const.Appointment_Id);
			if (b.containsKey("Inspection_id")) {
				inspection_id = b.getInt("Inspection_id");
			}
		}
/*
		ActionBar action = getSupportActionBar();
		// action.setTitle("Traps Scan Details");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Pest Evidence</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		chkMouse = (CheckBox) findViewById(R.id.chkMouse);
		chkOther = (CheckBox) findViewById(R.id.chkOther);
		chkRat = (CheckBox) findViewById(R.id.chkRat);
		btnSave = (Button) findViewById(R.id.btnSaveEvidance);
		btnSave.setOnClickListener(this);

		chkMouse.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isMouse = isChecked;

			}
		});
		chkOther.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isOther = isChecked;
			}
		});
		chkRat.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isRat = isChecked;

			}
		});

		loadValue();

	}

	public void loadValue() {
		info = InspectionList.Instance().getInspectionById(inspection_id);
		if (info != null) {
			if (info.evidence != null && info.evidence.length() > 0) {
				String evidence[] = info.evidence.split(",");
				for (String string : evidence) {
					if (string.equalsIgnoreCase("Droppings - Mouse")) {
						chkMouse.setChecked(true);
					}
					if (string.equalsIgnoreCase("Droppings - Rat")) {
						chkRat.setChecked(true);
					}
					if (string.equalsIgnoreCase("Droppings - Other")) {
						chkOther.setChecked(true);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.equals(btnSave)) {
			saveEvidence();
		}

	}
	
	private void saveEvidence(){
		String evidence = "";
		if (isMouse) {
			if (evidence.length() > 0) {
				evidence += ",Droppings - Mouse";
			} else {
				evidence += "Droppings - Mouse";
			}
		}
		if (isRat) {
			if (evidence.length() > 0) {
				evidence += ",Droppings - Rat";
			} else {
				evidence += "Droppings - Rat";
			}
		}
		if (isOther) {
			if (evidence.length() > 0) {
				evidence += ",Droppings - Other";
			} else {
				evidence += "Droppings - Other";
			}
		}
		// info.evidence = evidance;
		// try {
		// info.save();
		// } catch (ActiveRecordException e) {
		// e.printStackTrace();
		// }
		Intent i = new Intent();
		i.putExtra("evidance", evidence);
		setResult(RESULT_OK, i);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		AlertOnBack();
	}

	public void AlertOnBack() {
		String message = "You have not saved this record, would you like to save before proceeding?";
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(
				EvidenceActivity.this);

		alt_bld.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								saveEvidence();
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
}