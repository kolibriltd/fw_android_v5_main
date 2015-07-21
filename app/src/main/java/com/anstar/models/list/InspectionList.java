package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.InspectionInfo;
import com.anstar.models.InspectionPest;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class InspectionList {

	private InspectionList() {

	}

	private static volatile InspectionList _instance = null;

	public static InspectionList Instance() {
		if (_instance == null) {
			synchronized (InspectionList.class) {
				_instance = new InspectionList();
			}
		}
		return _instance;
	}

	public void refreshInspectionList(final int app_id,
			final UpdateInfoDelegate delegate) {
		String url = String.format("work_orders/%d/inspection_records", app_id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.GET, null);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {

				try {
					ClearDB();
					JSONArray ins = new JSONArray(res.RawResponse);

					for (int i = 0; i < ins.length(); i++) {
						JSONObject inspection = ins.getJSONObject(i);
						ModelMapHelper<InspectionInfo> maper = new ModelMapHelper<InspectionInfo>();
						InspectionInfo info = maper.getObject(
								InspectionInfo.class, inspection);
						if (inspection.toString().contains("pests_records")) {
							JSONArray arr = inspection
									.getJSONArray("pests_records");
							InspectionPestsList.Instance()
									.parseInspectionPestsList(arr, info.id);
						}
						if (inspection.toString().contains("material_usages")) {
							MaterialUsagesList.Instance().parseMatrialUsages(
									inspection, app_id, true);
							JSONArray arr = inspection
									.getJSONArray("material_usages");
							if (arr != null) {
								ArrayList<String> id = new ArrayList<String>();
								for (int j = 0; j < arr.length(); j++) {
									JSONObject material = arr.getJSONObject(j);
									id.add(material.optString("id"));
								}
								info.Material_ids = Utils.Instance().join(id,
										",");
							}
						}
						if (info != null) {
							try {
								info.AppointmentId = app_id;
								info.save();
							} catch (Exception e) {
							}
						}
					}
					if (delegate != null)
						delegate.UpdateSuccessFully(res);

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

	public void parseInspectionRecords(JSONObject obj, int a_id) {

		JSONArray Invoice;
		try {
			Invoice = obj.getJSONArray("inspection_records");
			if (Invoice != null) {
				for (int i = 0; i < Invoice.length(); i++) {
					JSONObject usages = Invoice.getJSONObject(i);
					ModelMapHelper<InspectionInfo> maper = new ModelMapHelper<InspectionInfo>();
					InspectionInfo info = maper.getObject(InspectionInfo.class,
							usages);
					if (usages.toString().contains("pests_records")) {
						JSONArray arr = usages.getJSONArray("pests_records");
						InspectionPestsList.Instance()
								.parseInspectionPestsList(arr, info.id);
					}
					if (usages.toString().contains("material_usages")) {
						MaterialUsagesList.Instance().parseMatrialUsages(
								usages, a_id, true);
						JSONArray arr = usages.getJSONArray("material_usages");
						if (arr != null) {
							ArrayList<String> id = new ArrayList<String>();
							for (int j = 0; j < arr.length(); j++) {
								JSONObject material = arr.getJSONObject(j);
								id.add(material.optString("id"));
							}
							info.Material_ids = Utils.Instance().join(id, ",");
						}
					}
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
			FieldworkApplication.Connection().delete(InspectionInfo.class);
			FieldworkApplication.Connection().delete(InspectionPest.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<InspectionInfo> lst = FieldworkApplication.Connection().find(
					InspectionInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (InspectionInfo i : lst) {
					ArrayList<InspectionPest> m_list = InspectionPestsList
							.Instance().getInspectionPestByInspectionId(i.id);
					if (m_list != null && m_list.size() > 0) {
						for (InspectionPest mur : m_list) {
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

	public ArrayList<InspectionInfo> loadWhereDeletedisFalse(int app_id) {
		ArrayList<InspectionInfo> m_list = new ArrayList<InspectionInfo>();
		try {
			List<InspectionInfo> list = FieldworkApplication.Connection()
					.find(InspectionInfo.class,
							CamelNotationHelper.toSQLName("AppointmentId")
									+ "=? and "
									+ CamelNotationHelper
											.toSQLName("isDeleted") + "=?",
							new String[] { String.valueOf(app_id),
									String.valueOf(false) });

			// FieldworkApplication.Connection()
			// .findAll(InspectionInfo.class);

			if (list != null) {
				if (list.size() > 0) {
					m_list = new ArrayList<InspectionInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public ArrayList<InspectionInfo> loadAll(int app_id) {
		ArrayList<InspectionInfo> m_list = new ArrayList<InspectionInfo>();
		try {
			List<InspectionInfo> list = FieldworkApplication.Connection().find(
					InspectionInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(app_id) });
			// FieldworkApplication.Connection()
			// .findAll(InspectionInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_list = new ArrayList<InspectionInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public InspectionInfo getInspectionByApp_Id(int app_id, String barcode) {
		try {
			List<InspectionInfo> list = FieldworkApplication.Connection().find(
					InspectionInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=? and "
							+ CamelNotationHelper.toSQLName("barcode") + "=?",
					new String[] { String.valueOf(app_id), barcode });
			if (list != null && list.size() > 0) {
				for (InspectionInfo inspectionInfo : list) {
					if (inspectionInfo.isDeleted == false) {
						return inspectionInfo;
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return null;
	}

	public InspectionInfo getInspectionById(int id) {
		try {
			List<InspectionInfo> list = FieldworkApplication.Connection().find(
					InspectionInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(id) });
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return null;
	}

}
