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
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class MaterialUsagesList {

	private MaterialUsagesList() {

	}

	private static volatile MaterialUsagesList _instance = null;
	public static String MATERIAL_USAGE_LIST_NOTIFICATION = "MATERIAL_USAGE_LIST_NOTIFICATION";

	// private static volatile int cat_id = 0;

	public static MaterialUsagesList Instance() {
		if (_instance == null) {
			synchronized (MaterialUsagesList.class) {
				_instance = new MaterialUsagesList();
			}
		}
		return _instance;
	}

	protected ArrayList<MaterialUsage> m_modelList = null;

	public void refreshMaterialUsage(final int appt_id,
			final UpdateInfoDelegate delegate) {
		String url = String.format("work_orders/%d/material_usages", appt_id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.GET, null);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {

				try {
					deleteMaterialUsage(appt_id);
					JSONArray mus = new JSONArray(res.RawResponse);

					for (int i = 0; i < mus.length(); i++) {
						JSONObject usages = mus.getJSONObject(i);
						ModelMapHelper<MaterialUsage> maper = new ModelMapHelper<MaterialUsage>();
						MaterialUsage info = maper.getObject(
								MaterialUsage.class, usages);
						if (usages.toString()
								.contains("material_usage_records")) {
							JSONArray arr = usages
									.getJSONArray("material_usage_records");
							MaterialUsagesRecordsList.Instance()
									.parseMaterialUsageRecordsList(arr,
											info.id, appt_id);
						}
						if (info != null) {
							try {
								info.AppointmentId = appt_id;
								info.save();
							} catch (Exception e) {
							}
						}
					}
					if (delegate != null) {
						delegate.UpdateSuccessFully(res);
						NotificationCenter.Instance().postNotification(
								MATERIAL_USAGE_LIST_NOTIFICATION);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				if (delegate != null)
					delegate.UpdateFail(ErrorMessage);
			}
		});
	}

	public void parseMatrialUsages(JSONObject obj, int a_id,
			boolean isForInspection) {

		JSONArray Invoice;
		try {
			Invoice = obj.getJSONArray("material_usages");
			if (Invoice != null) {
				for (int i = 0; i < Invoice.length(); i++) {
					JSONObject usages = Invoice.getJSONObject(i);
					ModelMapHelper<MaterialUsage> maper = new ModelMapHelper<MaterialUsage>();
					MaterialUsage info = maper.getObject(MaterialUsage.class,
							usages);
					if (usages.toString().contains("material_usage_records")) {
						JSONArray arr = usages
								.getJSONArray("material_usage_records");
						MaterialUsagesRecordsList.Instance()
								.parseMaterialUsageRecordsList(arr, info.id,
										a_id);
					}
					if (info != null) {
						try {
							MaterialUsage temp = MaterialUsage
									.getMaterialUsageById(info.id);
							if (temp != null) {
								if (temp.equals(info)) {
									info.copyFrom(temp);
									info.isForInspection = false;
								} else {
									info.isForInspection = isForInspection;
								}
							} else {
								info.isForInspection = isForInspection;
							}
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
			FieldworkApplication.Connection().delete(MaterialUsage.class);
			FieldworkApplication.Connection()
					.delete(MaterialUsageRecords.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			Utils.LogInfo("Delete material by appt id" + appid);
			List<MaterialUsage> lst = FieldworkApplication.Connection().find(
					MaterialUsage.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (MaterialUsage i : lst) {
					ArrayList<MaterialUsageRecords> m_list = MaterialUsagesRecordsList
							.Instance().getMaterialRecordsByUsageId(i.id);
					if (m_list != null && m_list.size() > 0) {
						for (MaterialUsageRecords mur : m_list) {
							mur.delete();
						}
					}
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void deleteMaterialUsage(int appt_id) {
		try {
			List<MaterialUsage> list = FieldworkApplication.Connection().find(
					MaterialUsage.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(appt_id) });

			for (MaterialUsage materialUsage : list) {
				MaterialUsagesRecordsList.Instance()
						.deleteMaterialUsageRecords(materialUsage.id);
			}

			int cnt = FieldworkApplication.Connection().delete(
					MaterialUsage.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(appt_id) });

			Utils.LogInfo(String.format(
					"%d records deleted of material usage for appt %d", cnt,
					appt_id));
		} catch (Exception ex) {

		}
	}

	public ArrayList<MaterialUsage> load(int app_id) {
		ArrayList<MaterialUsage> m_list = new ArrayList<MaterialUsage>();
		try {
			List<MaterialUsage> list = FieldworkApplication.Connection()
					.find(MaterialUsage.class,
							CamelNotationHelper.toSQLName("AppointmentId")
									+ "=? and "
									+ CamelNotationHelper
											.toSQLName("isDeleted") + "=?",
							new String[] { String.valueOf(app_id),
									String.valueOf(false) });

			if (list != null) {
				if (list.size() > 0) {
					for (MaterialUsage materialUsage : list) {
						if (!materialUsage.isForInspection) {
							m_list.add(materialUsage);
						}
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public ArrayList<MaterialUsage> loadAll(int app_id) {
		ArrayList<MaterialUsage> m_list = new ArrayList<MaterialUsage>();
		try {
			List<MaterialUsage> list = FieldworkApplication.Connection().find(
					MaterialUsage.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(app_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (MaterialUsage materialUsage : list) {
						// if (!materialUsage.isDeleted) {
						if (!materialUsage.isForInspection) {
							m_list.add(materialUsage);
						}
						// }
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}
}
