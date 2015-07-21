package com.anstar.fieldwork;

import com.anstar.models.ServiceLocationsInfo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class ServiceLocationNoteActivity extends BaseActivity {

	TextView txtServiceLocationNote;
	long id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_notes);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("SERVICELOCATIONID")) {
				id = b.getLong("SERVICELOCATIONID");
			}
		}
		ActionBar action = getSupportActionBar();
		action = getSupportActionBar();
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Service Location Note</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
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
