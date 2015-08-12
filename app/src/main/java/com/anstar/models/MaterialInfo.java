package com.anstar.models;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MaterialInfo extends ActiveRecordBase {

	public MaterialInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "epa_number")
	public String epa_number = "";
	@ModelMapper(JsonKey = "name")
	public String name = "";
	@ModelMapper(JsonKey = "price")
	public String price = "";
	@ModelMapper(JsonKey = "default_dilution_rate_id")
	public String default_dilution_rate_id = "";
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";

	public static void AddMaterial(String name, String epa, String price) {
		try {
			MaterialInfo material = FieldworkApplication.Connection()
					.newEntity(MaterialInfo.class);
			material.id = Utils.getRandomInt();
			material.name = org.json.simple.JSONObject.escape(name);
			material.epa_number = org.json.simple.JSONObject.escape(epa);
			material.price = price;
			material.save();
			if (NetworkConnectivity.isConnected()) {
				material.UpdateMaterial(name, epa, price);
			}

		} catch (Exception e) {
		}
	}

	public void UpdateMaterial(String name, String epa, String price) {
		String json = "{\"epa_number\":\"" + epa + "\"," + "\"name\":\"" + name
				+ "\"," + "\"price\":\"" + price + "\"}";
		String url = "materials";
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				try {

					JSONObject obj = new JSONObject(res.RawResponse);
					// {"material":{"id":32,"name":"sam2","price":"122.0","created_at":"2013-03-09T05:21:27-06:00","updated_at":"2013-03-09T05:21:27-06:00","epa_number":"122","show_in_printed_forms":false}}
					JSONObject pt = obj.getJSONObject("material");
					int m_id = pt.getInt("id");
					id = m_id;
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Toast.makeText(FieldworkApplication.getContext(),
						"Material add successfully", Toast.LENGTH_LONG).show();
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				Toast.makeText(FieldworkApplication.getContext(), ErrorMessage,
						Toast.LENGTH_LONG).show();

			}
		});
	}

	public static MaterialInfo getMaterialById(int id) {
		try {

			List<MaterialInfo> info = FieldworkApplication.Connection().find(
					MaterialInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(id) });
			if (info != null && info.size() > 0) {
				return info.get(0);
			}
			return null;
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getMaterialNamebyId(int id) {

		try {
			List<MaterialInfo> list = FieldworkApplication.Connection()
					.findAll(MaterialInfo.class);
			for (MaterialInfo materialInfo : list) {
				if (materialInfo.id == id) {
					return materialInfo.name;
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
		return "";

	}

	public void sync(final UpdateInfoDelegate updateInfoDelegate) {

		String json = "{\"epa_number\":\"" + epa_number + "\"," + "\"name\":\""
				+ name + "\"," + "\"price\":\"" + price + "\"}";
		String url = "materials";
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				if (!res.isError()) {
					try {
						JSONObject obj = new JSONObject(res.RawResponse);
						JSONObject pest = obj.getJSONObject("material");
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
			String url = "materials";
			List<MaterialInfo> mlist = FieldworkApplication.Connection().find(
					MaterialInfo.class,
					CamelNotationHelper.toSQLName("id") + "<?",
					new String[] { String.valueOf(0) });
			if (mlist != null && mlist.size() > 0) {
				Utils.LogInfo("New Material in sync **** : "+mlist.size());
				for (MaterialInfo materialInfo : mlist) {
					String json = "{\"epa_number\":\""
							+ materialInfo.epa_number + "\"," + "\"name\":\""
							+ materialInfo.name + "\"," + "\"price\":\""
							+ materialInfo.price + "\"}";
					ServiceCallerSync caller = new ServiceCallerSync(url,
							ServiceCallerSync.RequestMethod.POST, json);
					ServiceResponse res = caller.startRequest();
					int oldid = materialInfo.id;
					if (!res.isError()) {
						JSONObject obj = new JSONObject(res.RawResponse);
						JSONObject pest = obj.getJSONObject("material");
						materialInfo.id = pest.getInt("id");
						materialInfo.save();
					}
					MaterialUsage.updateMaterialId(oldid, materialInfo.id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
