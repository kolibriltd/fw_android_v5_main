package com.anstar.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.JsonCreator;
import com.anstar.common.NetworkConnectivity;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.internetbroadcast.ServiceCallerSync;
import com.anstar.model.helper.ServiceCaller;
import com.anstar.model.helper.ServiceHelper;
import com.anstar.model.helper.ServiceHelper.ModelProcess;
import com.anstar.model.helper.ServiceHelper.ServiceHelperDelegate;
import com.anstar.model.helper.ServiceResponse;
import com.anstar.model.mapper.ModelMapper;
import com.anstar.models.ModelDelegates.UpdateInfoDelegate;
import com.anstar.models.list.MaterialUsagesRecordsList;

public class MaterialUsage extends ActiveRecordBase {

	public MaterialUsage() {

	}

	@ModelMapper(JsonKey = "id")
	public int id = 0;
	@ModelMapper(JsonKey = "material_id")
	public int material_id = 0;
	@ModelMapper(JsonKey = "notes")
	public String notes = "";
	@ModelMapper(JsonKey = "created_at")
	public String created_at = "";
	@ModelMapper(JsonKey = "updated_at")
	public String updated_at = "";
	public int AppointmentId = 0;

	public boolean isDeleted = false;
	public boolean isForInspection = false;
	public MaterialInfo m_material_info = null;

	public interface UpdateMUInfoDelegate {
		public void UpdateSuccessFully(ServiceResponse res);

		public void UpdateFail(String ErrorMessage);
	}

	private UpdateMUInfoDelegate m_delegate = null;

