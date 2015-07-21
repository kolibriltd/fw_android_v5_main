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
import com.anstar.models.MaterialInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;

public class MaterialList implements ServiceHelperDelegate {

	private MaterialList() {

	}

	private static volatile MaterialList _instance = null;

	// private static volatile int cat_id = 0;

	public static MaterialList Instance() {
		if (_instance == null) {
			synchronized (MaterialList.class) {
				_instance = new MaterialList();
			}
		}
		return _instance;
	}

	protected ArrayList<MaterialInfo> m_modelList = null;
	private ModelDelegate<MaterialInfo> m_delegate = null;

	public void load(ModelDelegate<MaterialInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.MATERIALS);
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
			List<MaterialInfo> list = FieldworkApplication.Connection()
					.findAll(MaterialInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<MaterialInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(MaterialInfo.class);
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
				m_modelList = new ArrayList<MaterialInfo>();
				JSONArray subjectList = new JSONArray(res.RawResponse);
				for (int i = 0; i < subjectList.length(); i++) {
					JSONObject data = subjectList.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<MaterialInfo> mapper = new ModelMapHelper<MaterialInfo>();
						MaterialInfo info = mapper.getObject(
								MaterialInfo.class, data);
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

	public MaterialInfo getMaterialById(int material_id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (MaterialInfo info : m_modelList) {
				if (info.id == material_id) {
					return info;
				}
			}
		}
		return null;
	}

	public int getMaterialIdByname(String name) {
		int id = 0;
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (MaterialInfo m : m_modelList) {

				if (m.name.equalsIgnoreCase(name)) {
					id = m.id;
					break;
				}
			}
		}
		return id;
	}
}
