package com.doubleleft.hook.tests;

import java.util.concurrent.CountDownLatch;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.doubleleft.hook.*;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by glaet on 2/28/14.
 */
public class CollectionTest extends InstrumentationTestCase {

	public String appId = "2";
	public String appKey = "89b05c703fc10ad619a8a3dde3603c4e";
	public String endpointURL = "http://localhost:4665/";

	public void testCreateAndFetch() throws Exception {
		Client client = new Client(null, appId, appKey, endpointURL);

		final CountDownLatch signal = new CountDownLatch(1);

		JSONObject data = new JSONObject();
		data.put("device", "Samsung Galaxy");
		data.put("version", 10.0);
		data.put("hasCameraSupport", true);

		client.collection("android").create(data, new JsonHttpResponseHandler() {
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
		Client client = new Client(null, appId, appKey, endpointURL);

		final CountDownLatch signal = new CountDownLatch(1);
		client.collection("android").where("version", 10).get(new JsonHttpResponseHandler() {
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
		Client client = new Client(null, appId, appKey, endpointURL);

		final CountDownLatch signal = new CountDownLatch(2);

		JSONObject data = new JSONObject();
		data.put("device", "Samsung Galaxy");
		data.put("version", 5.0);

		client.collection("android").create(data, new JsonHttpResponseHandler() {
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
