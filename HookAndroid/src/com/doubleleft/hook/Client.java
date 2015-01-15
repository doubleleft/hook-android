package com.doubleleft.hook;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.doubleleft.hookandroid.R;

/**
 * Created by glaet on 2/28/14.
 */
public class Client {

	// Globally-available Client instance
	private static Client instance;

	// Extras
	public KeyValues keys;
	public Auth auth;
	public Files files;
	public System system;

	// Settings
	private String appId;
	private String appKey;
	private String endpointUrl;

	protected static Context context;

	/**
	 * Provides a Context so Hook can read strings and access SharedPreferences
	 *
	 * @param context
	 */
	public static void setup(Context context) {

		// We need to get the ApplicationContext to prevent memory leaks
		Client.context = context.getApplicationContext();

		if (instance == null) {
			instance = new Client(context.getString(R.string.hook_appId), context.getString(R.string.hook_appKey),
					context.getString(R.string.hook_endpointUrl));
			Log.d("hook", "Hook initialized");
		}
	}

	/**
	 * Returns the instance of the Client
	 *
	 * @return
	 */
	public static Client getInstance() {
		if (instance == null) {
			throw new ExceptionInInitializerError("Context not found. You need to call Client.setup(context) at least once.");
		}
		return instance;
	}

	private Client(String appId, String appKey, String endpointUrl) {

		this.appId = appId;
		this.appKey = appKey;
		this.endpointUrl = endpointUrl;

		Log.d("hook", "appId = " + this.appId);
		Log.d("hook", "key = " + this.appKey);
		Log.d("hook", "url = " + this.endpointUrl);

		auth = new Auth(this);
		keys = new KeyValues(this);
		system = new System(this);
	}

	public Collection collection(String collectionName) {
		return new Collection(this, collectionName);
	}

	public Channel channel(String name, JSONObject options) {
		// TODO: implement client Channel API
		throw new Error("Channel API not implemented");
	}

	public Request get(String segments, JSONObject data) {
		return this.request(segments, "GET", data);
	}

	public Request post(String segments, JSONObject data) {
		return this.request(segments, "POST", data);
	}

	public Request put(String segments, JSONObject data) {
		return this.request(segments, "PUT", data);
	}

	public Request remove(String segments) {
		return this.request(segments, "DELETE", null);
	}

	public Request request(String segments, String method, JSONObject data) {
		Request request = new Request();
		request.method = method;
		request.data = data;
		request.addHeader("Content-Type", "application/json");
		request.addHeader("X-App-Id", appId);
		request.addHeader("X-App-Key", appKey);

		Log.d("hook", "request " + data.toString());
		Log.d("hook", "URL_request " + endpointUrl + "/" + segments);

		if (auth.hasAuthToken()) {
			request.addHeader("X-Auth-Token", auth.getAuthToken());
		}

		request.execute(endpointUrl + "/" + segments);
		return request;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}
}
