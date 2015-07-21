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
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.LocationInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;

public class LocationInfoList implements ServiceHelperDelegate {

	private LocationInfoList() {

	}

	private static volatile LocationInfoList _instance = null;

	// private static volatile int cat_id = 0;

	public static LocationInfoList Instance() {
		if (_instance == null) {
			synchronized (LocationInfoList.class) {
				_instance = new LocationInfoList();
			}
		}
		return _instance;
	}

	protected ArrayList<LocationInfo> m_modelList = null;
	private ModelDelegate<LocationInfo> m_delegate = null;

	public void load(ModelDelegate<LocationInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.LOCATION_TYPE);
				helper.call(this);
			} else {
				m_delegate.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
			}
		} else {
			m_delegate.ModelLoaded(m_modelList);
		}
	}

	public void loadFromDB() {
		try {
			List<LocationInfo> list = FieldworkApplication.Connection()
					.findAll(LocationInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<LocationInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(LocationInfo.class);
			FieldworkApplication.Connection().delete(LocationAreaInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}

	}

	@Override
	public void CallFinish(ServiceResponse res) {
		if (!res.isError()) {

			try {
				ClearDB();
				m_modelList = new ArrayList<LocationInfo>();
				JSONArray location_list = new JSONArray(res.RawResponse);
				for (int i = 0; i < location_list.length(); i++) {
					JSONObject data = location_list.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<LocationInfo> mapper = new ModelMapHelper<LocationInfo>();
						LocationInfo info = mapper.getObject(
								LocationInfo.class, data);

						if (info != null) {
							info.save();
							m_modelList.add(info);
						}
						if (data.toString().contains("location_areas")) {
							LocationAreaList locationarealist = new LocationAreaList();
							locationarealist.parseLocationArea(data, info.id);
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

	public ArrayList<LocationInfo> getLocationTypes() {
		loadFromDB();
		if (m_modelList == null) {
			m_modelList = new ArrayList<LocationInfo>();
		}
		return m_modelList;
	}

	public int getLocationInfoIdByname(String name) {
		int id = 0;
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {

			for (LocationInfo d : m_modelList) {
				if (d.name.equalsIgnoreCase(name)) {
					id = d.id;
					break;
				}
			}
		}
		return id;
	}

	public String getLocationInfoNameByid(int id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (LocationInfo d : m_modelList) {
				if (d.id == id) {
					return d.name;
				}
			}
		}
		return "";

	}
}
