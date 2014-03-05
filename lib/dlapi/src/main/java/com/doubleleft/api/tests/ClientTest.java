package com.doubleleft.api.tests;

import android.test.InstrumentationTestCase;

import com.doubleleft.api.Client;

/**
 * Created by glaet on 2/28/14.
 */
public class ClientTest extends InstrumentationTestCase {
    public void test() throws Exception {
        String appId = "1";
        String appKey = "q1uU7tFtXnLad6FIGGn2cB+gxcx64/uPoDhqe2Zn5AE=";
        String endpointURL = "http://dl-api.ddll.co";

        Client client = new Client(null, endpointURL, appKey, appId);
        assertEquals(client.url, endpointURL);
        assertEquals(client.appId, appId);
        assertEquals(client.key, appKey);
    }
}
