package com.anstar.models;

import java.util.ArrayList;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapper;

public class ServiceLocationsInfo extends ActiveRecordBase {

	public ServiceLocationsInfo() {
	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "customer_id")
	public int customer_id = 0;
	@ModelMapper(JsonKey = "location_type_id")
	public int location_type_id = 0;
	@ModelMapper(JsonKey = "service_route_id")
	public int service_route_id = 0;
	@ModelMapper(JsonKey = "name")
	public String name = "";

	// Address
	@ModelMapper(JsonKey = "street")
	public String street = "";
	@ModelMapper(JsonKey = "street2")
	public String street_two = "";
	@ModelMapper(JsonKey = "city")
	public String city = "";
	@ModelMapper(JsonKey = "state")
	public String state = "";
	@ModelMapper(JsonKey = "zip")
	public String zip = "";
	@ModelMapper(JsonKey = "county")
	public String county = "";
	@ModelMapper(JsonKey = "suite")
	public String suite = "";
	@ModelMapper(JsonKey = "notes")
	public String notes = "";
	@ModelMapper(JsonKey = "country")
	public String country = "";
	@ModelMapper(JsonKey = "contact_name")
	public String contact_name = "";
	@ModelMapper(JsonKey = "email")
	public String email = "";
	@ModelMapper(JsonKey = "phone")
	public String phone = "";
	@ModelMapper(JsonKey = "phone_ext")
	public String phone_ext = "";
	@ModelMapper(JsonKey = "phone_kind")
	public String phone_kind = "";
	@ModelMapper(JsonKey = "lat")
	public String lat = "";
	@ModelMapper(JsonKey = "lng")
	public String lon = "";
	@ModelMapper(JsonKey = "bounds_ne")
	public String bounds_ne = "";
	@ModelMapper(JsonKey = "bounds_sw")
	public String bounds_sw = "";
	@ModelMapper(JsonKey = "attention")
	public String attention = "";
	@ModelMapper(JsonKey = "tax_rate_id")
	public int tax_rate_id = 0;

	@ModelMapper(JsonKey = "phones", IsArray = true)
	public ArrayList<String> phones;
	@ModelMapper(JsonKey = "phones_exts", IsArray = true)
	public ArrayList<String> phones_exts;
	@ModelMapper(JsonKey = "phones_kinds", IsArray = true)
	public ArrayList<String> phones_kinds;

	public boolean hasValidLocation() {
		boolean flag = false;

		if (this.lat != null && this.lon != null && this.lat.length() > 0
				&& this.lon.length() > 0) {
			return true;
		}
		return flag;
	}

	public static ServiceLocationsInfo getServiceLocationByDbId(long id) {
		try {
			ServiceLocationsInfo info = FieldworkApplication.Connection()
					.findByID(ServiceLocationsInfo.class, id);
			return info;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

}