	public static void AddMaterialUsageRecords(boolean isForInspection,
			int appointment_id, ArrayList<MaterialUsageRecords> info,
			MaterialUsage usage, UpdateMUInfoDelegate delegate) {

		try {
			MaterialUsage mu = FieldworkApplication.Connection().newEntity(
					MaterialUsage.class);
			mu.id = Utils.getRandomInt();
			mu.material_id = usage.material_id;
			mu.AppointmentId = appointment_id;
			mu.isForInspection = isForInspection;
			mu.save();

			MaterialUsageRecords records = null;
			for (int i = 0; i < info.size(); i++) {
				records = FieldworkApplication.Connection().newEntity(
						MaterialUsageRecords.class);

				records.id = Utils.getRandomInt();
				records.MaterialUsageId = mu.id;
				records.amount = info.get(i).amount;
				records.dilution_rate_id = info.get(i).dilution_rate_id;
				records.measurement = info.get(i).measurement;
				records.location_area_id = info.get(i).location_area_id;
				records.application_method = info.get(i).application_method;
				records.application_method_id = info.get(i).application_method_id;
				records.device = info.get(i).device;
				records.application_device_type_id = info.get(0).application_device_type_id;
				records.lot_number = info.get(i).lot_number;
				records.Pest_ids = info.get(i).Pest_ids;
				records.save();
			}
			if (NetworkConnectivity.isConnected()) {
				mu.UpdateRecords(ModelProcess.Insert, appointment_id, info, mu,
						delegate);
			} else {
				ServiceResponse res = new ServiceResponse();
				res.StatusCode = 200;
				delegate.UpdateSuccessFully(res);
				AppointmentInfo.updateDirtyFlag(appointment_id);
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public static void AddMaterialUsageRecordsForTrapOnly(
			boolean isForInspection, int appointment_id,
			ArrayList<MaterialUsageRecords> info, MaterialUsage usage,
			UpdateMUInfoDelegate delegate) {

		try {

			MaterialUsage mu = FieldworkApplication.Connection().newEntity(
					MaterialUsage.class);
			mu.id = Utils.getRandomInt();
			mu.material_id = usage.material_id;
			mu.AppointmentId = appointment_id;
			mu.isForInspection = isForInspection;
			mu.save();

			MaterialUsageRecords records = null;
			for (int i = 0; i < info.size(); i++) {
				records = FieldworkApplication.Connection().newEntity(
						MaterialUsageRecords.class);

				records.id = Utils.getRandomInt();
				records.MaterialUsageId = mu.id;
				records.amount = info.get(i).amount;
				records.dilution_rate_id = info.get(i).dilution_rate_id;
				records.measurement = info.get(i).measurement;
				records.location_area_id = info.get(i).location_area_id;
				records.application_method = info.get(i).application_method;
				records.application_method_id = info.get(i).application_method_id;
				records.device = info.get(i).device;
				records.application_device_type_id = info.get(0).application_device_type_id;
				records.lot_number = info.get(i).lot_number;
				records.Pest_ids = info.get(i).Pest_ids;
				records.save();
			}

			ServiceResponse res = new ServiceResponse();
			res.StatusCode = mu.id;
			delegate.UpdateSuccessFully(res);

		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void UpdateRecords(ServiceHelper.ModelProcess process, int app_id,
			ArrayList<MaterialUsageRecords> records, MaterialUsage info,
			final UpdateMUInfoDelegate delegate) {
		m_delegate = delegate;

		if (process == ModelProcess.Insert) {
			StringBuffer buffer = new StringBuffer();
			ArrayList<String> arr = new ArrayList<String>();
			ArrayList<String> arr_targets = new ArrayList<String>();
			String json = "{\"work_order\":{\"material_usages_attributes\":[{\"material_id\":%d, \"material_usage_records_attributes\":[##]}]}}";
			String url = String.format("work_orders/%d", app_id);
			String locationJson = "{\"location_area_id\":%d,\"dilution_rate_id\":%d,\"application_method\":\"%s\",\"amount\":%s,\"measurement\":\"%s\",\"device\":\"%s\",\"application_device_type_id\":%d,\"lot_number\":\"%s\",\"application_method_id\":%d,\"target_pests_attributes\":[##]}";
			String target_pest = "{\"pest_type_id\": %d}";
			// ArrayList<MaterialUsageTargetPestInfo> m_targets =
			// MaterialUsageTargetPestInfo
			// .getAll();
			StringBuffer target_buffer = new StringBuffer();
			// if (m_targets != null && m_targets.size() > 0) {
			// for (MaterialUsageTargetPestInfo m : m_targets) {
			// String mt = String.format(target_pest, m.pest_type_id);
			// arr_targets.add(mt);
			// }
			// for (int i = 0; i < arr_targets.size(); i++) {
			// if (i == arr_targets.size() - 1) {
			// target_buffer.append(arr_targets.get(i));
			// } else {
			// target_buffer.append(arr_targets.get(i));
			// target_buffer.append(",");
			// }
			// }
			// }
			if (records != null && records.size() > 0) {
				MaterialUsageRecords temp_info_for_pest_ids = records.get(0);
				if (temp_info_for_pest_ids != null) {
					String temp_pests = temp_info_for_pest_ids.Pest_ids;
					if (temp_pests.length() > 0) {
						if (temp_pests.contains(",")) {
							String pests[] = temp_pests.split(",");
							for (String string : pests) {
								String mt = String.format(target_pest,
										Utils.ConvertToInt(string));
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
						} else {
							String mt = String.format(target_pest,
									Utils.ConvertToInt(temp_pests));
							arr_targets.add(mt);
							for (int i = 0; i < arr_targets.size(); i++) {
								if (i == arr_targets.size() - 1) {
									target_buffer.append(arr_targets.get(i));
								} else {
									target_buffer.append(arr_targets.get(i));
									target_buffer.append(",");
								}
							}
						}
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
					String temp = record_json.replace("##",
							target_buffer.toString());
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
					String temp = record_json.replace("##",
							target_buffer.toString());
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
			json = json.replace("##", buffer.toString());
			if (target_buffer.length() <= 0) {
				json = json.replace(",\"target_pests_attributes\":[]", "");
			}
			Utils.LogInfo("Material usage add json ::::::: "+json);
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
		} else if (process == ModelProcess.Delete) {
			if (this.id == -1) {
				return;
			}
			String json = "{\"work_order\":{\"material_usages_attributes\":[{\"id\":%d,\"_destroy\":true}]}}";
			String url = String.format("work_orders/%d", app_id);
			json = String.format(json, info.id);
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

	}

	public static void DeleteMaterialUsageRecord(MaterialUsageRecords record,
			int app_id, MaterialUsage info, final UpdateMUInfoDelegate delegate) {
		ArrayList<MaterialUsageRecords> m_temp = new ArrayList<MaterialUsageRecords>();
		if (record != null)
			m_temp.add(record);
		try {
			List<MaterialUsage> lst = FieldworkApplication.Connection().find(
					MaterialUsage.class,
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
							AppointmentInfo.updateDirtyFlag(app_id);
							// lst.get(0).delete();
						}
						ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
								.Instance()
								.getMaterialRecordsByUsageId(info.id);
						for (MaterialUsageRecords materialUsageRecords : records) {
							materialUsageRecords.delete();
						}
						ServiceResponse res = new ServiceResponse();
						res.StatusCode = 200;
						delegate.UpdateSuccessFully(res);
					} else {
						lst.get(0).isDeleted = true;
						lst.get(0).save();
						if (NetworkConnectivity.isConnected()) {
							lst.get(0).UpdateRecords(ModelProcess.Delete,
									app_id, m_temp, info, delegate);
						} else {
							ServiceResponse res = new ServiceResponse();
							res.StatusCode = 200;
							delegate.UpdateSuccessFully(res);
							AppointmentInfo.updateDirtyFlag(app_id);
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

	public static MaterialUsage getMaterialUsageById(int id) {
		try {
			List<MaterialUsage> list = FieldworkApplication.Connection()
					.findAll(MaterialUsage.class);
			for (MaterialUsage m : list) {
				if (m.id == id) {
					return m;
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	// private ServiceHelperDelegate m_service_delegate = new
	// ServiceHelperDelegate() {
	//
	// @Override
	// public void CallFinish(ServiceResponse res) {
	// AppointmentInfo.refresh();
	// }
	//
	// @Override
	// public void CallFailure(String ErrorMessage) {
	// }
	// };

	public void sync() {
		// if(this.pa)
		Utils.LogInfo("Material sync function 369:::: " + id);
		final ArrayList<MaterialUsageRecords> temprecord = MaterialUsagesRecordsList
				.Instance().getMaterialRecordsByUsageId(id);
		String ids = temprecord.get(0).Pest_ids;
		final ArrayList<String> m_plusids = new ArrayList<String>();
		if (ids != null && ids.length() > 0) {
			if (ids.contains(",")) {
				String pestids[] = temprecord.get(0).Pest_ids.split(",");

				for (int i = 0; i < pestids.length; i++) {
					final int h = i;
					String ss = pestids[i];
					int pstid = Utils.ConvertToInt(ss);
					if (pstid < 0) {
						PestsTypeInfo info = PestsTypeInfo
								.getPestTypeById(pstid);
						if (info != null) {
							info.sync(new UpdateInfoDelegate() {

								@Override
								public void UpdateSuccessFully(
										ServiceResponse res) {
									try {
										JSONObject obj = new JSONObject(
												res.RawResponse);
										JSONObject pest = obj
												.getJSONObject("pest_type");
										int psrid = pest.getInt("id");
										m_plusids.add("" + psrid);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									if (h == res.Tag) {
										for (MaterialUsageRecords mu : temprecord) {
											mu.Pest_ids = Utils.Instance()
													.join(m_plusids, ",");
											try {
												mu.save();
											} catch (ActiveRecordException e) {
												e.printStackTrace();
											}
										}
										syncmaterial();
									}
								}

								@Override
								public void UpdateFail(String ErrorMessage) {

								}
							}, i);
						}
					} else {
						m_plusids.add(ss);
						if (h == pestids.length - 1) {
							for (MaterialUsageRecords mu : temprecord) {
								mu.Pest_ids = Utils.Instance().join(m_plusids,
										",");
								try {
									mu.save();
								} catch (ActiveRecordException e) {
									e.printStackTrace();
								}
							}
							syncmaterial();
						}

					}
				}

			} else {
				int pstid = Utils.ConvertToInt(ids);
				if (pstid < 0) {
					PestsTypeInfo info = PestsTypeInfo.getPestTypeById(pstid);
					if (info != null) {
						info.sync(new UpdateInfoDelegate() {
							@Override
							public void UpdateSuccessFully(ServiceResponse res) {
								try {
									JSONObject obj = new JSONObject(
											res.RawResponse);
									JSONObject pest = obj
											.getJSONObject("pest_type");
									int psrid = pest.getInt("id");
									for (MaterialUsageRecords mu : temprecord) {
										mu.Pest_ids = "" + psrid;
										mu.save();
									}
									syncmaterial();
								} catch (JSONException e) {
									e.printStackTrace();
								} catch (ActiveRecordException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void UpdateFail(String ErrorMessage) {

							}
						}, 0);

					}
				} else {
					syncmaterial();
				}
			}
		} else {
			syncmaterial();
		}
	}

	public void syncLocation(final ArrayList<MaterialUsageRecords> records) {
		int i = 0;
		for (MaterialUsageRecords rec : records) {
			final int h = i++;
			if (rec.location_area_id < 0) {
				LocationAreaInfo info = LocationAreaInfo
						.getLocationAreaById(rec.location_area_id);
				if (info != null) {

					info.syncLocation(info.name, info.Location_Type_id,
							new UpdateInfoDelegate() {
								@Override
								public void UpdateSuccessFully(
										ServiceResponse res) {
									try {
										JSONObject obj = new JSONObject(
												res.RawResponse);
										JSONObject area = obj
												.getJSONObject("location_area");
										id = area.getInt("id");
										records.get(res.Tag).location_area_id = id;
										records.get(res.Tag).save();
										if (h == res.Tag) {
											syncmaterial();
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								@Override
								public void UpdateFail(String ErrorMessage) {

								}
							}, i);
				}
			} else {
				if (h == records.size() - 1) {

				}
			}
		}
	}

	public void syncmaterial() {
		if (this.material_id < 0) {
			Utils.LogInfo("syncmaterial 470 :::::: " + this.material_id);
			this.m_material_info.sync(new UpdateInfoDelegate() {
				@Override
				public void UpdateSuccessFully(ServiceResponse res) {

					material_id = m_material_info.id;
					try {
						save();
					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}
					ArrayList<MaterialUsageRecords> record = MaterialUsagesRecordsList
							.Instance().getMaterialRecordsByUsageId(id);

					MaterialUsage mu = MaterialUsage.this;
					mu.UpdateRecords(ModelProcess.Insert, AppointmentId,
							record, mu, new UpdateMUInfoDelegate() {

								@Override
								public void UpdateSuccessFully(
										ServiceResponse res) {
									Utils.LogInfo("after adding material rerord AppointmentInfo.refresh() ::::: ");
									// AppointmentInfo.refresh();

									// MaterialUsagesList.Instance()
									// .refreshMaterialUsage(
									// AppointmentId, null);
								}

								@Override
								public void UpdateFail(String ErrorMessage) {

								}
							});

				}

				@Override
				public void UpdateFail(String ErrorMessage) {

				}
			});
		} else {
			ArrayList<MaterialUsageRecords> record = MaterialUsagesRecordsList
					.Instance().getMaterialRecordsByUsageId(id);
			MaterialUsage mu = MaterialUsage.this;
			mu.UpdateRecords(ModelProcess.Insert, AppointmentId, record, mu,
					new UpdateMUInfoDelegate() {

						@Override
						public void UpdateSuccessFully(ServiceResponse res) {
							AppointmentInfo.refresh();
							// MaterialUsagesList.Instance().refreshMaterialUsage(
							// AppointmentId, null);

						}

						@Override
						public void UpdateFail(String ErrorMessage) {
							// TODO Auto-generated method stub

						}
					});

		}

	}

	public void syncDelete() {
		String json = "{\"work_order\":{\"material_usages_attributes\":[{\"id\":%d,\"_destroy\":true}]}}";
		String url = String.format("work_orders/%d", AppointmentId);
		json = String.format(json, id);
		ServiceCaller caller = new ServiceCaller(url,
				ServiceCaller.RequestMethod.PUT, json);
		caller.startRequest(new ServiceHelperDelegate() {

			@Override
			public void CallFinish(ServiceResponse res) {
				ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
						.Instance().getMaterialRecordsByUsageId(id);
				for (MaterialUsageRecords materialUsageRecords : records) {
					try {
						materialUsageRecords.delete();
					} catch (ActiveRecordException e) {
						e.printStackTrace();
					}
				}
				try {
					delete();
				} catch (ActiveRecordException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void CallFailure(String ErrorMessage) {
			}
		});
	}

	@Override
	public boolean equals(Object o) {
		MaterialUsage usages = (MaterialUsage) o;
		if (this.id == usages.id) {
			return true;
		} else {
			return false;
		}
	}

	// String json =
	// "{\"appointment_occurrence\":{\"material_usages_attributes\":[{\"material_id\":%d, \"material_usage_records_attributes\":[{\"location_area_id\":%d,\"dilution_rate_id\":%d,\"application_method\":\"%s\",\"amount\":%s,\"measurement\":\"%s\"}]}]}}";
	// String url = String.format("appointment_occurrences/%d",
	// AppointmentId);
	// if (record.location_area_id == 0) {
	// json = String.format(json, material_id, null,
	// record.dilution_rate_id,
	// record.application_method, record.amount,
	// record.measurement);
	// } else {
	// json = String.format(json, material_id,
	// record.location_area_id,
	// record.dilution_rate_id,
	// record.application_method, record.amount,
	// record.measurement);
	// }
	// ServiceCaller caller = new ServiceCaller(url,
	// ServiceCaller.RequestMethod.PUT, json);
	// caller.startRequest(m_service_delegate);
	//

	// String json =
	// "{\"appointment_occurrence\":{\"material_usages_attributes\":[{\"material_id\":%d, \"material_usage_records_attributes\":[{\"location_area_id\":%d,\"dilution_rate_id\":%d,\"application_method\":\"%s\",\"amount\":%s,\"measurement\":\"%s\"}]}]}}";
	// String url = String.format("appointment_occurrences/%d",
	// AppointmentId);
	//
	// if (record.location_area_id == 0) {
	// json = String.format(json, material_id, null,
	// record.dilution_rate_id, record.application_method,
	// record.amount, record.measurement);
	// } else {
	// json = String.format(json, material_id,
	// record.location_area_id, record.dilution_rate_id,
	// record.application_method, record.amount,
	// record.measurement);
	// }
	// ServiceCaller caller = new ServiceCaller(url,
	// ServiceCaller.RequestMethod.PUT, json);
	// caller.startRequest(m_service_delegate);

	public String DeleteMaterialObjectJson() {
		String json = "";
		if (this.id > 0) {
			json = "{\"id\":%d,\"_destroy\":true}";
			json = String.format(json, this.id);
		}
		return json;
	}

	public String GetMaterialObjectJson() {
		String json = "";
		StringBuffer buffer = new StringBuffer();
		ArrayList<String> arr = new ArrayList<String>();
		ArrayList<String> arr_targets = new ArrayList<String>();
		json = "{\"material_id\":%d, \"material_usage_records_attributes\":##}";
		// String locationJson =
		// "{\"location_area_id\":%d,\"dilution_rate_id\":%d,\"application_method\":\"%s\",\"amount\":%s,\"measurement\":\"%s\",\"device\":\"%s\",\"application_device_type_id\":%d,\"lot_number\":\"%s\",\"application_method_id\":%d,\"target_pests_attributes\":[##]}";
		// String target_pest = "{\"pest_type_id\": %d}";
		// StringBuffer target_buffer = new StringBuffer();
		JSONArray pestarr = new JSONArray();
		ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
				.Instance().getMaterialRecordsByUsageId(this.id);
		if (records != null && records.size() > 0) {
			MaterialUsageRecords temp_info_for_pest_ids = records.get(0);
			if (temp_info_for_pest_ids != null) {
				String temp_pests = temp_info_for_pest_ids.Pest_ids;
				if (temp_pests.length() > 0) {
					List<HashMap<String, Object>> pestlst = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> pestitemHash = new HashMap<String, Object>();
					if (temp_pests.contains(",")) {
						String pests[] = temp_pests.split(",");

						for (String string : pests) {
							pestitemHash = new HashMap<String, Object>();
							pestitemHash.put("pest_type_id", string);
							pestlst.add(pestitemHash);
							pestarr = JsonCreator.getJsonArray(pestlst);
						}
					} else {
//						if (Integer.parseInt(temp_pests) < 0) {
							pestitemHash = new HashMap<String, Object>();
							pestitemHash.put("pest_type_id", temp_pests);
							pestlst.add(pestitemHash);
							pestarr = JsonCreator.getJsonArray(pestlst);
//						}
					}
				}
			}
		}
		JSONArray arr1 = new JSONArray();
		List<HashMap<String, Object>> linelst = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < records.size(); i++) {
			// Create Single Object for Line Item
			HashMap<String, Object> lineitemHash = new HashMap<String, Object>();
			if (records.get(i).location_area_id == 0) {
				lineitemHash.put("location_area_id", null);
			} else {
				lineitemHash.put("location_area_id",
						records.get(i).location_area_id);
			}
			lineitemHash.put("dilution_rate_id",
					records.get(i).dilution_rate_id);
			lineitemHash.put("application_method",
					records.get(i).application_method);
			lineitemHash.put("amount", records.get(i).amount);
			lineitemHash.put("measurement", records.get(i).measurement);
			lineitemHash.put("device", records.get(i).device);
			lineitemHash.put("application_device_type_id",
					records.get(i).application_device_type_id);
			lineitemHash.put("lot_number", records.get(i).lot_number);
			lineitemHash.put("application_method_id",
					records.get(i).application_method_id);
			if (pestarr.length() > 0)
				lineitemHash.put("target_pests_attributes", pestarr);
			linelst.add(lineitemHash);
			arr1 = JsonCreator.getJsonArray(linelst);
		}
		for (int i = 0; i < arr.size(); i++) {
			if (i == arr.size() - 1) {
				buffer.append(arr.get(i));
			} else {
				buffer.append(arr.get(i));
				buffer.append(",");
			}
		}
		json = String.format(json, this.material_id);
		json = json.replace("##", arr1.toString());
		// if (target_buffer.length() <= 0) {
		// json = json.replace(",\"target_pests_attributes\":[]", "");
		// }
		return json;

	}

	public static void updatePestIds(int oldid, int newid) {
		try {
			List<MaterialUsage> mlst = FieldworkApplication.Connection().find(
					MaterialUsage.class,
					CamelNotationHelper.toSQLName("id") + "<?",
					new String[] { String.valueOf("0") });
			if (mlst != null && mlst.size() > 0) {
				for (MaterialUsage materialUsage : mlst) {
					ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
							.Instance().getMaterialRecordsByUsageId(
									materialUsage.id);
					if (records != null && records.size() > 0)
						for (MaterialUsageRecords materialUsageRecords : records) {
							String temp_pests = materialUsageRecords.Pest_ids;
							if (temp_pests.length() > 0) {
								Utils.LogInfo("Pest ids before edit :::: "
										+ materialUsageRecords.Pest_ids);
								String pests[] = temp_pests.split(",");
								ArrayList<String> newpestids = new ArrayList<String>();
								for (String string : pests) {
									if (string.equals(String.valueOf(oldid))) {
										newpestids.add(String.valueOf(newid));
									} else {
										newpestids.add(string);
									}
								}
								materialUsageRecords.Pest_ids = Utils
										.Instance().join(newpestids, ",");
								Utils.LogInfo("Pest ids after edit :::: "
										+ materialUsageRecords.Pest_ids);
								materialUsageRecords.save();
							}
						}

				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static void updateLocationIds(int oldid, int newid) {
		try {
			List<MaterialUsage> mlst = FieldworkApplication.Connection().find(
					MaterialUsage.class,
					CamelNotationHelper.toSQLName("id") + "<?",
					new String[] { String.valueOf("0") });
			if (mlst != null && mlst.size() > 0) {
				for (MaterialUsage materialUsage : mlst) {
					ArrayList<MaterialUsageRecords> records = MaterialUsagesRecordsList
							.Instance().getMaterialRecordsByUsageId(
									materialUsage.id);
					if (records != null && records.size() > 0)
						for (MaterialUsageRecords materialUsageRecords : records) {
							Utils.LogInfo("Pest ids before edit :::: "
									+ materialUsageRecords.location_area_id);
							if (materialUsageRecords.location_area_id == oldid) {
								materialUsageRecords.location_area_id = newid;
								materialUsageRecords.save();
							}
							Utils.LogInfo("Pest ids before edit :::: "
									+ materialUsageRecords.location_area_id);
						}

				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static void updateMaterialId(int oldid, int newid) {
		try {
			List<MaterialUsage> mlst = FieldworkApplication.Connection().find(
					MaterialUsage.class,
					CamelNotationHelper.toSQLName("material_id") + "<?",
					new String[] { String.valueOf("0") });
			if (mlst != null && mlst.size() > 0) {
				for (MaterialUsage materialUsage : mlst) {
					if (materialUsage.material_id == oldid) {
						materialUsage.material_id = newid;
						materialUsage.save();
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public void deleteSync() {
		String json = "{\"work_order\":{\"material_usages_attributes\":[{\"id\":%d,\"_destroy\":true}]}}";
		String url = String.format("work_orders/%d", this.AppointmentId);
		json = String.format(json, this.id);
		ServiceCallerSync caller = new ServiceCallerSync(url,
				ServiceCallerSync.RequestMethod.PUT, json);
		ServiceResponse res = caller.startRequest();
		if (!res.isError()) {
			Utils.LogInfo("Deleted material usage" + this.id);
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

}
