package com.doubleleft.hook.tests;

import java.util.concurrent.CountDownLatch;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.doubleleft.hook.Client;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by glaet on 2/28/14.
 */
public class CollectionTest extends InstrumentationTestCase {

	public String appId = "1";
	public String appKey = "q1uU7tFtXnLad6FIGGn2cB+gxcx64/uPoDhqe2Zn5AE=";
	public String endpointURL = "http://dl-api.ddll.co";

	public void testCreateAndFetch() throws Exception {

		final CountDownLatch signal = new CountDownLatch(1);

		RequestParams data = new RequestParams();
		data.put("device", "Samsung Galaxy");
		data.put("version", 10.0);
		data.put("hasCameraSupport", true);

		Client.getInstance().collection("android").create(data, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Log.d("dl-api", response.toString());
				boolean cameraSupport = response.optBoolean("hasCameraSupport");
				assertTrue(cameraSupport);
				assertEquals("10", response.optString("version"));
				assertEquals("Samsung Galaxy", response.optString("device"));
				signal.countDown();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Log.d("dl-api", "onError: " + errorResponse.toString());
				signal.countDown();
			}
		});
		signal.await();
	}

	public void testWhere() throws Exception {

		final CountDownLatch signal = new CountDownLatch(1);
		Client.getInstance().collection("android").where("version", 10).get(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				assertEquals("10", response.optString("version"));
				signal.countDown();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Log.d("dl-api", "onError: " + errorResponse.toString());
				signal.countDown();
			}
		});

		signal.await();
	}

	public void testSort() throws Exception {

		final CountDownLatch signal = new CountDownLatch(2);

		RequestParams data = new RequestParams();
		data.put("device", "Samsung Galaxy");
		data.put("version", 5.0);

		Client.getInstance().collection("android").create(data, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Client.getInstance().collection("android").sort("version", "desc").get(new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						assertEquals("10", response.optString("version"));
						signal.countDown();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
						Log.d("dl-api", "onError: " + errorResponse.toString());
						signal.countDown();
					}
				});
				signal.countDown();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Log.d("dl-api", "onError: " + errorResponse.toString());
				signal.countDown();
			}
		});

		signal.await();
	}
}
