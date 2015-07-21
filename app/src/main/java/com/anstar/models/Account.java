package com.anstar.models;

import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.models.ModelDelegates.LoginDelegate;

public class Account extends ActiveRecordBase implements ServiceHelperDelegate {

	public String UserName = "";
	public String Password = "";
	public String ApiKey = "";
	public int id = 0;
	public String license_number = "";
	public int account_id = 0;
	public String first_name = "";
	public String last_name = "";
	public boolean mobile_customers_access = false;
	public boolean inspections_enabled = false;
	public String LastModifiedCustomerData = "";
	public boolean isLogin = false;
	public boolean isCustomerLoded = false;

	private LoginDelegate m_loginDelegate = null;

	public void authenticate(ModelDelegates.LoginDelegate delegate, String url)
			throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate could not be null.");
		}
		m_loginDelegate = delegate;
		ServiceHelper helper = new ServiceHelper(ServiceHelper.LOGIN);
		helper.callUrl(url, this);
	}

	@Override
	public void CallFinish(ServiceResponse res) {
		if (!res.isError()) {

			m_loginDelegate.LoginDidSuccess(res.RawResponse);
		} else {
			m_loginDelegate.LoginFailedWithError(res.getErrorMessage());
		}
	}

	@Override
	public void CallFailure(String ErrorMessage) {

		m_loginDelegate.LoginFailedWithError(ErrorMessage);
	}

	public static String getkey() {
		String key = "";
		try {
			List<Account> list = FieldworkApplication.Connection().findAll(
					Account.class);
			if (list.size() > 0) {
				key = list.get(0).ApiKey;
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return key;
	}

	public static Account getUser() {
		try {
			List<Account> list = FieldworkApplication.Connection().findAll(
					Account.class);
			if (list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
	}

}
