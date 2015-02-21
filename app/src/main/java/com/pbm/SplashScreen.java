package com.pbm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SplashScreen extends PBMUtil implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private GoogleApiClient locationClient;
	private LocationManager locationManager;
	private LocationRequest locationRequest;
	private android.location.Location yourLocation;
	private	ActionBar.Tab regionsByNameTab, regionsByLocationTab;
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_tab_container);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


		PreferenceManager.setDefaultValues(this, PBMUtil.PREFS_NAME, 0, R.xml.preferences, false);
		final SharedPreferences settings = getSharedPreferences(PBMUtil.PREFS_NAME, 0);
		Integer prefRegion = settings.getInt("region", -1);
		PBMApplication app = (PBMApplication) getApplication();

		try {
			if (!haveInternet(getBaseContext()) || !app.initializeRegions()) {
				closeWithNoInternet();
				return;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		updateLocation(settings);

		if (prefRegion != -1) {
			Region region = app.getRegion(prefRegion);
			setRegionBase(httpBase + apiPath + "region/" + region.name + "/");						

			Intent myIntent = new Intent();	
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			myIntent.setClassName("com.pbm", "com.pbm.InitializingScreen");
			startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);

		} else {
			final ArrayList<Region> regionValues = app.getRegionValues();
			Button alpha = (Button) findViewById(R.id.region_alpha);
			alpha.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					RegionsTab regionsTab = (RegionsTab) getSupportFragmentManager().findFragmentById(R.id.region_fragment);
					Bundle b = new Bundle();
					b.putSerializable("regions", regionValues);
					b.putBoolean("sortByDistance", false);
					getSupportLoaderManager().restartLoader(0, b, regionsTab);
				}
			});

			Button distance = (Button) findViewById(R.id.region_distance);
			distance.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					updateLocation(settings);
					RegionsTab regionsTab = (RegionsTab) getSupportFragmentManager().findFragmentById(R.id.region_fragment);
					Bundle b = new Bundle();
					b.putSerializable("regions", regionValues);
					b.putBoolean("sortByDistance", true);
					getSupportLoaderManager().restartLoader(0, b, regionsTab);
				}
			});


		}
	}

	private void updateLocation(SharedPreferences settings) {
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		String locationProvider = locationManager.getBestProvider(criteria, true);
		if (locationProvider != null) {
			Location location = locationManager.getLastKnownLocation(locationProvider);
			if (location != null) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putFloat("yourLat", (float) location.getLatitude());
				editor.putFloat("yourLon", (float) location.getLongitude());
				editor.commit();
			}
		}
	}

	public void onStart() {
		super.onStart();
		
//		locationClient.connect();
	}

	public void onStop() {
//		locationClient.disconnect();
		super.onStop();
	}


	public void onConnected(Bundle arg0) {

		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(5000);
		LocationServices.FusedLocationApi.requestLocationUpdates(locationClient,
				locationRequest, this);
	}

	public void onLocationChanged(Location yourLocation) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("yourLat", (float) yourLocation.getLatitude());
		editor.putFloat("yourLon", (float) yourLocation.getLongitude());
		editor.commit();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.i("PBM", "GoogleApiClient connection suspended");
	}

	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(getBaseContext(), "I couldn't get a fix on your position. Try again, please.",
				Toast.LENGTH_LONG).show();
	}
}