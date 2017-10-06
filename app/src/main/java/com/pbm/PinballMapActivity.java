package com.pbm;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class PinballMapActivity extends AppCompatActivity implements OnQueryTextListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	static final int MENU_RESULT = 8;
	public static final int QUIT_RESULT = 42;
	static final int RESET_RESULT = 23;
	static final int REFRESH_RESULT = 30;
	public static final int HTTP_RETRIES = 5;
	public static final String PREFS_NAME = "pbmPrefs";
	public static final float METERS_TO_MILES = (float) 0.000621371192;

	public static String httpBase = BuildConfig.SERVER_URL; // see build.gradle
	public static String apiPath = "api/v1/";
	public static String regionBase = "THIS IS SET DURING APP INIT";
	public static String regionlessBase = httpBase + apiPath;

	public ListView table;
	private GoogleApiClient googleApiClient;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("regionBase", regionBase);
		outState.putLong("datatimestamp", getPBMApplication().getDataLoadTimestamp());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		googleApiClient = new GoogleApiClient.Builder(getPBMActivity()).addApi(LocationServices.API)
				.addConnectionCallbacks(getPBMActivity()).addOnConnectionFailedListener(getPBMActivity()).build();
		if (savedInstanceState != null) {
			regionBase = savedInstanceState.getString("regionBase");
			PBMApplication pbm = getPBMApplication();
			pbm.setDataLoadTimestamp(savedInstanceState.getLong("datatimestamp"));
		}
	}

	public PBMApplication getPBMApplication() {
		return (PBMApplication) getApplication();
	}

	public PinballMapActivity getPBMActivity() {
		return this;
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

	private class ReloadData extends Thread {
		public void run() {
			try {
				final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
				String authToken = settings.getString("authToken", "");
				int region = settings.getInt("region", -1);

				if (!authToken.equals("") && region != -1) {
					getPBMApplication().initializeData();
				}
			} catch (UnsupportedEncodingException | ParseException | InterruptedException | JSONException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		googleApiClient.disconnect();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		googleApiClient.connect();
		if (System.currentTimeMillis() - getPBMApplication().getDataLoadTimestamp() >= BuildConfig.DATA_EXPIRY_TIME_IN_MS) {
			Log.d("com.pbm", "starting thread");
			ReloadData reloadDataThread = new ReloadData();
			reloadDataThread.run();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.prefs:
				Intent myIntent = new Intent();
				myIntent.setClassName("com.pbm", "com.pbm.Preferences");
				startActivityForResult(myIntent, QUIT_RESULT);

				return true;
			case R.id.profile:
				Intent profileIntent = new Intent();
				String profileClassName = getPBMApplication().userIsAuthenticated() ? "com.pbm.Profile" : "com.pbm.Login";
				profileIntent.setClassName("com.pbm", profileClassName);
				startActivityForResult(profileIntent, QUIT_RESULT);

				return true;
			case R.id.about:
				Intent aboutIntent = new Intent();
				aboutIntent.setClassName("com.pbm", "com.pbm.About");
				startActivityForResult(aboutIntent, QUIT_RESULT);

				return true;
			case R.id.donate:
				Intent donateIntent = new Intent();
				donateIntent.setClassName("com.pbm", "com.pbm.Donate");
				startActivityForResult(donateIntent, QUIT_RESULT);

				return true;
			case R.id.store:
				Intent storeIntent = new Intent();
				storeIntent.setClassName("com.pbm", "com.pbm.Store");
				startActivityForResult(storeIntent, QUIT_RESULT);

				return true;
			case R.id.contact_admin:
				Intent contactIntent = new Intent();
				String contactClassName = getPBMApplication().userIsAuthenticated() ? "com.pbm.ContactAdmin" : "com.pbm.Login";
				contactIntent.setClassName("com.pbm", contactClassName);
				startActivityForResult(contactIntent, QUIT_RESULT);

				return true;
			case R.id.suggest_region:
				Intent suggestIntent = new Intent();
				String suggestRegionClassName = getPBMApplication().userIsAuthenticated() ? "com.pbm.SuggestRegion" : "com.pbm.Login";
				suggestIntent.setClassName("com.pbm", suggestRegionClassName);
				startActivityForResult(suggestIntent, QUIT_RESULT);

				return true;
			case R.id.suggest_location:
				Intent suggestLocationIntent = new Intent();
				String suggestLocationClassName = getPBMApplication().userIsAuthenticated() ? "com.pbm.SuggestLocation" : "com.pbm.Login";
				suggestLocationIntent.setClassName("com.pbm", suggestLocationClassName);
				startActivityForResult(suggestLocationIntent, QUIT_RESULT);

				return true;
			case R.id.logout:
				SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
                editor.putInt("region", -1);
                editor.putString("authToken", "");
				editor.putString("username", "");
				editor.putString("email", "");
				editor.putString("id", "");
				editor.commit();

				Intent loginIntent = new Intent();
				loginIntent.setClassName("com.pbm", "com.pbm.Login");
				startActivityForResult(loginIntent, QUIT_RESULT);

				return true;
			case R.id.login:
				loginIntent = new Intent();
				loginIntent.setClassName("com.pbm", "com.pbm.Login");
				startActivityForResult(loginIntent, QUIT_RESULT);

				return true;
			case R.id.quit:
				setResult(QUIT_RESULT);
				android.os.Process.killProcess(android.os.Process.myPid());

				return true;
		}
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
		if (settings.getInt("region", -1) == -1) {
			menu.removeItem(R.id.contact_admin);
			menu.removeItem(R.id.suggest_location);
		}

		if (getPBMApplication().userIsAuthenticated()) {
			menu.removeItem(R.id.login);
		} else {
			menu.removeItem(R.id.logout);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	public void activityQuitResult() {
		setResult(QUIT_RESULT);

		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void activityResetResult() {
		setResult(RESET_RESULT);

		super.finish();
		this.finish();
	}

	public void activityRefreshResult() {}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
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

	public void setTable(ListView table) {
		table.setTextFilterEnabled(true);
		this.table = table;
	}

	public void logAnalyticsHit(String page) {
		Tracker tracker = getPBMApplication().getTracker();
		tracker.setScreenName(page);
		tracker.send(new HitBuilders.AppViewBuilder().build());
	}

	public static InputStream openHttpConnection(String urlString, String requestType) throws IOException {
		URL url = new URL(urlString);
		try {
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

				if (
					((httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) || (httpConn.getResponseCode() == HttpURLConnection.HTTP_CREATED))
					&& (inputStream != null)
				) {
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

	public void closeOnMissingServer() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Sorry")
			.setMessage("The Pinball Map Server is missing! Please try later, thank you.")
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

	public android.location.Location getLocation() {
		return getPBMApplication().getLocation();
	}

	private void setLocation(android.location.Location location) {
		getPBMApplication().setLocation(location);
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("com.pbm.location", "PBM onConnected");
		LocationRequest locationRequest = LocationRequest.create();
		if (Build.MODEL.equals("sdk") || Build.MODEL.contains("Genymotion") || Build.FINGERPRINT.contains("generic")) {
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationRequest.setInterval(60000);
		} else {
			locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			locationRequest.setInterval(60000 * 60);
			locationRequest.setFastestInterval(60000);
		}
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO refactor into something more useful
		} else {
			setLocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
			LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("com.pbm.location", "PBM onConnectionSuspended");
		Log.i("PBM", "GoogleApiClient connection suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(getBaseContext(), "I couldn't get a fix on your position. Try again, please.", Toast.LENGTH_LONG).show();
		Log.d("com.pbm.location", "PBM onConnectionFailed");
	}

	@Override
	public void onLocationChanged(android.location.Location location) {
		Log.d("com.pbm.location", "PBM onLocationChanged" + location);
		setLocation(location);
		processLocation();
	}

	public void processLocation() {
		Log.d("com.pbm.location", "PBM processLocation " + getLocation());
	}
}
