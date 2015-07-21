package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.list.AppointmentModelList;

public class UserInfo extends ActiveRecordBase implements ServiceHelperDelegate {

	public UserInfo() {

	}

	@ModelMapper(JsonKey = "id", IsUnique = true)
	public int id = 0;
	@ModelMapper(JsonKey = "license_number")
	public String license_number = "";
	@ModelMapper(JsonKey = "first_name")
	public String first_name = "";
	@ModelMapper(JsonKey = "last_name")
	public String last_name = "";
	@ModelMapper(JsonKey = "account_id")
	public int account_id = 0;
	@ModelMapper(JsonKey = "mobile_customers_access")
	public boolean mobile_customers_access = false;
	@ModelMapper(JsonKey = "service_route_id")
	public int service_route_id = 0;
	@ModelMapper(JsonKey = "inspections_enabled")
	public boolean inspections_enabled = false;
	@ModelMapper(JsonKey = "show_environment_fields")
	public boolean show_environment_fields = false;
	@ModelMapper(JsonKey = "country")
	public String country = "";
	@ModelMapper(JsonKey = "hide_customer_details")
	public boolean hide_customer_details = false;
	@ModelMapper(JsonKey = "show_photos")
	public boolean show_photos = false;
	
	private static volatile UserInfo m_instance = null;
	protected ArrayList<UserInfo> m_modelList = null;
	private ModelDelegate<UserInfo> m_delegate = null;

	public static UserInfo Instance() {
		if (m_instance == null) {
			synchronized (AppointmentModelList.class) {
				m_instance = new UserInfo();
				m_instance.m_modelList = new ArrayList<UserInfo>();
			}
		}
		return m_instance;
	}

	public void load(ModelDelegate<UserInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null || m_modelList.size() <= 0) {
			if (NetworkConnectivity.isConnected()) {
				ServiceHelper helper = new ServiceHelper(ServiceHelper.GET_USER);
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
			List<UserInfo> list = FieldworkApplication.Connection().findAll(
					UserInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<UserInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(UserInfo.class);
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
				m_modelList = new ArrayList<UserInfo>();
				JSONObject data = new JSONObject(res.RawResponse);
				if (data != null) {
					JSONObject obj = data.getJSONObject("user");
					if (obj != null) {
						ModelMapHelper<UserInfo> mapper = new ModelMapHelper<UserInfo>();
						UserInfo info = mapper.getObject(UserInfo.class, obj);
						if (info != null) {
							info.save();
							m_modelList.add(info);
						}
					}
					m_delegate.ModelLoaded(m_modelList);
				}

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

	public UserInfo getUser() {
		try {
			List<UserInfo> list = FieldworkApplication.Connection().findAll(
					UserInfo.class);
			if (list != null && list.size() > 0) {
				return list.get(0);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

}
