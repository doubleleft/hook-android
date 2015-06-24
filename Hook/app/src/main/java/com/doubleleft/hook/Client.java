package com.doubleleft.hook;

import com.loopj.android.http.*;

import android.content.Context;
import android.util.Log;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by glaet on 2/28/14.
 */
public class Client {

    // Globally-available Client instance
    private static Client instance;
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public static Context context;

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

    public Client(Context context, String appId, String appKey, String endpoint) {
        this.appId = appId;
        this.appKey = appKey;
        this.endpoint= endpoint;

        auth = new Auth(this);
        keys = new KeyValues(this);
        system = new System(this);

        // Singleton
        instance = this;
        instance.context = context;

        // Request headers
        httpClient.addHeader("Content-Type", "application/json");
        httpClient.addHeader("X-App-Id", appId);
        httpClient.addHeader("X-App-Key", appKey);
    }

    public Collection collection(String collectionName) {
        return new Collection(this, collectionName);
    }

    public Channel channel(String name, JSONObject options) {
        // TODO: implement client Channel API
        throw new Error("Channel API not implemented");
    }

    public RequestHandle get(String segments, JSONObject data, AsyncHttpResponseHandler responseHandler) {
        return this.request(segments, "GET", data, responseHandler);
    }

    public RequestHandle post(String segments, JSONObject data, AsyncHttpResponseHandler responseHandler) {
        return this.request(segments, "POST", data, responseHandler);
    }

    public RequestHandle put(String segments, JSONObject data, AsyncHttpResponseHandler responseHandler) {
        return this.request(segments, "PUT", data, responseHandler);
    }

    public RequestHandle remove(String segments, AsyncHttpResponseHandler responseHandler) {
        // Send empty request params
        JSONObject params = new JSONObject();
        return this.request(segments, "DELETE", params, responseHandler);
    }

    public RequestHandle request(String segments, String method, JSONObject data, AsyncHttpResponseHandler responseHandler) {
        if (auth.hasAuthToken()) {
            httpClient.addHeader("X-Auth-Token", auth.getAuthToken());
        } else {
            httpClient.removeHeader("X-Auth-Token");
        }

        StringEntity entity = null;
        RequestHandle handle = null;

        try {
            entity = new StringEntity( data.toString() );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (method == "POST") {
            handle = httpClient.post(context, endpoint + "/" + segments, entity, "application/json", responseHandler);

        } else if (method == "PUT") {
            handle = httpClient.put(context, endpoint + "/" + segments, entity, "application/json", responseHandler);

        } else if (method == "DELETE" || method == "GET") {
            String queryString = "";
            try {
                queryString = URLEncoder.encode(data.toString(), "utf-8");

            } catch (IOException e) {
                e.printStackTrace();
            }

            handle = httpClient.get(endpoint + "/" + segments + "?" + queryString, responseHandler);
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpointUrl) {
        this.endpoint = endpointUrl;
    }
}
