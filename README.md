hook-android [![Download](https://api.bintray.com/packages/doubleleft/hook-android/hook-android/images/download.svg)  ](https://bintray.com/doubleleft/hook-android/hook-android/_latestVersion)
===

Android client for [hook](https://github.com/doubleleft/hook).

#How to Use

## Configure as a depedency

In your application `build.gradle` file, add the following lines:

```gradle
repositories {
    maven {
        url  "http://dl.bintray.com/doubleleft/hook-android"
    }
}

dependencies {
    // ...
    compile 'com.doubleleft.hook:hook-android-client:0.2.0'
}
```

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
JSONObject data = new JSONObject();
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
JSONObject data = new JSONObject();
data.put("email", "gabriel@doubleleft.com");
data.put("name", "Gabriel Laet");
data.put("password", "123");
client.auth.register(data, new JsonHttpResponseHandler() {...});
```

Once the user is created, you don't need to verify/login again.
The library also takes care to store the current user internally.

### Authentication: login user

```java
JSONObject data = new JSONObject();
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

Contributing
---

**Creating a new release version**

```
cd Hook
gradle generateRelease
```

Log-in on
[bintray](https://bintray.com/doubleleft/hook-android/hook-android/new/version),
create a new version.  Click on "Upload Files", from the recently version
created, and attach the `.zip` file genereated via `gradle generateRelease`
task. Make sure to select "Explode this archive" from the "Attached Files" list.

License
---

MIT
