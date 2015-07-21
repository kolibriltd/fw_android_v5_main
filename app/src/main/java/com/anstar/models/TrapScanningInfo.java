package com.anstar.models;

import java.util.List;

import org.json.JSONException;
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

public class TrapScanningInfo extends ActiveRecordBase {
	public TrapScanningInfo() {

	}

	@ModelMapper(JsonKey = "id", IsUnique = true)
	public int id = 0;
	@ModelMapper(JsonKey = "barcode")
	public String barcode = "";
	@ModelMapper(JsonKey = "building")
	public String building = "";
	@ModelMapper(JsonKey = "customer_id")
	public int customer_id = 0;
	@ModelMapper(JsonKey = "floor")
	public String floor = "";
	@ModelMapper(JsonKey = "location_details")
	public String location_details = "";
	@ModelMapper(JsonKey = "service_location_id")
	public int service_location_id = 0;
	@ModelMapper(JsonKey = "notes")
	public String notes = "";
	@ModelMapper(JsonKey = "number")
	public String number = "";
	@ModelMapper(JsonKey = "trap_type_id")
	public int trap_type_id = 0;
	@ModelMapper(JsonKey = "service_frequency")
	public String service_frequency = "";

	public boolean isChecked = false;

	// private UpdateInfoDelegate m_delegate = null;

	public static void AddTraps(String number, int trap_tyape_id, int c_id,
			String barcode, String building, String floor, String location,
			int service_location_id, UpdateInfoDelegate del) {

		try {
			TrapScanningInfo t = FieldworkApplication.Connection().newEntity(
					TrapScanningInfo.class);
			t.id = Utils.getRandomInt();
			t.barcode = barcode;
			t.building = building;
			t.floor = floor;
			t.number = number;
			t.trap_type_id = trap_tyape_id;
			t.service_location_id = service_location_id;
			t.location_details = location;
			t.customer_id = c_id;
			t.save();
			if (NetworkConnectivity.isConnected()) {
				t.UpdateService(del);
			} else {
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 200;
				del.UpdateSuccessFully(res);
			}
		} catch (Exception e) {
		}
	}

	public void UpdateService(final UpdateInfoDelegate delegate) {
		String json = "{\"barcode\":\"%s\",\"building\":\"%s\",\"floor\":\"%s\",\"location_details\":\"%s\",\"number\":\"%s\",\"trap_type_id\":%d}";
		String url = String.format("customers/%d/service_locations/%d/devices",
				this.customer_id, this.service_location_id);
		json = String.format(json, this.barcode, this.building, this.floor,
				this.location_details, this.number, this.trap_type_id);

		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.POST, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				if (!res.isError()) {
					if (res.RawResponse != null && res.RawResponse.length() > 0) {
						try {
							JSONObject obj = new JSONObject(res.RawResponse);
							JSONObject device = obj.getJSONObject("device");
							if (device != null) {
								id = 0;
								id = device.optInt("id");
								save();
								delegate.UpdateSuccessFully(res);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}
					} else {
						id = (id * -1);
						try {
							save();
						} catch (ActiveRecordException e) {
							e.printStackTrace();
						}
						delegate.UpdateSuccessFully(res);
					}
				} else {
					id = (id * -1);
					try {
						save();
					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}
					delegate.UpdateSuccessFully(res);
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				delegate.UpdateFail(ErrorMessage);
			}
		});
	}

	public void syncTrap() {
		UpdateService(new UpdateInfoDelegate() {

			@Override
			public void UpdateSuccessFully(ServiceResponse res) {

			}

			@Override
			public void UpdateFail(String ErrorMessage) {
			}
		});

	}

	public static void sync1() {
		try {
			List<TrapScanningInfo> lst = FieldworkApplication.Connection()
					.find(TrapScanningInfo.class,
							CamelNotationHelper.toSQLName("id") + "<?",
							new String[] { String.valueOf("0") });
			if (lst != null && lst.size() > 0) {
				Utils.LogInfo("New Trp Scanning in sync **** : " + lst.size());
				for (TrapScanningInfo trapInfo : lst) {
					String url = String.format(
							"customers/%d/service_locations/%d/devices",
							trapInfo.customer_id, trapInfo.service_location_id);
					String json = "{\"barcode\":\"%s\",\"building\":\"%s\",\"floor\":\"%s\",\"location_details\":\"%s\",\"number\":\"%s\",\"trap_type_id\":%d}";
					json = String.format(json, trapInfo.barcode,
							trapInfo.building, trapInfo.floor,
							trapInfo.location_details, trapInfo.number,
							trapInfo.trap_type_id);
					ServiceCallerSync caller = new ServiceCallerSync(url,
							ServiceCallerSync.RequestMethod.POST, json);
					ServiceResponse res = caller.startRequest();
					int oldid = trapInfo.id;
					if (!res.isError()) {
						JSONObject obj = new JSONObject(res.RawResponse);
						JSONObject pest = obj.getJSONObject("device");
						trapInfo.id = pest.getInt("id");
						trapInfo.save();
						InspectionInfo.updateTrapIds(oldid, trapInfo.id);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
