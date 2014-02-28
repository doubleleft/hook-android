package com.doubleleft.http;
import android.os.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class HttpRequest extends AsyncTask<String, String, String>
{
    HttpRequestDelegate delegate;
    Hashtable postData;

    public HttpRequest()
    {

    }

    public void setDelegate(HttpRequestDelegate delegate)
    {
        this.delegate = delegate;
    }

    public HttpRequestDelegate getDelegate()
    {
        return this.delegate;
    }

    public void setPostData(Hashtable postData)
    {
        this.postData = postData;
    }

    private void notifyError()
    {
        if(delegate != null){
            delegate.onError();
        }
    }

    @Override
    protected String doInBackground(String... uri)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try{
            if(postData == null){
                //build HTTP GET request
                response = httpclient.execute(new HttpGet(uri[0]));

            }else{
                HttpPost httpPost = new HttpPost(uri[0]);

                int numberOfFields = postData.size();
                List<NameValuePair> postPairs = new ArrayList<NameValuePair>(numberOfFields);

                Enumeration keys = postData.keys();
                while(keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = postData.get(key);
                    postPairs.add(new BasicNameValuePair((String)key, (String)value));
                }

                httpPost.setEntity(new UrlEncodedFormEntity(postPairs));
                response = httpclient.execute(httpPost);
            }

            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();

            }else{
                response.getEntity().getContent().close();
                //statusLine.getReasonPhrase()
                notifyError();
            }

        }catch (ClientProtocolException e) {
            notifyError();

        }catch (IOException e) {
            notifyError();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        if(delegate != null){
            delegate.onComplete(result);
        }

    }
}