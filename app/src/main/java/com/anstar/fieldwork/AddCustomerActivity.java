package com.anstar.fieldwork;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anstar.common.BaseLoader;
import com.anstar.common.Generics;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.SegmentedRadioGroup;
import com.anstar.dialog.ConfirmDialog;
import com.anstar.models.BillingTermsInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.LocationInfo;
import com.anstar.models.ModelDelegates;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.ServiceRoutesInfo;
import com.anstar.models.TaxRates;
import com.anstar.models.UserInfo;
import com.anstar.models.list.BillingTermsList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.LocationInfoList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.models.list.ServiceRoutesList;
import com.anstar.models.list.TaxRateList;

import java.util.ArrayList;

public class AddCustomerActivity extends AppCompatActivity implements
		OnClickListener, ConfirmDialog.OnConfirmDialogListener {
	private Button btnSave, btnPhonePlus, btnPhoneMinusTwo, btnPhoneMinusThree,
			btnServicePhonePlus, btnServicePhoneMinusTwo,
			btnServicePhoneMinusThree;
	private EditText edtPrefix, edtFirstName, edtLastName, edtNameCom,
			edtBillingName, edtAddress1, edtAddress2, edtCity, edtState,
			edtZip, edtServiceEmail, edtPhoneOne, edtPhoneTwo, edtPhoneThree,
			edtPhoneExtOne, edtPhoneExtTwo, edtPhoneExtThree,
			edtServiceLocationName, edtServiceAddress1, edtServiceAddress2,
			edtServiceCity, edtServiceState, edtServiceZip, edtServicePhoneOne,
			edtServicePhoneTwo, edtServicePhoneThree, edtServicePhoneExtOne,
			edtServicePhoneExtTwo, edtServicePhoneExtThree, edtAttn;
	private Spinner spnPhoneTypeOne, spnPhoneTypeTwo, spnPhoneTypeThree,
			spnServicePhoneTypeOne, spnServicePhoneTypeTwo,
			spnServicePhoneTypeThree, spnServiceRoute;
	//ActionBar action = null;
	private SegmentedRadioGroup segmentCustType;
	private LinearLayout llResidentialField, llCommercialField, llPhoneOne,
			llPhoneTwo, llPhoneThree, llServiceaddressthesame,
			llServicePhoneOne, llServicePhoneTwo, llServicePhoneThree;
	private Spinner spnBillingTerms, spnLocationTypes, spnTaxRates;
	private ToggleButton tgServiceAddressSame;
	private int CustType = 0, addrow = 0, addrowService = 0;
	String customer_type = "Residential";
	ArrayList<TaxRates> m_taxes = new ArrayList<TaxRates>();
	ArrayList<ServiceRoutesInfo> m_routes = new ArrayList<ServiceRoutesInfo>();
	ArrayList<LocationInfo> m_location = new ArrayList<LocationInfo>();
	ArrayList<BillingTermsInfo> m_billingTerms = new ArrayList<BillingTermsInfo>();
	boolean ServiceAddressSame = true;
	private boolean isEdit = false;
	private int cust_id = 0;
	private CustomerInfo customerinfo;
	private ServiceLocationsInfo location;
	private ActionBar action;
    private BaseLoader mBaseLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_customer);
/*
		action = getSupportActionBar();
		// action.setTitle("Add Material");
		action.setTitle(Html.fromHtml("<font color='"
				+ getString(R.string.header_text_color)
				+ "'>New Customer</font>"));
*/

        mBaseLoader = new BaseLoader(this);

		if (!NetworkConnectivity.isConnected()) {
			Toast.makeText(getApplicationContext(),
					"You need internet connection to add customer",
					Toast.LENGTH_LONG).show();
		}

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("customer_id")) {
				cust_id = b.getInt("customer_id");
				customerinfo = CustomerList.Instance().getCustomerById(cust_id);
				isEdit = true;
			}
		}

