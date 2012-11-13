package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LookupByLocationList extends PBMUtil {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_location_list);

		List<Zone> primaryZones = new ArrayList<Zone>();
		List<Zone> secondaryZones = new ArrayList<Zone>();

		primaryZones.add(new Zone(0, "All", "All", 0));

		ListView table = (ListView)findViewById(R.id.locationLookupListTable);
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();	
				myIntent.putExtra("Zone", (Zone) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		PBMApplication app = (PBMApplication) getApplication();
		HashMap<Integer, com.pbm.Zone> zones = app.getZones();
		for(Integer key : zones.keySet()) {
			Zone zone = zones.get(key);

			if(zone.isPrimary == 1) {
				primaryZones.add(zone);
			} else {
				secondaryZones.add(zone);
			}
		}
		
	    /* TODO put breaks between these two lists*/
		sort(primaryZones);
		sort(secondaryZones);
		primaryZones.addAll(secondaryZones);

		table.setAdapter(new ArrayAdapter<Zone>(this, android.R.layout.simple_list_item_1, primaryZones));
	}

	private void sort(List<Zone> zones) {
		Collections.sort(zones, new Comparator<Zone>() {
			public int compare(Zone z1, Zone z2) {
				return z1.name.compareTo(z2.name);
			}
		});
	}
}