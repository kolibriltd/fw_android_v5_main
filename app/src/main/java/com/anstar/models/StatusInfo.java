package com.anstar.models;

import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.fieldwork.FieldworkApplication;

public class StatusInfo extends ActiveRecordBase {

	public StatusInfo() {
	}

	public String statusName = "";
	public String statusValue = "";

	public static String getNameByValue(String value) {
		String name = "";
		try {
			List<StatusInfo> lst = FieldworkApplication.Connection().findAll(
					StatusInfo.class);
			for (StatusInfo s : lst) {
				if (s.statusValue.equalsIgnoreCase(value)) {
					name = s.statusName;
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return name;
	}

	public static String getValueByName(String name) {
		String value = "";
		try {
			List<StatusInfo> lst = FieldworkApplication.Connection().findAll(
					StatusInfo.class);
			for (StatusInfo s : lst) {
				if (s.statusName.equalsIgnoreCase(name)) {
					value = s.statusValue;
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return value;
	}

}
