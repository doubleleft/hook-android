package com.doubleleft.api;

import org.json.JSONObject;

/**
 * Created by endel on 5/21/14.
 */
public class PushNotification {

    protected Client client;

    public PushNotification(Client client)
    {
        this.client = client;
    }

    public void register(String senderID, String regId, Responder responder)
    {
        /* String versionName; */
        /* String packageName = this.cordova.getActivity().getPackageName(); */
        /* PackageManager pm = this.cordova.getActivity().getPackageManager(); */
        /* try { */
        /*     PackageInfo packageInfo = pm.getPackageInfo(packageName, 0); */
        /*     versionName = packageInfo.versionName; */
        /* } catch (NameNotFoundException nnfe) { */
        /*     versionName = "unknown"; */
        /* } */

        JSONObject data = new JSONObject();
        data.putOpt("platform", "android");
        data.putOpt("app_name", packageName);
        data.putOpt("app_version", packageVersion);
        data.putOpt("device_id", regId);

        this.client.post("push/register", data, responder);
    }

}

