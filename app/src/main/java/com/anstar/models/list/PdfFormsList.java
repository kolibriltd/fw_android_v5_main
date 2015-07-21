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
import com.anstar.models.PdfFormsInfo;

public class PdfFormsList {

	public PdfFormsList() {

	}

	public static String PDF_FORMS_LIST_NOTIFICATION = "PDF_FORMS_LIST_NOTIFICATION";

	private static volatile PdfFormsList _instance = null;

	public static PdfFormsList Instance() {
		if (_instance == null) {
			synchronized (PdfFormsList.class) {
				_instance = new PdfFormsList();
			}
		}
		return _instance;
	}

	protected ArrayList<PdfFormsInfo> m_modelList = null;

	public void parsePdfForms(JSONObject obj, int w_id) {
		m_modelList = new ArrayList<PdfFormsInfo>();
		JSONArray items;
		try {
			items = obj.getJSONArray("pdf_forms");
			if (items != null) {
				for (int i = 0; i < items.length(); i++) {
					JSONObject form = items.getJSONObject(i);
					ModelMapHelper<PdfFormsInfo> areamaper = new ModelMapHelper<PdfFormsInfo>();
					PdfFormsInfo info = areamaper.getObject(PdfFormsInfo.class,
							form);

					if (info != null) {
						try {
							info.WorkOrderId = w_id;
							info.save();
							m_modelList.add(info);
						} catch (Exception e) {
						}
					}
				}
				ArrayList<String> m_temp = new ArrayList<String>();
				ArrayList<String> m_tempattachment = new ArrayList<String>();
				for (PdfFormsInfo p : m_modelList) {
					m_temp.add(p.name);
				}
				ArrayList<AttachmentsInfo> m_attachmanes = AttachmentsList
						.Instance().load(w_id);
				for (AttachmentsInfo p : m_attachmanes) {
					m_tempattachment.add(p.attached_pdf_form_file_name);
				}
				if (m_attachmanes != null && m_attachmanes.size() > 0) {
					for (String a : m_temp) {
						PdfFormsInfo temp = PdfFormsInfo
								.getPdfFormsByfilename(a);
						if (!m_tempattachment.contains(a)) {
							AttachmentsInfo attach = FieldworkApplication
									.Connection().newEntity(
											AttachmentsInfo.class);
							attach.attached_pdf_form_content_type = temp.document_content_type;
							attach.attached_pdf_form_file_name = temp.name;
							attach.id = -1;
							attach.pdf_id = temp.pid;
							attach.WorkOrderId = temp.WorkOrderId;
							attach.save();
						} else {
							AttachmentsInfo attch = AttachmentsInfo
									.getPdfFormsByfilename(a);
							attch.pdf_id = temp.pid;
							attch.save();
						}
					}
				} else {
					for (PdfFormsInfo pdf : m_modelList) {
						AttachmentsInfo attach = FieldworkApplication
								.Connection().newEntity(AttachmentsInfo.class);
						attach.attached_pdf_form_content_type = pdf.document_content_type;
						attach.attached_pdf_form_file_name = pdf.name;
						attach.id = -1;
						attach.pdf_id = pdf.pid;
						attach.WorkOrderId = pdf.WorkOrderId;
						attach.save();
					}
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void refreshPdfForms(final int wo_id,
			final UpdateInfoDelegate delegate) {
		String url = String.format("work_orders/%d/pdf_forms", wo_id);
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
						ModelMapHelper<PdfFormsInfo> maper = new ModelMapHelper<PdfFormsInfo>();
						PdfFormsInfo info = maper.getObject(PdfFormsInfo.class,
								pest);
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
							PDF_FORMS_LIST_NOTIFICATION);
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
			FieldworkApplication.Connection().delete(PdfFormsInfo.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<PdfFormsInfo> lst = FieldworkApplication.Connection().find(
					PdfFormsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (PdfFormsInfo i : lst) {
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

	public ArrayList<PdfFormsInfo> load(int wo_id) {
		ArrayList<PdfFormsInfo> m_list = new ArrayList<PdfFormsInfo>();
		try {
			List<PdfFormsInfo> list = FieldworkApplication.Connection().find(
					PdfFormsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(wo_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (PdfFormsInfo PdfFormsInfo : list) {
						if (!PdfFormsInfo.isDeleted) {
							m_list.add(PdfFormsInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return m_list;
	}

	public ArrayList<PdfFormsInfo> loadAll(int wo_id) {
		ArrayList<PdfFormsInfo> m_list = new ArrayList<PdfFormsInfo>();
		try {
			List<PdfFormsInfo> list = FieldworkApplication.Connection().find(
					PdfFormsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(wo_id) });

			if (list != null) {
				if (list.size() > 0) {
					for (PdfFormsInfo PdfFormsInfo : list) {
						m_list.add(PdfFormsInfo);
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
					PdfFormsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(wo_id) });
			Utils.LogInfo(String.format(
					"%d records deleted of target pests for work order %d",
					cnt, wo_id));
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public ArrayList<PdfFormsInfo> getPdfFormsByWorkOrderId(int id) {
		ArrayList<PdfFormsInfo> m_list = new ArrayList<PdfFormsInfo>();
		try {
			List<PdfFormsInfo> list = FieldworkApplication.Connection()
					.findAll(PdfFormsInfo.class);
			if (list.size() > 0) {
				for (PdfFormsInfo record : list) {
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

	// public PdfFormsInfo getMaterialByIdHiren(int material_id) {
	// if (m_modelList == null) {
	// // loadFromDB();
	// }
	// if (m_modelList != null) {
	// for (PdfFormsInfo info : m_modelList) {
	// if (info.id == material_id) {
	// return info;
	// }
	// }
	// }
	// return null;
	// }
}
