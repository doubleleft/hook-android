package com.doubleleft.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Created by glaet on 2/28/14.
 */
public class Response {
    public JSONObject object;
    public JSONArray array;
    public String raw;
    public int code;
    
    public Integer toInt()
    {
    	return Integer.parseInt(raw.replace("\"", ""));
    }
}
