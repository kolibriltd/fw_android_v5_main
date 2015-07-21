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
import com.anstar.models.PestsTypeInfo;

public class PestTypeList implements ServiceHelperDelegate {

	private PestTypeList() {

	}

	private static volatile PestTypeList _instance = null;

	// private static volatile int cat_id = 0;

	public static PestTypeList Instance() {
		if (_instance == null) {
			synchronized (PestTypeList.class) {
				_instance = new PestTypeList();
			}
		}
		return _instance;
	}

	protected ArrayList<PestsTypeInfo> m_modelList = null;
	private ModelDelegate<PestsTypeInfo> m_delegate = null;

	public void load(ModelDelegate<PestsTypeInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList != null) {
			m_delegate.ModelLoaded(m_modelList);
		}

		if (NetworkConnectivity.isConnected()) {
			ServiceHelper helper = new ServiceHelper(ServiceHelper.PEST_TYPES);
			helper.call(this);
		}
	}

	public void loadFromDB() {
		try {
			List<PestsTypeInfo> list = FieldworkApplication.Connection()
					.findAll(PestsTypeInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<PestsTypeInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(PestsTypeInfo.class);
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
				m_modelList = new ArrayList<PestsTypeInfo>();
				JSONArray subjectList = new JSONArray(res.RawResponse);
				for (int i = 0; i < subjectList.length(); i++) {
					JSONObject data = subjectList.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<PestsTypeInfo> mapper = new ModelMapHelper<PestsTypeInfo>();
						PestsTypeInfo info = mapper.getObject(
								PestsTypeInfo.class, data);
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

	public PestsTypeInfo getPestById(int pest_id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (PestsTypeInfo info : m_modelList) {
				if (info.id == pest_id) {
					return info;
				}
			}
		}
		return null;
	}
}
