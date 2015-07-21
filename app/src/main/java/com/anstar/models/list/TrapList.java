package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.InspectionInfo;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.TrapScanningInfo;

public class TrapList implements ServiceHelperDelegate {

	private TrapList() {

	}

	private static volatile TrapList _instance = null;

	static int c_id = 0;
	static int service_loc_id = 0;

	public static TrapList Instance() {
		if (_instance == null) {
			synchronized (TrapList.class) {
				_instance = new TrapList();
			}
		}
		return _instance;
	}

	protected ArrayList<TrapScanningInfo> m_modelList = null;
	private ModelDelegate<TrapScanningInfo> m_delegate = null;

	public void load(ModelDelegate<TrapScanningInfo> delegate, int cust_id,
			int s_id) throws Exception {
		if (delegate == null) {
			throw new Exception("Delegate can not be null.");
		}
		m_delegate = delegate;
		c_id = cust_id;
		service_loc_id = s_id;
		loadFromDB(cust_id);
		if (m_modelList == null) {
			if (NetworkConnectivity.isConnectedwithoutmode()) {
				ServiceHelper helper = new ServiceHelper("customers/" + cust_id
						+ "/" + ServiceHelper.GET_TRAPS);
				helper.call(this);
			} else {
				m_delegate.ModelLoadFailedWithError(ServiceHelper.COMMON_ERROR);
			}
		} else {
			m_delegate.ModelLoaded(m_modelList);
		}
	}

