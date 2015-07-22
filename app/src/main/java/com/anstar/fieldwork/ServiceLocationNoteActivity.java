package com.anstar.fieldwork;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.anstar.models.ServiceLocationsInfo;

public class ServiceLocationNoteActivity extends AppCompatActivity {

	TextView txtServiceLocationNote;
	long id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_notes);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("SERVICELOCATIONID")) {
				id = b.getLong("SERVICELOCATIONID");
			}
		}
/*
		ActionBar action = getSupportActionBar();
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Service Location Note</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		ServiceLocationsInfo info = ServiceLocationsInfo
				.getServiceLocationByDbId(id);
		txtServiceLocationNote = (TextView) findViewById(R.id.txtServiceLocationNote);
		if (info != null && info.notes != null && info.notes.length() > 0) {
			txtServiceLocationNote.setVisibility(View.VISIBLE);
			txtServiceLocationNote.setText(info.notes);
		} else {
			txtServiceLocationNote.setVisibility(View.GONE);
		}

	}

}
