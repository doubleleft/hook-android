package com.doubleleft.hook.tests;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.doubleleft.hook.Client;

/**
 * Created by glaet on 2/28/14.
 */
public class ClientTest extends InstrumentationTestCase {

	public void test(Context context) throws Exception {
		assertNotNull(Client.getInstance().getAppId());
		assertNotNull(Client.getInstance().getAppKey());
		assertNotNull(Client.getInstance().getEndpoint());
	}
}
