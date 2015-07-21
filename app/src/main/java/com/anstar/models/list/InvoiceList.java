package com.anstar.models.list;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.InvoiceInfo;

public class InvoiceList {

	private InvoiceList() {

	}

	private static volatile InvoiceList _instance = null;

	// private static volatile int cat_id = 0;

	public static InvoiceList Instance() {
		if (_instance == null) {
			synchronized (InvoiceList.class) {
				_instance = new InvoiceList();
			}
		}
		return _instance;
	}

	public void parseInvoice(JSONObject obj, int a_id) {

		JSONObject Invoice;
		try {
			Invoice = obj.optJSONObject("invoice");
			if (Invoice != null) {
				ModelMapHelper<InvoiceInfo> areamaper = new ModelMapHelper<InvoiceInfo>();
				InvoiceInfo info = areamaper.getObject(InvoiceInfo.class,
						Invoice);
				if (info != null) {

					if (Invoice.getJSONArray("payments") != null) {
						JSONArray arr = Invoice.getJSONArray("payments");
						PaymentsList.Instance().parsePayments(arr, a_id,
								info.invoice_number);
					}

					try {
						info.AppointmentId = a_id;
						info.save();
					} catch (Exception e) {
					}
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(InvoiceInfo.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<InvoiceInfo> lst = FieldworkApplication.Connection().find(
					InvoiceInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (InvoiceInfo i : lst) {
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

}
