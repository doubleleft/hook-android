package com.doubleleft.hook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

/**
 * Base Model Class. Your model Classes should extend this Class.
 *
 * @author lucas.tulio
 *
 */
public abstract class Model {

	public void create(AsyncHttpResponseHandler responseHandler) {

		// Populate fields using Reflection
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		for (Field field : this.getModelFields()) {
			try {
				hashMap.put(field.getName().toLowerCase(Locale.getDefault()), field.get(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// D-D-D-D-DROP THE BASS
		String collectionName = this.getModelName();
		JSONObject jsonObject = new JSONObject(hashMap);
		Client.getInstance().collection(collectionName).create(jsonObject, responseHandler);
	}

	/**
	 * Reflection method to return the table fields
	 *
	 * @return
	 */
	private Field[] getModelFields() {

		ArrayList<Field> fields = new ArrayList<Field>();
		for (Field field : this.getClass().getFields()) {

			if (field.getDeclaringClass() != Model.class) {
				Log.d("Reflection", "Field: " + field.getName());
				fields.add(field);
			} else {
				Log.d("Reflection", "(Ignoring Model field: " + field.getName() + ")");
			}
		}

		return fields.toArray(new Field[fields.size()]);
	}

	/**
	 * Reflection method to return the table name
	 *
	 * @return
	 */
	private String getModelName() {
		String fullClassName = this.getClass().getName().toLowerCase(Locale.getDefault());
		String[] parts = fullClassName.split("\\.");
		String modelName = parts[parts.length - 1];
		Log.d("Reflection", "Model name: " + modelName);
		return modelName;
	}
}
