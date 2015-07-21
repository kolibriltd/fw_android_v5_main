package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.model.mapper.ModelMapper;

public class ServiceLocationContactInfo extends ActiveRecordBase {

	public ServiceLocationContactInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "first_name")
	public String first_name = "";
	@ModelMapper(JsonKey = "last_name")
	public String last_name = "";
	@ModelMapper(JsonKey = "title")
	public String title = "";
	@ModelMapper(JsonKey = "description")
	public String description = "";

	@ModelMapper(JsonKey = "phone")
	public String phone = "";
	@ModelMapper(JsonKey = "phone_ext")
	public String phone_ext = "";
	@ModelMapper(JsonKey = "phone_kind")
	public String phone_kind = "";
	@ModelMapper(JsonKey = "email")
	public String email = "";
	@ModelMapper(JsonKey = "email_invoices")
	public boolean email_invoices = false;
	@ModelMapper(JsonKey = "email_work_orders")
	public boolean email_work_orders = false;

	// String arrays
	@ModelMapper(JsonKey = "phones", IsArray = true)
	public ArrayList<String> phones;
	@ModelMapper(JsonKey = "phones_exts", IsArray = true)
	public ArrayList<String> phones_exts;
	@ModelMapper(JsonKey = "phones_kinds", IsArray = true)
	public ArrayList<String> phones_kinds;

	public int Service_Location_Id = 0;

	public static void Parse(int sild, JSONArray contacts) {
		try {
			for (int i = 0; i < contacts.length(); i++) {
				JSONObject contact = contacts.getJSONObject(i);
				ModelMapHelper<ServiceLocationContactInfo> notemapper = new ModelMapHelper<ServiceLocationContactInfo>();
				ServiceLocationContactInfo info = notemapper.getObject(
						ServiceLocationContactInfo.class, contact);

				ServiceLocationContactInfo c_info = getServiceLocationContactsById(info.id);
				if (c_info == null) {
					c_info = FieldworkApplication.Connection().newEntity(
							ServiceLocationContactInfo.class);
					c_info.id = info.id;
				}
				c_info.copyFrom(info);
				c_info.Service_Location_Id = sild;
				c_info.save();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static ServiceLocationContactInfo getServiceLocationContactsById(
			int id) {
		try {
			List<ServiceLocationContactInfo> list = FieldworkApplication
					.Connection().findAll(ServiceLocationContactInfo.class);
			if (list.size() > 0) {
				for (ServiceLocationContactInfo record : list) {
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

	public static ArrayList<ServiceLocationContactInfo> getContactsByServiceId(
			int id) {
		ArrayList<ServiceLocationContactInfo> m_list = new ArrayList<ServiceLocationContactInfo>();
		try {
			List<ServiceLocationContactInfo> lst = FieldworkApplication
					.Connection().find(
							ServiceLocationContactInfo.class,
							CamelNotationHelper
									.toSQLName("Service_Location_Id") + "=?",
							new String[] { String.valueOf(id) });
			if (lst != null && lst.size() > 0) {
				m_list = new ArrayList<ServiceLocationContactInfo>(lst);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

}
