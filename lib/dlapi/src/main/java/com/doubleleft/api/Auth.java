package com.doubleleft.api;

import org.json.JSONObject;

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

    public Auth(Client client)
    {
        this.client = client;
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

    protected void setCurrentUser(JSONObject data)
    {

    }

    protected JSONObject getCurrentUser()
    {
        return null;
    }

    protected void registerToken(JSONObject data)
    {

    }

}
