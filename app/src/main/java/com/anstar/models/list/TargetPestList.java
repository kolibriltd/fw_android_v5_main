package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.TargetPestInfo;

public class TargetPestList {

	public TargetPestList() {

	}

	public static String TARGET_PEST_LIST_NOTIFICATION = "TARGET_PEST_LIST_NOTIFICATION";

	private static volatile TargetPestList _instance = null;

	public static TargetPestList Instance() {
		if (_instance == null) {
			synchronized (TargetPestList.class) {
				_instance = new TargetPestList();
			}
		}
		return _instance;
	}

	protected ArrayList<TargetPestInfo> m_modelList = null;

	public void refreshTragetPests(final int appt_id,
			final UpdateInfoDelegate delegate) {
		String url = String.format("work_orders/%d/pests_targets", appt_id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.GET, null);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {

				try {
					deleteTargetPests(appt_id);
					JSONArray pests = new JSONArray(res.RawResponse);
					for (int i = 0; i < pests.length(); i++) {
						JSONObject pest = pests.getJSONObject(i);
						ModelMapHelper<TargetPestInfo> maper = new ModelMapHelper<TargetPestInfo>();
						TargetPestInfo info = maper.getObject(
								TargetPestInfo.class, pest);
						if (info != null) {
							try {
								info.AppointmentId = appt_id;
								info.save();
							} catch (Exception e) {
							}
						}
					}
					delegate.UpdateSuccessFully(res);
					NotificationCenter.Instance().postNotification(
							TARGET_PEST_LIST_NOTIFICATION);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				delegate.UpdateFail(ErrorMessage);
			}
		});
	}

	public void parseTargetPests(JSONObject obj, int a_id) {

		JSONArray pests;
		try {
			pests = obj.getJSONArray("pests_targets");
			if (pests != null) {
				for (int i = 0; i < pests.length(); i++) {
					JSONObject pest = pests.getJSONObject(i);
					ModelMapHelper<TargetPestInfo> maper = new ModelMapHelper<TargetPestInfo>();
					TargetPestInfo info = maper.getObject(TargetPestInfo.class,
							pest);
					if (info != null) {
						try {
							info.AppointmentId = a_id;
							info.save();
						} catch (Exception e) {
						}
					}
				}

			}

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(TargetPestInfo.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<TargetPestInfo> lst = FieldworkApplication.Connection().find(
					TargetPestInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (TargetPestInfo i : lst) {
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	// public void refresh(int appointment_id) throws Exception {
	// m_appointmentId = appointment_id;
	// if (NetworkConnectivity.isConnected()) {
	// ServiceHelper helper = new ServiceHelper(
	// ServiceHelper.APPOINTMENT_OCCURENCES + "/" + appointment_id
	// + "/" + ServiceHelper.PEST_TARGETS);
	// helper.call(this);
	// }
	// }

	public ArrayList<TargetPestInfo> load(int app_id) {
		ArrayList<TargetPestInfo> m_list = new ArrayList<TargetPestInfo>();
		try {
			List<TargetPestInfo> list = FieldworkApplication.Connection().find(
					TargetPestInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(app_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (TargetPestInfo targetPestInfo : list) {
						if (!targetPestInfo.isDeleted) {
							m_list.add(targetPestInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public ArrayList<TargetPestInfo> loadAll(int app_id) {
		ArrayList<TargetPestInfo> m_list = new ArrayList<TargetPestInfo>();
		try {
			List<TargetPestInfo> list = FieldworkApplication.Connection().find(
					TargetPestInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(app_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (TargetPestInfo targetPestInfo : list) {
						m_list.add(targetPestInfo);
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public void deleteTargetPests(int appt_id) {
		try {
			int cnt = FieldworkApplication.Connection().delete(
					TargetPestInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(appt_id) });
			Utils.LogInfo(String.format(
					"%d records deleted of target pests for appt %d", cnt,
					appt_id));
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	// public TargetPestInfo getMaterialByIdHiren(int material_id) {
	// if (m_modelList == null) {
	// // loadFromDB();
	// }
	// if (m_modelList != null) {
	// for (TargetPestInfo info : m_modelList) {
	// if (info.id == material_id) {
	// return info;
	// }
	// }
	// }
	// return null;
	// }
}
