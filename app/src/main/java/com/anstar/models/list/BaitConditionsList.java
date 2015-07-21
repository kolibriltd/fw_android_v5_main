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
import com.anstar.models.BaitConditionsInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;

public class BaitConditionsList implements ServiceHelperDelegate {

	private BaitConditionsList() {

	}

	private static volatile BaitConditionsList _instance = null;

	// private static volatile int cat_id = 0;

	public static BaitConditionsList Instance() {
		if (_instance == null) {
			synchronized (BaitConditionsList.class) {
				_instance = new BaitConditionsList();
			}
		}
		return _instance;
	}

	protected ArrayList<BaitConditionsInfo> m_modelList = null;
	private ModelDelegate<BaitConditionsInfo> m_delegate = null;

	public void load(ModelDelegate<BaitConditionsInfo> delegate)
			throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.BAIT_CONDITIONS);
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
			FieldworkApplication.Connection().delete(BaitConditionsInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<BaitConditionsInfo> list = FieldworkApplication.Connection()
					.findAll(BaitConditionsInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<BaitConditionsInfo>(list);
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
				m_modelList = new ArrayList<BaitConditionsInfo>();
				JSONArray b_conditions = new JSONArray(res.RawResponse);
				if (b_conditions != null) {
					for (int j = 0; j < b_conditions.length(); j++) {
						JSONObject obj = b_conditions.getJSONObject(j);
						BaitConditionsInfo bc = FieldworkApplication
								.Connection().newEntity(
										BaitConditionsInfo.class);
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

	public ArrayList<BaitConditionsInfo> getBaitConditionsList() {
		loadFromDB();
		if (m_modelList != null)
			return m_modelList;
		else {
			return new ArrayList<BaitConditionsInfo>();
		}
	}
}
