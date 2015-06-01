package com.doubleleft.hook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//import java.net.URLEncoder;
import java.util.HashMap;

import android.util.Log;

public class Request {
	public String method = "GET";
	public RequestParams data;

	protected Responder responder;
	protected HashMap<String, String> headers;

	private static AsyncHttpClient client = new AsyncHttpClient();

	public Request() {
		headers = new HashMap<String, String>();
	}

	public Request setResponder(Responder responder) {
		this.responder = responder;
		return this;
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

					if (result instanceof RequestParams) {
						responseObj.object = (RequestParams) result;

					} else if (result instanceof JSONArray) {
						responseObj.array = (JSONArray) result;
					}

				} catch (JSONException exception) {
					Log.d("hook", "Invalid JSON response: " + exception.toString());
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
