package com.doubleleft.hook;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.doubleleft.hook.exceptions.AuthNotSetupException;
import com.doubleleft.hook.exceptions.ClientNotSetupException;

/**
 * Created by glaet on 2/28/14.
 */
public class Client {

	// Globally-available Client instance
	public static Client instance;

	private final String CLIENT_EXCEPTION_MESSAGE = "Hook Client not setup. Please, set the values for the statics Client.appId, Client.appKey and Client.endpoint accordingly.";
	private final String AUTH_EXCEPTION_MESSAGE = "Context not set. Requests requiring authentication will not work. Allow Hook to setup authentication by setting Client.context to your app context.";

	public KeyValues keys;
	public Auth auth;
	public Files files;
	public System system;

	protected String appId;
	protected String key;
	protected String endpoint;

	protected Context context;

	public static Client configure(Context context) {
		Client instance = new Client(
			context.getString(R.string.hook_appId),
			context.getString(R.string.hook_appKey),
			context.getString(R.string.hook_endpointUrl)
		);

		instance.setContext(context);
		Client.instance = instance;

		return instance;
	}

	public Client(String appId, String key, String endpoint) {
		this.appId = appId;
		this.key = key;
		this.endpoint = endpoint;

		Log.d("hook", "appId = " + this.appId);
		Log.d("hook", "key = " + this.key);
		Log.d("hook", "url = " + this.endpoint);

		auth = new Auth(this);
		keys = new KeyValues(this);
		system = new System(this);

		// Not implemented yet
		// files = new Files(this);
	}

	public Collection collection(String collectionName) {
		return new Collection(this, collectionName);
	}

	public Channel channel(String name, JSONObject options) {
		// TODO: implement client Channel API
		throw new Error("Channel API not implemented");
	}

	public Request get(String segments, JSONObject data, Responder responder) {
		return this.request(segments, "GET", data, responder);
	}

	public Request post(String segments, JSONObject data, Responder responder) {
		return this.request(segments, "POST", data, responder);
	}

	public Request put(String segments, JSONObject data, Responder responder) {
		return this.request(segments, "PUT", data, responder);
	}

	public Request remove(String segments, Responder responder) {
		return this.request(segments, "DELETE", null, responder);
	}

	public Request request(String segments, String method, JSONObject data, Responder responder) {
		Request request = new Request();
		request.method = method;
		request.data = data;
		request.addHeader("Content-Type", "application/json");
		request.addHeader("X-App-Id", appId);
		request.addHeader("X-App-Key", key);

		Log.d("hook", "request " + data.toString());
		Log.d("hook", "URL_request " + url + "/" + segments);

		if (auth.hasAuthToken()) {
			request.addHeader("X-Auth-Token", auth.getAuthToken());
		}

		request.setResponder(responder);

		request.execute(url + "/" + segments);
		return request;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void getContext() {
		return this.context;
	}

	public String getAppId() {
		return this.appId;
	}

	public String getKey() {
		return this.key;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

}
