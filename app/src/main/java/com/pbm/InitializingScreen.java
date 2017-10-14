package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class InitializingScreen extends PinballMapActivity {

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initializing_screen);

		setSupportProgressBarIndeterminateVisibility(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		SplashThread splashThread = new SplashThread();
		splashThread.start();
	}

	private class SplashThread extends Thread {
		public void run() {
			try {
				getPBMApplication().initializeData();
			} catch (UnsupportedEncodingException | InterruptedException | ExecutionException | ParseException | JSONException e) {
				e.printStackTrace();
			} finally {
				Intent myIntent = new Intent();
				myIntent.setClassName("com.pbm", "com.pbm.PBMMenu");
				startActivity(myIntent);

				finish();
				interrupt();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}