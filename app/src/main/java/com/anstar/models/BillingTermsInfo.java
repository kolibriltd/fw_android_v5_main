package com.anstar.models;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.model.mapper.ModelMapper;

public class BillingTermsInfo extends ActiveRecordBase {

	public BillingTermsInfo() {
	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "name")
	public String name = "";
	@ModelMapper(JsonKey = "days")
	public String days = "";
	@ModelMapper(JsonKey = "is_default")
	public boolean is_default = false;
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";

}
