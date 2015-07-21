package com.anstar.fieldwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.anstar.common.BaseLoader;
import com.anstar.common.Utils;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.ServicesInfo;
import com.anstar.models.list.MaterialList;
import com.anstar.models.list.ServicesList;

import java.util.ArrayList;

public class AddLineItemActivity extends AppCompatActivity {
	private Button btnSave;
	private EditText edtQty, edtPrice;
	private AutoCompleteTextView edtName;
	private CheckBox chkTaxable;
	private Spinner spnLineType;
	//ActionBar action = null;
	LineItemsInfo lineinfo = new LineItemsInfo();
	private boolean isedit = false, isFromDetails = false, service_taxable = false;
	int id = 0, payable_id = 0;
	private String desc = "";
    private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_line_item);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("isedit"))
				isedit = b.getBoolean("isedit");
			if (b.containsKey("position"))
				id = b.getInt("position");
			if (b.containsKey("isFromDetails"))
				isFromDetails = b.getBoolean("isFromDetails");
			if (b.containsKey("service_taxable"))
				service_taxable = b.getBoolean("service_taxable");

		}

		btnSave = (Button) findViewById(R.id.btnSave);
		edtName = (AutoCompleteTextView) findViewById(R.id.edtName);
		edtQty = (EditText) findViewById(R.id.edtQty);
		edtPrice = (EditText) findViewById(R.id.edtPrice);
		spnLineType = (Spinner) findViewById(R.id.spnLineType);
		chkTaxable = (CheckBox) findViewById(R.id.chkTaxable);
		edtName.setThreshold(1);

		chkTaxable.setChecked(service_taxable);

		if (isedit) {
			if (isFromDetails)
				lineinfo = LineItemsActivity.m_lineitems.get(id);
			else
				lineinfo = AddAppointmentActivity.lineitems.get(id);

			loadData();
		}
