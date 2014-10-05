package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationDetail extends PBMUtil {
	private ListView table;

	private Location location;
	private List<LocationMachineXref> lmxes = new ArrayList<LocationMachineXref>();
	private List<Machine> machines = new ArrayList<Machine>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		logAnalyticsHit("com.pbm.LocationDetail");

		lmxes.clear();

		location = (Location) getIntent().getExtras().get("Location");
		
		if (location != null) {
			setTitle(location.name);

			loadLocationData();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.location_detail_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	private void loadLocationData() {
		new Thread(new Runnable() {
			public void run() {
				LocationDetail.super.runOnUiThread(new Runnable() {
					public void run() {
						lmxes = location.getLmxes(LocationDetail.this);
						machines = location.getMachines(LocationDetail.this);
		
						TextView locationName = (TextView)findViewById(R.id.locationName);
						TextView locationMetadata = (TextView)findViewById(R.id.locationMetadata);
						
						String locationTypeName = "";
						LocationType locationType = location.getLocationType(LocationDetail.this);
						if (locationType != null) {
							locationTypeName = "(" + locationType.name + ")";
						}

						locationName.setText(location.name);
						
						locationMetadata.setText(
							((locationTypeName == null || locationTypeName.equals("") || locationTypeName.equals("null")) ? "" : locationTypeName + "\n\t") +
							location.street + "\n\t" + location.city + " " + location.state + " " + location.zip +
							((location.phone == null || location.phone.equals("") || location.phone.equals("null")) ? "" : "\n\t" + location.phone)
						);

						TextView website = (TextView)findViewById(R.id.website);
						if (location.website != null && !location.website.equals("") && !location.website.equals("null")) {
							website.setVisibility(View.VISIBLE);
							website.setMovementMethod(LinkMovementMethod.getInstance());
							website.setText("\t" + location.website);
						} else {
							website.setVisibility(View.INVISIBLE);
						}

						table = (ListView)findViewById(R.id.locationDetailTable);
						table.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
								Machine machine = machines.get(position);
		
								Intent myIntent = new Intent();
								PBMApplication app = (PBMApplication) getApplication();
								myIntent.putExtra("lmx", app.getLmxFromMachine(machine, lmxes));
								myIntent.setClassName("com.pbm", "com.pbm.LocationMachineEdit");
								startActivityForResult(myIntent, QUIT_RESULT);
							}
						});
		
						updateTable();
					}
				});
			}
		}).start();
	}

	private void updateTable() {
		try {
			Collections.sort(machines, new Comparator<Machine>() {
				public int compare(Machine m1, Machine m2) {
					return m1.name.replaceAll("^(?i)The ", "").compareTo(m2.name.replaceAll("^(?i)The ", ""));
				}
			});
		} catch (java.lang.NullPointerException nep) {}

		if (machines != null) {
			table.setAdapter(new MachineListAdapter(this, machines, false));
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_button :
				Intent myIntent = new Intent();
				myIntent.putExtra("Locations", new Location[] {location});
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, QUIT_RESULT);

				return true;
			case R.id.edit_button :
				Intent editIntent = new Intent();
				editIntent.putExtra("Location", location);
				editIntent.setClassName("com.pbm", "com.pbm.LocationEdit");
				startActivityForResult(editIntent, QUIT_RESULT);    
			
				return true;
			case R.id.add_machine_button :
				Intent newMachineIntent = new Intent();
				newMachineIntent.putExtra("Location", location);
				newMachineIntent.setClassName("com.pbm", "com.pbm.AddMachine");
				startActivityForResult(newMachineIntent, QUIT_RESULT);    
			
				return true;
			case R.id.nav_button :
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + location.street + ", " + location.city + ", " + location.state + ", " + location.zip));
				startActivity(intent);
			
				return true;
	    	default:
	    	    return super.onOptionsItemSelected(item);
		}
	}

	public void activityRefreshResult() {
		lmxes.clear();

		new Thread(new Runnable() {
			public void run() {
				PBMApplication app = (PBMApplication) getApplication();
				location = app.getLocation(location.id);
				lmxes = location.getLmxes(LocationDetail.this);
				machines = location.getMachines(LocationDetail.this);

				loadLocationData();
			}
		}).start();
	}
}