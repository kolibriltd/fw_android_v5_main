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

import com.anstar.models.MaterialInfo;

public class AddMaterialActivity extends AppCompatActivity {
	private Button btnSave;
	private EditText edtMaterialName, edtEpa;
	//ActionBar action = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_material);
/*
		action = getSupportActionBar();
		// action.setTitle("Add Material");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Add Material</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		btnSave = (Button) findViewById(R.id.btnSave);
		edtMaterialName = (EditText) findViewById(R.id.edtMaterialName);
		edtEpa = (EditText) findViewById(R.id.edtEpa);
		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (edtMaterialName.getText().toString().trim().length() > 0
						&& edtEpa.getText().toString().trim().length() > 0) {
					MaterialInfo
							.AddMaterial(edtMaterialName.getText().toString(),
									edtEpa.getText().toString().trim(), "");
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please insert value", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
