package com.anstar.models;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.internetbroadcast.ServiceCallerSync;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class PestsTypeInfo extends ActiveRecordBase {
	public PestsTypeInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "name")
	public String name;
	@ModelMapper(JsonKey = "created_at")
	public String created_at;
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at;

	public static PestsTypeInfo getPestTypeById(int id) {
		try {
			List<PestsTypeInfo> lst = FieldworkApplication.Connection().find(
					PestsTypeInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { "" + id });
			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void AddPestTypes(String name) {
		try {
			PestsTypeInfo pestType = FieldworkApplication.Connection()
					.newEntity(PestsTypeInfo.class);
			pestType.id = Utils.getRandomInt();
			pestType.name = org.json.simple.JSONObject.escape(name);
			pestType.save();
			if (NetworkConnectivity.isConnected()) {
				pestType.UpdatePestType(name);
			}
		} catch (Exception e) {
		}
	}

	public void UpdatePestType(String name) {
		String json = "{\"name\":\"" + name + "\"}";
		String url = "pest_types";
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				// {"pest_type":{"id":85,"name":"temp","created_at":"2013-03-09T04:47:13-06:00","updated_at":"2013-03-09T04:47:13-06:00","show_in_printed_forms":false}}
				try {
					JSONObject obj = new JSONObject(res.RawResponse);
					JSONObject pt = obj.getJSONObject("pest_type");
					int pt_id = pt.getInt("id");
					id = pt_id;
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Toast.makeText(FieldworkApplication.getContext(),
						"Pest add successfully", 1).show();
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
						1).show();

			}
		});
	}

	public void sync(final UpdateInfoDelegate updateInfoDelegate, int index) {
		String json = "{\"name\":\"" + name + "\"}";
		String url = "pest_types";
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.SetTag(index);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				if (!res.isError()) {
					try {
						JSONObject obj = new JSONObject(res.RawResponse);
						JSONObject pest = obj.getJSONObject("pest_type");
						id = pest.getInt("id");
						save();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}

				}
				updateInfoDelegate.UpdateSuccessFully(res);
			}

			@Override
			public void CallFailure(String ErrorMessage) {

			}
		});
	}

	public static void sync1() {
		try {
			String url = "pest_types";
			List<PestsTypeInfo> lst = FieldworkApplication.Connection().find(
					PestsTypeInfo.class,
					CamelNotationHelper.toSQLName("id") + "<?",
					new String[] { String.valueOf("0") });
			if (lst != null && lst.size() > 0) {
				Utils.LogInfo("New Pest in sync **** : " + lst.size());
				for (PestsTypeInfo pestInfo : lst) {
					String json = "{\"name\":\"" + pestInfo.name + "\"}";
					ServiceCallerSync caller = new ServiceCallerSync(url,
							ServiceCallerSync.RequestMethod.POST, json);
					ServiceResponse res = caller.startRequest();
					int oldid = pestInfo.id;
					if (!res.isError()) {
						JSONObject obj = new JSONObject(res.RawResponse);
						JSONObject pest = obj.getJSONObject("pest_type");
						pestInfo.id = pest.getInt("id");
						Utils.LogInfo("old pest id **** : " + oldid
								+ " New pest id :: " + pestInfo.id);
						pestInfo.save();
						MaterialUsage.updatePestIds(oldid, pestInfo.id);
						InspectionPest.updateInspectionpestId(oldid,
								pestInfo.id);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
