package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.ModelDelegates.ModelDelegate;

public class MeasurementInfo extends ActiveRecordBase {

	public MeasurementInfo() {

	}

	public String name = "";

	protected static ArrayList<MeasurementInfo> m_modelList = null;
	private static ModelDelegate<MeasurementInfo> m_delegate = null;

	public static void getMeasurements(ModelDelegate<MeasurementInfo> delegate)
			throws Exception {

		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.MEASUREMENTS);
				helper.call(new ServiceHelperDelegate() {

					@Override
					public void CallFinish(ServiceResponse res) {

						if (!res.isError()) {

							try {
								ClearDB();
								m_modelList = new ArrayList<MeasurementInfo>();
								Thread.sleep(100);
								JSONArray measurements = new JSONArray(
										res.RawResponse);
								if (measurements != null) {
									for (int j = 0; j < measurements.length(); j++) {
										MeasurementInfo info = FieldworkApplication
												.Connection().newEntity(
														MeasurementInfo.class);
										info.name = measurements.getString(j);
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
			List<MeasurementInfo> list = FieldworkApplication.Connection()
					.findAll(MeasurementInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<MeasurementInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public static void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(MeasurementInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public ArrayList<MeasurementInfo> getMeasurementList() {
		loadFromDB();
		return m_modelList;
	}

}
