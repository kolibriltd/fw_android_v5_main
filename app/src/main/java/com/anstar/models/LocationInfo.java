package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapper;

public class LocationInfo extends ActiveRecordBase {

	public LocationInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "name")
	public String name = "";
	@ModelMapper(JsonKey = "color")
	public String color = "";
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";

	public ArrayList<LocationAreaInfo> loadLocationAreas() {
		ArrayList<LocationAreaInfo> m_modelList = null;
		try {
			List<LocationAreaInfo> list = FieldworkApplication.Connection()
					.findByColumn(LocationAreaInfo.class,
							CamelNotationHelper.toSQLName("Location_Type_id"),
							String.valueOf(this.id));
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<LocationAreaInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_modelList;
	}

}
