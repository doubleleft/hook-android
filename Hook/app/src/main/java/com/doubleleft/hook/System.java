package com.doubleleft.hook;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by glaet on 2/28/14.
 */
public class System {

	protected Client client;

	public System(Client client) {
		this.client = client;
	}

	public void time(AsyncHttpResponseHandler responseHandler) {
		this.client.get("system/time", null, responseHandler);
	}
}
