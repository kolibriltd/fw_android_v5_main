package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.CustomerContactInfo;
import com.anstar.models.CustomerInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.ServiceLocationContactInfo;
import com.anstar.models.ServiceLocationsInfo;
import com.anstar.models.UserInfo;

public class CustomerList implements ServiceHelperDelegate {

	private CustomerList() {

	}

	private static volatile CustomerList _instance = null;

	// private static volatile int cat_id = 0;

	public static CustomerList Instance() {
		if (_instance == null) {
			synchronized (CustomerList.class) {
				_instance = new CustomerList();
			}
		}
		return _instance;
	}

	protected ArrayList<CustomerInfo> m_modelList = null;
	private ModelDelegate<CustomerInfo> m_delegate = null;

	public void load(ModelDelegate<CustomerInfo> delegate) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		loadFromDB();
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnected()) {

				ServiceHelper helper = new ServiceHelper(
						ServiceHelper.CUSTOMERS_THINNED);
				helper.call(this);
			} else {
				if (m_delegate != null)
					m_delegate
							.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
			}
		} else {
			if (m_delegate != null)
				m_delegate.ModelLoaded(m_modelList);
		}
	}

	public void loadLocal(ModelDelegate<CustomerInfo> delegate) {
		loadFromDB();
		delegate.ModelLoaded(m_modelList);
	}

	public void refreshCustomerList(ModelDelegate<CustomerInfo> delegate)
			throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		if (NetworkConnectivity.isConnected()) {
			UserInfo info = UserInfo.Instance().getUser();
			if (info != null) {
				if (info.mobile_customers_access) {
					ServiceHelper helper = new ServiceHelper(
							ServiceHelper.CUSTOMERS_THINNED);
					helper.call(this);
				} else {
					loadFromDB();
					if (m_delegate != null)
						m_delegate.ModelLoaded(m_modelList);
				}
			}

		} else {
			if (m_delegate != null)
				m_delegate.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
		}

	}

	public void loadFromDB() {
		try {
			List<CustomerInfo> list = FieldworkApplication.Connection()
					.findAll(CustomerInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<CustomerInfo>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(CustomerInfo.class);
			FieldworkApplication.Connection()
					.delete(ServiceLocationsInfo.class);
			FieldworkApplication.Connection().delete(
					ServiceLocationContactInfo.class);
			FieldworkApplication.Connection().delete(CustomerContactInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	@Override
	public void CallFinish(ServiceResponse res) {
		m_modelList = new ArrayList<CustomerInfo>();
		if (!res.isError()) {

			try {
				JSONArray subjectList = new JSONArray(res.RawResponse);
				LoadCustomer load = new LoadCustomer(subjectList, m_delegate);
				load.execute();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			if (m_delegate != null)
				m_delegate.ModelLoadFailedWithError(res.getErrorMessage());

		}
	}

	public class LoadCustomer extends AsyncTask<Void, Void, Void> {
		JSONArray m_array;
		ModelDelegate<CustomerInfo> del = null;

		public LoadCustomer(JSONArray result,
				ModelDelegate<CustomerInfo> delegate) {
			m_array = result;
			del = delegate;
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < m_array.length(); i++) {
				JSONObject data;
				try {
					data = m_array.getJSONObject(i);
					if (data != null) {
						ModelMapHelper<CustomerInfo> mapper = new ModelMapHelper<CustomerInfo>();
						CustomerInfo info = mapper.getObject(
								CustomerInfo.class, data);

						// JSONArray arr = data.getJSONArray("service_emails");
						// ArrayList<String> emails = new ArrayList<String>();
						// for (int j = 0; j < arr.length(); j++) {
						// emails.add(arr.get(j).toString());
						// }
						// info.service_emails = Utils.Instance().join(emails,
						// ",");

						if (info != null) {
							List<CustomerInfo> list = FieldworkApplication
									.Connection().find(
											CustomerInfo.class,
											CamelNotationHelper.toSQLName("id")
													+ "=?",
											new String[] { String
													.valueOf(info.id) });

							if (list != null && list.size() > 0) {
								CustomerInfo cinfo = list.get(0);
								m_modelList.add(cinfo);
							} else {
								info.save();
								m_modelList.add(info);
							}

						}
					}
				} catch (JSONException e) {
					if (del != null)
						del.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
					e.printStackTrace();
				} catch (ActiveRecordException e) {
					if (del != null)
						del.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (del != null)
				del.ModelLoaded(m_modelList);
		}
	}

	@Override
	public void CallFailure(String ErrorMessage) {
		if (m_delegate != null)
			m_delegate.ModelLoadFailedWithError(ErrorMessage);
	}

	public CustomerInfo getCustomer1(int cust_id) {
		if (m_modelList == null) {
			loadFromDB();
		}
		if (m_modelList != null) {
			for (CustomerInfo info : m_modelList) {

				if (info.id == cust_id) {
					return info;
				}
			}
		}
		return null;
	}

	public CustomerInfo getCustomerById(int cust_id) {
		try {
			List<CustomerInfo> lst = FieldworkApplication.Connection().find(
					CustomerInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(cust_id) });
			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CustomerInfo getCustomerByIdForApptList(int cust_id) {
		try {
			List<CustomerInfo> lst = FieldworkApplication.Connection().find(
					CustomerInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(cust_id) });
			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			} else {

			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<CustomerInfo> getAllCustomer() {
		ArrayList<CustomerInfo> m_list = new ArrayList<CustomerInfo>();
		try {
			List<CustomerInfo> lst = FieldworkApplication.Connection().findAll(
					CustomerInfo.class);
			if (lst != null && lst.size() > 0) {
				m_list = new ArrayList<CustomerInfo>(lst);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}
	//
	// public ArrayList<String> getCustomerEmails(int cust_id) {
	// if (m_modelList == null) {
	// loadFromDB();
	// }
	// ArrayList<String> temp = new ArrayList<String>();
	// for (CustomerInfo c : m_modelList) {
	// if (c.id == cust_id) {
	// //temp = c.invoice_email;
	// }
	// }
	// return temp;
	//
	// }
}
