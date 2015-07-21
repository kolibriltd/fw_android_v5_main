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
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ModelProcess;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class TargetPestInfo extends ActiveRecordBase {
	public TargetPestInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "pest_type_id")
	public int pest_type_id = 0;
	@ModelMapper(JsonKey = "work_order_id")
	public int AppointmentId = 0;

	public PestsTypeInfo m_pest_info = null;

	public boolean isDeleted = false;

	private UpdateInfoDelegate m_delegate = null;

	public void AddTargetPests(UpdateInfoDelegate del) {
		try {
			List<TargetPestInfo> lst = FieldworkApplication.Connection().find(
					TargetPestInfo.class,
					CamelNotationHelper.toSQLName("pest_type_id") + "=?",
					new String[] { String.valueOf(this.pest_type_id) });
			if (lst != null) {
				if (lst.size() > 0) {
					for (TargetPestInfo targetPestInfo : lst) {
						if (targetPestInfo.AppointmentId == this.AppointmentId) {
							del.UpdateFail("");
							return;
						}
					}
				}
			}
			this.save();
			// TargetPestInfo target_pest = FieldworkApplication.Connection()
			// .newEntity(TargetPestInfo.class);
			// target_pest.id = Utils.getRandomInt();
			// target_pest.pest_type_id = pest_id;
			// target_pest.AppointmentId = appointment_id;
			// target_pest.save();
			if (NetworkConnectivity.isConnected()) {
				UpdateService(ModelProcess.Insert, del);
			} else {
				del.UpdateFail("");
			}
		} catch (Exception e) {
		}
	}

	public void UpdateService(ServiceHelper.ModelProcess process,
			UpdateInfoDelegate delegate) {
		if (delegate != null) {
			m_delegate = delegate;
		}
		if (process == ModelProcess.Insert) {
			String json = "{\"pest_type_id\":%d}";
			// String json =
			// "{\"appointment_occurrence\":{\"pests_targets_attributes\":[{\"pest_type_id\":%d}]}}";
			String url = String.format("work_orders/%d/pests_targets",
					this.AppointmentId);
			json = String.format(json, this.pest_type_id);

			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.POST, json);
			caller.startRequest(new ServiceHelperDelegate() {

				@Override
				public void CallFinish(ServiceResponse res) {
					if (!res.isError()) {
						if (res.RawResponse != null
								&& res.RawResponse.length() > 0) {
							try {
								JSONObject obj = new JSONObject(res.RawResponse);
								if (obj != null) {
									TargetPestInfo.this.id = obj.getInt("id");
									TargetPestInfo.this.pest_type_id = obj
											.getInt("pest_type_id");
									TargetPestInfo.this.save();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (ActiveRecordException e) {
								e.printStackTrace();
							}
						}
					}
					m_delegate.UpdateSuccessFully(res);
				}

				@Override
				public void CallFailure(String ErrorMessage) {
					m_delegate.UpdateFail(ErrorMessage);

				}
			});

		} else if (process == ModelProcess.Delete) {
			if (this.id == -1) {
				return;
			}
			String json = "{\"work_order\":{\"pests_targets_attributes\":[{\"id\":%d,\"_destroy\":true}]}}";
			String url = String.format("work_orders/%d", this.AppointmentId);
			json = String.format(json, this.id);
			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.PUT, json);
			caller.startRequest(new ServiceHelperDelegate() {

				@Override
				public void CallFinish(ServiceResponse res) {
					m_delegate.UpdateSuccessFully(res);
				}

				@Override
				public void CallFailure(String ErrorMessage) {
					m_delegate.UpdateFail(ErrorMessage);
				}
			});
		}

		// URL :
		// http://api.fieldworkapp.com/appointment_occurrences/172?api_key=368c73060720e0f178095e897c959aa0952cdb7ac198db0b

		// PUT

		// DATA :
		// {"appointment_occurrence":{"pests_targets_attributes":[{"pest_type_id":10}]}}

		/*
		 * ou can either send direct DELETE request to
		 * /appointment_occurrences/1/pests_targets/2 to delete pests_target
		 * with id 2. Or send PUT request to /appointment_occurrences/1 with
		 * "pests_targets_attributes": [{"id": 2, "_destroy":true}]
		 */

	}

	public static void DeleteTargetPests(int id, UpdateInfoDelegate del) {
		try {
			List<TargetPestInfo> lst = FieldworkApplication.Connection().find(
					TargetPestInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(id) });
			if (lst != null) {
				if (lst.size() > 0) {
					if (lst.get(0).id < 0) {
						if (NetworkConnectivity.isConnected()) {
							lst.get(0).delete();
						} else {
							lst.get(0).isDeleted = true;
							lst.get(0).save();
						}
						ServiceResponse res = new ServiceResponse();
						res.StatusCode = 200;
						del.UpdateSuccessFully(res);
					} else {
						lst.get(0).isDeleted = true;
						lst.get(0).save();
						if (NetworkConnectivity.isConnected()) {
							lst.get(0).UpdateService(ModelProcess.Delete, del);
						} else {
							ServiceResponse res = new ServiceResponse();
							res.StatusCode = 200;
							del.UpdateSuccessFully(res);
						}
					}
				}
			} else {
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 1;
				del.UpdateFail("");
			}
		} catch (Exception e) {
		}
	}

	private ServiceHelperDelegate m_service_delegate = new ServiceHelperDelegate() {

		@Override
		public void CallFinish(ServiceResponse res) {
			if (!res.isError()) {
				if (res.RawResponse != null && res.RawResponse.length() > 0) {
					try {
						JSONObject obj = new JSONObject(res.RawResponse);
						if (obj != null) {
							TargetPestInfo.this.id = obj.getInt("id");
							TargetPestInfo.this.pest_type_id = obj
									.getInt("pest_type_id");
							TargetPestInfo.this.save();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}
				}
			}
			//AppointmentInfo.refresh();

		}

		@Override
		public void CallFailure(String ErrorMessage) {
		}
	};

	public void sync() {
		if (this.pest_type_id < 0) {
			this.m_pest_info.sync(new UpdateInfoDelegate() {

				@Override
				public void UpdateSuccessFully(ServiceResponse res) {
					pest_type_id = m_pest_info.id;
					String json = "{\"pest_type_id\":%d}";
					// String json =
					// "{\"appointment_occurrence\":{\"pests_targets_attributes\":[{\"pest_type_id\":%d}]}}";
					String url = String.format("work_orders/%d/pests_targets",
							AppointmentId);
					json = String.format(json, m_pest_info.id);
					Utils.LogInfo("JSON ON SYNC + TARGET PEST INFO *********************::: "
							+ json);
					ServiceCaller caller = new ServiceCaller(url,
							ServiceCaller.RequestMethod.POST, json);
					caller.startRequest(m_service_delegate);
				}

				@Override
				public void UpdateFail(String ErrorMessage) {

				}
			}, 0);
		} else {
			String json = "{\"work_order\":{\"pests_targets_attributes\":[{\"pest_type_id\":%d}]}}";
			String url = String.format("work_orders/%d", AppointmentId);
			json = String.format(json, pest_type_id);

			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.PUT, json);
			caller.startRequest(m_service_delegate);

		}
	}

	public void syncDelete() {
		String json = "{\"work_order\":{\"pests_targets_attributes\":[{\"id\":%d,\"_destroy\":true}]}}";
		String url = String.format("work_orders/%d", this.AppointmentId);
		json = String.format(json, this.id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.PUT, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				// m_delegate.UpdateSuccessFully(res);
				try {
					delete();
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				// m_delegate.UpdateFail(ErrorMessage);
			}
		});
	}

}
