package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class InitializingScreen extends PinballMapActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			getPBMApplication().initializeData();
		} catch (IOException | InterruptedException | ExecutionException | JSONException | ParseException e) {
			e.printStackTrace();
		}

		Intent myIntent = new Intent();
		myIntent.setClassName("com.pbm", "com.pbm.PBMMenu");
		startActivityForResult(myIntent, QUIT_RESULT);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}