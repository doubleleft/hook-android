package com.doubleleft.hook;

import org.json.JSONArray;
import org.json.RequestParams;

/**
 * Created by glaet on 2/28/14.
 */
public class Response {
	public RequestParams object;
	public JSONArray array;
	public String raw;
	public int code;

	public Integer toInt() {
		return Integer.parseInt(raw.replace("\"", ""));
	}
}
