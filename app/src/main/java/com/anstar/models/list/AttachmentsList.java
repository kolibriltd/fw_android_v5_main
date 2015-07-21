package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NotificationCenter;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.AttachmentsInfo;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class AttachmentsList {

	public AttachmentsList() {

	}

	public static String ATTACHMENT_LIST_NOTIFICATION = "ATTACHMENT_LIST_NOTIFICATION";

	private static volatile AttachmentsList _instance = null;

	public static AttachmentsList Instance() {
		if (_instance == null) {
			synchronized (AttachmentsList.class) {
				_instance = new AttachmentsList();
			}
		}
		return _instance;
	}

	protected ArrayList<AttachmentsInfo> m_modelList = null;

	public void parseAttachments(JSONObject obj, int w_id) {

		JSONArray items;
		try {
			items = obj.getJSONArray("attachments");
			if (items != null) {
				for (int i = 0; i < items.length(); i++) {
					JSONObject form = items.getJSONObject(i);
					ModelMapHelper<AttachmentsInfo> areamaper = new ModelMapHelper<AttachmentsInfo>();
					AttachmentsInfo info = areamaper.getObject(
							AttachmentsInfo.class, form);
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

	public void refreshAttachments(final int wo_id,
			final UpdateInfoDelegate delegate) {
		String url = String.format("work_orders/%d/attachments", wo_id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.GET, null);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {

				try {
					deletePdfForm(wo_id);
					JSONArray items = new JSONArray(res.RawResponse);
					for (int i = 0; i < items.length(); i++) {
						JSONObject pest = items.getJSONObject(i);
						ModelMapHelper<AttachmentsInfo> maper = new ModelMapHelper<AttachmentsInfo>();
						AttachmentsInfo info = maper.getObject(
								AttachmentsInfo.class, pest);
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
							ATTACHMENT_LIST_NOTIFICATION);
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
			FieldworkApplication.Connection().delete(AttachmentsInfo.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<AttachmentsInfo> lst = FieldworkApplication.Connection().find(
					AttachmentsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (AttachmentsInfo i : lst) {
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

	public ArrayList<AttachmentsInfo> load(int wo_id) {
		ArrayList<AttachmentsInfo> m_list = new ArrayList<AttachmentsInfo>();
		try {
			List<AttachmentsInfo> list = FieldworkApplication
					.Connection()
					.find(AttachmentsInfo.class,
							CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
							new String[] { String.valueOf(wo_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (AttachmentsInfo AttachmentsInfo : list) {
						if (!AttachmentsInfo.isDeleted) {
							m_list.add(AttachmentsInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public ArrayList<AttachmentsInfo> loadAll(int wo_id) {
		ArrayList<AttachmentsInfo> m_list = new ArrayList<AttachmentsInfo>();
		try {
			List<AttachmentsInfo> list = FieldworkApplication
					.Connection()
					.find(AttachmentsInfo.class,
							CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
							new String[] { String.valueOf(wo_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (AttachmentsInfo AttachmentsInfo : list) {
						m_list.add(AttachmentsInfo);
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public void deletePdfForm(int wo_id) {
		try {
			int cnt = FieldworkApplication.Connection().delete(
					AttachmentsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(wo_id) });
			Utils.LogInfo(String.format(
					"%d records deleted of target pests for work order %d",
					cnt, wo_id));
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public ArrayList<AttachmentsInfo> getPdfFormsByWorkOrderId(int id) {
		ArrayList<AttachmentsInfo> m_list = new ArrayList<AttachmentsInfo>();
		try {
			List<AttachmentsInfo> list = FieldworkApplication.Connection()
					.findAll(AttachmentsInfo.class);
			if (list.size() > 0) {
				for (AttachmentsInfo record : list) {
					if (record.WorkOrderId == id) {
						m_list.add(record);
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}
}
