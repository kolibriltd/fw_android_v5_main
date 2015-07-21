package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.internetbroadcast.ServiceCallerSync;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.MaterialUsage.UpdateMUInfoDelegate;
import com.anstar.models.list.InspectionList;
import com.anstar.models.list.InspectionPestsList;
import com.anstar.models.list.MaterialUsagesRecordsList;

public class InspectionInfo extends ActiveRecordBase {

	public InspectionInfo() {

	}

	@ModelMapper(JsonKey = "id", IsUnique = true)
	public int id = 0;
	@ModelMapper(JsonKey = "notes")
	public String notes = "";
	@ModelMapper(JsonKey = "barcode")
	public String barcode = "";
	@ModelMapper(JsonKey = "location_area_id")
	public int LocationId = 0;
	@ModelMapper(JsonKey = "evidence")
	public String evidence = "";
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";

	// added on 27/11/2013
	@ModelMapper(JsonKey = "trap_number")
	public String trap_number = "";
	@ModelMapper(JsonKey = "trap_type_id")
	public int trap_type_id = 0;
	@ModelMapper(JsonKey = "scanned_on")
	public String scanned_on = "";
	@ModelMapper(JsonKey = "trap_condition_id")
	public int trap_condition_id = 0;
	@ModelMapper(JsonKey = "bait_condition_id")
	public int bait_condition_id = 0;
	@ModelMapper(JsonKey = "removed")
	public boolean removed = false;
	@ModelMapper(JsonKey = "exception")
	public String exception = "";
	//

	public int AppointmentId = 0;
	public boolean isDeleted = false;
	public String Material_ids;
	public boolean isForUnchecked = false;

	private UpdateMUInfoDelegate m_delegate = null;

