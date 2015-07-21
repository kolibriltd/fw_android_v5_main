package com.anstar.models;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.model.mapper.ModelMapper;

public class AddressInfo extends ActiveRecordBase {

	public AddressInfo() {
	}

	@ModelMapper(JsonKey = "street")
	public String street = "";
	@ModelMapper(JsonKey = "street2")
	public String street2 = "";
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
	@ModelMapper(JsonKey = "phone")
	public String phone = "";
	@ModelMapper(JsonKey = "phone_ext")
	public String phone_ext = "";
	@ModelMapper(JsonKey = "phone_kind")
	public String phone_kind = "";
	@ModelMapper(JsonKey = "lat")
	public String lat = "";
	@ModelMapper(JsonKey = "lon")
	public String lon = "";
	@ModelMapper(JsonKey = "bounds_ne")
	public String bounds_ne = "";
	@ModelMapper(JsonKey = "bounds_sw")
	public String bounds_sw = "";
	@ModelMapper(JsonKey = "attention")
	public String attention = "";
}
