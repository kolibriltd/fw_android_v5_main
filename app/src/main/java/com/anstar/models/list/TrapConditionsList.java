package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.TrapConditionsInfo;

public class TrapConditionsList implements ServiceHelperDelegate {

	private TrapConditionsList() {

	}

	private static volatile TrapConditionsList _instance = null;

	// private static volatile int cat_id = 0;

	public static TrapConditionsList Instance() {
		if (_instance == null) {
			synchronized (TrapConditionsList.class) {
				_instance = new TrapConditionsList();
			}
		}
		return _instance;
	}

	protected ArrayList<TrapConditionsInfo> m_modelList = null;
	private ModelDelegate<TrapConditionsInfo> m_delegate = null;

	public void load(ModelDelegate<TrapConditionsInfo> delegate)
			throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.TRAP_CONDITIONS);
				helper.call(this);
			} else {
				m_delegate.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
			}
		} else {
			m_delegate.ModelLoaded(m_modelList);
		}
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(TrapConditionsInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<TrapConditionsInfo> list = FieldworkApplication.Connection()
					.findAll(TrapConditionsInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<TrapConditionsInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	@Override
	public void CallFinish(ServiceResponse res) {
		if (!res.isError()) {
			try {
				ClearDB();
				m_modelList = new ArrayList<TrapConditionsInfo>();
				JSONArray t_conditions = new JSONArray(res.RawResponse);
				if (t_conditions != null) {
					for (int j = 0; j < t_conditions.length(); j++) {
						JSONObject obj = t_conditions.getJSONObject(j);
						TrapConditionsInfo tc = FieldworkApplication
								.Connection().newEntity(
										TrapConditionsInfo.class);
						tc.id = obj.optInt("id");
						tc.name = obj.getString("name");
						tc.save();
						m_modelList.add(tc);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			m_delegate.ModelLoaded(m_modelList);
		} else {
			m_delegate.ModelLoadFailedWithError(res.getErrorMessage());

		}
	}

	@Override
	public void CallFailure(String ErrorMessage) {
		m_delegate.ModelLoadFailedWithError(ErrorMessage);
	}

	public ArrayList<TrapConditionsInfo> getTrapConditionsList() {
		loadFromDB();
		return m_modelList;
	}
}
