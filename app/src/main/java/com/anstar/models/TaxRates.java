package com.anstar.models;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.model.mapper.ModelMapper;

public class TaxRates extends ActiveRecordBase {

	public TaxRates() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "name")
	public String name = "";
	@ModelMapper(JsonKey = "code")
	public String code = "";
	@ModelMapper(JsonKey = "rate")
	public String rate = "";
	@ModelMapper(JsonKey = "is_default")
	public boolean is_default = false;
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";
	@ModelMapper(JsonKey = "service_taxable")
	public boolean service_taxable = false;
	@ModelMapper(JsonKey = "freight_taxable")
	public boolean freight_taxable = false;
	@ModelMapper(JsonKey = "total_sales_tax")
	public float total_sales_tax = 0;
	@ModelMapper(JsonKey = "total_use_tax")
	public float total_use_tax = 0;
}