/*
		action.setHomeButtonEnabled(true);
		action.setDisplayHomeAsUpEnabled(true);
*/
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		action = getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setDisplayShowHomeEnabled(true);

		btnSave = (Button) findViewById(R.id.btnSubmit);
		btnPhonePlus = (Button) findViewById(R.id.btnPhonePlus);
		btnPhoneMinusTwo = (Button) findViewById(R.id.btnPhoneMinusTwo);
		btnPhoneMinusThree = (Button) findViewById(R.id.btnPhoneMinusThree);
		btnServicePhonePlus = (Button) findViewById(R.id.btnServicePhonePlus);
		btnServicePhoneMinusTwo = (Button) findViewById(R.id.btnServicePhoneMinusTwo);
		btnServicePhoneMinusThree = (Button) findViewById(R.id.btnServicePhoneMinusThree);

		edtPrefix = (EditText) findViewById(R.id.edtPrefix);
		edtFirstName = (EditText) findViewById(R.id.edtFirstName);
		edtLastName = (EditText) findViewById(R.id.edtLastName);
		edtNameCom = (EditText) findViewById(R.id.edtNameCom);
		edtBillingName = (EditText) findViewById(R.id.edtBillingName);
		edtAddress1 = (EditText) findViewById(R.id.edtAddress1);
		edtAddress2 = (EditText) findViewById(R.id.edtAddress2);
		edtCity = (EditText) findViewById(R.id.edtCity);
		edtState = (EditText) findViewById(R.id.edtState);
		edtZip = (EditText) findViewById(R.id.edtZip);
		edtServiceEmail = (EditText) findViewById(R.id.edtServiceEmail);
		edtPhoneOne = (EditText) findViewById(R.id.edtPhoneOne);
		edtPhoneTwo = (EditText) findViewById(R.id.edtPhoneTwo);
		edtPhoneThree = (EditText) findViewById(R.id.edtPhoneThree);
		edtPhoneExtOne = (EditText) findViewById(R.id.edtPhoneExtOne);
		edtPhoneExtTwo = (EditText) findViewById(R.id.edtPhoneExtTwo);
		edtPhoneExtThree = (EditText) findViewById(R.id.edtPhoneExtThree);
		edtServiceLocationName = (EditText) findViewById(R.id.edtServiceLocationName);
		edtServiceAddress1 = (EditText) findViewById(R.id.edtServiceAddress1);
		edtServiceAddress2 = (EditText) findViewById(R.id.edtServiceAddress2);
		edtServiceCity = (EditText) findViewById(R.id.edtServiceCity);
		edtServiceState = (EditText) findViewById(R.id.edtServiceState);
		edtServiceZip = (EditText) findViewById(R.id.edtServiceZip);
		edtServicePhoneOne = (EditText) findViewById(R.id.edtServicePhoneOne);
		edtServicePhoneTwo = (EditText) findViewById(R.id.edtServicePhoneTwo);
		edtServicePhoneThree = (EditText) findViewById(R.id.edtServicePhoneThree);
		edtServicePhoneExtOne = (EditText) findViewById(R.id.edtServicePhoneExtOne);
		edtServicePhoneExtTwo = (EditText) findViewById(R.id.edtServicePhoneExtTwo);
		edtServicePhoneExtThree = (EditText) findViewById(R.id.edtServicePhoneExtThree);
		edtAttn = (EditText) findViewById(R.id.edtAttn);

		spnBillingTerms = (Spinner) findViewById(R.id.spnBillingTerm);
		segmentCustType = (SegmentedRadioGroup) findViewById(R.id.segmentCustType);
		spnTaxRates = (Spinner) findViewById(R.id.spnTaxRates);
		spnLocationTypes = (Spinner) findViewById(R.id.spnLocation);
		spnServiceRoute = (Spinner) findViewById(R.id.spnServiceRoute);
		spnPhoneTypeOne = (Spinner) findViewById(R.id.spnPhoneTypeOne);
		spnPhoneTypeTwo = (Spinner) findViewById(R.id.spnPhoneTypeTwo);
		spnPhoneTypeThree = (Spinner) findViewById(R.id.spnPhoneTypeThree);
		spnServicePhoneTypeOne = (Spinner) findViewById(R.id.spnServicePhoneTypeOne);
		spnServicePhoneTypeTwo = (Spinner) findViewById(R.id.spnServicePhoneTypeTwo);
		spnServicePhoneTypeThree = (Spinner) findViewById(R.id.spnServicePhoneTypeThree);

		tgServiceAddressSame = (ToggleButton) findViewById(R.id.tgServiceAddressSame);
		llCommercialField = (LinearLayout) findViewById(R.id.llCommercialField);
		llResidentialField = (LinearLayout) findViewById(R.id.llResidentialField);
		llServiceaddressthesame = (LinearLayout) findViewById(R.id.llServiceaddressthesame);
		llServiceaddressthesame.setVisibility(View.GONE);
		m_taxes = TaxRateList.Instance().getTexRateist();
		m_routes = ServiceRoutesList.Instance().getRouteList();
		m_location = LocationInfoList.Instance().getLocationTypes();
		m_billingTerms = BillingTermsList.Instance().getBillingTermsList();
		llPhoneOne = (LinearLayout) findViewById(R.id.llPhoneOne);
		llPhoneTwo = (LinearLayout) findViewById(R.id.llPhoneTwo);
		llPhoneThree = (LinearLayout) findViewById(R.id.llPhoneThree);
		llServicePhoneOne = (LinearLayout) findViewById(R.id.llServicePhoneOne);
		llServicePhoneTwo = (LinearLayout) findViewById(R.id.llServicePhoneTwo);
		llServicePhoneThree = (LinearLayout) findViewById(R.id.llServicePhoneThree);

		btnSave.setOnClickListener(this);
		btnPhonePlus.setOnClickListener(this);
		btnPhoneMinusTwo.setOnClickListener(this);
		btnPhoneMinusThree.setOnClickListener(this);
		btnServicePhonePlus.setOnClickListener(this);
		btnServicePhoneMinusTwo.setOnClickListener(this);
		btnServicePhoneMinusThree.setOnClickListener(this);
		LoadValues();

		tgServiceAddressSame
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						ServiceAddressSame = isChecked;
						if (isChecked) {
							llServiceaddressthesame.setVisibility(View.GONE);
						} else {
							llServiceaddressthesame.setVisibility(View.VISIBLE);
						}
					}
				});
		tgServiceAddressSame.setChecked(true);

		segmentCustType
				.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (group == segmentCustType) {
							if (checkedId == R.id.rdbtnResidential) {
								CustType = 0;
								llCommercialField.setVisibility(View.GONE);
								llResidentialField.setVisibility(View.VISIBLE);
								customer_type = "Residential";
							} else if (checkedId == R.id.rdbtnCommercial) {
								CustType = 1;
								llCommercialField.setVisibility(View.VISIBLE);
								llResidentialField.setVisibility(View.GONE);
								customer_type = "Commercial";
							}
						}
					}
				});
		if (isEdit) {

			setEditData();
		}
	}

	private void setEditData() {
		if (customerinfo != null) {
			tgServiceAddressSame.setChecked(false);
			ArrayList<ServiceLocationsInfo> serviceLocations = ServiceLocationsList
					.Instance().getServiceLocationByCustId(cust_id);

			if (customerinfo.customer_type.equalsIgnoreCase("Commercial")) {
				action.setTitle(Html.fromHtml("<font color='"
						+ getString(R.string.header_text_color)
						+ "'>Edit " + customerinfo.name+"</font>"));
				segmentCustType.check(R.id.rdbtnCommercial);
				edtNameCom.setText(customerinfo.name);
			} else {
				action.setTitle(Html.fromHtml("<font color='"
						+ getString(R.string.header_text_color)
						+ "'>Edit " + customerinfo.first_name+"</font>"));
				segmentCustType.check(R.id.rdbtnResidential);
				edtPrefix.setText(customerinfo.name_prefix);
				edtFirstName.setText(customerinfo.first_name);
				edtLastName.setText(customerinfo.last_name);
			}
			edtBillingName.setText(customerinfo.billing_name);
			edtAddress1.setText(customerinfo.billing_street);
			edtAddress2.setText(customerinfo.billing_street_two);
			edtCity.setText(customerinfo.billing_city);
			edtState.setText(customerinfo.billing_state);
			edtZip.setText(customerinfo.billing_zip);
			edtPhoneOne.setText(customerinfo.billing_phone);
			edtPhoneExtOne.setText(customerinfo.billing_phone_ext);
			edtAttn.setText(customerinfo.billing_attention);
			// spnPhoneTypeOne.setText(customerinfo.billing_phone_kind);
			String[] kinds = getResources().getStringArray(
					R.array.phone_type_array);
			for (int i = 0; i < kinds.length; i++) {
				if (String.valueOf(kinds[i]).equalsIgnoreCase(
						String.valueOf(customerinfo.billing_phone_kind))) {
					spnPhoneTypeOne.setSelection(i);
				}
				if (customerinfo.billing_phones_kinds.size() > 0) {
					if (String.valueOf(kinds[i]).equalsIgnoreCase(
							String.valueOf(customerinfo.billing_phones_kinds
									.get(0)))) {
						spnPhoneTypeTwo.setSelection(i);
					}
				}
				if (customerinfo.billing_phones_kinds.size() > 1) {
					if (String.valueOf(kinds[i]).equalsIgnoreCase(
							String.valueOf(customerinfo.billing_phones_kinds
									.get(1)))) {
						spnPhoneTypeThree.setSelection(i);
					}
				}
			}
			if (customerinfo.billing_phones.size() > 0) {
				llPhoneTwo.setVisibility(View.VISIBLE);
				edtPhoneTwo.setText(customerinfo.billing_phones.get(0));
				if (customerinfo.billing_phones_exts.size() > 0)
					edtPhoneExtTwo.setText(customerinfo.billing_phones_exts
							.get(0));
			}
			if (customerinfo.billing_phones.size() > 1) {
				llPhoneThree.setVisibility(View.VISIBLE);
				edtPhoneThree.setText(customerinfo.billing_phones.get(1));
				if (customerinfo.billing_phones_exts.size() > 1)
					edtPhoneExtThree.setText(customerinfo.billing_phones_exts
							.get(1));
			}
			if (serviceLocations != null && serviceLocations.size() > 0) {
				location = serviceLocations.get(0);
				edtServiceEmail.setText(location.email);
				edtServiceLocationName.setText(location.name);
				edtServiceAddress1.setText(location.street);
				edtServiceAddress2.setText(location.street_two);
				edtServiceCity.setText(location.city);
				edtServiceState.setText(location.state);
				edtServiceZip.setText(location.zip);
				edtServicePhoneOne.setText(location.phone);
				edtServicePhoneExtOne.setText(location.phone_ext);
				
				int tax_id = 0;
				for (int i = 0; i < m_taxes.size(); i++) {
					if (String.valueOf(m_taxes.get(i).id).equalsIgnoreCase(
							String.valueOf(location.tax_rate_id))) {
						tax_id = i;
						break;
					}
				}
				spnTaxRates.setSelection(tax_id);
				int loc_id = 0;
				for (int i = 0; i < m_location.size(); i++) {
					if (String.valueOf(m_location.get(i).id).equalsIgnoreCase(
							String.valueOf(location.location_type_id))) {
						loc_id = i;
						break;
					}
				}
				spnLocationTypes.setSelection(loc_id);
				int route_id = 0;
				for (int i = 0; i < m_routes.size(); i++) {
					if (String.valueOf(m_routes.get(i).id).equalsIgnoreCase(
							String.valueOf(location.service_route_id))) {
						route_id = i;
						break;
					}
				}
				spnServiceRoute.setSelection(route_id);
				
				for (int i = 0; i < kinds.length; i++) {
					if (String.valueOf(kinds[i]).equalsIgnoreCase(
							String.valueOf(location.phone_kind))) {
						spnServicePhoneTypeOne.setSelection(i);
					}
					if (location.phones_kinds.size() > 0) {
						if (String.valueOf(kinds[i]).equalsIgnoreCase(
								String.valueOf(location.phones_kinds
										.get(0)))) {
							spnServicePhoneTypeTwo.setSelection(i);
						}
					}
					if (location.phones_kinds.size() > 1) {
						if (String.valueOf(kinds[i]).equalsIgnoreCase(
								String.valueOf(location.phones_kinds
										.get(1)))) {
							spnServicePhoneTypeThree.setSelection(i);
						}
					}
				}
				if (location.phones.size() > 0) {
					llServicePhoneTwo.setVisibility(View.VISIBLE);
					edtServicePhoneTwo.setText(location.phones.get(0));
					if (location.phones_exts.size() > 0)
						edtServicePhoneExtTwo.setText(location.phones_exts
								.get(0));
				}
				if (location.phones.size() > 1) {
					llServicePhoneThree.setVisibility(View.VISIBLE);
					edtServicePhoneThree.setText(location.phones.get(1));
					if (location.phones_exts.size() > 1)
						edtServicePhoneExtThree.setText(location.phones_exts
								.get(1));
				}
			}
			String billingterm = BillingTermsList.Instance()
					.getBillingTermsNameByid(customerinfo.billing_term_id);
			int bill_id = 0;

			for (int i = 0; i < m_billingTerms.size(); i++) {
				if (String.valueOf(m_billingTerms.get(i).id).equalsIgnoreCase(
						String.valueOf(customerinfo.billing_term_id))) {
					bill_id = i;
					break;
				}
			}
			spnBillingTerms.setSelection(bill_id);
		}
	}

	public void SetHint() {
		TaxRates d = new TaxRates();
		d.name = "Tax Rate";
		m_taxes.add(0, d);

		LocationInfo l = new LocationInfo();
		l.name = "Location Type";
		m_location.add(0, l);

		BillingTermsInfo b = new BillingTermsInfo();
		b.name = "Billing Terms";
		m_billingTerms.add(0, b);

		ServiceRoutesInfo s = new ServiceRoutesInfo();
		s.name = "Select Service Route";
		m_routes.add(0, s);
	}

	public void LoadValues() {
		SetHint();
		if (m_taxes.size() > 0) {
			setSpinnerValues("name", m_taxes, TaxRates.class, spnTaxRates);
		}
		if (m_location.size() > 0) {
			setSpinnerValues("name", m_location, LocationInfo.class,
					spnLocationTypes);
		}
		if (m_billingTerms.size() > 0) {
			setSpinnerValues("name", m_billingTerms, BillingTermsInfo.class,
					spnBillingTerms);
		}
		if (m_routes.size() > 0) {
			setSpinnerValues("name", m_routes, ServiceRoutesInfo.class,
					spnServiceRoute);
		}
		if (m_billingTerms != null) {
			for (int i = 0; i < m_billingTerms.size(); i++) {
				if (m_billingTerms.get(i).is_default) {
					spnBillingTerms.setSelection(i);
				}
			}
		}
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

	@Override
	public void onClick(View v) {

		if (v == btnPhonePlus) {
			if (addrow == 0) {
				llPhoneTwo.setVisibility(View.VISIBLE);
				addrow = 1;
			} else if (addrow == 1) {
				llPhoneThree.setVisibility(View.VISIBLE);
			}
		} else if (v == btnPhoneMinusTwo) {
			llPhoneTwo.setVisibility(View.GONE);
			edtPhoneTwo.setText("");
			edtPhoneExtTwo.setText("");
			spnPhoneTypeTwo.setSelection(0);
		} else if (v == btnPhoneMinusThree) {
			llPhoneThree.setVisibility(View.GONE);
			edtPhoneThree.setText("");
			edtPhoneExtThree.setText("");
			spnPhoneTypeThree.setSelection(0);
			addrow = 0;
		} else if (v == btnServicePhonePlus) {
			if (addrowService == 0) {
				llServicePhoneTwo.setVisibility(View.VISIBLE);
				addrowService = 1;
			} else if (addrowService == 1) {
				llServicePhoneThree.setVisibility(View.VISIBLE);
			}
		} else if (v == btnServicePhoneMinusTwo) {
			llServicePhoneTwo.setVisibility(View.GONE);
			edtServicePhoneTwo.setText("");
			edtServicePhoneExtTwo.setText("");
			spnServicePhoneTypeTwo.setSelection(0);
		} else if (v == btnServicePhoneMinusThree) {
			llServicePhoneThree.setVisibility(View.GONE);
			edtServicePhoneThree.setText("");
			edtServicePhoneExtThree.setText("");
			spnServicePhoneTypeThree.setSelection(0);
			addrowService = 0;
		} else if (v == btnSave) {
			boolean flag = true;
			String msg = "";
			String ser_email = edtServiceEmail.getText().toString().trim();
			String ser_loc_name = edtServiceLocationName.getText().toString();
			// 0 = res, 1 = comm
			if (CustType == 0) {
				if (edtFirstName.getText().toString().trim().length() <= 0) {
					flag = false;
					msg += "First Name should not be blank\n";
				}
				if (edtLastName.getText().toString().trim().length() <= 0) {
					flag = false;
					msg += "Last Name should not be blank\n";
				}
			} else if (CustType == 1) {
				if (edtNameCom.getText().toString().trim().length() <= 0) {
					flag = false;
					msg += "Name should not be blank\n";
				}
			}
			if (ser_email.length() > 0) {
				if (!isValidEmail(ser_email)) {
					flag = false;
					msg += "Please enter valid email address\n";
				}
			}
			if (spnBillingTerms.getSelectedItem().toString()
					.equalsIgnoreCase("Billing Terms")) {
				flag = false;
				msg += "Please select Billing terms\n";
			}
			if (spnTaxRates.getSelectedItem().toString()
					.equalsIgnoreCase("Tax Rate")) {
				flag = false;
				msg += "Please select Tax rate\n";
			}
			if (ser_loc_name.length() <= 0) {
				flag = false;
				msg += "Please enter service location name\n";
			}
			if (flag) {
				CustomerInfo cinfo = new CustomerInfo();
				cinfo.name = edtNameCom.getText().toString().trim();
				cinfo.name_prefix = edtPrefix.getText().toString().trim();
				cinfo.first_name = edtFirstName.getText().toString().trim();
				cinfo.last_name = edtLastName.getText().toString().trim();
				cinfo.customer_type = customer_type;
				cinfo.invoice_email = ser_email;
				cinfo.billing_name = edtBillingName.getText().toString().trim();
				cinfo.billing_street = edtAddress1.getText().toString().trim();
				cinfo.billing_street_two = edtAddress2.getText().toString()
						.trim();
				cinfo.billing_city = edtCity.getText().toString().trim();
				cinfo.billing_state = edtState.getText().toString().trim();
				cinfo.billing_zip = edtZip.getText().toString().trim();

				cinfo.billing_phone = edtPhoneOne.getText().toString().trim();
				cinfo.billing_phone_ext = edtPhoneExtOne.getText().toString()
						.trim();
				cinfo.billing_attention = edtAttn.getText().toString();
				cinfo.billing_phone_kind = spnPhoneTypeOne.getSelectedItem()
						.toString();

				cinfo.billing_phones = new ArrayList<String>();
				cinfo.billing_phones_kinds = new ArrayList<String>();
				cinfo.billing_phones_exts = new ArrayList<String>();
				if (edtPhoneTwo.getText().toString().length() > 0) {
					cinfo.billing_phones.add(edtPhoneTwo.getText().toString()
							.trim());
					if(edtPhoneExtTwo.getText()
							.toString().length() > 0)
						cinfo.billing_phones_exts.add(edtPhoneExtTwo.getText()
							.toString().trim());
					cinfo.billing_phones_kinds.add(spnPhoneTypeTwo
							.getSelectedItem().toString().trim());
				}
				if (edtPhoneThree.getText().toString().length() > 0) {
					cinfo.billing_phones.add(edtPhoneThree.getText().toString()
							.trim());
					if(edtPhoneExtThree.getText()
							.toString().length() > 0)
						cinfo.billing_phones_exts.add(edtPhoneExtThree.getText()
							.toString().trim());
					cinfo.billing_phones_kinds.add(spnPhoneTypeThree
							.getSelectedItem().toString().trim());
				}

				// cinfo.same_billing_address = ServiceAddressSame;

				ServiceLocationsInfo serviceInfo = new ServiceLocationsInfo();
				serviceInfo.email = edtServiceEmail.getText().toString();
				serviceInfo.name = edtServiceLocationName.getText().toString();
				if (ServiceAddressSame) {
					serviceInfo.street = edtAddress1.getText().toString();
					serviceInfo.street_two = edtAddress2.getText().toString();
					serviceInfo.city = edtCity.getText().toString();
					serviceInfo.state = edtState.getText().toString();
					UserInfo user = UserInfo.Instance().getUser();
					if (user != null)
						serviceInfo.country = user.country;
					else
						serviceInfo.country = "United States";
					serviceInfo.zip = edtZip.getText().toString();
					serviceInfo.phone = edtPhoneOne.getText().toString().trim();
					serviceInfo.phone_ext = edtPhoneExtOne.getText().toString()
							.trim();
					serviceInfo.phone_kind = spnPhoneTypeOne.getSelectedItem()
							.toString();
					serviceInfo.phones = new ArrayList<String>();
					serviceInfo.phones.add(edtPhoneTwo.getText().toString()
							.trim());
					serviceInfo.phones.add(edtPhoneThree.getText().toString()
							.trim());
					serviceInfo.phones_exts = new ArrayList<String>();
					serviceInfo.phones_exts.add(edtPhoneExtTwo.getText()
							.toString().trim());
					serviceInfo.phones_exts.add(edtPhoneExtThree.getText()
							.toString().trim());
					serviceInfo.phones_kinds = new ArrayList<String>();
					serviceInfo.phones_kinds.add(spnPhoneTypeTwo
							.getSelectedItem().toString().trim());
					serviceInfo.phones_kinds.add(spnPhoneTypeThree
							.getSelectedItem().toString().trim());
				} else {
					serviceInfo.street = edtServiceAddress1.getText()
							.toString();
					serviceInfo.street_two = edtServiceAddress2.getText()
							.toString();
					// serviceInfo.attention = edtAttn.getText().toString();
					serviceInfo.city = edtServiceCity.getText().toString();
					serviceInfo.state = edtServiceState.getText().toString();
					UserInfo user = UserInfo.Instance().getUser();
					if (user != null)
						serviceInfo.country = user.country;
					else
						serviceInfo.country = "United States";
					serviceInfo.zip = edtServiceZip.getText().toString();
					serviceInfo.phone = edtServicePhoneOne.getText().toString()
							.trim();
					serviceInfo.phone_ext = edtServicePhoneExtOne.getText()
							.toString().trim();
					serviceInfo.phone_kind = spnServicePhoneTypeOne
							.getSelectedItem().toString();
					serviceInfo.phones = new ArrayList<String>();
					serviceInfo.phones_exts = new ArrayList<String>();
					serviceInfo.phones_kinds = new ArrayList<String>();
					if (edtServicePhoneTwo.getText().toString().length() > 0) {
						serviceInfo.phones.add(edtServicePhoneTwo.getText()
								.toString().trim());
						serviceInfo.phones_exts.add(edtServicePhoneExtTwo
								.getText().toString().trim());
						serviceInfo.phones_kinds.add(spnServicePhoneTypeTwo
								.getSelectedItem().toString().trim());
					}
					if (edtServicePhoneThree.getText().toString().length() > 0) {
						serviceInfo.phones.add(edtServicePhoneThree.getText()
								.toString().trim());
						serviceInfo.phones_exts.add(edtServicePhoneExtThree
								.getText().toString().trim());
						serviceInfo.phones_kinds.add(spnServicePhoneTypeThree
								.getSelectedItem().toString().trim());
					}
				}
				// serviceInfo.service_route_id =
				// edtServiceZip.getText().toString();

				if (!spnTaxRates.getSelectedItem().toString()
						.equalsIgnoreCase("Tax Rate")) {
					serviceInfo.tax_rate_id = TaxRateList.Instance()
							.getTexRateIdByname(
									spnTaxRates.getSelectedItem().toString());
				}
				if (!spnLocationTypes.getSelectedItem().toString()
						.equalsIgnoreCase("Location Type")) {
					serviceInfo.location_type_id = LocationInfoList.Instance()
							.getLocationInfoIdByname(
									spnLocationTypes.getSelectedItem()
											.toString());
				}

				if (!spnBillingTerms.getSelectedItem().toString()
						.equalsIgnoreCase("Billing Terms")) {
					cinfo.billing_term_id = BillingTermsList.Instance()
							.getBillingTermsIdByname(
									spnBillingTerms.getSelectedItem()
											.toString());
				}
				if (!spnServiceRoute.getSelectedItem().toString()
						.equalsIgnoreCase("Select Service Route")) {
					serviceInfo.service_route_id = ServiceRoutesList.Instance()
							.getServiceRouteIdByname(
									spnServiceRoute.getSelectedItem()
											.toString());
				}
				if(isEdit){
					if(customerinfo != null){
						cinfo.id = customerinfo.id;
					}
					if(location != null){
						serviceInfo.id = location.id;
					}
				}

                mBaseLoader.showProgress("Saving Customer...");
				CustomerInfo.AddCustomer(cinfo, serviceInfo,
						new ModelDelegates.UpdateCustomerDelegate() {
							@Override
							public void UpdateSuccessFully(CustomerInfo info) {
                                mBaseLoader.hideProgress();
								Toast.makeText(
										FieldworkApplication.getContext(),
										"Customer data saved successfully", Toast.LENGTH_LONG)
										.show();
								finish();
							}

							@Override
							public void UpdateFail(String ErrorMessage) {
                                mBaseLoader.hideProgress();
								Toast.makeText(
										FieldworkApplication.getContext(),
										ErrorMessage, Toast.LENGTH_LONG).show();
							}
						});
				// MaterialInfo.AddMaterial(edtPrefix.getText()
				// .toString(), edtFirstName, edtLastName,
				// edtPrefix.getText().toString().trim(), "");
				// finish();
			} else {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		}

	}

	public final static boolean isValidEmail(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}


	@Override
	public void onBackPressed() {
		ConfirmDialog dlg = ConfirmDialog.newInstance("Are your sure want to exit from this screen?");
		dlg.show(getSupportFragmentManager(), "confirm_exit");
	}

	@Override
	public void onDialogConmfirm(String tag) {
		if (tag.equals("confirm_exit")) {
			finish();
		}
	}

	@Override
	public void onDialogCancel(String tag) {

	}
}
