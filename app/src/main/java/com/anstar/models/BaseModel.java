package com.anstar.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;
import com.anstar.model.mapper.ModelMapHelper;

public class BaseModel<T extends ActiveRecordBase> {

	long TimeStampForNewDataLoad = 1 * 60 * 60 * 1000;

	Class<T> m_type = null;
	protected ArrayList<T> m_modelList = null;

	protected BaseModel(Class<T> type) {
		m_type = type;
	}

	public void ClearDB(Class<T> type) {
		try {
			FieldworkApplication.Connection().delete(type);
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public void loadFromDB() {
		try {
			List<T> list = FieldworkApplication.Connection().findAll(m_type);
			if (list != null) {
				if (list.size() > 0) {
					m_modelList = new ArrayList<T>(list);
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}
	}

	public T isExists(T obj) {

		try {
			ModelMapHelper<T> helper = new ModelMapHelper<T>();
			String column = CamelNotationHelper.toSQLName(helper
					.getUniqueFieldName(m_type));
			// List<T> list = CourseKartApplication.Connection().findByColumn(
			// m_type, column,
			// String.valueOf(helper.getUniqueFieldValue(m_type, obj)));

			List<T> list = FieldworkApplication.Connection().find(
					m_type,
					column + "=?",
					new String[] { String.valueOf(helper.getUniqueFieldValue(
							m_type, obj)) });

			if (list != null) {
				if (list.size() > 0) {
					T dbObj = list.get(0);
					dbObj.copyFrom(obj);
					return dbObj;
				}
			}
		} catch (Exception e) {
			Utils.LogException(e);
		}

		return obj;

	}

	public ArrayList<T> getList() {
		if (m_modelList == null)
			m_modelList = new ArrayList<T>();
		return m_modelList;
	}

	public boolean shouldLoadNewData() {
		String classtype = this.m_type.getName();
		LoadedData data = LoadedData.getLoadedData(classtype);
		long currentMillies = new Date().getTime();
		if (data != null) {
			if (currentMillies - data.LoadedTimestamp > TimeStampForNewDataLoad) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	public void updateCurrentTimestampLoaded() {
		LoadedData.updateLoadedTimestamp(this.m_type.getName());
	}
}
