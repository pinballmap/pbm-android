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

public class MachineLookupDetail extends PBMUtil {
	private Machine machine;
	private ArrayList<Location> locationsWithMachine = new ArrayList<Location>();
	private ListView table;
	private Parcelable listState;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.machine_lookup_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		logAnalyticsHit("com.pbm.MachineLookupDetail");

		Bundle extras = getIntent().getExtras();
		machine = (Machine) extras.get("Machine");

		setTitle(machine.name);

		table = (ListView) findViewById(R.id.machineLookupDetailTable);
		table.setFastScrollEnabled(true);
		table.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent();
				com.pbm.Location location = locationsWithMachine.get(position);
				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
			}
		});

		loadLocationData();

		super.onCreate(savedInstanceState, table);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void loadLocationData() {
		new Thread(new Runnable() {
			public void run() {
				MachineLookupDetail.super.runOnUiThread(new Runnable() {
					public void run() {
						locationsWithMachine = getLocationsWithMachine(machine);

						if (locationsWithMachine != null) {
							try {
								Collections.sort(locationsWithMachine, new Comparator<Location>() {
									public int compare(Location l1, Location l2) {
										return l1.name.compareTo(l2.name);
									}
								});
							} catch (java.lang.NullPointerException nep) {
								nep.printStackTrace();
							}

							table.setAdapter(new LocationListAdapter(MachineLookupDetail.this, locationsWithMachine));
							if (listState != null) {
								table.onRestoreInstanceState(listState);
							}

						}
					}
				});
			}
		}).start();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		listState = table.onSaveInstanceState();
		outState.putParcelable("listState", listState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		listState = savedInstanceState.getParcelable("listState");
	}

	public void onResume() {
		super.onResume();
		loadLocationData();
		listState = null;
		Log.d("com.pbm", "done");
	}

	ArrayList<Location> getLocationsWithMachine(Machine machine) {
		ArrayList<Location> locations = new ArrayList<Location>();

		PBMApplication app = (PBMApplication) getApplication();
		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.getMachine(this).id == machine.id) {
				locations.add(lmx.getLocation(this));
			}
		}

		return locations;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_button:
				if (locationsWithMachine != null) {
					Intent myIntent = new Intent();
					myIntent.putExtra("Locations", locationsWithMachine.toArray());
					myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
					startActivityForResult(myIntent, QUIT_RESULT);

					return true;
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}