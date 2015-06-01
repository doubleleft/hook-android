package com.doubleleft.hook;

import com.loopj.android.http.*;
import com.loopj.android.http.RequestParams;

import android.util.Log;

/**
 * Created by glaet on 2/28/14.
 */
public class Client {

	// Globally-available Client instance
	private static Client instance;
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

	// Extras
	public KeyValues keys;
	public Auth auth;
	public System system;

	// Settings
	private String appId;
	private String appKey;
	private String endpoint;

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

	public Client(String appId, String appKey, String endpoint) {
		this.appId = appId;
		this.appKey = appKey;
		this.endpoint= endpoint;

		Log.d("hook", "appId = " + this.appId);
		Log.d("hook", "key = " + this.appKey);
		Log.d("hook", "url = " + this.endpoint);

		auth = new Auth(this);
		keys = new KeyValues(this);
		system = new System(this);

		// Singleton
		instance = this;
	}

	public Collection collection(String collectionName) {
		return new Collection(this, collectionName);
	}

	public Channel channel(String name, RequestParams options) {
		// TODO: implement client Channel API
		throw new Error("Channel API not implemented");
	}

	public RequestHandle get(String segments, RequestParams data, ResponseHandlerInterface responseHandler) {
		this.request(segments, "GET", data, responseHandler);
	}

	public RequestHandle post(String segments, RequestParams data, ResponseHandlerInterface responseHandler) {
		this.request(segments, "POST", data, responseHandler);
	}

	public RequestHandle put(String segments, RequestParams data, ResponseHandlerInterface responseHandler) {
		this.request(segments, "PUT", data, responseHandler);
	}

	public RequestHandle remove(String segments, ResponseHandlerInterface responseHandler) {
        // Send empty request params
        RequestParams params = new RequestParams();
        this.request(segments, "DELETE", params, responseHandler);
	}

	public RequestHandle request(String segments, String method, RequestParams data, ResponseHandlerInterface responseHandler) {

        // Request headers
        httpClient.addHeader("Content-Type", "application/json");
        httpClient.addHeader("X-App-Id", appId);
        httpClient.addHeader("X-App-Key", appKey);

        if (auth.hasAuthToken()) {
            httpClient.addHeader("X-Auth-Token", auth.getAuthToken());
        }

        data.setUseJsonStreamer(true);

        RequestHandle handle;

        if (method == "POST") {
            handle = httpClient.get(endpoint + "/" + segments, data, responseHandler);

        } else if (method == "PUT") {
            handle = httpClient.put(endpoint + "/" + segments, data, responseHandler);

        } else if (method == "DELETE") {
            handle = httpClient.delete(endpoint + "/" + segments, responseHandler);

        } else {
            handle = httpClient.get(endpoint + "/" + segments, data, responseHandler);
        }

		Log.d("hook", "request " + data.toString());
		Log.d("hook", "URL_request " + endpoint + "/" + segments);

		return handle;
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
