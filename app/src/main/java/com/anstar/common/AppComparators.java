/**
 *	Copyright (c) @ Samcom Technobrains.
 *   AvantarMay 4, 2011
 **/
package com.anstar.common;

import java.util.Comparator;

import com.anstar.models.DeviceTypesInfo;
import com.anstar.models.LocationAreaInfo;
import com.anstar.models.MaterialInfo;
import com.anstar.models.PestsTypeInfo;

public class AppComparators {

	private static AppComparators _instance = null;

	public static AppComparators Instance() {
		if (_instance == null) {
			synchronized (AppComparators.class) {
				_instance = new AppComparators();
			}
		}
		return _instance;
	}

	public Comparator<PestsTypeInfo> PestByAtoZ = new Comparator<PestsTypeInfo>() {

		public int compare(PestsTypeInfo object1, PestsTypeInfo object2) {
			int o1 = 0;
			int o2 = 0;
			o1 = (int)object1.name.toLowerCase().charAt(0);
			o2 = (int)object2.name.toLowerCase().charAt(0);

			return (o1 < o2?-1:(o1== o2 ?0:1));
		}
	};
	public Comparator<MaterialInfo> MaterialByAtoZ = new Comparator<MaterialInfo>() {

		public int compare(MaterialInfo object1, MaterialInfo object2) {
			int o1 = 0;
			int o2 = 0;
			o1 = (int)object1.name.toLowerCase().charAt(0);
			o2 = (int)object2.name.toLowerCase().charAt(0);

			return (o1 < o2?-1:(o1== o2 ?0:1));
		}
	};
	
	public Comparator<LocationAreaInfo> LocationAreaByAtoZ = new Comparator<LocationAreaInfo>() {

		public int compare(LocationAreaInfo object1, LocationAreaInfo object2) {
			int o1 = 0;
			int o2 = 0;
			o1 = (int)object1.name.toLowerCase().charAt(0);
			o2 = (int)object2.name.toLowerCase().charAt(0);

			return (o1 < o2?-1:(o1== o2 ?0:1));
		}
	};
	
	public Comparator<DeviceTypesInfo> DeviceTypeByAtoZ = new Comparator<DeviceTypesInfo>() {

		public int compare(DeviceTypesInfo object1, DeviceTypesInfo object2) {
			int o1 = 0;
			int o2 = 0;
			o1 = (int)object1.name.toLowerCase().charAt(0);
			o2 = (int)object2.name.toLowerCase().charAt(0);

			return (o1 < o2?-1:(o1== o2 ?0:1));
		}
	};

	public Comparator<String> ComparatorByStringAtoZ = new Comparator<String>() {

		public int compare(String object1, String object2) {
			return object1.compareTo(object2);
		}
	};
	
}
