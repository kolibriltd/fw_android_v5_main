package com.anstar.models;

import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.fieldwork.FieldworkApplication;

public class AppointmentConditionsInfo extends ActiveRecordBase {

	public AppointmentConditionsInfo() {
	}

	public int id = 0;
	public String name = "";


	public static String getNameById(int id) {
		String value = "";
		try {
			List<AppointmentConditionsInfo> lst = FieldworkApplication.Connection().findAll(
					AppointmentConditionsInfo.class);
			for (AppointmentConditionsInfo s : lst) {
				if (s.id == id) {
					value = s.name;
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static int getIdByName(String name) {
		int did = 0;
		try {
			List<AppointmentConditionsInfo> lst = FieldworkApplication.Connection().findAll(
					AppointmentConditionsInfo.class);
			for (AppointmentConditionsInfo s : lst) {
				if (s.name.equalsIgnoreCase(name)) {
					did = s.id;
					break;
				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return did;
	}
}
