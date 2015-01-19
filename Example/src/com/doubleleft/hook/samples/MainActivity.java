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
import com.doubleleft.hook.samples.model.Person;

public class MainActivity extends Activity {

	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);
		this.context = this;
		
		// Setup Hook
		Client.setup(this);

		// Button action
		Button requestButton = (Button) this.findViewById(R.id.request_button);
		requestButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) context).createNewPerson();
			}
		});
	}

	private void createNewPerson() {

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
