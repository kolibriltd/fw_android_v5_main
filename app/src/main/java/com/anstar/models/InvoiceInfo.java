package com.anstar.models;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.model.mapper.ModelMapper;

public class InvoiceInfo extends ActiveRecordBase {

	public InvoiceInfo() {
	}

	@ModelMapper(JsonKey = "invoice_number")
	public int invoice_number = 0;
//	@ModelMapper(JsonKey = "approved")
//	public boolean approved = false;
//	@ModelMapper(JsonKey = "paid")
//	public boolean paid = false;
//	@ModelMapper(JsonKey = "payment_method")
//	public String payment_method = "";
//	@ModelMapper(JsonKey = "check_number")
//	public String check_number;
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";
	@ModelMapper(JsonKey = "tax_amount")
	public String tax_amount = "";
	@ModelMapper(JsonKey = "total")
	public String total = "";
	@ModelMapper(JsonKey = "discount")
	public int discount;
	@ModelMapper(JsonKey = "discount_amount")
	public String discount_amount = "";
	
	public int AppointmentId = 0;

	// private UpdateInfoDelegate m_delegate = null;

	// public static void AddInvoice(InvoiceInfo info, int appointment_id,
	// AppointmentInfo app, UpdateInfoDelegate delegate) {
	// try {
	// InvoiceInfo i_info = InvoiceInfo.UpdateInfo(info);
	// if (NetworkConnectivity.isConnected()
	// && app.status.equalsIgnoreCase("Completed")) {
	// i_info.UpdateInvoiceService(i_info, appointment_id, app,
	// delegate);
	// } else {
	// ServiceResponse res = new ServiceResponse();
	// res.StatusCode = 200;
	// delegate.UpdateSuccessFully(res);
	// i_info.invoice_number = Utils.getRandomInt();
	// i_info.save();
	// }
	// app.save();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// public void UpdateService(InvoiceInfo info, int appointment_id,
	// AppointmentInfo app, UpdateInfoDelegate delegate) {
	// m_delegate = delegate;
	// String json =
	// "{\"appointment_occurrence\":{\"price\":\"%s\",\"starts_at\":\"%s\",\"started_at_time\":\"%s\",\"finished_at_time\":\"%s\" ,\"invoice_attributes\":{\"paid\":\"%s\",\"payment_method\":\"%s\",\"check_number\":\"%s\"}}}";
	// String url = String
	// .format("appointment_occurrences/%d", appointment_id);
	// if (!info.payment_method.equalsIgnoreCase("Check")) {
	// check_number = "";
	// }
	// json = String.format(json, app.price, app.starts_at,
	// app.started_at_time, app.finished_at_time,
	// String.valueOf(info.paid), info.payment_method,
	// info.check_number);
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
	//
	// }
	//
	// public void UpdateInvoiceService(InvoiceInfo info, int appointment_id,
	// AppointmentInfo app, UpdateInfoDelegate delegate) {
	// m_delegate = delegate;
	// String json =
	// "{\"appointment_occurrence\":{\"price\":\"%s\",\"invoice_attributes\":{\"paid\":\"%s\",\"payment_method\":\"%s\",\"check_number\":\"%s\"}}}";
	// String url = String
	// .format("appointment_occurrences/%d", appointment_id);
	// if (!info.payment_method.equalsIgnoreCase("Check")) {
	// check_number = "";
	// }
	// json = String.format(json, app.price, String.valueOf(info.paid),
	// info.payment_method, info.check_number);
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
	//
	// }
	//
	// public static InvoiceInfo getInvoiceByAppId(int app_id) {
	//
	// try {
	// List<InvoiceInfo> list = FieldworkApplication.Connection().find(
	// InvoiceInfo.class,
	// CamelNotationHelper.toSQLName("AppointmentId") + "=?",
	// new String[] { String.valueOf(app_id) });
	// if (list != null && list.size() > 0) {
	// return list.get(0);
	// }
	// } catch (ActiveRecordException e) {
	// e.printStackTrace();
	// }
	// return null;
	//
	// }
	//
	// public static InvoiceInfo UpdateInfo(InvoiceInfo info) {
	//
	// try {
	// List<InvoiceInfo> list = FieldworkApplication.Connection().find(
	// InvoiceInfo.class,
	// CamelNotationHelper.toSQLName("AppointmentId") + "=?",
	// new String[] { String.valueOf(info.AppointmentId) });
	//
	// if (list != null && list.size() > 0) {
	// InvoiceInfo invoive = list.get(0);
	// invoive.copyFrom(info);
	// invoive.save();
	// return invoive;
	// } else {
	// InvoiceInfo newInvoice = FieldworkApplication.Connection()
	// .newEntity(InvoiceInfo.class);
	// newInvoice.copyFrom(info);
	// newInvoice.save();
	// return newInvoice;
	// }
	// } catch (ActiveRecordException e) {
	// e.printStackTrace();
	// }
	// return null;
	//
	// }
	//
	// public void sync(AppointmentInfo app) {
	// // String json =
	// //
	// "{\"appointment_occurrence\":{\"price\":\"%s\",\"starts_at\":\"%s\",\"started_at_time\":\"%s\",\"finished_at_time\":\"%s\" ,\"invoice_attributes\":{\"paid\":\"%s\",\"payment_method\":\"%s\",\"check_number\":\"%s\"}}}";
	// String json =
	// "{\"appointment_occurrence\":{\"price\":\"%s\",\"invoice_attributes\":{\"paid\":\"%s\",\"payment_method\":\"%s\",\"check_number\":\"%s\"}}}";
	// String url = String.format("appointment_occurrences/%d", AppointmentId);
	// if (!payment_method.equalsIgnoreCase("Check")) {
	// check_number = "";
	// }
	//
	// json = String.format(json, app.price, String.valueOf(paid),
	// payment_method, check_number);
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
