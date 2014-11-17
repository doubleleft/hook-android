package com.doubleleft.hook.samples;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.doubleleft.hook.Client;
import com.doubleleft.hook.Responder;
import com.doubleleft.hook.Response;
import com.doubleleft.hook.exceptions.ClientNotSetupException;
import com.doubleleft.hook.samples.model.Person;

public class MainActivity extends Activity {

	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);

		this.context = this;

		// Setup hook
		Client.appId = context.getString(R.string.hook_appId);
		Client.appKey = context.getString(R.string.hook_appKey);
		Client.url = context.getString(R.string.hook_endpointUrl);
		
		Client.context = this;

		Button requestButton = (Button) this.findViewById(R.id.request_button);
		requestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					((MainActivity) context).createNewPerson();
				} catch (ClientNotSetupException e) {
					Log.e("hook", e.getMessage());
				}
			}
		});
	}

	private void createNewPerson() throws ClientNotSetupException {

		Person person = new Person();
		person.name = "Tulio";
		person.age = 26;
		person.create(new Responder() {

			@Override
			public void onSuccess(Response response) {
				Log.d("hook", response.raw);
			}

			@Override
			public void onError(Response response) {
				Log.d("hook", "Error creating person");
			}

		});
	}
}
