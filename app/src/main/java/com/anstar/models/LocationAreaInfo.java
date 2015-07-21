package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

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

public class LocationAreaInfo extends ActiveRecordBase {

	public LocationAreaInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "name")
	public String name = "";
	public int Location_Type_id = 0;

	public UpdateInfoDelegate m_delegate = null;

	public static LocationAreaInfo getLocationAreaById(int id) {
		try {
			List<LocationAreaInfo> list = FieldworkApplication.Connection()
					.find(LocationAreaInfo.class,
							CamelNotationHelper.toSQLName("id") + "=?",
							new String[] { "" + id });
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<LocationAreaInfo> getAllLocationArea() {
		ArrayList<LocationAreaInfo> m_areas = new ArrayList<LocationAreaInfo>();
		try {
			List<LocationAreaInfo> list = FieldworkApplication.Connection()
					.findAll(LocationAreaInfo.class);
			if(list != null && list.size() > 0){
				m_areas.addAll(list);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_areas;
	}

	public static int getLocationIdByname(String name) {
		int id = 0;
		ArrayList<LocationAreaInfo> m_list = getAllLocationArea();
		for (LocationAreaInfo locationAreaInfo : m_list) {
			if (locationAreaInfo.name.equalsIgnoreCase(name)) {
				id = locationAreaInfo.id;
				break;
			}
		}
		return id;
	}

	public static String getLocationNameById(int id) {
		String name = "";
		ArrayList<LocationAreaInfo> m_list = getAllLocationArea();
		for (LocationAreaInfo locationAreaInfo : m_list) {
			if (locationAreaInfo.id == id) {
				return locationAreaInfo.name;
			}
		}
		return name;
	}

	public static ArrayList<LocationAreaInfo> getLocationAreaByType(int type_id) {
		int id = 0;
		ArrayList<LocationAreaInfo> m_list = getAllLocationArea();
		ArrayList<LocationAreaInfo> type_list = new ArrayList<LocationAreaInfo>();
		for (LocationAreaInfo locationAreaInfo : m_list) {
			if (locationAreaInfo.Location_Type_id == type_id) {
				type_list.add(locationAreaInfo);
			}
		}
		return type_list;
	}

	public static void AddLocationArea(String name, int locationType_Id,
			UpdateInfoDelegate delegate) {
		try {
			LocationAreaInfo location_area = FieldworkApplication.Connection()
					.newEntity(LocationAreaInfo.class);
			location_area.id = Utils.getRandomInt();
			location_area.name = org.json.simple.JSONObject.escape(name);
			location_area.Location_Type_id = locationType_Id;
			location_area.save();
			if (NetworkConnectivity.isConnected()) {
				location_area.UpdateLocationArea(name, locationType_Id,
						delegate);
			} else {
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 200;
				delegate.UpdateSuccessFully(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void UpdateLocationArea(String name, final int locationType_Id,
			UpdateInfoDelegate delegate) {
		m_delegate = delegate;
		String json = "{\"name\":\"" + name + "\"}";
		String url = "location_types/" + locationType_Id + "/location_areas";
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				try {
					JSONObject obj = new JSONObject(res.RawResponse);
					JSONObject area = obj.getJSONObject("location_area");
					id = area.getInt("id");
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				m_delegate.UpdateSuccessFully(res);
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				m_delegate.UpdateFail(ErrorMessage);

			}
		});
	}

	public void syncLocation(String name, int locationType_Id,
			UpdateInfoDelegate delegate, int index) {
		m_delegate = delegate;
		String json = "{\"name\":\"" + name + "\"}";
		String url = "location_types/" + locationType_Id + "/location_areas";
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.SetTag(index);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				try {
					JSONObject obj = new JSONObject(res.RawResponse);
					JSONObject area = obj.getJSONObject("location_area");
					id = area.getInt("id");
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}
				m_delegate.UpdateSuccessFully(res);
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				m_delegate.UpdateFail(ErrorMessage);

			}
		});
	}
	
	public static void sync1() {
		try {
			List<LocationAreaInfo> lst = FieldworkApplication.Connection().find(
					LocationAreaInfo.class,
					CamelNotationHelper.toSQLName("id") + "<?",
					new String[] { String.valueOf("0") });
			if (lst != null && lst.size() > 0) {
				Utils.LogInfo("New Location in sync **** : "+lst.size());
				for (LocationAreaInfo locInfo : lst) {
					String url = "location_types/" + locInfo.Location_Type_id + "/location_areas";
					String json = "{\"name\":\"" + locInfo.name + "\"}";
					ServiceCallerSync caller = new ServiceCallerSync(url,
							ServiceCallerSync.RequestMethod.POST, json);
					ServiceResponse res = caller.startRequest();
					int oldid = locInfo.id;
					if (!res.isError()) {
						JSONObject obj = new JSONObject(res.RawResponse);
						JSONObject loc = obj.getJSONObject("location_area");
						locInfo.id = loc.getInt("id");
						locInfo.save();
						MaterialUsage.updateLocationIds(oldid, locInfo.id);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
