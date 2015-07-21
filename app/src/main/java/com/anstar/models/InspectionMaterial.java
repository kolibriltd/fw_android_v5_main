package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.fieldwork.FieldworkApplication;

public class InspectionMaterial extends ActiveRecordBase {

	public InspectionMaterial() {
	}

	public int material_id = 0;

	public static ArrayList<InspectionMaterial> getAll() {
		ArrayList<InspectionMaterial> m_materials = new ArrayList<InspectionMaterial>();
		try {
			List<InspectionMaterial> list = FieldworkApplication.Connection()
					.findAll(InspectionMaterial.class);
			for (InspectionMaterial im : list) {
				m_materials.add(im);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_materials;
	}

	public static void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(InspectionMaterial.class);
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static void AddMaterial(int material_id) {
		try {
			List<InspectionMaterial> list = FieldworkApplication.Connection()
					.findAll(InspectionMaterial.class);
			if (list != null && list.size() > 0) {
				for (InspectionMaterial m : list) {
					if (m.material_id == material_id) {
						return;
					}
				}
				InspectionMaterial mt_info = FieldworkApplication.Connection()
						.newEntity(InspectionMaterial.class);
				mt_info.material_id = material_id;
				mt_info.save();
			} else {
				InspectionMaterial mt_info = FieldworkApplication.Connection()
						.newEntity(InspectionMaterial.class);
				mt_info.material_id = material_id;
				mt_info.save();
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static void RemoveMaterial(int material_id) {
		try {
			List<InspectionMaterial> list = FieldworkApplication.Connection()
					.findAll(InspectionMaterial.class);
			if (list != null && list.size() > 0) {
				for (InspectionMaterial l : list) {
					if (l.material_id == material_id) {
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
		InspectionMaterial temp = (InspectionMaterial) obj;
		if (temp.material_id == this.material_id) {
			return true;
		} else {
			return false;
		}
	}

}
