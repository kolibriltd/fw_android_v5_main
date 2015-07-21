package com.anstar.models.list;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.PaymentInfo;

public class PaymentsList {
	private PaymentsList() {

	}

	private static volatile PaymentsList _instance = null;

	public static PaymentsList Instance() {
		if (_instance == null) {
			synchronized (PaymentsList.class) {
				_instance = new PaymentsList();
			}
		}
		return _instance;
	}

	public void parsePayments(JSONArray arr, int a_id, int invoice_number) {

		try {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);

				ModelMapHelper<PaymentInfo> areamaper = new ModelMapHelper<PaymentInfo>();
				PaymentInfo info = areamaper.getObject(PaymentInfo.class, obj);
				if (info != null) {
					if (info.created_from_mobile) {
						info.AppointmentId = a_id;
						// info.syncId = info.PaymentId;
						info.invoice_number = invoice_number;
						info.save();
					}
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(PaymentInfo.class);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void ClearDB(int appid) {
		try {
			List<PaymentInfo> lst = FieldworkApplication.Connection().find(
					PaymentInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { "" + appid });
			if (lst != null && lst.size() > 0) {
				for (PaymentInfo i : lst) {
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

}
