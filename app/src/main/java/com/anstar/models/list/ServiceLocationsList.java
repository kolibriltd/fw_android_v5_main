package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.ServiceLocationContactInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.TargetPestInfo;

public class ServiceLocationsList {

	private ServiceLocationsList() {

	}

	private static volatile ServiceLocationsList _instance = null;

	// private static volatile int cat_id = 0;

	public static ServiceLocationsList Instance() {
		if (_instance == null) {
			synchronized (ServiceLocationsList.class) {
				_instance = new ServiceLocationsList();
			}
		}
		return _instance;
	}

	public void parseServiceLocationsInfoList(JSONArray servicelocationlist) {

		try {
			for (int i = 0; i < servicelocationlist.length(); i++) {
				JSONObject loc = servicelocationlist.getJSONObject(i);
				
				ModelMapHelper<ServiceLocationsInfo> notemapper = new ModelMapHelper<ServiceLocationsInfo>();
				// parse address
				JSONObject address = loc.getJSONObject("address");
				ServiceLocationsInfo info = notemapper.getObject(
						ServiceLocationsInfo.class, address);
				info.id = loc.optInt("id");
				info.customer_id = loc.optInt("customer_id");
				info.service_route_id = loc.optInt("service_route_id");
				info.location_type_id = loc.optInt("location_type_id");
				info.tax_rate_id = loc.optInt("tax_rate_id");
				info.name = loc.optString("name");
				info.email = loc.optString("email");
				ServiceLocationsInfo location = ServiceLocationsList.Instance()
						.getServiceLocationById(info.id);
				if (location == null) {
					location = FieldworkApplication.Connection().newEntity(
							ServiceLocationsInfo.class);
					location.id = info.id;
				}
				ArrayList<String> phones = new ArrayList<String>();
				if(address.getJSONArray("phones") != null){
					JSONArray arr = address.getJSONArray("phones");
					for (int j = 0; j < arr.length(); j++) {
						Utils.LogInfo("phones :::: "+arr.getString(j));
						String val = arr.getString(j);
						phones.add(val);
					}
				}
				ArrayList<String> phones_kinds = new ArrayList<String>();
				if(address.getJSONArray("phones_kinds") != null){
					JSONArray arr = address.getJSONArray("phones_kinds");
					for (int j = 0; j < arr.length(); j++) {
						String val = arr.getString(j);
						phones_kinds.add(val);
					}
				}
				ArrayList<String> phones_exts = new ArrayList<String>();
				if(address.getJSONArray("phones_exts") != null){
					JSONArray arr = address.getJSONArray("phones_exts");
					for (int j = 0; j < arr.length();j++) {
						String val = arr.getString(j);
						phones_exts.add(val);
					}
				}
				
				location.copyFrom(info);
				location.phones = phones;
				location.phones_kinds = phones_kinds;
				location.phones_exts = phones_exts;
				Utils.LogInfo(" location OBJECT SAVE  ::"+location.toString());
				location.save();

				// Prase Contact
				JSONArray service_contacts = loc.getJSONArray("contacts");
				ServiceLocationContactInfo.Parse(location.id, service_contacts);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void parseServiceLocationsAfterAddingCustomer(int cust_id, JSONArray servicelocationlist) {

		try {
			for (int i = 0; i < servicelocationlist.length(); i++) {
				JSONObject loc = servicelocationlist.getJSONObject(i);
				ModelMapHelper<ServiceLocationsInfo> notemapper = new ModelMapHelper<ServiceLocationsInfo>();
				// parse address
				JSONObject address = loc.getJSONObject("address");
				ServiceLocationsInfo info = notemapper.getObject(
						ServiceLocationsInfo.class, address);
				info.id = loc.optInt("id");
				info.customer_id = loc.optInt("customer_id");
				info.service_route_id = loc.optInt("service_route_id");
				info.location_type_id = loc.optInt("location_type_id");
				info.name = loc.optString("name");
				info.email = loc.optString("email");
				ArrayList<ServiceLocationsInfo> locations = ServiceLocationsList.Instance()
						.getServiceLocationByCustId(cust_id);
				
				locations.get(0).copyFrom(info);

				locations.get(0).save();

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	

	public ArrayList<ServiceLocationsInfo> getServiceLocationByCustId(int custId) {
		ArrayList<ServiceLocationsInfo> m_list = new ArrayList<ServiceLocationsInfo>();
		try {
			List<ServiceLocationsInfo> list = FieldworkApplication.Connection()
					.findAll(ServiceLocationsInfo.class);
			if (list.size() > 0) {
				for (ServiceLocationsInfo record : list) {
					if (record.customer_id == custId) {
						m_list.add(record);
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public ServiceLocationsInfo getServiceLocationById(int id) {
		try {
			List<ServiceLocationsInfo> list = FieldworkApplication.Connection()
					.findAll(ServiceLocationsInfo.class);
			if (list.size() > 0) {
				for (ServiceLocationsInfo record : list) {
					if (record.id == id) {
						return record;
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public ServiceLocationsInfo getServiceLocById(int slid) {
		try {
			List<ServiceLocationsInfo> lst = FieldworkApplication.Connection().find(
					ServiceLocationsInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(slid) });
			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void deleteServiceLocationsInfo(int cust_id) {
		try {
			int cnt = FieldworkApplication.Connection().delete(
					TargetPestInfo.class,
					CamelNotationHelper.toSQLName("CustomerId") + "=?",
					new String[] { String.valueOf(cust_id) });
			Utils.LogInfo(String
					.format("%d records deleted of material usage records for material usage %d",
							cnt, cust_id));
		} catch (Exception ex) {

		}
	}

}
