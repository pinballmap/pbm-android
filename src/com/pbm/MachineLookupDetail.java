package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class MachineLookupDetail extends PBMUtil {
	private Machine machine;
	private ArrayList<Location> locationsWithMachine = new ArrayList<Location>();
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
								Collections.sort(locationsWithMachine, new Comparator<Location>() {
									public int compare(Location l1, Location l2) {
										return l1.name.toString().compareTo(l2.name.toString());
									}
								});
							} catch (java.lang.NullPointerException nep) {}

							table.setAdapter(new LocationListAdapter(MachineLookupDetail.this, locationsWithMachine));
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
	
	public ArrayList<Location> getLocationsWithMachine(Machine machine) {
		ArrayList<Location> locations = new ArrayList<Location>();
		
		PBMApplication app = (PBMApplication) getApplication();
		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.getMachine(this).id == machine.id){ 
				locations.add(lmx.getLocation(this));
			}
		}

		return locations;
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