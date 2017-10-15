package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CloseLocations extends PinballMapActivity {
	private ListView closeLocationsTable;

	private List<com.pbm.Location> locationsForMap = new ArrayList<>();
	private static final int maxMilesFromYourLocation = 20;
	private static final int maxNumMachinesToDisplayOnMap = 100;
	private Parcelable listState;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			listState = savedInstanceState.getParcelable("listState");
		}

		setContentView(R.layout.close_locations);
		setTitle("Close locations");

		Tracker tracker = getPBMApplication().getTracker();
		tracker.setScreenName("com.pbm.CloseLocations");
		//noinspection deprecation
		tracker.send(new HitBuilders.AppViewBuilder().build());

		closeLocationsTable = (ListView) findViewById(R.id.closeLocationsTable);
		closeLocationsTable.setEmptyView(findViewById(R.id.closeLocationsEmpty));
		closeLocationsTable.setFastScrollEnabled(true);
		closeLocationsTable.setTextFilterEnabled(true);
		closeLocationsTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent myIntent = new Intent();
			com.pbm.Location location = locationsForMap.get(position);

			myIntent.putExtra("Location", location);
			myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
			startActivityForResult(myIntent, PinballMapActivity.QUIT_RESULT);
			}
		});

		setTable(closeLocationsTable);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		listState = closeLocationsTable.onSaveInstanceState();
		outState.putParcelable("listState", listState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		processLocation();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_button:
				if ((locationsForMap == null) || (locationsForMap.size() == 0)) {
					return true;
				}
				int numLocationsToDisplay = locationsForMap.size();
				if (numLocationsToDisplay > maxNumMachinesToDisplayOnMap) {
					numLocationsToDisplay = maxNumMachinesToDisplayOnMap - 1;
				}
				Intent myIntent = new Intent();
				Collections.sort(locationsForMap, com.pbm.Location.byNearestDistance);
				myIntent.putExtra("Locations", locationsForMap.subList(0, numLocationsToDisplay).toArray());
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, PinballMapActivity.QUIT_RESULT);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void processLocation() {
		super.processLocation();
		Log.d("com.pbm.location", "CL processLocation");
		locationsForMap.clear();
		PBMApplication app = getPBMApplication();
		ArrayList<com.pbm.Location> locations = app.getLocationValues();
		for (com.pbm.Location pbmLocation : locations) {
			if (getLocation() != null) {
				pbmLocation.setDistance(getLocation());
			}
			if (pbmLocation.getDistanceFromYou() < maxMilesFromYourLocation) {
				locationsForMap.add(pbmLocation);
			}
		}
		LocationListAdapter adapter = new LocationListAdapter(this, locationsForMap);
		adapter.sort(com.pbm.Location.byNearestDistance);
		closeLocationsTable.setAdapter(adapter);
		if (listState != null) {
			closeLocationsTable.onRestoreInstanceState(listState);
		}
	}
}