	public ArrayList<TrapScanningInfo> getAllTraps() {
		ArrayList<TrapScanningInfo> m_list = new ArrayList<TrapScanningInfo>();
		try {
			List<TrapScanningInfo> list = FieldworkApplication.Connection()
					.findAll(TrapScanningInfo.class);
			if (list != null) {
				if (list.size() > 0) {
					m_list = new ArrayList<TrapScanningInfo>(list);
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public ArrayList<TrapScanningInfo> getAllTrapsByCustomerId(int cust_id,
			int service_location_id) {
		ArrayList<TrapScanningInfo> m_list = new ArrayList<TrapScanningInfo>();
		try {
			List<TrapScanningInfo> list = FieldworkApplication
					.Connection()
					.find(TrapScanningInfo.class,
							CamelNotationHelper
									.toSQLName("service_location_id") + "=?",
							new String[] { String.valueOf(service_location_id) });
			if (list != null) {
				if (list.size() > 0) {
					m_list = new ArrayList<TrapScanningInfo>(list);
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public void LoadCheckedANDunCheckedTraps(int app_id, int cust_id,
			int service_location_id) {
		try {
			ArrayList<TrapScanningInfo> list = getAllTrapsByCustomerId(cust_id,
					service_location_id);
			if (list != null) {
				if (list.size() > 0) {
					ArrayList<InspectionInfo> m_inspections = InspectionList
							.Instance().loadWhereDeletedisFalse(app_id);
					ArrayList<String> barcodes = new ArrayList<String>();
					for (InspectionInfo i : m_inspections) {
						if (!i.isForUnchecked) {
							barcodes.add(i.barcode);
						}
					}
					for (TrapScanningInfo t : list) {
						if (barcodes.size() > 0) {
							if (barcodes.contains(t.barcode)) {
								t.isChecked = true;
								t.save();
							}else{
								t.isChecked = false;
								t.save();
							}
						}else{
							t.isChecked = false;
							t.save();
						}
					}
					// m_list = new ArrayList<TrapScanningInfo>(list);
				}
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public void loadFromDB(int customer_id) {
		try {
			List<TrapScanningInfo> list = FieldworkApplication
					.Connection()
					.find(TrapScanningInfo.class,
							CamelNotationHelper.toSQLName("customer_id") + "=?",
							new String[] { String.valueOf(customer_id) });
			if (list != null && list.size() > 0) {
				m_modelList = new ArrayList<TrapScanningInfo>(list);
			} else {
				m_modelList = null;
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(TrapScanningInfo.class);
			m_modelList = null;
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int customer_id) {
		try {
			List<TrapScanningInfo> lst = FieldworkApplication
					.Connection()
					.find(TrapScanningInfo.class,
							CamelNotationHelper.toSQLName("customer_id") + "=?",
							new String[] { "" + customer_id });
			if (lst != null && lst.size() > 0) {
				for (TrapScanningInfo i : lst) {
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	@Override
	public void CallFinish(ServiceResponse res) {
		if (!res.isError()) {
			ArrayList<TrapScanningInfo> m_traps = getAllTrapsByCustomerId(c_id,
					service_loc_id);
			if (m_traps != null && m_traps.size() > 0) {
				m_modelList = new ArrayList<TrapScanningInfo>(m_traps);
				m_delegate.ModelLoaded(m_modelList);
			} else {
				try {
					m_modelList = new ArrayList<TrapScanningInfo>();
					JSONArray subjectList = new JSONArray(res.RawResponse);
					for (int i = 0; i < subjectList.length(); i++) {
						JSONObject data = subjectList.getJSONObject(i);
						if (data != null) {
							Utils.LogInfo("Parsed Trap Record number**** "+i);
							TrapScanningInfo info = FieldworkApplication.Connection().newEntity(TrapScanningInfo.class);
							info.barcode = data.optString("barcode");
							info.building = data.optString("building");
							info.floor = data.optString("floor");
							info.location_details = data.optString("location_details");
							info.notes = data.optString("notes");
							info.number = data.optString("number");
							info.service_frequency = data.optString("service_frequency");
							info.id = data.optInt("id");
							info.customer_id = data.optInt("customer_id");
							info.service_location_id = data.optInt("service_location_id");
							info.trap_type_id = data.optInt("trap_type_id");
							
//							ModelMapHelper<TrapScanningInfo> mapper = new ModelMapHelper<TrapScanningInfo>();
//							TrapScanningInfo info = mapper.getObject(
//									TrapScanningInfo.class, data);
							if (info != null) {
								info = isExists(info);
								info.save();
//								m_modelList.add(info);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				loadFromDB(c_id);
				m_delegate.ModelLoaded(m_modelList);
			}
		} else {
			m_delegate.ModelLoadFailedWithError(res.getErrorMessage());
		}
	}

	@Override
	public void CallFailure(String ErrorMessage) {
		m_delegate.ModelLoadFailedWithError(ErrorMessage);
	}

	public TrapScanningInfo getTrapByBarcode(String barcode) {
		try {
			List<TrapScanningInfo> list = FieldworkApplication.Connection()
					.find(TrapScanningInfo.class,
							CamelNotationHelper.toSQLName("barcode") + "=?",
							new String[] { barcode });
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return null;
	}

	public TrapScanningInfo getTrapByBarcodeNdCustomerId(String barcode,
			int cust_id) {
		try {
			List<TrapScanningInfo> list = FieldworkApplication.Connection()
					.find(TrapScanningInfo.class,
							CamelNotationHelper.toSQLName("barcode")
									+ "=? and "
									+ CamelNotationHelper
											.toSQLName("customer_id") + "=?",
							new String[] { barcode, String.valueOf(cust_id) });
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return null;
	}

	public ArrayList<TrapScanningInfo> getTrapByCustomerId(int cust_id) {
		ArrayList<TrapScanningInfo> m_traps = new ArrayList<TrapScanningInfo>();
		try {
			List<TrapScanningInfo> list = FieldworkApplication
					.Connection()
					.find(TrapScanningInfo.class,
							CamelNotationHelper.toSQLName("customer_id") + "=?",
							new String[] { String.valueOf(cust_id) });
			if (list != null && list.size() > 0) {
				m_traps = new ArrayList<TrapScanningInfo>(list);
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_traps;
	}

	public TrapScanningInfo isExists(TrapScanningInfo obj) {

		try {
			ModelMapHelper<TrapScanningInfo> helper = new ModelMapHelper<TrapScanningInfo>();
			String column = CamelNotationHelper.toSQLName(helper
					.getUniqueFieldName(TrapScanningInfo.class));
			// List<T> list = CourseKartApplication.Connection().findByColumn(
			// m_type, column,
			// String.valueOf(helper.getUniqueFieldValue(m_type, obj)));

			List<TrapScanningInfo> list = FieldworkApplication.Connection()
					.find(TrapScanningInfo.class,
							column + "=?",
							new String[] { String.valueOf(helper
									.getUniqueFieldValue(
											TrapScanningInfo.class, obj)) });

			if (list != null) {
				if (list.size() > 0) {
					TrapScanningInfo dbObj = list.get(0);
					dbObj.copyFrom(obj);
					return dbObj;
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}

		return obj;

	}
}
