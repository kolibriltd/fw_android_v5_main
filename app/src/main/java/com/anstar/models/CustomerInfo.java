package com.anstar.models;

import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.common.JsonCreator;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.UpdateCustomerDelegate;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.ServiceLocationsList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerInfo extends ActiveRecordBase {

	public CustomerInfo() {

	}

	@ModelMapper(JsonKey = "account_number")
	public int account_number = 0;
	@ModelMapper(JsonKey = "billing_city")
	public String billing_city = "";
	@ModelMapper(JsonKey = "billing_contact")
	public String billing_contact = "";
	@ModelMapper(JsonKey = "billing_name")
	public String billing_name = "";
	@ModelMapper(JsonKey = "billing_phone")
	public String billing_phone = "";
	@ModelMapper(JsonKey = "billing_phone_kind")
	public String billing_phone_kind = "";
	@ModelMapper(JsonKey = "billing_phone_ext")
	public String billing_phone_ext = "";
	@ModelMapper(JsonKey = "billing_state")
	public String billing_state = "";
	@ModelMapper(JsonKey = "billing_street")
	public String billing_street = "";
	@ModelMapper(JsonKey = "billing_street2")
	public String billing_street_two = "";
	@ModelMapper(JsonKey = "billing_suite")
	public String billing_suite = "";
	@ModelMapper(JsonKey = "billing_term_id")
	public int billing_term_id = 0;
	@ModelMapper(JsonKey = "billing_attention")
	public String billing_attention = "";
	@ModelMapper(JsonKey = "billing_zip")
	public String billing_zip = "";
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "customer_type")
	public String customer_type = "";
	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "inspections_enabled")
	public boolean inspections_enabled = false;
	@ModelMapper(JsonKey = "email_marketing")
	public boolean email_marketing = false;
	@ModelMapper(JsonKey = "send_report_email")
	public boolean send_report_email = false;
	@ModelMapper(JsonKey = "invoice_email")
	public String invoice_email = "";
	@ModelMapper(JsonKey = "latitude")
	public String latitude = "";
	@ModelMapper(JsonKey = "location_type_id")
	public int location_type_id = 0;
	@ModelMapper(JsonKey = "longitude")
	public String longitude = "";
	@ModelMapper(JsonKey = "last_name")
	public String last_name = "";
	@ModelMapper(JsonKey = "first_name")
	public String first_name = "";
	@ModelMapper(JsonKey = "name")
	public String name = "";
	@ModelMapper(JsonKey = "name_prefix")
	public String name_prefix = "";
	@ModelMapper(JsonKey = "balance")
	public String balance = "";
	@ModelMapper(JsonKey = "site")
	public String site = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";

