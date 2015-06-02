hook-android [![Download](https://api.bintray.com/packages/endel/maven/hook-android/images/download.svg)  ](https://bintray.com/endel/maven/hook-android/_latestVersion)
===

Android client for [hook](https://github.com/doubleleft/hook).

TODO
---

- Use [android-async-http](https://github.com/loopj/android-async-http/) on
  Request class.
- Use SQLite to store Auth data.

#About

This is a port of the [JavaScript client](http://github.com/doubleleft/hook-javascript). The library is a native Android Library, with no external dependencies, targetting Android API Level 10. It doesn't require the JavaScript client either, the library communicates with hook's REST interface.

#How to Use

### Setup
```java
Context context = this;
String appId = "1";
String appKey = "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d";
String endpoint = "http://localhost:4665/";
Client client = new Client(context, endpoint, appKey, appId);
```

### Create Collection Item
```java
import com.loopj.android.http.*;

...

RequestParams data = new RequestParams();
data.put("name", "My Book Name");
data.put("edition", 1.0);

client.collection("books").create(data, new JsonHttpResponseHandler() {
	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		Log.d("success: ", response.toString());
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
		Log.d("failure: ", errorResponse.toString());
	}
});
```

### Fetching (and filtering) items
```java
client.collection("books").where("edition", 1).get(new JsonHttpResponseHandler() {
	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		Log.d("success: ", response.toString());
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
		Log.d("failure: ", errorResponse.toString());
	}
});
```

### Other available methods
- `sort`
- `where`
- `limit`
- `offset`
- `increment`
- `decrement`
- `avg`
- `max`
- `min`
- `sum`

### Authentication: create user

```java
RequestParams data = new RequestParams();
data.put("email", "gabriel@doubleleft.com");
data.put("name", "Gabriel Laet");
data.put("password", "123");
client.auth.register(data, new JsonHttpResponseHandler() {...});
```

Once the user is created, you don't need to verify/login again.
The library also takes care to store the current user internally.

### Authentication: login user

```java
RequestParams data = new RequestParams();
data.put("email", "gabriel@doubleleft.com");
data.put("password", "123");
client.auth.login(data, new JsonHttpResponseHandler() {...});
```

### Authentication: other methods
- `forgotPassword`
- `resetPassword`
- `logout`
- `getAuthToken`
- `hasAuthToken`

License
---

MIT
