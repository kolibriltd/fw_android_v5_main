package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class PdfFormsInfo extends ActiveRecordBase {
	public PdfFormsInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int pid = 0;
	@ModelMapper(JsonKey = "name")
	public String name = "";
	// @ModelMapper(JsonKey = "document_file_name")
	// public String document_file_name = "";
	@ModelMapper(JsonKey = "document_content_type")
	public String document_content_type = "";
	@ModelMapper(JsonKey = "document_file_size")
	public int document_file_size = 0;

	public int WorkOrderId = 0;
	public boolean isDeleted = false;

	private UpdateInfoDelegate m_delegate = null;

	public static ArrayList<PdfFormsInfo> getPdfFormsByWorkerId(int w_id) {

		ArrayList<PdfFormsInfo> m_list = new ArrayList<PdfFormsInfo>();

		try {
			List<PdfFormsInfo> lst = FieldworkApplication.Connection().find(
					PdfFormsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(w_id) });
			if (lst != null && lst.size() > 0) {
				m_list = new ArrayList<PdfFormsInfo>(lst);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public static PdfFormsInfo getPdfFormsByfilename(String name) {
		try {
			List<PdfFormsInfo> lst = FieldworkApplication.Connection().find(
					PdfFormsInfo.class,
					CamelNotationHelper.toSQLName("name") + "=?",
					new String[] { name });
			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PdfFormsInfo getPdfFormsByPdfId(int p_id, int appt_id) {
		try {
			// List<PdfFormsInfo> lst = FieldworkApplication.Connection().find(
			// PdfFormsInfo.class,
			// CamelNotationHelper.toSQLName("pid") + "=?",
			// new String[] { String.valueOf(p_id) });
			// if (lst != null && lst.size() > 0) {
			// return lst.get(0);
			// }
			List<PdfFormsInfo> lst = FieldworkApplication.Connection().findAll(
					PdfFormsInfo.class);

			if (lst != null && lst.size() > 0) {
				for (PdfFormsInfo pdfFormsInfo : lst) {
					if (p_id == pdfFormsInfo.pid && appt_id == pdfFormsInfo.WorkOrderId) {
						return pdfFormsInfo;
					}
				}
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

}
