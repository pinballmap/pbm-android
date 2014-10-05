package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.location.Location;

@SuppressLint("HandlerLeak")
public class CloseLocations extends PBMUtil implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private ListView table;
	private android.location.Location yourLocation;
	
	private LocationClient locationClient;
	private List<com.pbm.Location> locationsForMap;
	private static final int maxMilesFromYourLocation = 10;
	private static final int maxNumMachinesToDisplayOnMap = 25;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.close_locations);

		setTitle("Close locations");

		Tracker tracker = ((PBMApplication) getApplication()).getTracker();
        tracker.setScreenName("com.pbm.CloseLocations");
        tracker.send(new HitBuilders.AppViewBuilder().build());

		table = (ListView)findViewById(R.id.closeLocationsTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);
		table.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent();						
				com.pbm.Location location = locationsForMap.get(position);
				
				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
			}
		});
		
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient = new LocationClient(this, this, this);
        } else {
			Toast.makeText(getBaseContext(), "I couldn't get a fix on your position. Try again, please.", Toast.LENGTH_LONG).show();
        }
	}   
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	private List<com.pbm.Location> sortLocations(List<com.pbm.Location> locations) {
		Collections.sort(locations, new Comparator<com.pbm.Location>() {
			public int compare(com.pbm.Location l1, com.pbm.Location l2) {
				Float distanceFromYou1 = Float.valueOf(l1.distanceFromYou);
				Float distanceFromYou2 = Float.valueOf(l2.distanceFromYou);

				return distanceFromYou1.compareTo(distanceFromYou2);
			}
		});

		return locations;
	}
	
	public void showTable(List<com.pbm.Location> locations) {
		if (locations.size() > 0) {
			table.setAdapter(new LocationListAdapter(this, locations));
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

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_button :
				if ((locationsForMap == null) || (locationsForMap.size() == 0)) {
					return true;
				}
			
				int numLocationsToDisplay = locationsForMap.size();
				if (numLocationsToDisplay > maxNumMachinesToDisplayOnMap) {
					numLocationsToDisplay = maxNumMachinesToDisplayOnMap - 1;
				}
			
				Intent myIntent = new Intent();
				myIntent.putExtra("Locations", sortLocations(locationsForMap).subList(0, numLocationsToDisplay).toArray());
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
			
				return true;
	    	default :
	    	    return super.onOptionsItemSelected(item);
		}
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.mapButton :
			if ((locationsForMap == null) || (locationsForMap.size() == 0)) {
				return;
			}

			int numLocationsToDisplay = locationsForMap.size();
			if (numLocationsToDisplay > maxNumMachinesToDisplayOnMap) {
				numLocationsToDisplay = maxNumMachinesToDisplayOnMap - 1;
			}

			Intent myIntent = new Intent();
			myIntent.putExtra("Locations", sortLocations(locationsForMap).subList(0, numLocationsToDisplay).toArray());
			myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
			startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);

			break;
		}
	}

	public void onConnected(Bundle arg0) {
		yourLocation = locationClient.getLastLocation();
		locationsForMap = new ArrayList<com.pbm.Location>();

		PBMApplication app = (PBMApplication) getApplication();
		for (int i = 0; i < app.getLocationValues().length; i++) {
			com.pbm.Location location = (com.pbm.Location) app.getLocationValues()[i];

			float distance = yourLocation.distanceTo(location.toAndroidLocation()); 
			distance = (float) (distance * PBMUtil.METERS_TO_MILES);	

			if (distance < maxMilesFromYourLocation) {
				location.setDistance(distance);
				locationsForMap.add(location);
			}
		}

		showTable(sortLocations(locationsForMap));
	}

	public void onDisconnected() { }
	public void onLocationChanged(Location arg0) { }
	public void onConnectionFailed(ConnectionResult arg0) { }
	public void onProviderDisabled(String arg0) {}
	public void onProviderEnabled(String arg0) {}
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}	
}