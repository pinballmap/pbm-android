package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationLookupDetail extends PBMUtil {
	private Zone zone;
	private ArrayList<Location> foundLocations = new ArrayList<Location>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.location_lookup_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.map_titlebar);

		Bundle extras = getIntent().getExtras();
		zone = (Zone) extras.get("Zone");

		TextView title = (TextView)findViewById(R.id.title);
		title.setText(zone.name);

		ListView table = (ListView)findViewById(R.id.locationLookupDetailTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();
				myIntent.putExtra("Location", (Location) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		PBMApplication app = (PBMApplication) getApplication();
		HashMap<Integer, com.pbm.Location> locations = app.getLocations();
		for(Object key : locations.keySet()) {
			Location location = locations.get(key);

			if (location.zoneNo == zone.zoneNo || (zone.zoneNo == 0)) {
				foundLocations.add(location);
			}
		}

		Collections.sort(foundLocations, new Comparator<Location>() {
			public int compare(Location l1, Location l2) {
				return l1.name.toString().compareTo(l2.name.toString());
			}
		});

		table.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, foundLocations));
	}   

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.mapButton :
			Intent myIntent = new Intent();
			myIntent.putExtra("Locations", foundLocations.toArray());
			myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
			startActivityForResult(myIntent, QUIT_RESULT);

			break;
		}
	}
}