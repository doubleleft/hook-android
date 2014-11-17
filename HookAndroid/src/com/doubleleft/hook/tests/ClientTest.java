package com.doubleleft.hook.tests;

import android.test.InstrumentationTestCase;

import com.doubleleft.hook.Client;

/**
 * Created by glaet on 2/28/14.
 */
public class ClientTest extends InstrumentationTestCase {

	public void test() throws Exception {
		assertNotNull(Client.appId);
		assertNotNull(Client.appKey);
		assertNotNull(Client.url);
	}
}
