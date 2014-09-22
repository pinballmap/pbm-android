package com.pbm;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

public class PBMUtil extends Activity {	
	public static final int MENU_RESULT     = 8;
	public static final int QUIT_RESULT     = 42;
	public static final int RESET_RESULT    = 23;
	public static final int REFRESH_RESULT  = 30;
	public static final int CONDITION_DATE  = 0;
	public static final int CONDITION       = 1;
	public static final int PROGRESS_DIALOG = 0;
	public static final int MENU_PREFS      = 0;
	public static final int MENU_ABOUT      = 1;
	public static final int MENU_QUIT       = 2;
	public static final int HTTP_RETRIES    = 5;
	public static final String PREFS_NAME = "pbmPrefs";

//	public static String httpBase = "http://pinballmap.com/";
	public static String httpBase = "http://pinballmapstaging.herokuapp.com/";
	public static String apiPath = "api/v1/";

	public static String regionBase = "THIS IS SET DURING APP INIT";
	public static String regionlessBase = httpBase + apiPath;

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_PREFS, MENU_PREFS, MENU_PREFS, "Preferences");
		menu.add(MENU_ABOUT, MENU_ABOUT, MENU_ABOUT, "About");
		menu.add(MENU_QUIT, MENU_QUIT, MENU_QUIT, "Quit");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PREFS:
			Intent myIntent = new Intent();
			myIntent.setClassName("com.pbm", "com.pbm.Preferences");
			startActivityForResult(myIntent, QUIT_RESULT);

			return true;
		case MENU_ABOUT:
			Intent aboutIntent = new Intent();
			aboutIntent.setClassName("com.pbm", "com.pbm.About");
			startActivityForResult(aboutIntent, QUIT_RESULT);

			return true;
		case MENU_QUIT:
			setResult(QUIT_RESULT);
			super.finish();
			this.finish();  

			return true;
		}
		return false;
	}

	public void activityQuitResult() {
		setResult(QUIT_RESULT);
		super.finish();
		this.finish();
	}

	public void activityResetResult() {
		setResult(RESET_RESULT);
		super.finish();
		this.finish();
	}

	public void activityRefreshResult() {}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode) {
		case QUIT_RESULT:
			activityQuitResult();
			break;
		case RESET_RESULT:
			activityResetResult();
			break;
		case REFRESH_RESULT:
			activityRefreshResult();
		default:
			break;
		}
	}

	public static void setRegionBase(String newBase) {
		regionBase = newBase;
	}
	
	public void logAnalyticsHit(String page) {
		Tracker tracker = ((PBMApplication) getApplication()).getTracker();
        tracker.setScreenName(page);
        tracker.send(new HitBuilders.AppViewBuilder().build());
	}

	public static InputStream openHttpConnection(String urlString, String requestType) throws IOException {
		URL url = new URL(urlString); 
		try{
			for (int attempt = 0; attempt < HTTP_RETRIES; attempt++) {
				URLConnection urlConnection = url.openConnection();

				if (!(urlConnection instanceof HttpURLConnection))                     
					throw new IOException("Not an HTTP connection");

				HttpURLConnection httpConn = (HttpURLConnection) urlConnection;
				httpConn.setAllowUserInteraction(false);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod(requestType);

				httpConn.connect(); 
				InputStream inputStream = httpConn.getInputStream();                                 

				if ((httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) && (inputStream != null)) {
					return inputStream;                                 
				}

				httpConn.disconnect();
			}
		} catch (Exception ex) {
			return null;
		}
		return null;     
	}

	public static int convertPixelsToDip(int dipValue, DisplayMetrics displayMetrics) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dipValue, displayMetrics);
	}

	public static boolean haveInternet(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo == null) {
			return false;
		}
		
		return networkInfo.isConnected();
	}

	public void closeWithNoInternet() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Get some Internet, dude")
		.setMessage("This application requires an Internet connection, sorry.")
		.setPositiveButton("Bummer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activityQuitResult();		            
			}
		}).show();

		return;
	}
}