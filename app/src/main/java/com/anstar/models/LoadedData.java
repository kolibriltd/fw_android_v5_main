package com.anstar.models;

import java.util.Date;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.CamelNotationHelper;
import com.anstar.fieldwork.FieldworkApplication;

public class LoadedData extends ActiveRecordBase {

	public LoadedData() {

	}

	public String ClassType = "";
	public long LoadedTimestamp = 0;

	public static void updateLoadedTimestamp(String classtype) {
		try {
			LoadedData data = null;
			List<LoadedData> list = FieldworkApplication.Connection().find(
					LoadedData.class,
					CamelNotationHelper.toSQLName("ClassType") + "=?",
					new String[] { classtype });
			
			if(list != null && list.size() > 0){
				data = list.get(0);
			}
			if(data == null){
				data = FieldworkApplication.Connection().newEntity(LoadedData.class);
			}
			data.LoadedTimestamp = new Date().getTime();
			data.ClassType = classtype;
			data.save();
		} catch (Exception e) {
		}
	}
	
	public static LoadedData getLoadedData(String classtype){
		try {
			LoadedData data = null;
			List<LoadedData> list = FieldworkApplication.Connection().find(
					LoadedData.class,
					CamelNotationHelper.toSQLName("ClassType") + "=?",
					new String[] { classtype });
			
			if(list != null && list.size() > 0){
				data = list.get(0);
			}
			if(data == null){
				data = FieldworkApplication.Connection().newEntity(LoadedData.class);
				data.ClassType = classtype;
				data.LoadedTimestamp = 0;
				data.save();
			}
			return data;
		} catch (Exception e) {
		}
		return null;
	}
}
