package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.fieldwork.FieldworkApplication;

public class MaterialUsageTargetPestInfo extends ActiveRecordBase {

	public MaterialUsageTargetPestInfo() {

	}

	public int pest_type_id = 0;

	public static ArrayList<MaterialUsageTargetPestInfo> getAll() {
		ArrayList<MaterialUsageTargetPestInfo> m_pests = new ArrayList<MaterialUsageTargetPestInfo>();
		try {
			List<MaterialUsageTargetPestInfo> list = FieldworkApplication
					.Connection().findAll(MaterialUsageTargetPestInfo.class);
			for (MaterialUsageTargetPestInfo tempLocation : list) {
				m_pests.add(tempLocation);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_pests;
	}

	public static void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(
					MaterialUsageTargetPestInfo.class);
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static void AddTargetPest(int pest_id) {
		try {
			List<MaterialUsageTargetPestInfo> list = FieldworkApplication
					.Connection().findAll(MaterialUsageTargetPestInfo.class);
			if (list != null && list.size() > 0) {
				ArrayList<MaterialUsageTargetPestInfo> temp = new ArrayList<MaterialUsageTargetPestInfo>();
				for (MaterialUsageTargetPestInfo l : list) {
					temp.add(l);
				}
				for (MaterialUsageTargetPestInfo m : temp) {
					if (m.pest_type_id == pest_id) {
						return;
					}
				}
				// if (!temp.contains(info.pest_type_id)) {
				MaterialUsageTargetPestInfo mt_info = FieldworkApplication
						.Connection().newEntity(
								MaterialUsageTargetPestInfo.class);
				mt_info.pest_type_id = pest_id;
				mt_info.save();
				// }
			} else {
				MaterialUsageTargetPestInfo mt_info = FieldworkApplication
						.Connection().newEntity(
								MaterialUsageTargetPestInfo.class);
				mt_info.pest_type_id = pest_id;
				mt_info.save();
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static void RemoveTarget(int pest_id) {
		try {
			List<MaterialUsageTargetPestInfo> list = FieldworkApplication
					.Connection().findAll(MaterialUsageTargetPestInfo.class);
			if (list != null && list.size() > 0) {
				for (MaterialUsageTargetPestInfo l : list) {
					if (l.pest_type_id == pest_id) {
						l.delete();
						break;
					}
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean equals(Object obj) {
		MaterialUsageTargetPestInfo temp = (MaterialUsageTargetPestInfo) obj;
		if (temp.pest_type_id == this.pest_type_id) {
			return true;
		} else {
			return false;
		}
	}

}
