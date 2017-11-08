package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

import org.json.JSONException;

import java.io.IOException;
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

		BackgroundInitialize backgroundInitialize = new BackgroundInitialize();
		backgroundInitialize.start();

		SplashThread splashThread = new SplashThread();
		splashThread.start();
	}

	private class BackgroundInitialize extends Thread {
		public void run() {
			try {
				Log.d("com.pbm", "TIMING STARTING MACHINES");
				getPBMApplication().initializeAllMachines();
				Log.d("com.pbm", "TIMING STARTING TAG REGION MACHINES");
				getPBMApplication().initializeRegionMachines();
				Log.d("com.pbm", "TIMING STARTING LOCATIONS");
				getPBMApplication().initializeLocations();

				finish();
				interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private class SplashThread extends Thread {
		public void run() {
			try {
				getPBMApplication().initializeData();
			} catch (UnsupportedEncodingException | InterruptedException | ExecutionException | ParseException | JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Intent myIntent = new Intent();
				myIntent.setClassName("com.pbm", "com.pbm.PBMMenu");
				startActivityForResult(myIntent, QUIT_RESULT);

				finish();
				interrupt();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}