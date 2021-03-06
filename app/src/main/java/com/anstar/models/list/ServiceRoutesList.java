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
import com.anstar.models.ServiceRoutesInfo;

public class ServiceRoutesList implements ServiceHelperDelegate {

	private ServiceRoutesList() {

	}

	private static volatile ServiceRoutesList _instance = null;

	// private static volatile int cat_id = 0;

	public static ServiceRoutesList Instance() {
		if (_instance == null) {
			synchronized (ServiceRoutesList.class) {
				_instance = new ServiceRoutesList();
				// _instance.m_modelList = new ArrayList<CategoryItemInfo>();
			}
		}
		return _instance;
	}

	protected ArrayList<ServiceRoutesInfo> m_modelList = null;
	private ModelDelegate<ServiceRoutesInfo> m_delegate = null;

	public void load(ModelDelegate<ServiceRoutesInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.SERVICE_ROUTES);
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
			FieldworkApplication.Connection().delete(ServiceRoutesInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<ServiceRoutesInfo> list = FieldworkApplication.Connection()
					.findAll(ServiceRoutesInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<ServiceRoutesInfo>(list);
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
				m_modelList = new ArrayList<ServiceRoutesInfo>();
				JSONArray subjectList = new JSONArray(res.RawResponse);
				for (int i = 0; i < subjectList.length(); i++) {
					JSONObject data = subjectList.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<ServiceRoutesInfo> mapper = new ModelMapHelper<ServiceRoutesInfo>();
						ServiceRoutesInfo info = mapper.getObject(
								ServiceRoutesInfo.class, data);
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

	public ArrayList<ServiceRoutesInfo> getRouteList() {
		loadFromDB();
		if (m_modelList == null) {
			m_modelList = new ArrayList<ServiceRoutesInfo>();
		}
		return m_modelList;
	}

	public int getServiceRouteIdByname(String name) {
		int id = 0;
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {

			for (ServiceRoutesInfo d : m_modelList) {
				if (d.name.equalsIgnoreCase(name)) {
					id = d.id;
					break;
				}
			}
		}
		return id;

	}

	public String getServiceRouteNameByid(int id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (ServiceRoutesInfo d : m_modelList) {
				if (d.id == id) {
					return d.name;
				}
			}
		}
		return "";

	}

}
