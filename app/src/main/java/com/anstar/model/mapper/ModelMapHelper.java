package com.anstar.model.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anstar.activerecords.ActiveRecordBase;
import com.anstar.activerecords.ActiveRecordException;
import com.anstar.common.Utils;
import com.anstar.fieldwork.FieldworkApplication;

public class ModelMapHelper<T extends ActiveRecordBase> {

	Class<T> type = null;

	public T getObject(Class<T> cls, JSONObject jobj) {
		type = cls;
		List<Field> fields = getColumnFields();

		T entity = getInstance();
		for (Field field : fields) {
			ModelMapper mapper = field.getAnnotation(ModelMapper.class);
			if (mapper != null) {
				field.setAccessible(true);
				String key = mapper.JsonKey();
				boolean isArray = mapper.IsArray();
				if (isArray) {
					ArrayList<String> arr = getStringArray(jobj, key);
					try {
						field.set(entity, arr);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					continue;
				}
				Object val = jobj.opt(key);
				if (val != null) {
					if(val.toString().equalsIgnoreCase("null")){
						continue;
					}
					try {
						entity = setField(field, entity, val.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return entity;
	}

	private ArrayList<String> getStringArray(JSONObject jobj, String key) {

		ArrayList<String> arr = new ArrayList<String>();
		
		try {
			if(jobj.getJSONArray(key) == null || jobj.getJSONArray(key).toString().length() <= 0){
				Utils.LogInfo("array blank ::: "+jobj.getJSONArray(key).toString()+" KEY :: "+key);
				return arr;
			}
			
			JSONArray jarr = jobj.getJSONArray(key);
			for (int i = 0; i < jarr.length(); i++) {
				String val = jarr.getString(i);
				Utils.LogInfo("array blank for loop::: "+val+" KEY :: "+key);
				arr.add(val);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arr;

	}

	private T setField(Field field, T entity, String content)
			throws NumberFormatException, IllegalArgumentException,
			IllegalAccessException {
		if (field.getType() == long.class) {
			if (content != null) {
				field.setLong(entity, parseLong(content));
			}
		} else if (field.getType() == String.class) {
			field.set(entity, content);
		} else if (field.getType() == int.class) {
			if (content != null) {

				field.setInt(entity, parseInt(content));
			}
		} else if (field.getType() == Double.class) {
			if (content != null) {
				field.setDouble(entity, parseDouble(content));
			}
		} else if (field.getType() == float.class) {
			if (content != null) {
				field.setFloat(entity, parseFloat(content));
			}
		} else if (field.getType() == boolean.class) {
			boolean val = false;
			if (content.equalsIgnoreCase("yes")) {
				val = true;
			}
			if (content.equalsIgnoreCase("1")) {
				val = true;
			}
			if (content.equalsIgnoreCase("true")) {
				val = true;
			}
			field.setBoolean(entity, val);
		}
		return entity;

	}

	private T getInstance() {
		T entity = null;
		try {
			if (this.type.getSuperclass() == ActiveRecordBase.class) {
				entity = FieldworkApplication.Connection().newEntity(type);
			} else {
				entity = this.type.newInstance();
			}
		} catch (ActiveRecordException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return entity;
	}

	protected List<Field> getColumnFields() {
		Field[] fields = this.type.getDeclaredFields();
		List<Field> columns = new ArrayList<Field>();
		for (Field field : fields) {
			columns.add(field);
		}
		return columns;
	}

	public String getUniqueFieldName(Class<T> cls) {
		type = cls;
		List<Field> fields = getColumnFields();
		for (Field field : fields) {
			ModelMapper mapper = field.getAnnotation(ModelMapper.class);
			if (mapper != null) {
				boolean isUnique = mapper.IsUnique();
				if (isUnique) {
					return field.getName();
				}
			}

		}
		return "_id";
	}

	public Object getUniqueFieldValue(Class<T> cls, T obj) {
		type = cls;
		List<Field> fields = getColumnFields();
		for (Field field : fields) {
			ModelMapper mapper = field.getAnnotation(ModelMapper.class);
			if (mapper != null) {
				boolean isUnique = mapper.IsUnique();
				if (isUnique) {
					try {
						return field.get(obj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}

		}
		return "";
	}

	private int parseInt(String val) {
		if (val.length() <= 0) {
			return 0;
		}
		int intval = 0;
		try {
			intval = Integer.parseInt(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return intval;
	}

	private double parseDouble(String val) {
		double d = 0;
		try {
			d = Double.parseDouble(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return d;
	}
	
	private float parseFloat(String val) {
		float d = 0;
		try {
			d =Float.parseFloat(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return d;
	}

	private long parseLong(String val) {
		long d = 0;
		try {
			d = Long.parseLong(val);
		} catch (Exception e) {
			Utils.LogException(e);
		}
		return d;
	}

}
