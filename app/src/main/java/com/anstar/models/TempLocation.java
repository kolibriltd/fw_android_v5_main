package com.anstar.models;

import java.util.ArrayList;
import java.util.List;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.fieldwork.FieldworkApplication;

public class TempLocation extends ActiveRecordBase {

	public TempLocation() {

	}

	public String location = "";
	public int location_id = 0;

	public static ArrayList<TempLocation> getAll() {
		ArrayList<TempLocation> m_location = new ArrayList<TempLocation>();
		try {
			List<TempLocation> list = FieldworkApplication.Connection()
					.findAll(TempLocation.class);
			for (TempLocation tempLocation : list) {
				m_location.add(tempLocation);
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return m_location;
	}

	public static void ClearDB() {
		try {
			FieldworkApplication.Connection().delete(TempLocation.class);
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static void AddArea(String area, int id) {
		try {
			List<TempLocation> list = FieldworkApplication.Connection()
					.findAll(TempLocation.class);
			if (list != null && list.size() > 0) {
				// ArrayList<String> temp = new ArrayList<String>();
				TempLocation temp = FieldworkApplication.Connection()
						.newEntity(TempLocation.class);
				temp.location = area;
				temp.location_id = id;
				// for (TempLocation l : list) {
				// temp.add(l.location);
				// }
				if (!list.contains(temp)) {
					// TempLocation loc = FieldworkApplication.Connection()
					// .newEntity(TempLocation.class);
					// loc.location = area;
					temp.save();
				}
			} else {
				TempLocation loc = FieldworkApplication.Connection().newEntity(
						TempLocation.class);
				loc.location = area;
				loc.location_id = id;
				loc.save();
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}

	}

	public static void RemoveArea(String area, int id) {
		try {
			// List<TempLocation> list = FieldworkApplication.Connection()
			// .findAll(TempLocation.class);
			// if (list != null && list.size() > 0) {
			// for (TempLocation l : list) {
			// if (l.location.equalsIgnoreCase(area)) {
			// l.delete();
			// }
			// }
			// }
			TempLocation loc = getLocationByAreaAndId(area, id);
			if (loc != null) {
				loc.delete();
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
	}

	public static TempLocation getLocationByAreaAndId(String area, int id) {
		try {
			List<TempLocation> list = FieldworkApplication.Connection()
					.findAll(TempLocation.class);
			if (list != null && list.size() > 0) {
				for (TempLocation t : list) {
					if (t.location.equalsIgnoreCase(area)
							&& t.location_id == id) {
						return t;
					}

				}
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		TempLocation obj = (TempLocation) o;
		if (obj.location.equalsIgnoreCase(this.location)
				&& obj.location_id == this.location_id) {
			return true;
		} else {
			return false;
		}
	}

}
