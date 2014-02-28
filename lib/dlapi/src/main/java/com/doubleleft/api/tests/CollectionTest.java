package com.doubleleft.api.tests;

import android.test.InstrumentationTestCase;

import com.doubleleft.api.Client;
import com.doubleleft.api.Responder;
import com.doubleleft.api.Response;

import org.json.JSONObject;

/**
 * Created by glaet on 2/28/14.
 */
public class CollectionTest extends InstrumentationTestCase{
    public void test() throws Exception {
        String appId = "1";
        String appKey = "q1uU7tFtXnLad6FIGGn2cB+gxcx64/uPoDhqe2Zn5AE=";
        String endpointURL = "http://dl-api.ddll.co";

        Client client = new Client(endpointURL, appKey, appId);

        JSONObject data = new JSONObject();
        data.put("device", "Samsung Galaxy");
        data.put("version", 10.0);
        data.put("hasCameraSupport", true);

        client.collection("android").create(data, new Responder() {
            @Override
            public void onSuccess(Response response) {
                boolean cameraSupport = response.object.optBoolean("hasCameraSupport");
                assertTrue(cameraSupport);
                assertEquals(10.0, response.object.optInt("version"));
                assertEquals("Samsung Galaxy", response.object.optString("device"));
            }

            @Override
            public void onError(Response response) {
                
            }
        });
    }
}