	public static void AddInspectionRecordNew(int appointment_id,
			ArrayList<InspectionPest> m_pests, InspectionInfo inspection,
			UpdateMUInfoDelegate delegate) {
		inspection.deleteRelatedPestsRecords();
		try {
			InspectionPest ins_pests = null;
			for (int i = 0; i < m_pests.size(); i++) {
				ins_pests = FieldworkApplication.Connection().newEntity(
						InspectionPest.class);
				ins_pests.id = Utils.getRandomInt();
				ins_pests.count = m_pests.get(i).count;
				ins_pests.pest_type_id = m_pests.get(i).pest_type_id;
				ins_pests.inspection_id = inspection.id;
				ins_pests.save();
			}
		} catch (ActiveRecordException e) {
			Utils.LogException(e);
		}
		if (NetworkConnectivity.isConnected()) {
			if (inspection.id > 0) {
				inspection.syncDelete();
			}
			inspection.UpdateRecords(appointment_id, m_pests, inspection,
					delegate);
			try {
				BaseModel<InspectionInfo> baseModel = new BaseModel<InspectionInfo>(
						InspectionInfo.class);
				InspectionInfo i_info = baseModel.isExists(inspection);
				if (i_info != null) {
					i_info.evidence = inspection.evidence;
					i_info.isDeleted = false;
					i_info.bait_condition_id = inspection.bait_condition_id;
					i_info.trap_condition_id = inspection.trap_condition_id;
					i_info.trap_type_id = inspection.trap_type_id;
					i_info.removed = inspection.removed;
					i_info.exception = inspection.exception;
					i_info.notes = inspection.notes;
					i_info.trap_number = inspection.trap_number;
					i_info.scanned_on = inspection.scanned_on;
					i_info.save();
				}
			} catch (Exception e) {
				Utils.LogException(e);
			}

		} else { // When network is not connected
			try {
				if (inspection.id > 0) {
					inspection.isDeleted = true;
					inspection.save();
				}
				InspectionInfo info = FieldworkApplication.Connection()
						.newEntity(InspectionInfo.class);
				info.copyFrom(inspection);
				info.id = Utils.getRandomInt();
				info.isDeleted = false;
				info.save();
				List<InspectionPest> lst = FieldworkApplication.Connection()
						.find(InspectionPest.class,
								CamelNotationHelper.toSQLName("inspection_id")
										+ "=?",
								new String[] { String.valueOf(inspection.id) });
				if (lst != null) {
					for (InspectionPest ip : lst) {
						ip.inspection_id = info.id;
						ip.save();
					}
				}
			} catch (ActiveRecordException e) {
				e.printStackTrace();
			}
			if (inspection.id < 0) {
				try {
					inspection.delete();
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
			}
			ServiceResponse res = new ServiceResponse();
			res.StatusCode = 200;
			delegate.UpdateSuccessFully(res);
			AppointmentInfo.updateDirtyFlag(appointment_id);
		}

	}

	public static void AddInspectionRecord(int appointment_id,
			ArrayList<InspectionPest> m_pests, InspectionInfo inspection,
			UpdateMUInfoDelegate delegate) {

		if (inspection.id > 0) { // When Edit
			// Delete existing related pests
			inspection.deleteRelatedPestsRecords();
			// Add new pest with inspectionId
			try {
				InspectionPest ins_pests = null;
				for (int i = 0; i < m_pests.size(); i++) {
					ins_pests = FieldworkApplication.Connection().newEntity(
							InspectionPest.class);
					ins_pests.id = Utils.getRandomInt();
					ins_pests.count = m_pests.get(i).count;
					ins_pests.pest_type_id = m_pests.get(i).pest_type_id;
					ins_pests.inspection_id = inspection.id;
					ins_pests.save();
				}
			} catch (ActiveRecordException e) {
				Utils.LogException(e);
			}
			if (NetworkConnectivity.isConnected()) {
				// delete the existing inspection record from the service
				inspection.syncDelete();
				// add the record on service
				inspection.UpdateRecords(appointment_id, m_pests, inspection,
						delegate);
				try {
					// Find existing record and update it
					BaseModel<InspectionInfo> baseModel = new BaseModel<InspectionInfo>(
							InspectionInfo.class);
					InspectionInfo i_info = baseModel.isExists(inspection);
					if (i_info != null) {
						i_info.evidence = inspection.evidence;
						i_info.isDeleted = false;
						i_info.bait_condition_id = inspection.bait_condition_id;
						i_info.trap_condition_id = inspection.trap_condition_id;
						i_info.trap_type_id = inspection.trap_type_id;
						i_info.removed = inspection.removed;
						i_info.exception = inspection.exception;
						i_info.notes = inspection.notes;
						i_info.trap_number = inspection.trap_number;
						i_info.scanned_on = inspection.scanned_on;
						i_info.save();
					}
				} catch (Exception e) {
					Utils.LogException(e);
				}

			} else { // When network is not connected
				try {
					// Flag existing record to DELETED
					inspection.isDeleted = true;
					inspection.save();
					// Create new record and assign nagative id for future sync
					InspectionInfo info = FieldworkApplication.Connection()
							.newEntity(InspectionInfo.class);
					info.copyFrom(inspection);
					info.id = Utils.getRandomInt();
					info.isDeleted = false;
					info.save();
					// Update related pest with new nagative Id
					List<InspectionPest> lst = FieldworkApplication
							.Connection().find(
									InspectionPest.class,
									CamelNotationHelper
											.toSQLName("inspection_id") + "=?",
									new String[] { String
											.valueOf(inspection.id) });
					if (lst != null) {
						for (InspectionPest ip : lst) {
							ip.inspection_id = info.id;
							ip.save();
						}
					}
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 200;
				delegate.UpdateSuccessFully(res);
				AppointmentInfo.updateDirtyFlag(appointment_id);
			}

		} else { // If ID < 0

			inspection.deleteRelatedPestsRecords();
			try {
				int cnt = FieldworkApplication.Connection().delete(
						InspectionInfo.class,
						CamelNotationHelper.toSQLName("id") + "=?",
						new String[] { String.valueOf(inspection.id) });
				Utils.LogInfo(String.format(
						"%d record redelted of Inspection for ID : %d", cnt,
						inspection.id));
			} catch (Exception ex) {
				Utils.LogException(ex);
			}

			try {
				InspectionPest ins_pests = null;
				for (int i = 0; i < m_pests.size(); i++) {

					ins_pests = FieldworkApplication.Connection().newEntity(
							InspectionPest.class);

					ins_pests.id = Utils.getRandomInt();
					ins_pests.count = m_pests.get(i).count;
					ins_pests.pest_type_id = m_pests.get(i).pest_type_id;
					ins_pests.inspection_id = inspection.id;
					ins_pests.save();
				}
			} catch (ActiveRecordException e) {
				Utils.LogException(e);
			}
			if (NetworkConnectivity.isConnected()) {

				// directly call for add new record on service
				inspection.UpdateRecords(appointment_id, m_pests, inspection,
						delegate);
				// add new database record
				InspectionInfo info;
				try {
					info = FieldworkApplication.Connection().newEntity(
							InspectionInfo.class);

					info.copyFrom(inspection);
					info.id = Utils.getRandomInt() * -1;
					info.save();

					// Update related pest with new nagative Id
					List<InspectionPest> lst = FieldworkApplication
							.Connection().find(
									InspectionPest.class,
									CamelNotationHelper
											.toSQLName("inspection_id") + "=?",
									new String[] { String
											.valueOf(inspection.id) });
					if (lst != null) {
						for (InspectionPest ip : lst) {
							ip.inspection_id = info.id;
							ip.save();
						}
					}
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
			} else {
				// add new database record with nagative id
				InspectionInfo info;
				try {
					info = FieldworkApplication.Connection().newEntity(
							InspectionInfo.class);
					info.copyFrom(inspection);
					info.id = Utils.getRandomInt();
					info.AppointmentId = appointment_id;
					info.save();

					// Update related pest with new nagative Id
					List<InspectionPest> lst = FieldworkApplication
							.Connection().find(
									InspectionPest.class,
									CamelNotationHelper
											.toSQLName("inspection_id") + "=?",
									new String[] { String
											.valueOf(inspection.id) });
					if (lst != null) {
						for (InspectionPest ip : lst) {
							ip.inspection_id = info.id;
							ip.save();
						}
					}

				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 200;
				delegate.UpdateSuccessFully(res);
				AppointmentInfo.updateDirtyFlag(appointment_id);
			}

		}
	}

	public void deleteRelatedPestsRecords() {
		try {
			int deletedRecords = FieldworkApplication.Connection().delete(
					InspectionPest.class,
					CamelNotationHelper.toSQLName("inspection_id") + "=?",
					new String[] { String.valueOf(this.id) });
			Utils.LogInfo(String.format(
					"%d Inspection Pest Record Deleted for Inspection Id %d",
					deletedRecords, this.id));
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public void UpdateRecords(int app_id, ArrayList<InspectionPest> records,
			InspectionInfo info, final UpdateMUInfoDelegate delegate) {
		m_delegate = delegate;
		String json = "";
		String buffer = new String();
		String buffer_material = new String();

		ArrayList<String> arr = new ArrayList<String>();
		ArrayList<String> material_arr = new ArrayList<String>();
		// json =
		// "{\"appointment_occurrence\":{\"inspection_records_attributes\":[{\"barcode\":\"%s\",\"location_area_id\":%d,\"evidence\":\"%s\",\"pests_records_attributes\":[PEST],\"material_usages_attributes\":[MATERIAL]}]}}";
		json = "{\"work_order\":{\"inspection_records_attributes\":[{\"barcode\":\"%s\",\"location_area_id\":%d,\"evidence\":\"%s\",\"notes\":\"%s\",\"trap_number\":\"%s\",\"trap_type_id\":%d,\"scanned_on\":\"%s\",\"trap_condition_id\":%d,\"bait_condition_id\":%d,\"removed\":%s,\"exception\":\"%s\",\"pests_records_attributes\":[PEST],\"material_usages_attributes\":[MATERIAL]}]}}";
		if (info.evidence == null || info.evidence.length() <= 0) {
			json = json.replace("\"evidence\":\"%s\"", "\"evidence\":null");
		}
		// if (info.trap_number.length() <= 0) {
		// json = json.replace("\"trap_number\":\"%s\"",
		// "\"trap_number\":null");
		// }
		// if (info.scanned_on.length() <= 0) {
		// json = json.replace("\"scanned_on\":\"%s\"", "\"scanned_on\":null");
		// }
		// if (info.exception.length() <= 0) {
		// json = json.replace("\"exception\":\"%s\"", "\"exception\":null");
		// }

		String url = String.format("work_orders/%d", app_id);
		String pests_json = "{\"pest_type_id\":%d,\"count\":%d}";
		for (int i = 0; i < records.size(); i++) {
			String record_json = String.format(pests_json,
					records.get(i).pest_type_id, records.get(i).count);
			arr.add(record_json);
		}
		if (info.Material_ids != null && info.Material_ids.length() > 0) {
			if (info.Material_ids.contains(",")) {
				String[] ids = info.Material_ids.split(",");
				for (String s : ids) {
					String material_json = info.GetMaterialJson(Utils
							.ConvertToInt(s));
					material_arr.add(material_json);
				}
			} else {
				String material_json = info.GetMaterialJson(Utils
						.ConvertToInt(info.Material_ids));
				material_arr.add(material_json);
			}
		}
		buffer_material = Utils.Instance().join(material_arr, ",");
		buffer = Utils.Instance().join(arr, ",");
		// trap_number
		// trap_type_id
		// scanned_on
		// trap_condition_id
		// bait_condition_id
		// removed
		// exception
		if (info.evidence != null && info.evidence.length() > 0) {
			json = String.format(json, info.barcode, info.LocationId,
					info.evidence, info.notes, info.trap_number,
					info.trap_type_id, info.scanned_on, info.trap_condition_id,
					info.bait_condition_id, info.removed, info.exception);
		} else {
			json = String.format(json, info.barcode, info.LocationId,
					info.notes, info.trap_number, info.trap_type_id,
					info.scanned_on, info.trap_condition_id,
					info.bait_condition_id, info.removed, info.exception);
		}

		if (records.size() > 0) {
			json = json.replace("PEST", buffer.toString());
		} else {
			json = json.replace(",\"pests_records_attributes\":[PEST]", "");
		}
		if (info.Material_ids != null && info.Material_ids.length() > 0) {
			json = json.replace("MATERIAL", buffer_material.toString());
		} else {
			json = json.replace(",\"material_usages_attributes\":[MATERIAL]",
					"");
		}
		Utils.LogInfo("INSPECTION_JSON------>>>>>>>>>>>" + json);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.PUT, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				m_delegate.UpdateSuccessFully(res);
			}

			@Override
			public void CallFailure(String ErrorMessage) {
				m_delegate.UpdateFail(ErrorMessage);
			}
		});
	}

	public String GetMaterialJson(int material_id) {
		String main_json = "";
		MaterialUsage info = MaterialUsage.getMaterialUsageById(material_id);
		if (info != null) {
			ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
					.Instance().getMaterialRecordsByUsageId(material_id);
			StringBuffer buffer = new StringBuffer();
			ArrayList<String> arr = new ArrayList<String>();
			ArrayList<String> arr_targets = new ArrayList<String>();
			String json = "{\"material_id\":%d, \"material_usage_records_attributes\":[##]}";
			String locationJson = "{\"location_area_id\":%d,\"dilution_rate_id\":%d,\"application_method\":\"%s\",\"amount\":%s,\"measurement\":\"%s\",\"device\":\"%s\",\"application_device_type_id\":%d,\"lot_number\":\"%s\",\"application_method_id\":%d,\"target_pests_attributes\":[##]}";
			String target_pest = "{\"pest_type_id\": %d}";

			MaterialUsageRecords temp_record = MaterialUsagesRecordsList
					.Instance().getMaterialRecordByUsageId(material_id);
			ArrayList<MaterialUsageTargetPestInfo> m_targets = new ArrayList<MaterialUsageTargetPestInfo>();
			if (temp_record.Pest_ids != null
					&& temp_record.Pest_ids.length() > 0) {
				m_targets = new ArrayList<MaterialUsageTargetPestInfo>();
				String ids[] = temp_record.Pest_ids.split(",");
				for (String s : ids) {
					MaterialUsageTargetPestInfo.AddTargetPest(Utils
							.ConvertToInt(s));
				}
				m_targets = MaterialUsageTargetPestInfo.getAll();
			}
			StringBuffer target_buffer = new StringBuffer();
			if (m_targets != null && m_targets.size() > 0) {
				for (MaterialUsageTargetPestInfo m : m_targets) {
					String mt = String.format(target_pest, m.pest_type_id);
					arr_targets.add(mt);
				}
				for (int i = 0; i < arr_targets.size(); i++) {
					if (i == arr_targets.size() - 1) {
						target_buffer.append(arr_targets.get(i));
					} else {
						target_buffer.append(arr_targets.get(i));
						target_buffer.append(",");
					}
				}
			}
			for (int i = 0; i < records.size(); i++) {
				if (records.get(i).location_area_id == 0) {
					String record_json = String.format(locationJson, null,
							records.get(i).dilution_rate_id,
							records.get(i).application_method,
							records.get(i).amount, records.get(i).measurement,
							records.get(i).device,
							records.get(i).application_device_type_id,
							records.get(i).lot_number,
							records.get(i).application_method_id);
					String temp = "";
					if (m_targets.size() > 0) {
						temp = record_json.replace("##",
								target_buffer.toString());
					} else {
						temp = record_json.replace(
								",\"target_pests_attributes\":[##]", "");
					}
					arr.add(temp);
				} else {
					String record_json = String.format(locationJson,
							records.get(i).location_area_id,
							records.get(i).dilution_rate_id,
							records.get(i).application_method,
							records.get(i).amount, records.get(i).measurement,
							records.get(i).device,
							records.get(i).application_device_type_id,
							records.get(i).lot_number,
							records.get(i).application_method_id);
					String temp = "";
					if (m_targets.size() > 0) {
						temp = record_json.replace("##",
								target_buffer.toString());
					} else {
						temp = record_json.replace(
								",\"target_pests_attributes\":[##]", "");
					}
					arr.add(temp);
				}
			}
			for (int i = 0; i < arr.size(); i++) {
				if (i == arr.size() - 1) {
					buffer.append(arr.get(i));
				} else {
					buffer.append(arr.get(i));
					buffer.append(",");
				}
			}
			json = String.format(json, info.material_id);
			main_json = json.replace("##", buffer.toString());
			MaterialUsageTargetPestInfo.ClearDB();
		}
		return main_json;
	}

	public static void DeleteInspectionInfoRecord(int app_id,
			InspectionInfo info, final UpdateMUInfoDelegate delegate) {
		try {
			List<InspectionInfo> lst = FieldworkApplication.Connection().find(
					InspectionInfo.class,
					CamelNotationHelper.toSQLName("id") + "=?",
					new String[] { String.valueOf(info.id) });
			if (lst != null) {
				if (lst.size() > 0) {
					if (lst.get(0).id < 0) {
						if (NetworkConnectivity.isConnected()) {
							lst.get(0).delete();
						} else {
							lst.get(0).isDeleted = true;
							lst.get(0).save();
						}
						ArrayList<InspectionPest> records = InspectionPestsList
								.Instance().getInspectionPestByInspectionId(
										info.id);
						for (InspectionPest pest : records) {
							pest.delete();
						}
						ServiceResponse res = new ServiceResponse();
						res.StatusCode = 200;
						delegate.UpdateSuccessFully(res);
					} else {
						lst.get(0).isDeleted = true;
						lst.get(0).save();
						if (NetworkConnectivity.isConnected()) {
							// delete inspection info
							lst.get(0).syncDelete();
						} else {
							ServiceResponse res = new ServiceResponse();
							res.StatusCode = 200;
							delegate.UpdateSuccessFully(res);
						}
					}
				}
			} else {
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 1;
				delegate.UpdateFail("");
			}
		} catch (Exception e) {
		}
	}

	public void sync() {
		ArrayList<InspectionPest> record = InspectionPestsList.Instance()
				.getInspectionPestByInspectionId(id);
		this.UpdateRecords(AppointmentId, record, InspectionInfo.this,
				new UpdateMUInfoDelegate() {

					@Override
					public void UpdateSuccessFully(ServiceResponse res) {
						// AppointmentInfo.refresh();
						// InspectionList.Instance().refreshInspectionList(
						// AppointmentId, null);

					}

					@Override
					public void UpdateFail(String ErrorMessage) {
					}
				});
	}

	public void syncDelete() {
		String url = String.format("work_orders/%d/inspection_records/%d",
				AppointmentId, id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.DELETE, "");
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				ArrayList<InspectionPest> records = InspectionPestsList
						.Instance().getInspectionPestByInspectionId(id);
				for (InspectionPest ip : records) {
					try {
						ip.delete();
					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}
				}
				try {
					InspectionInfo.this.delete();
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
			}
		});
	}

	public void deleteSync() {
		String url = String.format("work_orders/%d/inspection_records/%d",
				AppointmentId, this.id);
		ServiceCallerSync caller = new ServiceCallerSync(url,
				ServiceCallerSync.RequestMethod.DELETE, "");
		ServiceResponse res = caller.startRequest();
		// ServiceCaller caller = new ServiceCaller(url,
		// ServiceCaller.RequestMethod.DELETE, "");
		// ServiceResponse res = caller.startRequest();
		if (!res.isError()) {
			Utils.LogInfo("Deleted Inspection " + this.id);
			// List<MaterialUsage> mlst;
			// try {
			// mlst = FieldworkApplication.Connection().find(
			// MaterialUsage.class,
			// CamelNotationHelper.toSQLName("id") + "<?",
			// new String[] { String.valueOf(this.id) });
			// if (mlst != null && mlst.size() > 0) {
			// mlst.get(0).delete();
			// }
			// } catch (ActiveRecordException e) {
			// e.printStackTrace();
			// }
		}
	}
	
	public String DeleteInspectionObjectJson() {
		String json = "";
		if (this.id > 0) {
			json = "{\"id\":%d,\"_destroy\":true}";
			json = String.format(json, this.id);
		}
		return json;
	}
	

	public String GetInspectionJson() {
		String json = "";
//		if (this.id > 0) {
//			json = "{\"id\":%d,\"_destroy\":true}";
//			json = String.format(json, this.id);
//		} else {
			String buffer = new String();
			String buffer_material = new String();

			ArrayList<String> arr = new ArrayList<String>();
			ArrayList<String> material_arr = new ArrayList<String>();
			json = "{\"barcode\":\"%s\",\"location_area_id\":%d,\"evidence\":\"%s\",\"notes\":\"%s\",\"trap_number\":\"%s\",\"trap_type_id\":%d,\"scanned_on\":\"%s\",\"trap_condition_id\":%d,\"bait_condition_id\":%d,\"removed\":%s,\"exception\":\"%s\",\"pests_records_attributes\":[PEST],\"material_usages_attributes\":[MATERIAL]}";
			if (this.evidence == null || this.evidence.length() <= 0) {
				json = json.replace("\"evidence\":\"%s\"", "\"evidence\":null");
			}
			ArrayList<InspectionPest> records = InspectionPestsList.Instance()
					.getInspectionPestByInspectionId(this.id);
			String pests_json = "{\"pest_type_id\":%d,\"count\":%d}";
			for (int i = 0; i < records.size(); i++) {
				String record_json = String.format(pests_json,
						records.get(i).pest_type_id, records.get(i).count);
				arr.add(record_json);
			}
			if (this.Material_ids != null && this.Material_ids.length() > 0) {
				if (this.Material_ids.contains(",")) {
					String[] ids = this.Material_ids.split(",");
					for (String s : ids) {
						String material_json = this.GetMaterialJson(Utils
								.ConvertToInt(s));
						material_arr.add(material_json);
					}
				} else {
					String material_json = this.GetMaterialJson(Utils
							.ConvertToInt(this.Material_ids));
					material_arr.add(material_json);
				}
			}
			buffer_material = Utils.Instance().join(material_arr, ",");
			buffer = Utils.Instance().join(arr, ",");
			if (this.evidence != null && this.evidence.length() > 0) {
				json = String.format(json, this.barcode, this.LocationId,
						this.evidence, this.notes, this.trap_number,
						this.trap_type_id, this.scanned_on,
						this.trap_condition_id, this.bait_condition_id,
						this.removed, this.exception);
			} else {
				json = String.format(json, this.barcode, this.LocationId,
						this.notes, this.trap_number, this.trap_type_id,
						this.scanned_on, this.trap_condition_id,
						this.bait_condition_id, this.removed, this.exception);
			}

			if (records.size() > 0) {
				json = json.replace("PEST", buffer.toString());
			} else {
				json = json.replace(",\"pests_records_attributes\":[PEST]", "");
			}
			if (this.Material_ids != null && this.Material_ids.length() > 0) {
				json = json.replace("MATERIAL", buffer_material.toString());
			} else {
				json = json.replace(
						",\"material_usages_attributes\":[MATERIAL]", "");
			}
//		}
		return json;
	}

	public static void updateTrapIds(int oldid, int newid) {
		try {
			List<InspectionInfo> mlst = FieldworkApplication.Connection().find(
					InspectionInfo.class,
					CamelNotationHelper.toSQLName("id") + "<?",
					new String[] { String.valueOf("0") });
			if (mlst != null && mlst.size() > 0) {
				for (InspectionInfo in : mlst) {
					if (in.trap_type_id == oldid) {
						in.trap_type_id = newid;
						in.save();
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

}
