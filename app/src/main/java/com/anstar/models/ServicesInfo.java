package com.anstar.models;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.model.mapper.ModelMapper;

public class ServicesInfo extends ActiveRecordBase {

	public ServicesInfo() {
	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "description")
	public String description = "";
	@ModelMapper(JsonKey = "price")
	public String price = "";
	@ModelMapper(JsonKey = "color")
	public String color = "";
	@ModelMapper(JsonKey = "frequency")
	public String frequency = "";
	@ModelMapper(JsonKey = "site_time")
	public String site_time = "";
	@ModelMapper(JsonKey = "start_time")
	public String start_time = "";
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";

}
