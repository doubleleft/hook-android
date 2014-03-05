package com.doubleleft.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by glaet on 2/28/14.
 */
public class KeyValues
{
    protected Client client;

    public KeyValues(Client client)
    {
        this.client = client;
    }

    public void get(String key, Responder responder)
    {
        this.client.get("key/" + key, null, responder);
    }

    public void set(String key, Object value, Responder responder)
    {
        JSONObject data = new JSONObject();
        try{
            data.putOpt("value", value);

        }catch (JSONException e){
            Log.d("dl-api", "error when setting key "+e.toString());
        }

        this.client.post("key/" + key, data, responder);
    }
}
