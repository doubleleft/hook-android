package com.doubleleft.dlapi;

import android.content.Context;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Created by glaet on 2/19/14.
 */

public class DLApi {
    protected WebView webView;
    protected String endpoint;
    protected DLApiChromeClient chromeClient;
    protected DLApiWebViewClient webviewClient;
    protected DLApiJavaScriptInterface jsInterface;
    protected Context context;
    protected int requestsCount;

    public DLApi(Context context, String endpointURL)
    {
        this.endpoint = endpointURL;
        this.context = context;

        chromeClient = new DLApiChromeClient();
        webviewClient = new DLApiWebViewClient();
        jsInterface = new DLApiJavaScriptInterface();

        webView = new WebView(context);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(webviewClient);
        webView.addJavascriptInterface(jsInterface, "native");
        webView.loadUrl(endpoint);

    }

    public void call(String method, DLApiResponseHandler handler){
        this.call(method, null, handler);
    }

    public void call(String method, JSONObject params, DLApiResponseHandler handler){

        DLApiRequest r = new DLApiRequest(requestsCount++, method, params, handler);
        jsInterface.executeRequest(r);
    }

    /*
    DLApi Request
    */
    protected class DLApiRequest
    {
        protected JSONObject params;
        protected String method;
        protected int id;
        protected DLApiResponseHandler responseHandler;

        protected DLApiRequest(int id, String method, JSONObject params, DLApiResponseHandler handler)
        {
            this.method = method;
            this.params = params;
            this.id = id;
            this.responseHandler = handler;
        }

        protected String getJavaScript()
        {
            String jsParams = "null";
            if(params != null){
                jsParams = "'"+params.toString()+"'";
            }
            return "javascript:dlApiCall('"+method+"',"+id+", "+jsParams+");";
        }
    }

    /*
    WebView client
     */
    protected class DLApiChromeClient extends WebChromeClient
    {
        protected DLApiChromeClient()
        {

        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage message) {
            Log.d("[dl-api]", message.message() + " -- From line " + message.lineNumber() + " of " + message.sourceId());
            return true;
        }
    }

    /*
     WebView client
     */
    protected class DLApiWebViewClient extends WebViewClient
    {
        boolean isReady;

        protected DLApiWebViewClient()
        {
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            Log.d("[dl-api]", "ready to launch");
            if(!isReady){
                isReady = true;
                jsInterface.runQueue();
            }
            super.onPageFinished(view, url);
        }
    }


    /*
    WebView JavaScript client
    */

    protected class DLApiJavaScriptInterface
    {
        protected HashMap<Integer, DLApiRequest> requests;
        protected Vector<DLApiRequest> queue;

        protected DLApiJavaScriptInterface()
        {
            queue = new Vector<DLApiRequest>();
            requests = new HashMap<Integer, DLApiRequest>();
        }

        public void executeRequest(DLApiRequest request)
        {
            if(webviewClient.isReady){
                requests.put(request.id, request);
                request.responseHandler.onStart();
                webView.loadUrl(request.getJavaScript());
            }else{
                queue.add(request);
            }
        }

        public void requestCallback(int requestId, String jsonResponse)
        {
            Log.d("[dl-api]", "hey callback");
            DLApiRequest r = requests.get(requestId);
            JSONObject response = null;
            try{
                response = new JSONObject(jsonResponse);

            }catch (Exception e){
            }
            r.responseHandler.onComplete(response);
        }

        public void runQueue()
        {
            if(queue.isEmpty()){
                return;
            }

            for(int i = 0; i<queue.size(); i++){
                DLApiRequest r = queue.get(i);
                executeRequest(r);
            }

            queue.clear();
        }
    }
}


