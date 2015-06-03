package com.doubleleft.hook;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by glaet on 2/28/14.
 */
public class KeyValues {
	protected Client client;

	public KeyValues(Client client) {
		this.client = client;
	}

	public void get(String key, AsyncHttpResponseHandler responseHandler) {
		this.client.get("key/" + key, null, responseHandler);
	}

	public void set(String key, Object value, AsyncHttpResponseHandler responseHandler) {
		JSONObject data = new JSONObject();
		try {
			data.put("value", value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.client.post("key/" + key, data, responseHandler);
	}
}
