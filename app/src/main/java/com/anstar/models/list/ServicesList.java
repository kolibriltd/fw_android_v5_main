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
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ServicesInfo;

public class ServicesList implements ServiceHelperDelegate {

	private ServicesList() {

	}

	private static volatile ServicesList _instance = null;

	// private static volatile int cat_id = 0;

	public static ServicesList Instance() {
		if (_instance == null) {
			synchronized (ServicesList.class) {
				_instance = new ServicesList();
				// _instance.m_modelList = new ArrayList<CategoryItemInfo>();
			}
		}
		return _instance;
	}

	protected ArrayList<ServicesInfo> m_modelList = null;
	private ModelDelegate<ServicesInfo> m_delegate = null;

	public void load(ModelDelegate<ServicesInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null || m_modelList.size() <= 0) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.SERVICES);
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
			FieldworkApplication.Connection().delete(ServicesInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<ServicesInfo> list = FieldworkApplication.Connection().findAll(
					ServicesInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<ServicesInfo>(list);
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
				m_modelList = new ArrayList<ServicesInfo>();
				JSONArray subjectList = new JSONArray(res.RawResponse);
				for (int i = 0; i < subjectList.length(); i++) {
					JSONObject data = subjectList.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<ServicesInfo> mapper = new ModelMapHelper<ServicesInfo>();
						ServicesInfo info = mapper.getObject(ServicesInfo.class, data);
						if (info != null) {
							info.save();
							m_modelList.add(info);
						}
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

	public ArrayList<ServicesInfo> getServicesList() {
		loadFromDB();
		if (m_modelList == null) {
			m_modelList = new ArrayList<ServicesInfo>();
		}
		return m_modelList;
	}

	public ServicesInfo getServicesByDesc(String desc) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {

			for (ServicesInfo d : m_modelList) {
				if (d.description.equalsIgnoreCase(desc)) {
					return d;
				}
			}
		}
		return null;
	}

	public ServicesInfo getServicesByid(int id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (ServicesInfo d : m_modelList) {
				if (d.id == id) {
					return d;
				}
			}
		}
		return null;

	}

}