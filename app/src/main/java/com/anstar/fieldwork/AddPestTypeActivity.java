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

import com.anstar.models.PestsTypeInfo;

public class AddPestTypeActivity extends AppCompatActivity {
	private Button btnSave;
	private EditText edtPestName;
	//ActionBar action = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_pest_type);

		btnSave = (Button) findViewById(R.id.btnSave);
		edtPestName = (EditText) findViewById(R.id.edtPestname);
/*
		action = getSupportActionBar();
//		action.setTitle("Add Pest Type");
		action.setTitle(Html.fromHtml("<font color='"+getString(R.string.header_text_color)+"'>Add Pest Type</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (edtPestName.getText().toString().trim().length() > 0) {
					PestsTypeInfo.AddPestTypes(edtPestName.getText()
							.toString().trim());
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please insert Pest type", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

}
