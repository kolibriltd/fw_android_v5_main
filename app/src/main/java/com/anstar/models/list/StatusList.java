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
import com.anstar.models.StatusInfo;

public class StatusList implements ServiceHelperDelegate {

	private StatusList() {

	}

	private static volatile StatusList _instance = null;

	// private static volatile int cat_id = 0;

	public static StatusList Instance() {
		if (_instance == null) {
			synchronized (StatusList.class) {
				_instance = new StatusList();
			}
		}
		return _instance;
	}

	protected ArrayList<StatusInfo> m_modelList = null;
	private ModelDelegate<StatusInfo> m_delegate = null;

	public void load(ModelDelegate<StatusInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(ServiceHelper.STATUSES);
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
			FieldworkApplication.Connection().delete(StatusInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<StatusInfo> list = FieldworkApplication.Connection().findAll(
					StatusInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<StatusInfo>(list);
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
				m_modelList = new ArrayList<StatusInfo>();
				JSONArray statuses = new JSONArray(res.RawResponse);
				if (statuses != null) {
					for (int j = 0; j < statuses.length(); j++) {
						JSONObject statuses_obj = statuses.getJSONObject(j);
						StatusInfo s = FieldworkApplication.Connection()
								.newEntity(StatusInfo.class);
						s.statusName = statuses_obj.getString("name");
						s.statusValue = statuses_obj.getString("value");
						s.save();
						m_modelList.add(s);
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

	public ArrayList<StatusInfo> getStatusList() {
		loadFromDB();
		return m_modelList;
	}
}
