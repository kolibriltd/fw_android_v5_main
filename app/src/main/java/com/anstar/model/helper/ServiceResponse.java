package com.anstar.model.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceResponse {

	// {"result":{"code":1002,"error":"Wrong Username and password combination."}}

	public String RawResponse;
	public boolean isError = false;
	public String ErrorMessage = "";
	public int Tag = 0;
	public int StatusCode = 200;
	public Object obj = null;

	public boolean isError() {
		boolean flag = false;
		// String error = "";
		if (StatusCode == 200 || StatusCode == 204 || StatusCode == 201) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
		// try {
		// JSONObject main = new JSONObject(RawResponse);
		// JSONObject result = main.getJSONObject("result");
		// error = result.optString("error");
		// } catch (JSONException e) {
		// return false;
		// }
		// if (error.length() > 1) {
		// return true;
		// }
		// return false;
	}

	public String getErrorMessage() {
		try {
			JSONObject main = new JSONObject(RawResponse);
			JSONObject result = main.getJSONObject("result");
			return result.optString("error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return RawResponse;
	}

	public String GetSuccessMessage() {
		try {
			JSONObject main = new JSONObject(RawResponse);
			JSONObject result = main.getJSONObject("result");
			return result.optString("success");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

}
