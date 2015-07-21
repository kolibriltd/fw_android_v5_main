package com.anstar.models;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.ModelDelegate;
import com.anstar.models.list.AttachmentsList;
import com.anstar.models.list.CustomerList;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.InvoiceList;
import com.anstar.models.list.LineItemsList;
import com.anstar.models.list.MaterialUsagesList;
import com.anstar.models.list.MaterialUsagesRecordsList;
import com.anstar.models.list.PdfFormsList;
import com.anstar.models.list.TargetPestList;
import com.anstar.models.list.TrapList;

public class WorkHistroyInfo extends ActiveRecordBase {

	@ModelMapper(JsonKey = "created_at")
	public String created_at;
	@ModelMapper(JsonKey = "customer_signature")
	public String customer_signature;
	@ModelMapper(JsonKey = "duration")
	public int duration;
	@ModelMapper(JsonKey = "id")
	public int id;
	@ModelMapper(JsonKey = "report_number")
	public int report_number = 0;
	@ModelMapper(JsonKey = "notes")
	public String notes;
	@ModelMapper(JsonKey = "instructions")
	public String instructions;
	@ModelMapper(JsonKey = "price2")
	public String price;
	@ModelMapper(JsonKey = "starts_at")
	public String starts_at;
	@ModelMapper(JsonKey = "status")
	public String status;
	@ModelMapper(JsonKey = "technician_signature")
	public String technician_signature;
	@ModelMapper(JsonKey = "technician_signature_name")
	public String technician_signature_name;
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at;
	@ModelMapper(JsonKey = "customer_id")
	public int customer_id;
	@ModelMapper(JsonKey = "service_location_id")
	public int service_location_id;
	@ModelMapper(JsonKey = "ends_at")
	public String ends_at;
	@ModelMapper(JsonKey = "started_at_time")
	public String started_at_time;
	@ModelMapper(JsonKey = "finished_at_time")
	public String finished_at_time;
	@ModelMapper(JsonKey = "square_feet")
	public int square_feet = 0;
	@ModelMapper(JsonKey = "wind_direction")
	public String wind_direction = "";
	@ModelMapper(JsonKey = "wind_speed")
	public String wind_speed = "";
	@ModelMapper(JsonKey = "temperature")
	public String temperature = "";
	// @ModelMapper(JsonKey = "has_attached_form")
	// public boolean has_attached_form = false;
	@ModelMapper(JsonKey = "worker_lat")
	public String worker_lat = "";
	@ModelMapper(JsonKey = "worker_lng")
	public String worker_lng = "";
	@ModelMapper(JsonKey = "starts_at_time")
	public String starts_at_time;
	@ModelMapper(JsonKey = "ends_at_time")
	public String ends_at_time;
	@ModelMapper(JsonKey = "discount")
	public int discount = 0; // percents, fractional here
	@ModelMapper(JsonKey = "discount_amount")
	public String discount_amount = ""; // discount value, read-only
	@ModelMapper(JsonKey = "tax_amount")
	public String tax_amount = "";
	@ModelMapper(JsonKey = "purchase_order_no")
	public String purchase_order_no = "";
	@ModelMapper(JsonKey = "starts_at_date")
	public String starts_at_date = "";

	@ModelMapper(JsonKey = "recommendation_ids", IsArray = true)
	public ArrayList<String> recommendation_ids;
	@ModelMapper(JsonKey = "appointment_condition_ids", IsArray = true)
	public ArrayList<String> appointment_condition_ids;

	public String json_string;
	public int inspection_records;
	public ArrayList<MaterialUsage> m_material_usages;
	public HashMap<Integer ,ArrayList<MaterialUsageRecords>> m_material_usages_records;
	public String pests_targets;

	public int customer_sign_id = 0;
	public int Tech_sign_id = 0;
	public int status_id = 0;
	public int notes_id = 0;
	public int environment_id = 0;

	public WorkHistroyInfo getDetails() {
		WorkHistroyInfo info = null;
		try {
			JSONObject data = new JSONObject(this.json_string);
			if (data != null) {
				ModelMapHelper<WorkHistroyInfo> mapper = new ModelMapHelper<WorkHistroyInfo>();
				info = mapper.getObject(WorkHistroyInfo.class, data);
				JSONArray musages = data.getJSONArray("material_usages");
				if (musages != null) {
					info.m_material_usages = new ArrayList<MaterialUsage>();
					info.m_material_usages_records = new HashMap<Integer, ArrayList<MaterialUsageRecords>>();
					for (int i = 0; i < musages.length(); i++) {
						JSONObject usages = musages.getJSONObject(i);
						ModelMapHelper<MaterialUsage> maper = new ModelMapHelper<MaterialUsage>();
						MaterialUsage minfo = maper.getObject(
								MaterialUsage.class, usages);
						if (usages.toString()
								.contains("material_usage_records")) {
							JSONArray arr = usages
									.getJSONArray("material_usage_records");
							info.m_material_usages.add(minfo);
							ArrayList<MaterialUsageRecords> mrecords = new ArrayList<MaterialUsageRecords>();
							for (int j = 0; j < arr.length(); j++) {
								JSONObject note = arr.getJSONObject(j);
								ModelMapHelper<MaterialUsageRecords> notemapper = new ModelMapHelper<MaterialUsageRecords>();
								MaterialUsageRecords muinfo = notemapper
										.getObject(MaterialUsageRecords.class,
												note);
								mrecords.add(muinfo);
								// JSONArray targets =
								// note.getJSONArray("target_pests");
								// if (targets != null) {
								// ArrayList<String> id = new
								// ArrayList<String>();
								// for (int j = 0; j < targets.length(); j++) {
								// JSONObject target = targets.getJSONObject(j);
								// id.add(target.optString("pest_type_id"));
								// }
								// muinfo.Pest_ids = Utils.Instance().join(id,
								// ",");
								// }
								// if (info != null) {
								// try {
								// info.MaterialUsageId = materialusageId;
								// info.save();
								// } catch (Exception e) {
								// Utils.LogException(e);
								// }
								// }

							}
							info.m_material_usages_records.put(minfo.id, mrecords);
						}

					}

				}

				// CustomerInfo customer = CustomerList.Instance()
				// .getCustomerById(info.customer_id);
				// if (customer != null) {
				// customer.RetriveData(null);
				// } else {
				// CustomerInfo cust = FieldworkApplication
				// .Connection().newEntity(
				// CustomerInfo.class);
				// cust.id = info.customer_id;
				// cust.RetriveData(null);
				// }
				//
				// InspectionList.Instance()
				// .parseInspectionRecords(data, info.id);
				// InvoiceList.Instance().parseInvoice(data,
				// info.id);
				// MaterialUsagesList.Instance()
				// .parseMatrialUsages(data, info.id,
				// false);
				// TargetPestList.Instance().parseTargetPests(
				// data, info.id);
				// LineItemsList.Instance().parseLineItems(data,
				// info.id);
				// if (info != null) {
				// WorkHistroyInfo.this.copyFrom(info);
				// WorkHistroyInfo.this.save();
				// }
				// m_delegate.UpdateSuccessFully(res);
				// if (delegate != null) {
				// delegate.UpdateSuccessFully(WorkHistroyInfo.this);
				// }

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

}
