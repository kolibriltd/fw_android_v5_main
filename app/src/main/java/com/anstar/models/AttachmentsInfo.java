package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapper;

public class AttachmentsInfo extends ActiveRecordBase {
	public AttachmentsInfo() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "attached_pdf_form_file_name")
	public String attached_pdf_form_file_name = "";
	@ModelMapper(JsonKey = "attached_pdf_form_content_type")
	public String attached_pdf_form_content_type = "";
	@ModelMapper(JsonKey = "attached_pdf_form_file_size")
	public int attached_pdf_form_file_size = 0;

	public int pdf_id = 0;
	public int WorkOrderId = 0;
	public boolean isDeleted = false;

	public static ArrayList<AttachmentsInfo> getPdfFormsByWorkerId(int w_id) {

		ArrayList<AttachmentsInfo> m_list = new ArrayList<AttachmentsInfo>();

		try {
			List<AttachmentsInfo> lst = FieldworkApplication.Connection().find(
					AttachmentsInfo.class,
					CamelNotationHelper.toSQLName("WorkOrderId") + "=?",
					new String[] { String.valueOf(w_id) });
			if (lst != null && lst.size() > 0) {
				m_list = new ArrayList<AttachmentsInfo>(lst);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public static AttachmentsInfo getPdfFormsByfilename1(String name) {
		try {
//			List<AttachmentsInfo> lst = FieldworkApplication.Connection().find(
//					AttachmentsInfo.class,
//					CamelNotationHelper
//							.toSQLName("attached_pdf_form_file_name") + "=?",
//					new String[] { name });
//			if (lst != null && lst.size() > 0) {
//				return lst.get(0);
//			}
			
			List<AttachmentsInfo> lst = FieldworkApplication.Connection()
					.findAll(AttachmentsInfo.class);
			if (lst != null && lst.size() > 0) {
				for (AttachmentsInfo a : lst) {
					if (a.attached_pdf_form_file_name.equalsIgnoreCase(name)) {
						return a;
					}
				}
			}


		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static AttachmentsInfo getPdfFormsByfilename(String name) {
		try {
			List<AttachmentsInfo> lst = FieldworkApplication.Connection().find(
					AttachmentsInfo.class,
					CamelNotationHelper
							.toSQLName("attached_pdf_form_file_name") + "=?",
					new String[] { name });
			if (lst != null && lst.size() > 0) {
				return lst.get(0);
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static AttachmentsInfo getPdfFormsByID(int attach_id) {
		try {
			List<AttachmentsInfo> lst = FieldworkApplication.Connection()
					.findAll(AttachmentsInfo.class);
			if (lst != null && lst.size() > 0) {
				for (AttachmentsInfo a : lst) {
					if (a.id == attach_id) {
						return a;
					}
				}
			}

		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}
}
