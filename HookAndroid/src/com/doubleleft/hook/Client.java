package com.doubleleft.hook;

import org.json.JSONObject;

import com.doubleleft.hookandroid.R;

import android.content.Context;
import android.util.Log;

/**
 * Created by glaet on 2/28/14.
 */
public class Client {

	private String appId;
	private String appKey;
	private String url;
	
	private KeyValues keys;
	private Auth auth;
	private Files files;
	private System system;
	private Context context;

	public Client(Context context) {
		
		this.context = context;
		appId = context.getString(R.string.hook_appId);
		appKey = context.getString(R.string.hook_appKey);
		url = context.getString(R.string.hook_endpointUrl);
		
		Log.d("hook", "appId = " + appId);
		Log.d("hook", "appKey = " + appKey);
		Log.d("hook", "url = " + url);
		
		keys = new KeyValues(this);
		auth = new Auth(this);
//		files = new Files(this);
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

		if (auth.hasAuthToken()) {
			request.addHeader("X-Auth-Token", auth.getAuthToken());
		}

		request.setResponder(responder);

		request.execute(this.url + "/" + segments);
		return request;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public String getUrl() {
		return url;
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
