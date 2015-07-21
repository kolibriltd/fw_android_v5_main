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
import com.anstar.models.RecomendationInfo;

public class RecomendationsList implements ServiceHelperDelegate {

	private RecomendationsList() {

	}

	private static volatile RecomendationsList _instance = null;

	// private static volatile int cat_id = 0;

	public static RecomendationsList Instance() {
		if (_instance == null) {
			synchronized (RecomendationsList.class) {
				_instance = new RecomendationsList();
			}
		}
		return _instance;
	}

	protected ArrayList<RecomendationInfo> m_modelList = null;
	private ModelDelegate<RecomendationInfo> m_delegate = null;

	public void load(ModelDelegate<RecomendationInfo> delegate)
			throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.RECOMENDATIONS);
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
			FieldworkApplication.Connection().delete(RecomendationInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<RecomendationInfo> list = FieldworkApplication.Connection()
					.findAll(RecomendationInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<RecomendationInfo>(list);
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
				m_modelList = new ArrayList<RecomendationInfo>();
				JSONArray b_conditions = new JSONArray(res.RawResponse);
				if (b_conditions != null) {
					for (int j = 0; j < b_conditions.length(); j++) {
						JSONObject obj = b_conditions.getJSONObject(j);
						RecomendationInfo bc = FieldworkApplication
								.Connection()
								.newEntity(RecomendationInfo.class);
						bc.id = obj.optInt("id");
						bc.name = obj.getString("name");
						bc.save();
						m_modelList.add(bc);
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

	public ArrayList<RecomendationInfo> getSelectedRecList(ArrayList<String> ids) {
		ArrayList<RecomendationInfo> temp = new ArrayList<RecomendationInfo>();
		for (String id : ids) {
			for (RecomendationInfo recomendationInfo : m_modelList) {
				if(recomendationInfo.id == Integer.parseInt(id)){
					temp.add(recomendationInfo);
					break;
				}
			}
		}
		return temp;
	}
}
