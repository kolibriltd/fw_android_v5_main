package com.anstar.models.list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.LocationAreaInfo;

public class LocationAreaList {

	public void parseLocationArea(JSONObject obj, int id) {
		JSONArray Location_areas;
		try {
			Location_areas = obj.getJSONArray("location_areas");
			for (int i = 0; i < Location_areas.length(); i++) {
				JSONObject area = Location_areas.getJSONObject(i);
				ModelMapHelper<LocationAreaInfo> areamaper = new ModelMapHelper<LocationAreaInfo>();
				LocationAreaInfo info = areamaper.getObject(
						LocationAreaInfo.class, area);
				if (info != null) {
					try {
						info.Location_Type_id = id;
						info.save();
					} catch (Exception e) {
					}
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

	}

}
