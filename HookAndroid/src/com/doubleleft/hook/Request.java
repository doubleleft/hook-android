package com.doubleleft.hook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

public class Request extends AsyncTask<String, String, String> {
	public String method = "GET";
	public JSONObject data;
	protected Responder responder;
	protected HashMap<String, String> headers;

	public Request() {
		headers = new HashMap<String, String>();
	}

	public void setResponder(Responder responder) {
		this.responder = responder;
	}

	public void addHeader(String name, String value) {
		headers.put(name, value);
	}

	@Override
	protected String doInBackground(String... uri) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		String errorString = null;
		int statusCode = -1;
		String url = uri[0];

		try {
			HttpUriRequest request;

			if (method == "POST") {
				HttpPost post = new HttpPost(url);
				if (data != null) {
					post.setEntity(new StringEntity(data.toString()));
				}
				request = post;

			} else if (method == "PUT") {
				HttpPut put = new HttpPut(url);
				if (data != null) {
					put.setEntity(new StringEntity(data.toString()));
				}
				request = put;

			} else if (method == "DELETE") {
				HttpDelete delete = new HttpDelete(url);
				request = delete;
			} else {
				String queryString = "";
				if (data != null) {
					queryString = "?"
							+ URLEncoder.encode(data.toString(), "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'")
									.replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
				}
				HttpGet get = new HttpGet(url + queryString);
				request = get;
			}

			for (String key : headers.keySet()) {
				request.setHeader(key, headers.get(key));
			}

			response = httpclient.execute(request);

			StatusLine statusLine = response.getStatusLine();
			statusCode = statusLine.getStatusCode();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			out.close();
			responseString = out.toString();

		} catch (ClientProtocolException e) {
			errorString = e.getMessage();

		} catch (IOException e) {
			errorString = e.getMessage();
		}

		Response responseObj = new Response();
		responseObj.raw = errorString != null ? errorString : responseString;
		responseObj.code = statusCode;

		if (responseString != null) {
			String jsonString = responseString.trim();
			if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
				try {
					Object result = new JSONTokener(jsonString).nextValue();

					if (result instanceof JSONObject) {
						responseObj.object = (JSONObject) result;

					} else if (result instanceof JSONArray) {
						responseObj.array = (JSONArray) result;
					}

				} catch (JSONException exception) {
					Log.d("dl-api", "Invalid JSON response: " + exception.toString());
				}
			}
		}

		if (responder != null) {
			if (statusCode == HttpStatus.SC_OK) {
				responder.onSuccess(responseObj);
			} else {
				responder.onError(responseObj);
			}
		}

		return responseString;
	}
}