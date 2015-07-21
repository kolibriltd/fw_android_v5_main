package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.LineItemsInfo;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class LineItemsList {

	public LineItemsList() {

	}

	public static String LINE_ITEMS_LIST_NOTIFICATION = "LINE_ITEMS_LIST_NOTIFICATION";

	private static volatile LineItemsList _instance = null;

	public static LineItemsList Instance() {
		if (_instance == null) {
			synchronized (LineItemsList.class) {
				_instance = new LineItemsList();
			}
		}
		return _instance;
	}

	protected ArrayList<LineItemsInfo> m_modelList = null;

	public void parseLineItems(JSONObject obj, int w_id) {

		JSONArray items;
		try {
			items = obj.getJSONArray("line_items");
			if (items != null) {
				for (int i = 0; i < items.length(); i++) {
					JSONObject line = items.getJSONObject(i);
					ModelMapHelper<LineItemsInfo> areamaper = new ModelMapHelper<LineItemsInfo>();
					LineItemsInfo info = areamaper.getObject(
							LineItemsInfo.class, line);
					if (info != null) {
						try {
							info.WorkOrderId = w_id;
							info.save();
						} catch (Exception e) {
						}
					}
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void refreshLineItems(final int wo_id,
			final UpdateInfoDelegate delegate) {
		String url = String.format("work_orders/%d/line_items", wo_id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.GET, null);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {

				try {
					deleteLineItems(wo_id);
					JSONArray items = new JSONArray(res.RawResponse);
					for (int i = 0; i < items.length(); i++) {
						JSONObject pest = items.getJSONObject(i);
						ModelMapHelper<LineItemsInfo> maper = new ModelMapHelper<LineItemsInfo>();
						LineItemsInfo info = maper.getObject(
								LineItemsInfo.class, pest);
						if (info != null) {
							try {
								info.WorkOrderId = wo_id;
								info.save();
							} catch (Exception e) {
							}
						}
					}
					delegate.UpdateSuccessFully(res);
					NotificationCenter.Instance().postNotification(
							LINE_ITEMS_LIST_NOTIFICATION);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				delegate.UpdateFail(ErrorMessage);
			}
		});
	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(LineItemsInfo.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<LineItemsInfo> lst = FieldworkApplication.Connection().find(
					LineItemsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (LineItemsInfo i : lst) {
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	// public void refresh(int appointment_id) throws Exception {
	// m_appointmentId = appointment_id;
	// if (NetworkConnectivity.isConnected()) {
	// ServiceHelper helper = new ServiceHelper(
	// ServiceHelper.APPOINTMENT_OCCURENCES + "/" + appointment_id
	// + "/" + ServiceHelper.PEST_TARGETS);
	// helper.call(this);
	// }
	// }

	public ArrayList<LineItemsInfo> load(int wo_id) {
		ArrayList<LineItemsInfo> m_list = new ArrayList<LineItemsInfo>();
		try {
			List<LineItemsInfo> list = FieldworkApplication.Connection().find(
					LineItemsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=? and "+CamelNotationHelper.toSQLName("isDeleted")+ "=?",
					new String[] { String.valueOf(wo_id), "false" });

			if (list != null) {
				if (list.size() > 0) {
					for (LineItemsInfo LineItemsInfo : list) {
						if (!LineItemsInfo.isDeleted) {
							m_list.add(LineItemsInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public ArrayList<LineItemsInfo> loadAll(int wo_id) {
		ArrayList<LineItemsInfo> m_list = new ArrayList<LineItemsInfo>();
		try {
			List<LineItemsInfo> list = FieldworkApplication.Connection().find(
					LineItemsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(wo_id)});

			if (list != null) {
				if (list.size() > 0) {
					for (LineItemsInfo LineItemsInfo : list) {
						m_list.add(LineItemsInfo);
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public void deleteLineItems(int wo_id) {
		try {
			int cnt = FieldworkApplication.Connection().delete(
					LineItemsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(wo_id) });
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public double getLineItemsPriceByAppt(int wo_id) {
		double price = 0;
		try {
			List<LineItemsInfo> list = FieldworkApplication.Connection().find(
					LineItemsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=? and "+CamelNotationHelper.toSQLName("isDeleted")+ "=?",
					new String[] { String.valueOf(wo_id),"false" });

			if (list != null) {
				if (list.size() > 0) {
					for (LineItemsInfo LineItemsInfo : list) {
						price += Utils.ConvertToDouble(LineItemsInfo.price) * Utils.ConvertToDouble(LineItemsInfo.quantity);
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return price;
	}

	public LineItemsInfo getFirstLineByWoId(int wo_id) {
		try {
			List<LineItemsInfo> list = FieldworkApplication.Connection().find(
					LineItemsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(wo_id) });

			if (list != null) {
				if (list.size() > 0) {
					return list.get(0);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return null;
	}
	

	// public LineItemsInfo getMaterialByIdHiren(int material_id) {
	// if (m_modelList == null) {
	// // loadFromDB();
	// }
	// if (m_modelList != null) {
	// for (LineItemsInfo info : m_modelList) {
	// if (info.id == material_id) {
	// return info;
	// }
	// }
	// }
	// return null;
	// }
}
