package com.doubleleft.hook.tests;

import android.test.InstrumentationTestCase;

import com.doubleleft.hook.Client;

/**
 * Created by glaet on 2/28/14.
 */
public class ClientTest extends InstrumentationTestCase {
	public void test() throws Exception {
		String appId = "1";
		String appKey = "q1uU7tFtXnLad6FIGGn2cB+gxcx64/uPoDhqe2Zn5AE=";
		String endpointURL = "http://dl-api.ddll.co";

		Client client = new Client(null);
		assertEquals(client.getAppId(), appId);
		assertEquals(client.getAppKey(), appKey);
		assertEquals(client.getUrl(), endpointURL);
	}
}