/*
	public boolean isAllreadyLoded() {
		return isAllreadyLoded;
	}
*/

	public synchronized void setIsAllreadyLoded(boolean isAllreadyLoded) {
		this.isAllreadyLoded = isAllreadyLoded;
	}

	public boolean isAllreadyLoded = false;

	// String arrays in service
	@ModelMapper(JsonKey = "billing_phones", IsArray = true)
	public ArrayList<String> billing_phones;
	@ModelMapper(JsonKey = "billing_phones_kinds", IsArray = true)
	public ArrayList<String> billing_phones_kinds;
	@ModelMapper(JsonKey = "billing_phones_exts", IsArray = true)
	public ArrayList<String> billing_phones_exts;

	public String setCustomerName() {
		String nm = "";
		if (this.customer_type.equalsIgnoreCase("Commercial")) {
			nm = this.name;
		} else {
			if (this.last_name != null && this.last_name.length() > 0
					&& !this.last_name.equalsIgnoreCase("null")) {
				nm += this.last_name + ", ";
			}
			if (this.name_prefix != null && this.name_prefix.length() > 0
					&& !this.name_prefix.equalsIgnoreCase("null")) {
				nm += this.name_prefix + " ";
			}
			if (this.first_name != null && this.first_name.length() > 0
					&& !this.first_name.equalsIgnoreCase("null")) {
				nm += this.first_name + " ";
			}
			// nm = this.name_prefix + " " + this.first_name + " "
			// + this.last_name;
		}
		return nm;
	}

	public void RetriveData(final UpdateCustomerDelegate delegate) {
		if (isAllreadyLoded) {
			if (delegate != null) {
				delegate.UpdateSuccessFully(this);
			}
		} else {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.CUSTOMERS + "/" + this.id);
				helper.call(new ServiceHelperDelegate() {

					@Override
					public void CallFinish(ServiceResponse res) {
						parseAndSaveCustomer(res, delegate);
					}

					@Override
					public void CallFailure(String ErrorMessage) {
						if (delegate != null) {
							delegate.UpdateFail(ErrorMessage);
						}
					}
				});
			} else {
				if (delegate != null) {
					delegate.UpdateFail("Please check your intenet connection to retrive customer information.");
				}
			}
		}

	}

	public static void AddCustomer(CustomerInfo info,
			ServiceLocationsInfo servInfo, UpdateCustomerDelegate delegate) {
		try {
			// {"customer_type":"Residential","account_number":2222,"site":"www.djsite.com","balance":"100.00",
			// "name_prefix":"","first_name":"Dhara","last_name":"Khatri","name":"","customer_name":"Ms. Dhara Khatri","billing_term_id":2,"inspections_enabled":false,"email_marketing":false,"send_report_email":true,
			// "invoice_email":"john.doe@example.com","billing_city":"City","billing_contact":"Contact",
			// "billing_name":"John Doe","billing_county":"","billing_state":"ST","billing_street":"Street",
			// "billing_street2":"House","billing_suite":"Suite","billing_zip":"12345","billing_phone":"12345678","billing_phone_ext":"123","billing_phone_kind":"Home","billing_phones":["76543210"],"billing_phones_ext":["123"],
			// "billing_phones_kinds":["Office"],"lat":null,"lng":null,"service_locations":
			// [{"name":"sk's restra","location_type_id":
			// 32,"email":"dk@example.com","address": {"street": "12","street2":
			// "paldi","city": "Ahmedabad","zip": "12345","county":
			// "County","suite": "Suite","notes": "Some notes","country":
			// "India","contact_name": "krn","phone": "123456789","phone_kind":
			// "Home","phone_ext":
			// "123","phones":["12345689"],"phones_exts":[""],"phones_kinds":["Fax"],
			// "attention":"","lat": "12.34657685","lng":
			// "54.13245657","bounds_ne": "12.34657685",
			// "bounds_sw": "54.13245657"}}
			// ]}
			if (NetworkConnectivity.isConnected()) {
				// material.UpdateMaterial(name, epa, price);
					info.UpdateCustomer(delegate, servInfo);
			} else {
				delegate.UpdateFail("Please check your internet connection. This feature is only available when you are online");
			}

		} catch (Exception e) {

		}
	}

	public void UpdateCustomer(final UpdateCustomerDelegate delegate,
			ServiceLocationsInfo servInfo) {
		HashMap<String, Object> custHash = new HashMap<String, Object>();
		custHash.put("name_prefix", this.name_prefix);
		custHash.put("first_name", this.first_name);
		custHash.put("last_name", this.last_name);
		custHash.put("name", this.name);
		custHash.put("invoice_email", this.invoice_email);
		custHash.put("customer_type", this.customer_type);
		custHash.put("billing_street", this.billing_street);
		custHash.put("billing_street2", this.billing_street_two);
		custHash.put("billing_city", this.billing_city);
		custHash.put("billing_state", this.billing_state);
		custHash.put("billing_state", this.billing_state);
		custHash.put("billing_zip", this.billing_zip);
		custHash.put("billing_term_id", this.billing_term_id);
		custHash.put("billing_phone", this.billing_phone);
		custHash.put("billing_phone_ext", this.billing_phone_ext);
		custHash.put("billing_phone_kind", this.billing_phone_kind);
		custHash.put("billing_attention", this.billing_attention);
		if (this.billing_phones.size() > 0)
			custHash.put("billing_phones", getPhoneArray(this.billing_phones));
		if (this.billing_phones_exts.size() > 0)
			custHash.put("billing_phones_exts",
					getPhoneArray(this.billing_phones_exts));
		if (this.billing_phones_kinds.size() > 0)
			custHash.put("billing_phones_kinds",
					getPhoneArray(this.billing_phones_kinds));

		HashMap<String, Object> servHash = new HashMap<String, Object>();
		servHash.put("email", servInfo.email);
		servHash.put("name", servInfo.name);
		servHash.put("location_type_id", servInfo.location_type_id);
		servHash.put("tax_rate_id", servInfo.tax_rate_id);
		servHash.put("service_route_id", servInfo.service_route_id);

		HashMap<String, Object> addHash = new HashMap<String, Object>();
		addHash.put("street", servInfo.street);
		addHash.put("street2", servInfo.street_two);
		addHash.put("city", servInfo.city);
		addHash.put("state", servInfo.state);
		addHash.put("country", servInfo.country);
		addHash.put("zip", servInfo.zip);
		addHash.put("phone", servInfo.phone);
		addHash.put("phone_ext", servInfo.phone_ext);
		addHash.put("phone_kind", servInfo.phone_kind);
		if (servInfo.phones.size() > 0)
			addHash.put("phones", getPhoneArray(servInfo.phones));
		if (servInfo.phones_exts.size() > 0)
			addHash.put("phones_exts", getPhoneArray(servInfo.phones_exts));
		if (servInfo.phones_kinds.size() > 0)
			addHash.put("phones_kinds", getPhoneArray(servInfo.phones_kinds));

		servHash.put("address_attributes", addHash);
		if(servInfo.id > 0)
			servHash.put("id", servInfo.id);
		
		List<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
		lst.add(servHash);
		JSONArray arr = JsonCreator.getJsonArray(lst);

		custHash.put("service_locations_attributes", arr);
		JSONObject obj = JsonCreator.getJsonObject(custHash);
		Utils.LogInfo("ADD CUSTOMER JSON *********** " + obj.toString());
		// residential = String.format(residential, this.name_prefix,
		// this.first_name,
		// this.last_name);
		// commercial = String.format(commercial, this.name);
		// String json = "{\"customer_type\": \"%s\", \"billing_term_id\":%d}";
		ServiceCaller caller;
		if(this.id > 0){
			caller = new ServiceCaller(ServiceHelper.CUSTOMERS+"/"+this.id,
					ServiceCaller.RequestMethod.PUT, "{\"customer\":"
							+ obj.toString() + "}");
			caller.startRequest(new ServiceHelperDelegate() {
				@Override
				public void CallFinish(ServiceResponse res) {
					try {
						if (res.StatusCode == 500 || res.StatusCode == 404 || res.StatusCode == 422 || res.StatusCode == 0) {
							Toast.makeText(FieldworkApplication.getContext(),
									"Status code - " + res.StatusCode, Toast.LENGTH_LONG).show();
							delegate.UpdateFail(res.ErrorMessage);
						} else {
							parseAndSaveCustomer(res, delegate);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void CallFailure(String ErrorMessage) {
					Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
							Toast.LENGTH_LONG).show();
				}
			});
		}else{
			caller = new ServiceCaller(ServiceHelper.CUSTOMERS,
				ServiceCaller.RequestMethod.POST, "{\"customer\":"
						+ obj.toString() + "}");
			caller.startRequest(new ServiceHelperDelegate() {
				@Override
				public void CallFinish(ServiceResponse res) {
					try {
						if (res.StatusCode == 500 || res.StatusCode == 404 || res.StatusCode == 422 || res.StatusCode == 0) {
							Toast.makeText(FieldworkApplication.getContext(),
									"Status code - " + res.StatusCode, Toast.LENGTH_LONG).show();
							delegate.UpdateFail(res.ErrorMessage);
						} else {
							JSONObject obj = new JSONObject(res.RawResponse);
							JSONObject customer = obj.getJSONObject("customer");
							id = customer.optInt("id");
							RetriveData(delegate);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void CallFailure(String ErrorMessage) {
					Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
							Toast.LENGTH_LONG).show();
				}
		
		});
		}
	}
	
	private void parseAndSaveCustomer(ServiceResponse res, UpdateCustomerDelegate delegate){
		if (!res.isError()) {
			Utils.LogInfo("Customer retrive data call finish*****  " + res.RawResponse);
			JSONObject obj = null;
			JSONObject customer = null;
			try {
				obj = new JSONObject(res.RawResponse);
				customer = obj.getJSONObject("customer");
				ModelMapHelper<CustomerInfo> mapper = new ModelMapHelper<CustomerInfo>();
				CustomerInfo info = mapper.getObject(
						CustomerInfo.class, customer);

				CustomerInfo cust = CustomerList.Instance()
						.getCustomerById(info.id);
				if (cust == null) {
					cust = FieldworkApplication.Connection()
							.newEntity(CustomerInfo.class);
					cust.id = info.id;
				}
				ArrayList<String> billingphones = new ArrayList<String>();
				if(customer.getJSONArray("billing_phones") != null){
					JSONArray arr = customer.getJSONArray("billing_phones");
					for (int i = 0; i < arr.length(); i++) {
						String val = arr.getString(i);
						billingphones.add(val);
					}
				}
				ArrayList<String> billing_phones_kinds = new ArrayList<String>();
				if(customer.getJSONArray("billing_phones_kinds") != null){
					JSONArray arr = customer.getJSONArray("billing_phones_kinds");
					for (int i = 0; i < arr.length(); i++) {
						String val = arr.getString(i);
						billing_phones_kinds.add(val);
					}
				}
				ArrayList<String> billing_phones_exts = new ArrayList<String>();
				if(customer.getJSONArray("billing_phones_exts") != null){
					JSONArray arr = customer.getJSONArray("billing_phones_exts");
					for (int i = 0; i < arr.length(); i++) {
						String val = arr.getString(i);
						billing_phones_exts.add(val);
					}
				}
				
				cust.copyFrom(info);
				cust.billing_phones = billingphones;
				cust.billing_phones_kinds = billing_phones_kinds;
				cust.billing_phones_exts = billing_phones_exts;
				cust.setIsAllreadyLoded(true);
				cust.save();

				JSONArray serviceLocArray = customer
						.getJSONArray("service_locations");
				ServiceLocationsList.Instance()
						.parseServiceLocationsInfoList(
								serviceLocArray);

				JSONArray customer_contacts = customer
						.getJSONArray("contacts");
				CustomerContactInfo.Parse(cust.id,
						customer_contacts);

				if (delegate != null) {
					delegate.UpdateSuccessFully(CustomerInfo.this);
				} else {
					Utils.LogInfo("Customer retrive data delegate is null");
				}
			} catch (Exception e) {
				if (delegate != null) {
					delegate.UpdateFail(e.getMessage());
				}
				e.printStackTrace();
			}
		}
		if (delegate != null) {
			delegate.UpdateFail(res.ErrorMessage);
		}
	}

	private JSONArray getPhoneArray(ArrayList<String> arList) {
		JSONArray arphone = new JSONArray();
		for (String p : arList) {
			try {
				arphone.put(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return arphone;
	}
	// public void sync(final UpdateInfoDelegate updateInfoDelegate) {
	//
	// String json = "{\"epa_number\":\"" + epa_number + "\"," + "\"name\":\""
	// + name + "\"," + "\"price\":\"" + price + "\"}";
	// String url = "materials";
	// ServiceCaller caller = new ServiceCaller(url,
	// ServiceCaller.RequestMethod.POST, json);
	// caller.startRequest(new ServiceHelperDelegate() {
	//
	// @Override
	// public void CallFinish(ServiceResponse res) {
	// if (!res.isError()) {
	// try {
	// JSONObject obj = new JSONObject(res.RawResponse);
	// JSONObject pest = obj.getJSONObject("material");
	// id = pest.getInt("id");
	// save();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } catch (ActiveRecordException e) {
	// e.printStackTrace();
	// }
	// }
	// updateInfoDelegate.UpdateSuccessFully(res);
	// }
	//
	// @Override
	// public void CallFailure(String ErrorMessage) {
	//
	// }
	// });
	//
	// }

}
