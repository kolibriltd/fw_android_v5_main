package com.anstar.fieldwork;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class AddLocationActivity extends AppCompatActivity implements
		UpdateInfoDelegate {
	private Button btnSave;
	private EditText edtLocationName;
	//ActionBar action = null;
	int locationtype_id = 0;
	private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("location_type_id")) {
				locationtype_id = b.getInt("location_type_id");
			}
		}
		btnSave = (Button) findViewById(R.id.btnSave);
		edtLocationName = (EditText) findViewById(R.id.edtLocationName);
/*
		action = getSupportActionBar();
		// action.setTitle("Add Location Area");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Add Location Area</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);
		mBaseLoader = new BaseLoader(this);

		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (edtLocationName.getText().toString().trim().length() > 0) {
					mBaseLoader.showProgress();
					LocationAreaInfo.AddLocationArea(edtLocationName.getText()
							.toString().trim(), locationtype_id,
							AddLocationActivity.this);
				} else {
					Toast.makeText(getApplicationContext(),
							"Please insert Location", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	@Override
	public void UpdateSuccessFully(ServiceResponse res) {
		mBaseLoader.hideProgress();
		// LocationInfoList.Instance().ClearDB();
		finish();
	}

	@Override
	public void UpdateFail(String ErrorMessage) {
		mBaseLoader.hideProgress();
		Toast.makeText(getApplicationContext(), ErrorMessage, Toast.LENGTH_LONG).show();
	}

}
