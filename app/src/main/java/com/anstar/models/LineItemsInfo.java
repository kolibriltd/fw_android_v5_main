package com.anstar.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.JsonCreator;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ModelProcess;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.list.AppointmentModelList;
import com.anstar.models.list.LineItemsList;
import com.anstar.models.list.ServiceLocationsList;
import com.anstar.models.list.TaxRateList;

public class LineItemsInfo extends ActiveRecordBase {
	public LineItemsInfo() {

	}

	@ModelMapper(JsonKey = "id", IsUnique = true)
	public int id = 0;
	@ModelMapper(JsonKey = "payable_id")
	public int payable_id = 0;
	@ModelMapper(JsonKey = "payable_type")
	public String payable_type = "";
	@ModelMapper(JsonKey = "type")
	public String type = "";
	@ModelMapper(JsonKey = "name")
	public String name = "";
	@ModelMapper(JsonKey = "quantity")
	public String quantity = "";
	@ModelMapper(JsonKey = "price")
	public String price = "";
	@ModelMapper(JsonKey = "total")
	public float total = 0;
	@ModelMapper(JsonKey = "taxable")
	public boolean taxable = false;
	@ModelMapper(JsonKey = "id")
	public int tempid = 0;
	public int WorkOrderId = 0;
	public boolean isDeleted = false;

	public PestsTypeInfo m_line_info = null;

	private UpdateInfoDelegate m_delegate = null;

	public void EditLineItems(UpdateInfoDelegate del) {
		try {
			if (NetworkConnectivity.isConnected()) {
				this.save();
				UpdateLineItemService(ModelProcess.Update, del);
			} else {
				if (this.id > 0)// online record edit
					this.tempid = (-1 * this.id);
				else
					// record added offline
					this.tempid = this.id;
				this.save();
				updateTaxAmount(this.WorkOrderId);
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 200;
				del.UpdateSuccessFully(res);
				AppointmentInfo.updateDirtyFlag(this.WorkOrderId);
			}
		} catch (Exception e) {
			del.UpdateFail("Please try again.");
		}
	}

	public void AddLineItems(UpdateInfoDelegate del) {
		try {
			LineItemsInfo info = FieldworkApplication.Connection().newEntity(
					LineItemsInfo.class);
			info.copyFrom(this);
			if (NetworkConnectivity.isConnected()) {
				info.save();
				UpdateLineItemService(ModelProcess.Insert, del);
			} else {
				info.id = Utils.getRandomInt();
				info.tempid = info.id;
				info.save();
				updateTaxAmount(this.WorkOrderId);
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 200;
				del.UpdateSuccessFully(res);
				AppointmentInfo.updateDirtyFlag(this.WorkOrderId);
			}
		} catch (Exception e) {
			del.UpdateFail("Please try again.");
		}
	}

