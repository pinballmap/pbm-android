package com.pbm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@SuppressLint("Registered")
public class PBMUtil extends ActionBarActivity implements OnQueryTextListener {
	static final int MENU_RESULT = 8;
	public static final int QUIT_RESULT     	  = 42;
	static final int RESET_RESULT = 23;
	static final int REFRESH_RESULT = 30;
	public static final int CONDITION_DATE  	  = 0;
	public static final int CONDITION       	  = 1;
	public static final int PROGRESS_DIALOG 	  = 0;
	public static final int MENU_SUGGEST_LOCATION = 0;
	public static final int MENU_CONTACT_ADMIN    = 1;
	public static final int MENU_PREFS      	  = 2;
	public static final int MENU_SUGGEST_REGION   = 3;
	public static final int MENU_ABOUT      	  = 4;
	public static final int MENU_QUIT       	  = 5;
	public static final int HTTP_RETRIES    	  = 5;
	public static final String PREFS_NAME = "pbmPrefs";
	public static final float METERS_TO_MILES = (float) 0.000621371192;

	public static String httpBase = BuildConfig.SERVER_URL;
//	"http://pinballmap.com/";
//  staging site testing
//	public static String httpBase = "http://pinballmapstaging.herokuapp.com/";

	public static String apiPath = "api/v1/";

	public static String regionBase = "THIS IS SET DURING APP INIT";
	public static String regionlessBase = httpBase + apiPath;
	
	public ListView table;

	public void onCreate(Bundle savedInstanceState, ListView table) {
		table.setTextFilterEnabled(true);
		this.table = table;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

        if (menu.findItem(R.id.search) != null) {
        	SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        	SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
 
        	searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        	searchView.setSubmitButtonEnabled(false);
        	searchView.setOnQueryTextListener(this);
        }

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.prefs:
			Intent myIntent = new Intent();
			myIntent.setClassName("com.pbm", "com.pbm.Preferences");
			startActivityForResult(myIntent, QUIT_RESULT);

			return true;
			case R.id.about:
			Intent aboutIntent = new Intent();
			aboutIntent.setClassName("com.pbm", "com.pbm.About");
			startActivityForResult(aboutIntent, QUIT_RESULT);

			return true;
			case R.id.contact_admin:
			Intent contactIntent = new Intent();
			contactIntent.setClassName("com.pbm", "com.pbm.ContactAdmin");
			startActivityForResult(contactIntent, QUIT_RESULT);

			return true;
			case R.id.suggest_region:
			Intent suggestIntent = new Intent();
			suggestIntent.setClassName("com.pbm", "com.pbm.SuggestRegion");
			startActivityForResult(suggestIntent, QUIT_RESULT);

			return true;
			case R.id.suggest_location:
			Intent suggestLocationIntent = new Intent();
			suggestLocationIntent.setClassName("com.pbm", "com.pbm.SuggestLocation");
			startActivityForResult(suggestLocationIntent, QUIT_RESULT);

			return true;
			case R.id.quit:
			setResult(QUIT_RESULT);
			super.finish();
			this.finish();  

			return true;
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences settings = getSharedPreferences(PBMUtil.PREFS_NAME, 0);
		if (settings.getInt("region", -1) == -1) {
			menu.removeItem(R.id.contact_admin);
			menu.removeItem(R.id.suggest_location);
		}
		return super.onPrepareOptionsMenu(menu);
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
				} else {
					Log.e("HTTP RESPONSE MESSAGE:", httpConn.getResponseMessage());
				}

				httpConn.disconnect();
			}
		} catch (Exception ex) {
			Log.e("EXCEPTION:", ex.toString());
		}
		return null;     
	}

	public static int convertPixelsToDip(int dipValue, DisplayMetrics displayMetrics) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dipValue, displayMetrics);
	}

	public static boolean haveInternet(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();

	}

	public void closeWithNoInternet() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Get some Internet, dude")
		.setMessage("This application requires an Internet connection, sorry.")
		.setPositiveButton("Bummer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activityQuitResult();		            
			}
		}).show();
	}
	
	public boolean onQueryTextChange(String newText) {
		if (TextUtils.isEmpty(newText)) {
			table.clearTextFilter();
	    } else {
			table.setFilterText(newText);
		}
	         
	    return true;
	 }
	 
	 public boolean onQueryTextSubmit(String query) {
		 return false;
	 }
}