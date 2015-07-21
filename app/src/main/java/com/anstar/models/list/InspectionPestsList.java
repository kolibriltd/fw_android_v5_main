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
import com.anstar.models.InspectionPest;

public class InspectionPestsList {

	private InspectionPestsList() {

	}

	private static volatile InspectionPestsList _instance = null;

	// private static volatile int cat_id = 0;

	public static InspectionPestsList Instance() {
		if (_instance == null) {
			synchronized (InspectionPestsList.class) {
				_instance = new InspectionPestsList();
			}
		}
		return _instance;
	}

	public void parseInspectionPestsList(JSONArray pests, int inspection_id) {

		try {
			for (int i = 0; i < pests.length(); i++) {
				JSONObject note = pests.getJSONObject(i);
				ModelMapHelper<InspectionPest> mapper = new ModelMapHelper<InspectionPest>();
				InspectionPest info = mapper.getObject(InspectionPest.class,
						note);
				if (info != null) {
					try {
						info.inspection_id = inspection_id;
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

	public ArrayList<InspectionPest> getInspectionPestByInspectionId(
			int InspectionId) {
		ArrayList<InspectionPest> m_list = new ArrayList<InspectionPest>();
		try {
			List<InspectionPest> list = FieldworkApplication.Connection().find(
					InspectionPest.class,
					CamelNotationHelper.toSQLName("inspection_id") + "=?",
					new String[] { String.valueOf(InspectionId) });
			List<InspectionPest> tlist = FieldworkApplication.Connection().findAll(InspectionPest.class);
			if (list != null && list.size() > 0) {
				m_list = new ArrayList<InspectionPest>(list);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_list;
	}
	
	public void ClearDB(int InspectionId) {
		try {
			List<InspectionPest> lst = FieldworkApplication.Connection().find(
					InspectionPest.class,
					CamelNotationHelper.toSQLName("inspection_id") + "=?",
					new String[] { "" + InspectionId });
			if (lst != null && lst.size() > 0) {
				for (InspectionPest i : lst) {
					i.delete();
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}
}
