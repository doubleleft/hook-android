package com.doubleleft.hook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;

import android.util.Log;

/**
 * Base Entity Class. Your model Classes should extend this Class.
 * 
 * @author lucas.tulio
 * 
 */
public abstract class Entity {

	public void create(Responder responder) {

		// Populate fields using Reflection
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		for (Field field : this.getEntityFields()) {
			try {
				hashMap.put(field.getName().toLowerCase(Locale.getDefault()), field.get(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// D-D-D-D-DROP THE BASS
		Client client = new Client();
		String collectionName = this.getEntityName();
		final JSONObject jsonObject = new JSONObject(hashMap);
		client.collection(collectionName).create(jsonObject, responder);
	}

	/**
	 * Reflection method to return the table fields
	 * 
	 * @return
	 */
	private Field[] getEntityFields() {

		ArrayList<Field> fields = new ArrayList<Field>();
		for (Field field : this.getClass().getFields()) {

			if (field.getDeclaringClass() != Entity.class) {
				Log.d("Reflection", "Field: " + field.getName());
				fields.add(field);
			} else {
				Log.d("Reflection", "(Ignoring Entity field: " + field.getName() + ")");
			}
		}

		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Reflection method to return the table name
	 * 
	 * @return
	 */
	private String getEntityName() {
		String fullClassName = this.getClass().getName().toLowerCase(Locale.getDefault());
		String[] parts = fullClassName.split("\\.");
		String entityName = parts[parts.length - 1];
		Log.d("Reflection", "Entity name: " + entityName);
		return entityName;
	}
}
