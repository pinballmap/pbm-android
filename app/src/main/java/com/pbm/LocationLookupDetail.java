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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class LocationLookupDetail extends PinballMapActivity {
	private Zone zone;
	private String city;
	private LocationType locationType;
	private Operator operator;
	private ArrayList<Location> foundLocations = new ArrayList<>();
	private ListView locationLookupDetailTable;
	private Parcelable listState;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			listState = savedInstanceState.getParcelable("listState");
		}
		setContentView(R.layout.location_lookup_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			initializeExtras(extras);
		} else {
			setTitle("Locations");
		}
		
		logAnalyticsHit("com.pbm.LocationLookupDetail");

		locationLookupDetailTable = (ListView)findViewById(R.id.locationLookupDetailTable);
		locationLookupDetailTable.setFastScrollEnabled(true);
		locationLookupDetailTable.setTextFilterEnabled(true);
		locationLookupDetailTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent myIntent = new Intent();
			com.pbm.Location location = foundLocations.get(position);

			myIntent.putExtra("Location", location);
			myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
			startActivityForResult(myIntent, PinballMapActivity.QUIT_RESULT);
			}
		});

		setTable(locationLookupDetailTable);
	}

	public void initializeExtras(Bundle extras) {
		if (extras.containsKey("LocationType")) {
			locationType = (LocationType) extras.get("LocationType");
			setTitle(locationType.name);
		} else if (extras.containsKey("Operator")) {
			operator = (Operator) extras.get("Operator");
			setTitle(operator.name);
		} else if (extras.containsKey("Zone")) {
			zone = (Zone) extras.get("Zone");
			setTitle(zone.name);
		} else if (extras.containsKey("City")) {
			city = (String) extras.get("City");
			setTitle(city);
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);

	    return super.onCreateOptionsMenu(menu);
	}

	void loadLocationData() {
		foundLocations.clear();

		PBMApplication app = getPBMApplication();
		HashMap<Integer, com.pbm.Location> locations = app.getLocations();
		if (this.getLocation() != null) {
			Log.d("com.pbm.location", "adjusting distance to locations");
		}
		for (Location location : locations.values()) {
			if (this.getLocation() != null) {
				location.setDistance(this.getLocation());
			}

			if (locationType == null && zone == null && operator == null && city == null) {
				foundLocations.add(location);
				continue;
			}

			if (city != null && location.city.equals(city)) {
				foundLocations.add(location);
				continue;
			}

			if (locationType != null && location.locationTypeID == locationType.id) {
				foundLocations.add(location);
				continue;
			}

			if (operator != null && location.operatorID == operator.id) {
				foundLocations.add(location);
				continue;
			}

			if (zone != null && (zone.id == 0 || location.zoneID == zone.id)) {
				foundLocations.add(location);
			}
		}

		Collections.sort(foundLocations, new Comparator<Location>() {
			public int compare(Location l1, Location l2) {
				return l1.name.compareTo(l2.name);
			}
		});
        locationLookupDetailTable.setAdapter(new LocationListAdapter(this, foundLocations));
        
		if (listState != null) {
			locationLookupDetailTable.onRestoreInstanceState(listState);
		}
	}

	public void onResume() {
		super.onResume();
		loadLocationData();
		Log.d("com.pbm", "done");
	}

	@Override
	public void processLocation() {
		super.processLocation();
		loadLocationData();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		listState = locationLookupDetailTable.onSaveInstanceState();
		outState.putParcelable("listState", listState);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_button :
				Intent myIntent = new Intent();
				myIntent.putExtra("Locations", foundLocations.toArray());
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, QUIT_RESULT);

				return true;
	    	default:
	    	    return super.onOptionsItemSelected(item);
		}
	}
}