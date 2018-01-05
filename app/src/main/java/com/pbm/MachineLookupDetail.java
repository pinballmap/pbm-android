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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class MachineLookupDetail extends PinballMapActivity {
	private Machine machine;
	private ArrayList<Location> locationsWithMachine = new ArrayList<>();
	private ListView machineLookupDetailTable;
	private Parcelable listState;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			listState = savedInstanceState.getParcelable("listState");
		}
		setContentView(R.layout.machine_lookup_detail);
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			machine = (Machine) extras.get("Machine");
		}

		if (machine != null) {
			setTitle(machine.getName());

			initializeMachineLookupDetailTable();
		}

		waitForInitializeAndLoad("com.pbm.MachineLookupDetail", R.id.machineLookupDetailRelativeLayout, new Runnable() {
			public void run() {
			loadLocationData();
			}
		});
	}

	public void initializeMachineLookupDetailTable() {
		machineLookupDetailTable = (ListView) findViewById(R.id.machineLookupDetailTable);
		machineLookupDetailTable.setFastScrollEnabled(true);
		machineLookupDetailTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent myIntent = new Intent();
			com.pbm.Location location = locationsWithMachine.get(position);

			myIntent.putExtra("Location", location);
			myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
			startActivityForResult(myIntent, PinballMapActivity.QUIT_RESULT);
			}
		});

		setTable(machineLookupDetailTable);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void loadLocationData() {
		try {
			locationsWithMachine = getPBMApplication().getLocationsWithMachine(machine);
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
		}

		if (locationsWithMachine != null) {
			Log.d("com.pbm.location", "adjusting distance to locations");
			for (Location location : locationsWithMachine) {
				if (this.getLocation() != null) {
					location.setDistance(this.getLocation());
				}
			}

			try {
				Collections.sort(locationsWithMachine, new Comparator<Location>() {
					public int compare(Location l1, Location l2) {
					return l1.getName().compareTo(l2.getName());
					}
				});
			} catch (java.lang.NullPointerException nep) {
				nep.printStackTrace();
			}

			machineLookupDetailTable.setAdapter(new LocationListAdapter(MachineLookupDetail.this, locationsWithMachine));
			if (listState != null) {
				machineLookupDetailTable.onRestoreInstanceState(listState);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		listState = machineLookupDetailTable.onSaveInstanceState();
		outState.putParcelable("listState", listState);
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