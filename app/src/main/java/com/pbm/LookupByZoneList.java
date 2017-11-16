package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class LookupByZoneList extends PinballMapActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_zone_list);

		waitForInitializeAndLoad("com.pbm.LookupByZoneList", (ViewGroup)findViewById(R.id.lookupByZoneRelativeView).getParent(), new Runnable() {
			public void run() {
				initializeLocationLookupListTable();
			}
		});
	}

	public void initializeLocationLookupListTable() {
		List<Zone> primaryZones = new ArrayList<>();
		List<Zone> secondaryZones = new ArrayList<>();

		ListView locationLookupListTable = (ListView)findViewById(R.id.locationLookupListTable);
		locationLookupListTable.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
			Intent myIntent = new Intent();
			myIntent.putExtra("Zone", (Zone) parentView.getItemAtPosition(position));
			myIntent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");

			startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		HashMap<Integer, com.pbm.Zone> zones = getPBMApplication().getZones();
		for(Integer key : zones.keySet()) {
			Zone zone = zones.get(key);

			if(zone.getIsPrimary() == 1) {
				primaryZones.add(zone);
			} else {
				secondaryZones.add(zone);
			}
		}
		
		sort(primaryZones);
		sort(secondaryZones);
		primaryZones.addAll(secondaryZones);

		locationLookupListTable.setAdapter(new ArrayAdapter<>(this, R.layout.custom_list_item_1, primaryZones));
		
		setTable(locationLookupListTable);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searchable_listview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void sort(List<Zone> zones) {
		Collections.sort(zones, new Comparator<Zone>() {
			public int compare(Zone z1, Zone z2) {
				return z1.getName().compareTo(z2.getName());
			}
		});
	}
}