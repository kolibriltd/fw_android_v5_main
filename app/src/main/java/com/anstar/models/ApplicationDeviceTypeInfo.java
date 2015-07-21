package com.anstar.models;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.model.mapper.ModelMapper;

public class ApplicationDeviceTypeInfo extends ActiveRecordBase {

	public ApplicationDeviceTypeInfo() {

	}
	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "name")
	public String name = "";
	@ModelMapper(JsonKey = "account_id")
	public int account_id = 0;
}
