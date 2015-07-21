package com.anstar.models;

import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;

public class PaymentInfo extends ActiveRecordBase {

	public PaymentInfo() {

	}

	@ModelMapper(JsonKey = "id", IsUnique = true)
	public int PaymentId = 0;
	@ModelMapper(JsonKey = "payment_date")
	public String payment_date = "";
	@ModelMapper(JsonKey = "amount")
	public String amount = "";
	@ModelMapper(JsonKey = "payment_method")
	public String payment_method = "";
	@ModelMapper(JsonKey = "check_number")
	public String check_number = "";
	@ModelMapper(JsonKey = "created_from_mobile")
	public boolean created_from_mobile = false;
	
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";
	
	public int AppointmentId = 0;
	public int invoice_number = 0;

	// public int syncId = 0;

	private UpdateInfoDelegate m_delegate = null;

	public static void AddPaymentInfo(PaymentInfo info, int appointment_id,
			AppointmentInfo app, UpdateInfoDelegate delegate) {
		try {
			PaymentInfo i_info = PaymentInfo.UpdateInfo(info);
			// if (NetworkConnectivity.isConnected()) {
			// AppointmentInfo.savePrice(app.getID());
			// if (app.status.equalsIgnoreCase("Complete")
			// || app.status.equalsIgnoreCase("Completed")) {
			//
			// i_info.UpdatePaymentService(i_info, appointment_id, app,
			// delegate);
			// } else {
			ServiceResponse res = new ServiceResponse();
			res.StatusCode = 200;
			delegate.UpdateSuccessFully(res);
			info.AppointmentId = app.id;
			// i_info.syncId = Utils.getRandomInt();
			i_info.save();
			// }
			app.save();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public void UpdatePaymentService(PaymentInfo info, int appointment_id,
	// AppointmentInfo app, UpdateInfoDelegate delegate) {
	// m_delegate = delegate;
	// String json =
	// "{\"appointment_occurrence\":{\"invoice_attributes\":{\"payments_attributes\":[{\"id\":\"%s\",\"amount\":\"%s\", \"payment_method\":\"%s\", \"check_number\":\"%s\",\"created_from_mobile\": \"%s\"}]}}}";
	// String url = String
	// .format("appointment_occurrences/%d", appointment_id);
	// if (!info.payment_method.equalsIgnoreCase("Check")) {
	// check_number = "";
	// }
	// String id = "";
	// if (info.PaymentId > 0) {
	// id = "" + info.PaymentId;
	// }
	// info.created_from_mobile = true;
	// json = String.format(json, id, info.amount, info.payment_method,
	// info.check_number, String.valueOf(info.created_from_mobile));
	//
	// ServiceCaller caller = new ServiceCaller(url,
	// ServiceCaller.RequestMethod.PUT, json);
	// caller.startRequest(new ServiceHelperDelegate() {
	//
	// @Override
	// public void CallFinish(ServiceResponse res) {
	// m_delegate.UpdateSuccessFully(res);
	// }
	//
	// @Override
	// public void CallFailure(String ErrorMessage) {
	// m_delegate.UpdateFail(ErrorMessage);
	// }
	// });
	// }

	public static PaymentInfo getPaymentsInfoByAppId(int app_id) {

		try {
			List<PaymentInfo> list = FieldworkApplication.Connection().find(
					PaymentInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(app_id) });
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static PaymentInfo UpdateInfo(PaymentInfo info) {
		try {
			List<PaymentInfo> list = FieldworkApplication.Connection().find(
					PaymentInfo.class,
					CamelNotationHelper.toSQLName("AppointmentId") + "=?",
					new String[] { String.valueOf(info.AppointmentId) });

			if (list != null && list.size() > 0) {
				PaymentInfo p = list.get(0);
				p.copyFrom(info);
				p.save();
				return p;
			} else {
				PaymentInfo p = FieldworkApplication.Connection().newEntity(
						PaymentInfo.class);
				p.copyFrom(info);
				p.save();
				return p;
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	// public void sync(AppointmentInfo app) {
	// AppointmentInfo.savePrice(app.getID());
	// // String json =
	// //
	// "{\"appointment_occurrence\":{\"price\":\"%s\",\"starts_at\":\"%s\",\"started_at_time\":\"%s\",\"finished_at_time\":\"%s\" ,\"invoice_attributes\":{\"paid\":\"%s\",\"payment_method\":\"%s\",\"check_number\":\"%s\"}}}";
	// String json =
	// "{\"appointment_occurrence\":{\"invoice_attributes\":{\"payments_attributes\":[{\"id\":\"%s\",\"amount\":\"%s\", \"payment_method\":\"%s\", \"check_number\":\"%s\",\"created_from_mobile\": \"%s\"}]}}}";
	// String url = String.format("appointment_occurrences/%d", AppointmentId);
	// if (!payment_method.equalsIgnoreCase("Check")) {
	// check_number = "";
	// }
	// String id = "";
	// if (PaymentId > 0) {
	// id = "" + PaymentId;
	// }
	// created_from_mobile = true;
	// json = String.format(json, id, amount, payment_method, check_number,
	// String.valueOf(created_from_mobile));
	//
	// ServiceCaller caller = new ServiceCaller(url,
	// ServiceCaller.RequestMethod.PUT, json);
	// caller.startRequest(new ServiceHelperDelegate() {
	//
	// @Override
	// public void CallFinish(ServiceResponse res) {
	// loadappointment();
	// }
	//
	// @Override
	// public void CallFailure(String ErrorMessage) {
	//
	// }
	// });
	//
	// }
	//
	// public void loadappointment() {
	// AppointmentModelList.Instance().ClearDB();
	// try {
	// AppointmentModelList.Instance().load(
	// new ModelDelegate<AppointmentInfo>() {
	// @Override
	// public void ModelLoaded(ArrayList<AppointmentInfo> list) {
	// }
	//
	// @Override
	// public void ModelLoadFailedWithError(String error) {
	// }
	// });
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
