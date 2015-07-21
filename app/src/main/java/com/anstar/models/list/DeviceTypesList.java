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
import com.anstar.models.DeviceTypesInfo;
import com.anstar.models.DilutionInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;

public class DeviceTypesList implements ServiceHelperDelegate {

	private DeviceTypesList() {

	}

	private static volatile DeviceTypesList _instance = null;

	// private static volatile int cat_id = 0;

	public static DeviceTypesList Instance() {
		if (_instance == null) {
			synchronized (DeviceTypesList.class) {
				_instance = new DeviceTypesList();
			}
		}
		return _instance;
	}

	protected ArrayList<DeviceTypesInfo> m_modelList = null;
	private ModelDelegate<DeviceTypesInfo> m_delegate = null;

	public void load(ModelDelegate<DeviceTypesInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.DEVICE_TYPES);
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
			FieldworkApplication.Connection().delete(DeviceTypesInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<DeviceTypesInfo> list = FieldworkApplication.Connection()
					.findAll(DeviceTypesInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<DeviceTypesInfo>(list);
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
				m_modelList = new ArrayList<DeviceTypesInfo>();
				JSONArray types = new JSONArray(res.RawResponse);
				if (types != null) {
					for (int j = 0; j < types.length(); j++) {
						JSONObject obj = types.getJSONObject(j);
						DeviceTypesInfo dt = FieldworkApplication.Connection()
								.newEntity(DeviceTypesInfo.class);
						dt.id = obj.optInt("id");
						dt.name = obj.getString("name");
						dt.save();
						m_modelList.add(dt);
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

	public ArrayList<DeviceTypesInfo> getDeviceTypesList() {
		loadFromDB();
		m_modelList = Utils.Instance().sortDeviceTypesCollections(m_modelList);
		return m_modelList;
	}
	

	public int getDeviceIdByname(String name) {
		int id = 0;
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {

			for (DeviceTypesInfo d : m_modelList) {
				if (d.name.equalsIgnoreCase(name)) {
					id = d.id;
					break;
				}
			}
		}
		return id;

	}
}
