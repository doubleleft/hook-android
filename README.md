dl-api-android
===

dl-api android client

#About
This is a temporary solution to use dl-api on Android. The current version uses the [JavaScript client](https://github.com/doubleleft/dl-api-javascript) + Android native Webview to communicate with dl-api. The client does not make HTTP requests directly to the REST API.

#How to Use

You need to host and create your own JavaScript bridge for your project.
Example:

`bridge.html`
```javascript
<script type="text/javascript" src="http://dl-api.ddll.co/dist/dl.min.js"></script>
<script type="text/javascript">
	window.client = new DL.Client(apiConfig);
	window.api = {};

	window.api.myAwesomeMethod = function(){
		return client.collection('randomCollection').get();
	}

	window.api.anotherMethod = function(params){
		return client.collection('randomCollection').create(params);
	}

	//This is the function to bridge JavaScript <> Java calls	
	function dlApiCall(method, requestId, paramsJSON){
		var obj = window.api[method];
		var native = window["native"];
		var params = paramsJSON == null ? null : JSON.parse(paramsJSON); 
		var request = obj(params).then(function(response){
			native.requestCallback(requestId, JSON.stringify({response:response}));	
		});
	}	
</script>
```

`Class.java`
```java
DLApi api = new DLApi(this, "http://path/to/js/bridge.html");
api.call("myAwesomeMethod", new DLApiResponseHandler(){
	@Override
	public void onComplete(JSONObject response)
	{
		if(response != null){
			Log.d("[dl-api]", response.toString());
			JSONArray myList = response.getJSONArray("response");
		}else{
			Log.d("[dl-api]", "null response");
	   }
	}
});
```

