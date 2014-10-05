package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class LocationLookupDetail extends PBMUtil {
	private Zone zone;
	private ArrayList<Location> foundLocations = new ArrayList<Location>();
	private ListView table;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_lookup_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		zone = (Zone) extras.get("Zone");
		
		logAnalyticsHit("com.pbm.LocationLookupDetail");

		setTitle(zone.name);

		table = (ListView)findViewById(R.id.locationLookupDetailTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);
		table.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent();						
				com.pbm.Location location = foundLocations.get(position);
				
				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
			}
		});
	}   
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	public void loadLocationData() {
		foundLocations.clear();
		table.setAdapter(null);

		PBMApplication app = (PBMApplication) getApplication();
		HashMap<Integer, com.pbm.Location> locations = app.getLocations();
		for(Object key : locations.keySet()) {
			Location location = locations.get(key);

			if (zone.id == 0 || location.zoneID == zone.id) {
				foundLocations.add(location);
			}
		}

		Collections.sort(foundLocations, new Comparator<Location>() {
			public int compare(Location l1, Location l2) {
				return l1.name.toString().compareTo(l2.name.toString());
			}
		});

		table.setAdapter(new LocationListAdapter(this, foundLocations));
	}

	public void onResume() {
		super.onResume();
		loadLocationData();
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