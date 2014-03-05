package com.doubleleft.api.tests;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.doubleleft.api.Client;
import com.doubleleft.api.Responder;
import com.doubleleft.api.Response;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

/**
 * Created by glaet on 2/28/14.
 */
public class CollectionTest extends InstrumentationTestCase {

    public String appId = "1";
    public String appKey = "q1uU7tFtXnLad6FIGGn2cB+gxcx64/uPoDhqe2Zn5AE=";
    public String endpointURL = "http://dl-api.ddll.co";

    public void testCreateAndFetch() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        Client client = new Client(null, endpointURL, appKey, appId);

        JSONObject data = new JSONObject();
        data.put("device", "Samsung Galaxy");
        data.put("version", 10.0);
        data.put("hasCameraSupport", true);

        client.collection("android").create(data, new Responder() {
            @Override
            public void onSuccess(Response response) {
                Log.d("dl-api", response.raw);
                boolean cameraSupport = response.object.optBoolean("hasCameraSupport");
                assertTrue(cameraSupport);
                assertEquals("10", response.object.optString("version"));
                assertEquals("Samsung Galaxy", response.object.optString("device"));
                signal.countDown();
            }

            @Override
            public void onError(Response response) {
                Log.d("dl-api", "onError: "+response.raw);
                signal.countDown();
            }
        });
        signal.await();
    }

    public void testWhere() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        Client client = new Client(null, endpointURL, appKey, appId);
        client.collection("android").where("version", 10).get(new Responder() {
            @Override
            public void onSuccess(Response response) {
                assertEquals("10", response.object.optString("version"));
                signal.countDown();
            }

            @Override
            public void onError(Response response) {
                Log.d("dl-api", "onError: "+response.raw);
                signal.countDown();
            }
        });

        signal.await();
    }

    public void testSort() throws Exception {
        final CountDownLatch signal = new CountDownLatch(2);
        final Client client = new Client(null, endpointURL, appKey, appId);

        JSONObject data = new JSONObject();
        data.put("device", "Samsung Galaxy");
        data.put("version", 5.0);

        client.collection("android").create(data, new Responder() {
            @Override
            public void onSuccess(Response response) {
                client.collection("android").sort("version", "desc").get(new Responder() {
                    @Override
                    public void onSuccess(Response response) {
                        assertEquals("10", response.object.optString("version"));
                        signal.countDown();
                    }

                    @Override
                    public void onError(Response response) {
                        Log.d("dl-api", "onError: "+response.raw);
                        signal.countDown();
                    }
                });
                signal.countDown();
            }

            @Override
            public void onError(Response response) {
                Log.d("dl-api", "onError: "+response.raw);
                signal.countDown();
            }
        });

        signal.await();
    }
}
