package com.pbm;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CloseLocations extends PBMUtil implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private ListView table;

	private GoogleApiClient googleApiClient;
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

		super.onCreate(savedInstanceState, table);
		
		googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
				.addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	private List<com.pbm.Location> sortLocations(List<com.pbm.Location> locations) {
		Collections.sort(locations, new Comparator<com.pbm.Location>() {
			public int compare(com.pbm.Location l1, com.pbm.Location l2) {
				Float distanceFromYou1 = l1.distanceFromYou;
				Float distanceFromYou2 = l2.distanceFromYou;

				return distanceFromYou1.compareTo(distanceFromYou2);
			}
		});

		return locations;
	}

	void showTable(List<com.pbm.Location> locations) {
		if (locations.size() > 0) {
			table.setAdapter(new LocationListAdapter(this, locations));
		}
	}

	protected void onStart() {
		super.onStart();
		googleApiClient.connect();
	}

	protected void onStop() {
		googleApiClient.disconnect();
		super.onStop();
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

	public void onConnected(Bundle arg0) {
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(5000);
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
				locationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.i("PBM","GoogleApiClient connection suspended");
	}

	public void onLocationChanged(Location yourLocation) {
		locationsForMap = new ArrayList<com.pbm.Location>();
		PBMApplication app = (PBMApplication) getApplication();
		ArrayList<com.pbm.Location> locations = app.getLocationValues();
		for (com.pbm.Location location: locations) {
			float distance = yourLocation.distanceTo(location.toAndroidLocation()) * PBMUtil.METERS_TO_MILES;

			if (distance < maxMilesFromYourLocation) {
				location.setDistance(distance);
				locationsForMap.add(location);
			}
		}
		showTable(sortLocations(locationsForMap));
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(getBaseContext(), "I couldn't get a fix on your position. Try again, please.", Toast.LENGTH_LONG).show();
	}
}