/*
		action = getSupportActionBar();
		// action.setTitle("Add Pest Type");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>Add Line Item</font>"));
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar action = getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.setDisplayShowHomeEnabled(true);
        mBaseLoader = new BaseLoader(this);

		loadServices();
		
		spnLineType.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				if (spnLineType.getSelectedItem().toString()
						.equalsIgnoreCase("Material")) {
					loadMaterials();
					if (!isedit)
						chkTaxable.setChecked(true);

				} else if (spnLineType.getSelectedItem().toString()
						.equalsIgnoreCase("Service")) {
					loadServices();
					if (!isedit)
						chkTaxable.setChecked(service_taxable);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		edtName.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View arg1,
					int position, long arg3) {

				// MasterSubjectTitle title = subjectTitleList.get(position);
				Object o = listView.getItemAtPosition(position);
				desc = o.toString();

				if (spnLineType.getSelectedItem().toString()
						.equalsIgnoreCase("Material")) {
					payable_id = MaterialList.Instance().getMaterialIdByname(
							desc);
				} else if (spnLineType.getSelectedItem().toString()
						.equalsIgnoreCase("Service")) {
					ServicesInfo sinfo = ServicesList.Instance()
							.getServicesByDesc(desc);
					if (sinfo != null) {
						payable_id = sinfo.id;
						edtPrice.setText(sinfo.price);
					}
				}
				// if (subjectTitleList.containsKey(o.toString())) {
				// actId = subjectTitleList.get(o.toString());
				// }

			}
		});

		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveLineItem();
			}
		});

	}

	private void loadServices() {
		try {
			ServicesList.Instance().load(new ModelDelegate<ServicesInfo>() {
				@Override
				public void ModelLoaded(ArrayList<ServicesInfo> list) {
					if (list != null) {
						ArrayList<String> services = new ArrayList<String>();
						for (ServicesInfo m : list) {
							services.add(m.description);
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								AddLineItemActivity.this,
								android.R.layout.simple_list_item_1, services);
						edtName.setAdapter(adapter);
						// edtName.showDropDown();
					}
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
		}
	}

	private void loadMaterials() {
		try {

			MaterialList.Instance().load(new ModelDelegate<MaterialInfo>() {
				@Override
				public void ModelLoaded(ArrayList<MaterialInfo> list) {
					if (list != null) {
						ArrayList<String> materials = new ArrayList<String>();
						for (MaterialInfo m : list) {
							materials.add(m.name);
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								AddLineItemActivity.this,
								android.R.layout.simple_list_item_1, materials);
						edtName.setAdapter(adapter);
						// edtName.showDropDown();
					}
				}

				@Override
				public void ModelLoadFailedWithError(String error) {

				}
			});
		} catch (Exception e) {
		}
	}

	private void saveLineItem() {
		String name = edtName.getText().toString().trim();
		String qty = edtQty.getText().toString().trim();
		String price = edtPrice.getText().toString().trim();
		String type = spnLineType.getSelectedItem().toString();

		if (name.length() <= 0) {
			Toast.makeText(getApplicationContext(), "Please enter Name",
					Toast.LENGTH_LONG).show();
		} else if (price.length() <= 0) {
			Toast.makeText(getApplicationContext(), "Please enter Price",
					Toast.LENGTH_LONG).show();
		} else {
			int lineid = 0;
			if (isedit) {
				if (isFromDetails) {
					LineItemsActivity.m_lineitems.get(id).name = name;
					LineItemsActivity.m_lineitems.get(id).quantity = qty;
					LineItemsActivity.m_lineitems.get(id).price = price;
					LineItemsActivity.m_lineitems.get(id).type = type;
					LineItemsActivity.m_lineitems.get(id).payable_id = payable_id;
					LineItemsActivity.m_lineitems.get(id).total = Utils
							.ConvertToFloat(qty) * Utils.ConvertToFloat(price);
					if (chkTaxable.isChecked()) {
						LineItemsActivity.m_lineitems.get(id).taxable = true;
					} else {
						LineItemsActivity.m_lineitems.get(id).taxable = false;
					}
					lineid = LineItemsActivity.m_lineitems.get(id).id;
				} else {

					AddAppointmentActivity.lineitems.get(id).name = name;
					AddAppointmentActivity.lineitems.get(id).quantity = qty;
					AddAppointmentActivity.lineitems.get(id).price = price;
					AddAppointmentActivity.lineitems.get(id).type = type;
					AddAppointmentActivity.lineitems.get(id).payable_id = payable_id;
					AddAppointmentActivity.lineitems.get(id).total = Utils
							.ConvertToFloat(qty) * Utils.ConvertToFloat(price);
					if (chkTaxable.isChecked()) {
						AddAppointmentActivity.lineitems.get(id).taxable = true;
					} else {
						AddAppointmentActivity.lineitems.get(id).taxable = false;
					}
				}
				Intent i = new Intent();
				i.putExtra("position", id);
				setResult(RESULT_OK, i);
				finish();
			} else {
				LineItemsInfo info = new LineItemsInfo();
				info.name = name;
				info.quantity = qty;
				info.price = price;
				info.type = type;
				info.payable_id = payable_id;
				info.total = Utils.ConvertToFloat(qty)
						* Utils.ConvertToFloat(price);
				if (chkTaxable.isChecked()) {
					info.taxable = true;
				} else {
					info.taxable = false;
				}
				if (isFromDetails) {
                    mBaseLoader.showProgress();
					info.WorkOrderId = id;
					info.AddLineItems(new UpdateInfoDelegate() {
						@Override
						public void UpdateSuccessFully(ServiceResponse res) {
                            mBaseLoader.hideProgress();
							Intent i = new Intent();
							setResult(RESULT_OK, i);
							finish();
						}
						@Override
						public void UpdateFail(String ErrorMessage) {
                            mBaseLoader.hideProgress();
						}
					});
				} else {
					AddAppointmentActivity.lineitems.add(info);
					Intent i = new Intent();
					i.putExtra("position", id);
					setResult(RESULT_OK, i);
					finish();
				}
			}
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		}
	}

	private void loadData() {
		if (lineinfo != null) {
			edtName.setText(lineinfo.name);
			edtQty.setText(lineinfo.quantity);
			edtPrice.setText(lineinfo.price);
			if (lineinfo.taxable) {
				chkTaxable.setChecked(true);
			}
		}
	}
}
