package com.anstar.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.common.Utils;

public class SignaturePoints extends ActiveRecordBase {

	public SignaturePoints() {

	}

	public String Type = "";
	public float lx = 0f;
	public float ly = 0f;
	public float mx = 0f;
	public float my = 0f;

	public static ArrayList<SignaturePoints> parse(String points, String type) {
		ArrayList<SignaturePoints> signInfo = new ArrayList<SignaturePoints>();
		try {
			JSONArray arr = new JSONArray(points);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject jsonobj = arr.getJSONObject(i);

				SignaturePoints point = new SignaturePoints();
				point.lx = Utils.ConvertToFloat(jsonobj.optString("lx"));
				point.ly = Utils.ConvertToFloat(jsonobj.optString("ly"));
				point.mx = Utils.ConvertToFloat(jsonobj.optString("mx"));
				point.my = Utils.ConvertToFloat(jsonobj.optString("my"));
				point.Type = type;
				signInfo.add(point);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signInfo;
	}
}
