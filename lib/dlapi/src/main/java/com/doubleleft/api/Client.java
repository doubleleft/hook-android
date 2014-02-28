package com.doubleleft.api;

import java.util.Hashtable;

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

    public Client(String url, String key, String appId)
    {
        this.url = url;
        this.key = key;
        this.appId = appId;

        keys = new KeyValues(this);
        auth = new Auth(this);
        files = new Files(this);
        system = new System(this);
    }

    public Collection collection(String collectionName)
    {
        return new Collection(this, collectionName);
    }

    public Channel channel(String name, Hashtable options)
    {
        return null;
    }

    public void post()
    {

    }

    public void get()
    {

    }

    public void put()
    {

    }

    public void delete()
    {

    }

    public void request()
    {

    }
}
