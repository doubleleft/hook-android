package com.doubleleft.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;


/**
 * Created by glaet on 2/28/14.
 */
public class Client {

    public String url;
    public String key;
    public String appId;

    public KeyValues keys;
    public Auth auth;
    public Files files;
    public System system;
    public PushNotification push;
    public Context context;

    public Client(Context context, String url, String key, String appId)
    {
        this.context = context;
        this.url = url;
        this.key = key;
        this.appId = appId;

        keys = new KeyValues(this);
        auth = new Auth(this);
        //files = new Files(this);
        system = new System(this);
        push = new PushNotification(this);
    }

    public Collection collection(String collectionName)
    {
        return new Collection(this, collectionName);
    }

    public Channel channel(String name, JSONObject options)
    {
        //TODO: implement client Channel API
        throw new Error("Channel API not implemented");
    }

    public Request get(String segments, JSONObject data, Responder responder)
    {
        return this.request(segments, "GET", data, responder);
    }

    public Request post(String segments, JSONObject data, Responder responder)
    {
        return this.request(segments, "POST", data, responder);
    }

    public Request put(String segments, JSONObject data, Responder responder)
    {
        return this.request(segments, "PUT", data, responder);
    }

    public Request remove(String segments, Responder responder)
    {
        return this.request(segments, "DELETE", null, responder);
    }

    public Request request(String segments, String method, JSONObject data, Responder responder)
    {
        Request request = new Request();
        request.method = method;
        request.data = data;
        request.addHeader("Content-Type", "application/json");
        request.addHeader("X-App-Id", appId);
        request.addHeader("X-App-Key", key);

        Log.d("dl-api", "request "+data.toString());
        Log.d("dl-api", "URL_request "+this.url + "/" + segments);

        if(auth.hasAuthToken()){
            request.addHeader("X-Auth-Token", auth.getAuthToken());
        }

        request.setResponder(responder);

        request.execute(this.url + "/" + segments);
        return request;
    }
}
