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
import com.anstar.models.ApplicationDeviceTypeInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;

public class ApplicationDeviceTypeList implements ServiceHelperDelegate {

	private ApplicationDeviceTypeList() {

	}

	private static volatile ApplicationDeviceTypeList _instance = null;

	// private static volatile int cat_id = 0;

	public static ApplicationDeviceTypeList Instance() {
		if (_instance == null) {
			synchronized (ApplicationDeviceTypeList.class) {
				_instance = new ApplicationDeviceTypeList();
				// _instance.m_modelList = new ArrayList<CategoryItemInfo>();
			}
		}
		return _instance;
	}

	protected ArrayList<ApplicationDeviceTypeInfo> m_modelList = null;
	private ModelDelegate<ApplicationDeviceTypeInfo> m_delegate = null;

	public void load(ModelDelegate<ApplicationDeviceTypeInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.DILUTION_RATES);
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
			FieldworkApplication.Connection().delete(ApplicationDeviceTypeInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<ApplicationDeviceTypeInfo> list = FieldworkApplication.Connection()
					.findAll(ApplicationDeviceTypeInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<ApplicationDeviceTypeInfo>(list);
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
				m_modelList = new ArrayList<ApplicationDeviceTypeInfo>();
				JSONArray subjectList = new JSONArray(res.RawResponse);
				for (int i = 0; i < subjectList.length(); i++) {
					JSONObject data = subjectList.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<ApplicationDeviceTypeInfo> mapper = new ModelMapHelper<ApplicationDeviceTypeInfo>();
						ApplicationDeviceTypeInfo info = mapper.getObject(
								ApplicationDeviceTypeInfo.class, data);
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

	public ArrayList<ApplicationDeviceTypeInfo> getAppDeviceList() {
		loadFromDB();
		if (m_modelList == null) {
			m_modelList = new ArrayList<ApplicationDeviceTypeInfo>();
		}
		return m_modelList;
	}

	public int getAppDeviceIdByname(String name) {
		int id = 0;
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {

			for (ApplicationDeviceTypeInfo d : m_modelList) {
				if (d.name.equalsIgnoreCase(name)) {
					id = d.id;
					break;
				}
			}
		}
		return id;

	}

	public String getAppDeviceNameByid(int id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (ApplicationDeviceTypeInfo d : m_modelList) {
				if (d.id == id) {
					return d.name;
				}
			}
		}
		return "";

	}

}
