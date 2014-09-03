package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.location_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.location_detail_titlebar);

		logAnalyticsHit("com.pbm.LocationDetail");

		lmxes.clear();

		location = (Location) getIntent().getExtras().get("Location");
		
		if (location != null) {
			loadLocationData();
		}
	}
	
	private void loadLocationData() {
		new Thread(new Runnable() {
			public void run() {
				LocationDetail.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						lmxes = location.getLmxes(LocationDetail.this);
						machines = location.getMachines(LocationDetail.this);
		
						TextView title = (TextView)findViewById(R.id.title);
						title.setText(location.name);
						TextView locationName = (TextView)findViewById(R.id.locationName);
						locationName.setMovementMethod(LinkMovementMethod.getInstance());
						
						String locationTypeName = "";
						LocationType locationType = location.getLocationType(LocationDetail.this);
						if (locationType != null) {
							locationTypeName = "\n\t(" + locationType.name + ")";
						}
		
						locationName.setText(
							location.name + "\n\t" +
							location.street + "\n\t" + location.city + " " + location.state + " " + location.zip +
							((location.phone == null || location.phone.equals("") || location.phone.equals("null")) ? "" : "\n\t" + location.phone) +
							((location.website == null || location.website.equals("") || location.website.equals("null")) ? "" : "\n\t" + location.website) +
							locationTypeName
						);
						table = (ListView)findViewById(R.id.locationDetailTable);
						table.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
								Machine machine = (Machine) parentView.getItemAtPosition(position);
		
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
			table.setAdapter(new ArrayAdapter<Machine>(this, android.R.layout.simple_list_item_1, machines));
		}
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.editLocationButton :
			Intent editIntent = new Intent();
			editIntent.putExtra("Location", location);
			editIntent.setClassName("com.pbm", "com.pbm.LocationEdit");
			startActivityForResult(editIntent, QUIT_RESULT);    

			break;
		case R.id.mapButton :
			Intent myIntent = new Intent();
			myIntent.putExtra("Locations", new Location[] {location});
			myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
			startActivityForResult(myIntent, QUIT_RESULT);  

			break;
		case R.id.addMachineButton :
			Intent newMachineIntent = new Intent();
			newMachineIntent.putExtra("Location", location);
			newMachineIntent.setClassName("com.pbm", "com.pbm.AddMachine");
			startActivityForResult(newMachineIntent, QUIT_RESULT);    

			break;
		case R.id.navButton :
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + location.street + ", " + location.city + ", " + location.state + ", " + location.zip));
			startActivity(intent);

			break;
		default:
			break;
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