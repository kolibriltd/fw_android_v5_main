package com.anstar.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonCreator {

	public static void TEST() {
		List<HashMap<String, Object>> mainlist = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> innerlist = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> inner = new HashMap<String, Object>();
		inner.put("count", "1");
		inner.put("pest_type_id", "5");
		innerlist.add(inner);
		inner = new HashMap<String, Object>();
		inner.put("count", "2");
		inner.put("pest_type_id", "10");
		innerlist.add(inner);

		HashMap<String, Object> main = new HashMap<String, Object>();
		main.put("barcode", "111111");
		main.put("notes", "hello");
		main.put("pests_records", innerlist);
		mainlist.add(main);

		JSONArray arr = getJsonArray(mainlist);
		// JSONArray arr = new JSONArray(mainlist);
		try {
			Utils.LogInfo("JSON : => : " + arr.toString(4));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static JSONArray getJsonArray(List<HashMap<String, Object>> data) {
		JSONArray arr = new JSONArray();
		try {
			for (HashMap<String, Object> hashMap : data) {
				arr.put(getJsonObject(hashMap));
			}
		} catch (Exception ex) {
			Utils.LogException(ex);
		}

		return arr;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getJsonObject(HashMap<String, Object> data) {
		JSONObject obj = new JSONObject();
		try {
			for (String key : data.keySet()) {
				Object val;
				if (data.get(key) == null) {
					val = "";
				} else {
					val = data.get(key);
				}
				if (val.getClass() == HashMap.class) {
					obj.put(key, getJsonObject((HashMap<String, Object>) val));
				} else if (val.getClass() == ArrayList.class) {
					obj.put(key,
							getJsonArray((List<HashMap<String, Object>>) val));
				} else if (val.getClass() == int[].class) {
					JSONArray arr_val = getPrimitiveArray((int[]) val,
							int.class);
					obj.put(key, arr_val);
				} else {
					obj.put(key, val);
				}
			}
		} catch (Exception ex) {
			Utils.LogException(ex);
		}
		return obj;
	}

	private static JSONArray getPrimitiveArray(int[] arr, Class type) {
		JSONArray jarr = new JSONArray();
		for (int i : arr) {
			if (i > 0) {
				String vv = String.valueOf(i);
				jarr.put(Utils.ConvertToInt(vv));
			}
		}
		return jarr;
	}

}
