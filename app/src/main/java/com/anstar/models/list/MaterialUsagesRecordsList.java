package com.anstar.models.list;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordException;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapHelper;
import com.anstar.models.MaterialUsage;
import com.anstar.models.MaterialUsageRecords;
import com.anstar.models.TargetPestInfo;

public class MaterialUsagesRecordsList {

	private MaterialUsagesRecordsList() {

	}

	private static volatile MaterialUsagesRecordsList _instance = null;

	// private static volatile int cat_id = 0;

	public static MaterialUsagesRecordsList Instance() {
		if (_instance == null) {
			synchronized (MaterialUsagesRecordsList.class) {
				_instance = new MaterialUsagesRecordsList();
			}
		}
		return _instance;
	}

	public void parseMaterialUsageRecordsList(JSONArray notelist,
			int materialusageId, int app_id) {

		try {
			for (int i = 0; i < notelist.length(); i++) {
				JSONObject note = notelist.getJSONObject(i);
				ModelMapHelper<MaterialUsageRecords> notemapper = new ModelMapHelper<MaterialUsageRecords>();
				MaterialUsageRecords info = notemapper.getObject(
						MaterialUsageRecords.class, note);
				JSONArray targets = note.getJSONArray("target_pests");
				if (targets != null) {
					ArrayList<String> id = new ArrayList<String>();
					for (int j = 0; j < targets.length(); j++) {
						JSONObject target = targets.getJSONObject(j);
						id.add(target.optString("pest_type_id"));
					}
					info.Pest_ids = Utils.Instance().join(id, ",");
				}
				if (info != null) {
					try {
						info.MaterialUsageId = materialusageId;
						info.save();
					} catch (Exception e) {
						Utils.LogException(e);
					}
				}

			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	public ArrayList<MaterialUsageRecords> getMaterialRecordsByUsageId(
			int usageId) {
		ArrayList<MaterialUsageRecords> m_list = new ArrayList<MaterialUsageRecords>();
		try {
			List<MaterialUsageRecords> list = FieldworkApplication.Connection()
					.findAll(MaterialUsageRecords.class);
			if (list.size() > 0) {
				for (MaterialUsageRecords record : list) {
					if (record.MaterialUsageId == usageId) {
						m_list.add(record);
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}

	public MaterialUsageRecords getMaterialRecordByUsageId(int usageId) {
		try {
			List<MaterialUsageRecords> list = FieldworkApplication.Connection()
					.findAll(MaterialUsageRecords.class);
			if (list.size() > 0) {
				for (MaterialUsageRecords record : list) {
					if (record.MaterialUsageId == usageId) {
						return record;
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public MaterialUsageRecords getUsageRecordById(int id) {
		try {
			List<MaterialUsageRecords> list = FieldworkApplication.Connection()
					.findAll(MaterialUsageRecords.class);
			if (list.size() > 0) {
				for (MaterialUsageRecords record : list) {
					if (record.id == id) {
						return record;
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}

		return null;
	}

	public MaterialUsage getMaterialUsageByRecordId(int usageIdFromRecord) {
		try {
			List<MaterialUsage> list = FieldworkApplication.Connection()
					.findAll(MaterialUsage.class);
			if (list.size() > 0) {
				for (MaterialUsage usage : list) {
					if (usage.id == usageIdFromRecord) {
						return usage;
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteMaterialUsageRecords(int materialUsage_id) {
		try {
			int cnt = FieldworkApplication.Connection().delete(
					MaterialUsageRecords.class,
					CamelNotationHelper.toSQLName("MaterialUsageId") + "=?",
					new String[] { String.valueOf(materialUsage_id) });
			Utils.LogInfo(String
					.format("%d records deleted of material usage records for material usage %d",
							cnt, materialUsage_id));
		} catch (Exception ex) {

		}
	}

}
