package com.anstar.fieldwork;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.widget.TextView;

import com.anstar.common.Const;
import com.anstar.models.AppointmentInfo;
import com.anstar.models.list.AppointmentModelList;

public class InstructionActivity extends BaseActivity {

	int appointment_id = 0;
	AppointmentInfo appointmentInfo = null;
	TextView txtInstruction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instruction);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey(Const.Appointment_Id)) {
				appointment_id = b.getInt(Const.Appointment_Id);
			}
		}
		ActionBar action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Instructions</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
		appointmentInfo = AppointmentModelList.Instance().getAppointmentById(
				appointment_id);
		txtInstruction = (TextView) findViewById(R.id.txtInstruction);
		if (appointmentInfo != null) {
			if (appointmentInfo.instructions != null
					&& appointmentInfo.instructions.length() > 0
					&& !appointmentInfo.instructions.equalsIgnoreCase("null")) {
				txtInstruction.setText(appointmentInfo.instructions);
			} else {
				txtInstruction.setText("No Instructions");
			}
		}
	}
}
