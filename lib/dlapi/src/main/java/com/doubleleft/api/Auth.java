package com.doubleleft.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by glaet on 2/28/14.
 */
public class Auth
{
    public static String PROVIDER_EMAIL = "email";
    public static String PROVIDER_FACEBOOK = "facebook";

    protected static String AUTH_TOKEN_KEY = "dl-api-auth-token";
    protected static String AUTH_DATA_KEY = "dl-api-auth-data";
    protected Client client;
    protected SharedPreferences localStorage;
    protected JSONObject _currentUser;

    public Auth(Client client)
    {
        this.client = client;
        if(client.context != null){
            localStorage = client.context.getSharedPreferences("dl-api-localStorage-"+client.appId, Context.MODE_PRIVATE);
        }

        if(localStorage != null){
            String currentUser = localStorage.getString(client.appId + "-" + AUTH_DATA_KEY, null);
            if(currentUser != null){
                try{
                    JSONObject user = (JSONObject) new JSONTokener(currentUser).nextValue();
                    setCurrentUser(user);

                }catch(JSONException e){
                    Log.d("dl-api", "error on Auth module " + e.toString());
                }
            }
        }
    }

    public void authenticate(String provider, JSONObject data, Responder responder)
    {
        final Responder clientResponder = responder;

        client.post("auth/" + provider, data, new Responder() {
            @Override
            public void onSuccess(Response response) {
                registerToken(response.object);
                clientResponder.onSuccess(response);
            }

            @Override
            public void onError(Response response) {
                clientResponder.onError(response);
            }
        });
    }

    public void verify(String provider, JSONObject data, Responder responder)
    {
        final Responder clientResponder = responder;

        client.post("auth/" + provider + "/verify", data, new Responder() {
            @Override
            public void onSuccess(Response response) {
                registerToken(response.object);
                clientResponder.onSuccess(response);
            }

            @Override
            public void onError(Response response) {
                clientResponder.onError(response);
            }
        });
    }

    public void forgotPassword(JSONObject data, Responder responder)
    {
        client.post("auth/"+PROVIDER_EMAIL+"/forgotPassword", data, responder);
    }

    public void resetPassword(JSONObject data, Responder responder)
    {
        client.post("auth/"+PROVIDER_EMAIL+"/resetPassword", data, responder);
    }

    public void logout()
    {
        setCurrentUser(null);
    }

    public boolean hasAuthToken()
    {
        return getAuthToken() != null;
    }

    public String getAuthToken()
    {
        return localStorage != null ? localStorage.getString(client.appId + "-" + AUTH_DATA_KEY, null) : null;
    }

    protected void setCurrentUser(JSONObject data)
    {
        _currentUser = data;

        if(localStorage != null){
            SharedPreferences.Editor editor = localStorage.edit();
            if(_currentUser == null){
                editor.remove(client.appId + "-" + AUTH_TOKEN_KEY);
                editor.remove(client.appId + "-" + AUTH_DATA_KEY);
            }else{
                editor.putString(client.appId + "-" + AUTH_DATA_KEY, _currentUser.toString());
            }
            editor.commit();
        }
    }

    protected JSONObject getCurrentUser()
    {
        return _currentUser;
    }

    protected void registerToken(JSONObject data)
    {
        JSONObject tokenObject = data.optJSONObject("token");
        if(tokenObject != null){
            if(localStorage != null){
                SharedPreferences.Editor editor = localStorage.edit();
                editor.putString(client.appId + "-" + AUTH_TOKEN_KEY, tokenObject.optString("token"));
                editor.commit();
            }
            setCurrentUser(data);
        }
    }

}