	public void UpdateLineItemService(ServiceHelper.ModelProcess process,
			UpdateInfoDelegate delegate) {
		if (delegate != null) {
			m_delegate = delegate;
		}
		if (process == ModelProcess.Insert) {
			// {"payable_id":1,"payable_type":"Service","type":"service","name":"Residential Service","quantity":2.0,"price":100.0,"total":200.0,"taxable":true}
			HashMap<String, Object> lineHash = new HashMap<String, Object>();
			lineHash.put("payable_id", this.payable_id);
			lineHash.put("type", this.type.toString().toLowerCase());
			lineHash.put("name", this.name);
			lineHash.put("quantity", this.quantity);
			lineHash.put("price", this.price);
			lineHash.put("total", this.total);
			lineHash.put("payable_type", this.type);
			lineHash.put("taxable", this.taxable);
			JSONObject obj = JsonCreator.getJsonObject(lineHash);

			String url = String.format("work_orders/%d/line_items",
					this.WorkOrderId);
			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.POST, obj.toString());
			final int workorderid = this.WorkOrderId;
			caller.startRequest(new ServiceHelperDelegate() {

				@Override
				public void CallFinish(ServiceResponse res) {
					if (!res.isError()) {
						updateTaxAmount(workorderid);
						LineItemsList.Instance().refreshLineItems(
								LineItemsInfo.this.WorkOrderId,
								new UpdateInfoDelegate() {
									@Override
									public void UpdateSuccessFully(
											ServiceResponse res) {
										m_delegate.UpdateSuccessFully(res);
									}

									@Override
									public void UpdateFail(String ErrorMessage) {
										m_delegate.UpdateFail(ErrorMessage);
									}
								});
					}
				}

				@Override
				public void CallFailure(String ErrorMessage) {
					m_delegate.UpdateFail(ErrorMessage);

				}
			});

		} else if (process == ModelProcess.Update) {
			HashMap<String, Object> lineHash = new HashMap<String, Object>();
			lineHash.put("payable_id", this.payable_id);
			lineHash.put("type", this.type.toString().toLowerCase());
			lineHash.put("name", this.name);
			lineHash.put("quantity", this.quantity);
			lineHash.put("price", this.price);
			lineHash.put("total", this.total);
			lineHash.put("payable_type", this.payable_type);
			lineHash.put("taxable", this.taxable);
			JSONObject obj = JsonCreator.getJsonObject(lineHash);

			// String json =
			// "{\"appointment_occurrence\":{\"pests_targets_attributes\":[{\"pest_type_id\":%d}]}}";
			String url = String.format("work_orders/%d/line_items/%d",
					this.WorkOrderId, this.id);
			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.PUT, obj.toString());
			final int workorderid = this.WorkOrderId;
			caller.startRequest(new ServiceHelperDelegate() {

				@Override
				public void CallFinish(ServiceResponse res) {
					if (!res.isError()) {
						updateTaxAmount(workorderid);
						LineItemsList.Instance().refreshLineItems(
								LineItemsInfo.this.WorkOrderId,
								new UpdateInfoDelegate() {
									@Override
									public void UpdateSuccessFully(
											ServiceResponse res) {
										m_delegate.UpdateSuccessFully(res);
									}

									@Override
									public void UpdateFail(String ErrorMessage) {
										m_delegate.UpdateFail(ErrorMessage);
									}
								});
					}
				}

				@Override
				public void CallFailure(String ErrorMessage) {
					m_delegate.UpdateFail(ErrorMessage);

				}
			});

		} else if (process == ModelProcess.Delete) {
			if (this.id == -1) {
				return;
			}
			String json = "{\"work_order\":{\"line_items_attributes\":[{\"id\":%d,\"_destroy\":true}]}}";
			String url = String.format("work_orders/%d", this.WorkOrderId);
			json = String.format(json, this.id);
			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.PUT, json);
			final int workorderid = this.WorkOrderId;
			caller.startRequest(new ServiceHelperDelegate() {

				@Override
				public void CallFinish(ServiceResponse res) {
					try {
						delete();
						updateTaxAmount(workorderid);
					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}
					m_delegate.UpdateSuccessFully(res);
				}

				@Override
				public void CallFailure(String ErrorMessage) {
					m_delegate.UpdateFail(ErrorMessage);
				}
			});
		}
	}

	private void updateTaxAmount(int workorderid) {
		AppointmentInfo appointmentInfo = AppointmentModelList.Instance()
				.getAppointmentById(workorderid);
		ServiceLocationsInfo info = ServiceLocationsList.Instance()
				.getServiceLocationById(appointmentInfo.service_location_id);

		if (info.tax_rate_id != 0) {
			TaxRates trate = TaxRateList.Instance().getTaxRateByid(
					info.tax_rate_id);
			if (trate != null) {
				ArrayList<LineItemsInfo> lineitems = LineItemsList.Instance()
						.load(workorderid);
				if (lineitems != null) {
					float tax = 0;
					float totaldisc = 0;
					for (LineItemsInfo item : lineitems) {
						float p = item.total;
						float disc = (p * Utils.ConvertToFloat(appointmentInfo.discount)) / 100;
						totaldisc += disc;
						if (item.taxable) {
							tax += ((p - disc) * trate.total_sales_tax);
						}
					}
					try {
						List<AppointmentInfo> apps = FieldworkApplication
								.Connection().find(AppointmentInfo.class,
										CamelNotationHelper.toSQLName("id")+ "=?",
										new String[] { String.valueOf(workorderid) });
						apps.get(0).tax_amount = String.valueOf(tax);
						apps.get(0).discount_amount = String.valueOf(totaldisc);
						apps.get(0).status_id = Utils.getRandomInt();
						apps.get(0).save();

					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}
					if (NetworkConnectivity.isConnected())
						AppointmentInfo.UpdateTaxAmount(tax,totaldisc, workorderid);
				}
			}
		}
	}

	public static void DeleteLineItem(int id, UpdateInfoDelegate del) {
		try {
			List<LineItemsInfo> lst = FieldworkApplication.Connection().find(
					LineItemsInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(id) });
			if (lst != null) {
				if (lst.size() > 0) {
					if (lst.get(0).id < 0) {
						lst.get(0).delete();
						lst.get(0).updateTaxAmount(lst.get(0).WorkOrderId);
						ServiceResponse res = new ServiceResponse();
						res.StatusCode = 200;
						del.UpdateSuccessFully(res);
					} else {
						if (lst.get(0).tempid < 0) {// offline edited record
							if (NetworkConnectivity.isConnected()) {
								lst.get(0).UpdateLineItemService(
										ModelProcess.Delete, del);
							} else {
								lst.get(0).isDeleted = true;
								lst.get(0).tempid = (-1 * lst.get(0).id);
								lst.get(0).save();
								lst.get(0).updateTaxAmount(lst.get(0).WorkOrderId);
								ServiceResponse res = new ServiceResponse();
								res.StatusCode = 200;
								del.UpdateSuccessFully(res);
							}
						} else {
							lst.get(0).isDeleted = true;
							lst.get(0).tempid = (-1 * lst.get(0).id);
							lst.get(0).save();
							if (NetworkConnectivity.isConnected()) {
								lst.get(0).UpdateLineItemService(
										ModelProcess.Delete, del);
							} else {
								lst.get(0).updateTaxAmount(lst.get(0).WorkOrderId);
								ServiceResponse res = new ServiceResponse();
								res.StatusCode = 200;
								del.UpdateSuccessFully(res);
							}
						}
					}
				}
			} else {
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 1;
				del.UpdateFail("Please try again");
			}
		} catch (Exception e) {
		}
	}

	private ServiceHelperDelegate m_service_delegate = new ServiceHelperDelegate() {

		@Override
		public void CallFinish(ServiceResponse res) {
			if (!res.isError()) {
				try {
					if (LineItemsInfo.this.id < 0) {
						JSONObject main = new JSONObject(res.RawResponse);
						JSONObject litem = main.getJSONObject("line_item");
						ModelMapHelper<LineItemsInfo> maper = new ModelMapHelper<LineItemsInfo>();
						LineItemsInfo info = maper.getObject(
								LineItemsInfo.class, litem);
						LineItemsInfo.this.id = info.id;
						LineItemsInfo.this.tempid = info.id;
					} else {
						LineItemsInfo.this.tempid = LineItemsInfo.this.id;
					}
					LineItemsInfo.this.save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void CallFailure(String ErrorMessage) {
		}
	};

	public void syncAdd(boolean flag) {
		HashMap<String, Object> lineHash = new HashMap<String, Object>();
		lineHash.put("payable_id", this.payable_id);
		lineHash.put("type", this.type.toString().toLowerCase());
		lineHash.put("name", this.name);
		lineHash.put("quantity", this.quantity);
		lineHash.put("price", this.price);
		lineHash.put("total", this.total);
		lineHash.put("payable_type", this.payable_type);
		lineHash.put("taxable", this.taxable);
		JSONObject obj = JsonCreator.getJsonObject(lineHash);
		if (flag) {

			String url = String.format("work_orders/%d/line_items",
					this.WorkOrderId);
			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.POST, obj.toString());
			caller.startRequest(m_service_delegate);

		} else {
			String url = String.format("work_orders/%d/line_items/%d",
					this.WorkOrderId, this.id);

			ServiceCaller caller = new ServiceCaller(url,
					ServiceCaller.RequestMethod.PUT, obj.toString());
			caller.startRequest(m_service_delegate);
		}
	}

	public void syncDelete() {
		String json = "{\"work_order\":{\"line_items_attributes\":[{\"id\":%d,\"_destroy\":true}]}}";
		String url = String.format("work_orders/%d", this.WorkOrderId);
		json = String.format(json, this.id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.PUT, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				// m_delegate.UpdateSuccessFully(res);
				try {
					delete();
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				// m_delegate.UpdateFail(ErrorMessage);
			}
		});
	}
	
	public String DeleteLineObjectJson() {
		String json = "";
		if (this.id > 0) {
			json = "{\"id\":%d,\"_destroy\":true}";
			json = String.format(json, this.id);
		}
		return json;
	}

}
