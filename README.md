hook-android
===

Java/Android client for [hook](https://github.com/doubleleft/hook).

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
String appId = "1";
String appKey = "q1uU7tFtXnLad6FIGGn2cB+gxcx64/uPoDhqe2Zn5AE=";
String endpointURL = "http://hook.ddll.co";
Context context = this;
Client client = new Client(context, endpointURL, appKey, appId);
```

### Create Collection Item
```java
JSONObject data = new JSONObject();
data.put("name", "My Book Name");
data.put("edition", 1.0);

client.collection("books").create(data, new Responder() {
	@Override
	public void onSuccess(Response response) {
		Log.d("hook", response.raw);
		/*
			Response object:
				public JSONObject object;
				public JSONArray array;
				public String raw;
				public int code;
		*/
	}

	@Override
	public void onError(Response response) {
		Log.d("hook", "onError: "+response.raw);
	}
});
```

### Fetching (and filtering) items
```java
client.collection("books").where("edition", 1).get(new Responder() {
	@Override
	public void onSuccess(Response response) {
		Log.d("hook", response.object.optString("name"));
	}

	@Override
	public void onError(Response response) {
		Log.d("hook", "onError: "+response.raw);
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
- See [Collection.java](https://github.com/doubleleft/hook-android/blob/master/lib/dlapi/src/main/java/com/doubleleft/api/Collection.java) for better reference

### Authentication: create user

```java
JSONObject data = new JSONObject();
data.put("email", "gabriel@doubleleft.com");
data.put("name", "Gabriel Laet");
data.put("password", "123");
client.auth.register(data, responder);
```

Once the user is created, you don't need to verify/login again.
The library also takes care to store the current user internally.

### Authentication: login user

```java
JSONObject data = new JSONObject();
data.put("email", "gabriel@doubleleft.com");
data.put("password", "123");
client.auth.login(data, responder);
```

### Authentication: other methods
- `forgotPassword`
- `resetPassword`
- `logout`
- `getAuthToken`
- `hasAuthToken`

- See [Auth.java](https://github.com/doubleleft/hook-android/blob/master/lib/dlapi/src/main/java/com/doubleleft/api/Auth.java) for better reference
