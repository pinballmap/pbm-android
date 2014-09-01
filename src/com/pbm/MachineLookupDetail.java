package com.pbm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MachineLookupDetail extends PBMUtil {
	private Machine machine;
	private static Location[] locationsWithMachine;
	private	ListView table;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.machine_lookup_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.map_titlebar);

		logAnalyticsHit("com.pbm.MachineLookupDetail");

		Bundle extras = getIntent().getExtras();
		machine = (Machine) extras.get("Machine");

		TextView title = (TextView)findViewById(R.id.title);
		title.setText(machine.name);

		table = (ListView)findViewById(R.id.machineLookupDetailTable);
		table.setFastScrollEnabled(true);
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();
				myIntent.putExtra("Location", (Location) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail"); 
				startActivityForResult(myIntent, QUIT_RESULT);    
			}
		});

		loadLocationData();
	}   
	
	private void loadLocationData() {
		table.setAdapter(null);

		new Thread(new Runnable() {
	        public void run() {
	        	MachineLookupDetail.super.runOnUiThread(new Runnable() {
					public void run() {
						locationsWithMachine = getLocationsWithMachine(machine);
						
						if (locationsWithMachine != null) {
							try {
								Arrays.sort(locationsWithMachine, new Comparator<Location>() {
									public int compare(Location l1, Location l2) {
										return l1.name.toString().compareTo(l2.name.toString());
									}
								});
							} catch (java.lang.NullPointerException nep) {}

							table.setAdapter(new ArrayAdapter<Location>(MachineLookupDetail.this, android.R.layout.simple_list_item_1, locationsWithMachine));
						}
					}
	        	});
	        }
	    }).start();
	}

	public void onResume() {
		super.onResume();
		loadLocationData();
	}
	
	public Location[] getLocationsWithMachine(Machine machine) {
		List<Location> locations = new ArrayList<Location>();
		
		PBMApplication app = (PBMApplication) getApplication();
		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.getMachine(this).id == machine.id){ 
				locations.add(lmx.getLocation(this));
			}
		}

		return locations.toArray(new Location[locations.size()]);
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.mapButton :
			if (locationsWithMachine != null) {
				Intent myIntent = new Intent();
				myIntent.putExtra("Locations", locationsWithMachine);
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, QUIT_RESULT);
			}

			break;
		}
	}
}