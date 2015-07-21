package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.ModelDelegates.ModelDelegate;

public class ApplicationMethodInfo extends ActiveRecordBase {

	public ApplicationMethodInfo() {

	}
	public int id = 0;
	public String name = "";

	protected static ArrayList<ApplicationMethodInfo> m_modelList = null;
	private static ModelDelegate<ApplicationMethodInfo> m_delegate = null;

	public static void getMeasurements(
			ModelDelegate<ApplicationMethodInfo> delegate) throws Exception {

		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.APPLICATIONMETHOD);
				helper.call(new ServiceHelperDelegate() {

					@Override
					public void CallFinish(ServiceResponse res) {

						if (!res.isError()) {
							try {
								ClearDB();
								m_modelList = new ArrayList<ApplicationMethodInfo>();
								Thread.sleep(100);
								JSONArray arr = new JSONArray(res.RawResponse);
								if (arr != null) {
									for (int j = 0; j < arr.length(); j++) {
										JSONObject obj = arr.getJSONObject(j);
										ApplicationMethodInfo info = FieldworkApplication
												.Connection()
												.newEntity(
														ApplicationMethodInfo.class);
										info.id = obj.optInt("id");
										info.name = obj.optString("name");
										info.save();
										m_modelList.add(info);

									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								m_delegate.ModelLoadFailedWithError(res
										.getErrorMessage());
							}
							m_delegate.ModelLoaded(m_modelList);
						} else {
							m_delegate.ModelLoadFailedWithError(res
									.getErrorMessage());
						}
					}

					@Override
					public void CallFailure(String ErrorMessage) {
						m_delegate.ModelLoadFailedWithError(ErrorMessage);
					}
				});
			} else {
				m_delegate.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
			}
		}

		else {
			m_delegate.ModelLoaded(m_modelList);
		}
	}

	public static void loadFromDB() {
		try {
			List<ApplicationMethodInfo> list = FieldworkApplication
					.Connection().findAll(ApplicationMethodInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<ApplicationMethodInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public static void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(
					ApplicationMethodInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public ArrayList<ApplicationMethodInfo> getApplicationMethodList() {
		loadFromDB();
		return m_modelList;
	}
	

	public static int getMethodIdByname(String name) {
		int id = 0;
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {

			for (ApplicationMethodInfo d : m_modelList) {
				if (d.name.equalsIgnoreCase(name)) {
					id = d.id;
					break;
				}
			}
		}
		return id;

	}
}
