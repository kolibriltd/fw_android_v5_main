package com.anstar.common;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Generics {

	public static <T> ArrayList<String> getStringList(String propertyName,
			ArrayList<? extends T> mainList, Class<T> type)
			throws NoSuchFieldException {
		ArrayList<String> lst = new ArrayList<String>();
		Field one = null;
		try {
			one = type.getField(propertyName);
		} catch (NoSuchFieldException e1) {
			throw e1;
		}

		for (T t : mainList) {
			try {
				if (one != null) {
					String val = one.get(t).toString();
					if (val != null) {
						lst.add(val);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return lst;
	}
}
