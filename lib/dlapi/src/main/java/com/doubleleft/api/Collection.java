package com.doubleleft.api;

import org.json.JSONObject;


/**
 * Created by glaet on 2/28/14.
 */

public class Collection {
    protected Client client;
    protected String name;
    protected String segments;

    public Collection(Client client, String name)
    {
        if(!name.matches("^[a-z_//0-9]+$")){
            throw new Error("Invalid name"+ name);
        }

        this.client = client;
        this.name = name;
        this.segments = "collection/" + this.name;
        this.reset();
    }

    public void create(JSONObject data, Responder responder)
    {
        client.post(this.segments, data, responder);
    }

    public void get(Responder responder)
    {
        client.get(this.segments, this.buildQuery(), responder);
    }

    public void update(int id, JSONObject data, Responder responder)
    {
        client.post(this.segments + "/" + id, data, responder);
    }

    public void remove(int id, Responder responder)
    {
        client.remove(this.segments + "/" + id, responder);
    }

    public JSONObject buildQuery()
    {
        return new JSONObject();
    }

    public void reset()
    {

    }
}
