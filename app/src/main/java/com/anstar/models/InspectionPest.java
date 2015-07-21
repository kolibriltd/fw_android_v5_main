package com.anstar.models;

import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapper;

public class InspectionPest extends ActiveRecordBase {

	public InspectionPest() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "pest_type_id")
	public int pest_type_id = 0;
	@ModelMapper(JsonKey = "count")
	public int count = 0;
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";

	public int inspection_id = 0;
	
	
	
	public static void updateInspectionpestId(int oldid, int newid) {
		try {
			List<InspectionPest> mlst = FieldworkApplication.Connection().find(
					InspectionPest.class,
					CamelNotationHelper.toSQLName("pest_type_id") + "<?",
					new String[] { String.valueOf("0") });
			if (mlst != null && mlst.size() > 0) {
				for (InspectionPest materialUsage : mlst) {
					if (materialUsage.pest_type_id == oldid) {
						materialUsage.pest_type_id = newid;
						materialUsage.save();
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

}
