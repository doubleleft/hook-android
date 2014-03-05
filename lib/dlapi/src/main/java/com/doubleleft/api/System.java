package com.doubleleft.api;

/**
 * Created by glaet on 2/28/14.
 */
public class System {

    protected Client client;

    public System(Client client)
    {
        this.client = client;
    }

    public void time(Responder responder)
    {
        this.client.get("system/time", null, responder);
    }
}
