package com.doubleleft.hook;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.doubleleft.hook.exceptions.ClientNotSetupException;

/**
 * Created by glaet on 2/28/14.
 */
public class Client {

	public static String appId;
	public static String appKey;
	public static String url;
	private Context context;

	private KeyValues keys;
	private Auth auth;
	private Files files;
	private System system;

	public Client() throws ClientNotSetupException {

		// Check if the Client has been setup
		if (appId == null || appKey == null || url == null) {
			throw new ClientNotSetupException(
					"Hook Client not setup. Please, set the values for the statics Client.appId, Client.appKey and Client.url accordingly.");
		}

		Log.d("hook", "appId = " + appId);
		Log.d("hook", "appKey = " + appKey);
		Log.d("hook", "url = " + url);

		keys = new KeyValues(this);
		// auth = new Auth(this);
		// files = new Files(this);
		system = new System(this);
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
		request.addHeader("X-App-Key", appKey);

		Log.d("dl-api", "request " + data.toString());
		Log.d("dl-api", "URL_request " + this.url + "/" + segments);

		if (auth != null && auth.hasAuthToken()) {
			request.addHeader("X-Auth-Token", auth.getAuthToken());
		}

		request.setResponder(responder);

		request.execute(this.url + "/" + segments);
		return request;
	}

	public KeyValues getKeys() {
		return keys;
	}

	public void setKeys(KeyValues keys) {
		this.keys = keys;
	}

	public Auth getAuth() {
		return auth;
	}

	public void setAuth(Auth auth) {
		this.auth = auth;
	}

	public Files getFiles() {
		return files;
	}

	public void setFiles(Files files) {
		this.files = files;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
