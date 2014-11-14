package com.doubleleft.hook;

/**
 * Created by glaet on 2/28/14.
 */
public interface Responder {
	void onSuccess(Response response);

	void onError(Response response);
}
