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

public class CustomerContactInfo extends ActiveRecordBase {

	public CustomerContactInfo() {

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
	public int Customer_id = 0;

	public static void Parse(int cust_id, JSONArray contacts) {
		try {
			for (int i = 0; i < contacts.length(); i++) {
				JSONObject contact = contacts.getJSONObject(i);
				ModelMapHelper<CustomerContactInfo> notemapper = new ModelMapHelper<CustomerContactInfo>();
				CustomerContactInfo info = notemapper.getObject(
						CustomerContactInfo.class, contact);
				CustomerContactInfo c_info = getCustomerContactInfoById(info.id);
				if (c_info == null) {
					c_info = FieldworkApplication.Connection().newEntity(
							CustomerContactInfo.class);
					c_info.id = info.id;
				}
				c_info.copyFrom(info);
				info.Customer_id = cust_id;
				info.save();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static CustomerContactInfo getCustomerContactInfoById(int id) {
		try {
			List<CustomerContactInfo> list = FieldworkApplication.Connection()
					.findAll(CustomerContactInfo.class);
			if (list.size() > 0) {
				for (CustomerContactInfo cci : list) {
					if (cci.id == id) {
						return cci;
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList<CustomerContactInfo> getContactsByCustomerId(int id) {
		ArrayList<CustomerContactInfo> m_list = new ArrayList<CustomerContactInfo>();
		try {
			List<CustomerContactInfo> lst = FieldworkApplication
					.Connection()
					.find(CustomerContactInfo.class,
							CamelNotationHelper.toSQLName("Customer_id") + "=?",
							new String[] { String.valueOf(id) });
			if (lst != null && lst.size() > 0) {
				m_list = new ArrayList<CustomerContactInfo>(lst);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}
}
