package com.anstar.models;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.model.mapper.ModelMapper;

public class MaterialUsageRecords extends ActiveRecordBase {
	public MaterialUsageRecords() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "amount")
	public String amount;
	@ModelMapper(JsonKey = "application_method")
	public String application_method;
	@ModelMapper(JsonKey = "application_method_id")
	public int application_method_id = 0;
	@ModelMapper(JsonKey = "dilution_rate_id")
	public int dilution_rate_id = 0;
	@ModelMapper(JsonKey = "location_area_id")
	public int location_area_id = 0;
	@ModelMapper(JsonKey = "measurement")
	public String measurement;
	@ModelMapper(JsonKey = "lot_number")
	public String lot_number;
	@ModelMapper(JsonKey = "device")
	public String device = "";
	@ModelMapper(JsonKey = "application_device_type_id")
	public int application_device_type_id = 0;
	@ModelMapper(JsonKey = "created_at")
	public String created_at;
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at;

	public int MaterialUsageId = 0;
	public String Pest_ids;

	// public static boolean isExists(int materialUsageRecordId,
	// MaterialUsageRecords rec) {
	// boolean isExists = false;
	// try {
	// List<MaterialUsageRecords> lst = FieldworkApplication.Connection()
	// .find(MaterialUsageRecords.class,
	// CamelNotationHelper.toSQLName("id") + "=?",
	// new String[] { String
	// .valueOf(materialUsageRecordId) });
	// if (lst != null) {
	// if (lst.size() > 0) {
	// rec = lst.get(0);
	// isExists = true;
	// }
	// }
	// } catch (Exception e) {
	// Utils.LogException(e);
	// }
	// return isExists;
	// }

}
