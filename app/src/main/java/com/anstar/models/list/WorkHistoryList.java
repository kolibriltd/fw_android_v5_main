package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.WorkHistroyInfo;

public class WorkHistoryList implements ServiceHelperDelegate {

	private WorkHistoryList() {

	}

	private static volatile WorkHistoryList _instance = null;

	public static WorkHistoryList Instance() {
		if (_instance == null) {
			synchronized (WorkHistoryList.class) {
				_instance = new WorkHistoryList();
				_instance.m_modelList = new ArrayList<WorkHistroyInfo>();
			}
		}
		return _instance;
	}

	protected ArrayList<WorkHistroyInfo> m_modelList = null;
	private ModelDelegate<WorkHistroyInfo> m_delegate = null;

	public void load(ModelDelegate<WorkHistroyInfo> delegate, int cid, int sid) {
		m_delegate = delegate;
		//loadFromDB();
		if (m_modelList == null || m_modelList.size() <= 0) {
			if (NetworkConnectivity.isConnected()) {
				String url = ServiceHelper.CUSTOMERS +"/"+cid+"/"+ServiceHelper.SERVICE_LOCATIONS+"/"+sid+"/history";
				ServiceHelper helper = new ServiceHelper(url);
				helper.call(this);
			} else {
				m_delegate.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
			}
		} else {
			m_delegate.ModelLoaded(m_modelList);
		}
	}

	public void ClearDB() {
		m_modelList = new ArrayList<WorkHistroyInfo>();
	}

	public ArrayList<WorkHistroyInfo> getApoointments() {
		loadFromDB();
		return m_modelList;
	}

	public void loadFromDB() {
		try {
			List<WorkHistroyInfo> list = FieldworkApplication.Connection()
					.findAll(WorkHistroyInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<WorkHistroyInfo>(list);
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
				m_modelList = new ArrayList<WorkHistroyInfo>();
				JSONArray historyList = new JSONArray(res.RawResponse);
				for (int i = 0; i < historyList.length(); i++) {
					JSONObject data = historyList.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<WorkHistroyInfo> mapper = new ModelMapHelper<WorkHistroyInfo>();
						WorkHistroyInfo info = mapper.getObject(
								WorkHistroyInfo.class, data);
						
						if (info != null) {
							info.json_string = data.toString();
							info.save();
							m_modelList.add(info);
						}
					}
				}
				if(m_delegate != null)
					m_delegate.ModelLoaded(m_modelList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			m_delegate.ModelLoadFailedWithError(res.getErrorMessage());
		}
	}


	@Override
	public void CallFailure(String ErrorMessage) {
		m_delegate.ModelLoadFailedWithError(ErrorMessage);
	}


	public WorkHistroyInfo getHistoryById(int id) {
		try {
			List<WorkHistroyInfo> list = FieldworkApplication.Connection()
					.findAll(WorkHistroyInfo.class);
			if (list.size() > 0) {
				for (WorkHistroyInfo record : list) {
					if (record.id == id) {
						return record;
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}

		return null;
	}
	
}
