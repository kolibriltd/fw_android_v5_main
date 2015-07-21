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
import com.anstar.models.TrapTypesInfo;

public class TrapTypesList implements ServiceHelperDelegate {

	private TrapTypesList() {

	}

	private static volatile TrapTypesList _instance = null;

	// private static volatile int cat_id = 0;

	public static TrapTypesList Instance() {
		if (_instance == null) {
			synchronized (TrapTypesList.class) {
				_instance = new TrapTypesList();
			}
		}
		return _instance;
	}

	protected ArrayList<TrapTypesInfo> m_modelList = null;
	private ModelDelegate<TrapTypesInfo> m_delegate = null;

	public void load(ModelDelegate<TrapTypesInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.TRAP_TYPES);
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
			FieldworkApplication.Connection().delete(TrapTypesInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<TrapTypesInfo> list = FieldworkApplication.Connection()
					.findAll(TrapTypesInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<TrapTypesInfo>(list);
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
				m_modelList = new ArrayList<TrapTypesInfo>();
				JSONArray types = new JSONArray(res.RawResponse);
				if (types != null) {
					for (int j = 0; j < types.length(); j++) {
						JSONObject obj = types.getJSONObject(j);
						TrapTypesInfo tt = FieldworkApplication.Connection()
								.newEntity(TrapTypesInfo.class);
						tt.id = obj.optInt("id");
						tt.name = obj.getString("name");
						tt.save();
						m_modelList.add(tt);
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

	public ArrayList<TrapTypesInfo> getTrapTypesList() {
		loadFromDB();
		return m_modelList;
	}

	public int getTrapTypesInfoIdByname(String name) {
		int id = 0;
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {

			for (TrapTypesInfo d : m_modelList) {
				if (d.name.equalsIgnoreCase(name)) {
					id = d.id;
					break;
				}
			}
		}
		return id;

	}

	public String getTrapTypesInfoNameByid(int id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (TrapTypesInfo d : m_modelList) {
				if (d.id == id) {
					return d.name;
				}
			}
		}
		return "";

	}
}
