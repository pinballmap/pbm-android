package com.pbm;

import java.io.UnsupportedEncodingException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

public class SplashScreen extends PBMUtil implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private LocationClient locationClient;
	private android.location.Location yourLocation;
	private	ActionBar.Tab regionsByNameTab, regionsByLocationTab;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.region_tab_container);

        android.app.ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
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

		if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient = new LocationClient(this, this, this);
        }

		if (prefRegion != -1) {
			Region region = app.getRegion(prefRegion);
			setRegionBase(httpBase + apiPath + "region/" + region.name + "/");						

			Intent myIntent = new Intent();	
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			myIntent.setClassName("com.pbm", "com.pbm.InitializingScreen");
			startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
		} else {
			Fragment regionsByName = new RegionsTab(app.getRegionValues(), false);
			Fragment regionsByLocation = new RegionsTab(app.getRegionValues(), true);
		
			regionsByNameTab = actionBar.newTab().setText("Sorted Alphabetically");
	    	regionsByLocationTab = actionBar.newTab().setText("Sorted By Distance");
	    
        	regionsByNameTab.setTabListener(new RegionTabListener(regionsByName));
        	regionsByLocationTab.setTabListener(new RegionTabListener(regionsByLocation));
	    
	    	actionBar.addTab(regionsByNameTab);
        	actionBar.addTab(regionsByLocationTab);
		}
	}
	
	public void onStart() {
		super.onStart();
		
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.connect();
        }
	}

	public void onStop() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onStop();
	}

	public void onPause() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onPause();
	}

	public void onDestroy() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onDestroy();
	}

	public void onConnected(Bundle arg0) {
		if (locationClient != null) {
			yourLocation = locationClient.getLastLocation();

			if (yourLocation != null) {
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putFloat("yourLat", (float) yourLocation.getLatitude());
				editor.putFloat("yourLon", (float) yourLocation.getLongitude());
				editor.commit();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void onConnectionFailed(ConnectionResult arg0) {}
	public void onDisconnected() {}
	public void onLocationChanged(Location arg0) {}
}