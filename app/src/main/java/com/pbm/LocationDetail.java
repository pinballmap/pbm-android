package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocationDetail extends PinballMapActivity {
	private ListView table;

	private Location location;
	private List<LocationMachineXref> lmxes = new ArrayList<>();
	private List<Machine> machines = new ArrayList<>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		logAnalyticsHit("com.pbm.LocationDetail");

		lmxes.clear();

		location = (Location) getIntent().getExtras().get("Location");

		if (location != null) {
			setTitle("");

			loadLocationData();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_detail_menu, menu);
		Button b = (Button) findViewById(R.id.edit_button);

		return super.onCreateOptionsMenu(menu);
	}

	private void loadLocationData() {
		new Thread(new Runnable() {
			public void run() {
				LocationDetail.super.runOnUiThread(new Runnable() {
					public void run() {
						lmxes = location.getLmxes(LocationDetail.this);
						machines = location.getMachines(LocationDetail.this);

						TextView locationName = (TextView) findViewById(R.id.locationName);
						TextView locationType = (TextView) findViewById(R.id.locationType);
						TextView locationMetadata = (TextView) findViewById(R.id.locationMetadata);
						TextView locationWebsite = (TextView) findViewById(R.id.website);
						TextView locationPhone = (TextView) findViewById(R.id.locationPhone);

						String locationTypeName = "";
						LocationType type = location.getLocationType(LocationDetail.this);
						if (type != null) {
							locationTypeName = "(" + type.name + ")";
						}

						locationName.setText(location.name);
						locationMetadata.setText(location.street + ", " + location.city + ", " + location.state + ", " + location.zip);

						if (location.phone != null && !location.phone.equals("") && !location.phone.equals("null")) {
							locationPhone.setVisibility(View.VISIBLE);
							locationPhone.setText(location.phone);
						} else {
							locationPhone.setVisibility(View.GONE);
						}

						if (!locationTypeName.equals("") && !locationTypeName.equals("null")) {
							locationType.setVisibility(View.VISIBLE);
							locationType.setText(locationTypeName);
						} else {
							locationType.setVisibility(View.GONE);
						}

						if (location.website != null && !location.website.equals("") && !location.website.equals("null")) {
							locationWebsite.setVisibility(View.VISIBLE);
							locationWebsite.setMovementMethod(LinkMovementMethod.getInstance());
							locationWebsite.setText(location.website);
						} else {
							locationWebsite.setVisibility(View.GONE);
						}

						table = (ListView) findViewById(R.id.locationDetailTable);
						table.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {
								Machine machine = machines.get(position);

								Intent myIntent = new Intent();
								PBMApplication app = getPBMApplication();
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
		} catch (java.lang.NullPointerException nep) {
			nep.printStackTrace();
		}

		if (machines != null) {
			table.setAdapter(new MachineDetailListAdapter(this, machines, location.getLMXMap(LocationDetail.this)));
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_button:
				Intent myIntent = new Intent();
				myIntent.putExtra("Locations", new Location[]{location});
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, QUIT_RESULT);

				return true;
			case R.id.edit_button:
				Intent editIntent = new Intent();
				editIntent.putExtra("Location", location);
				editIntent.setClassName("com.pbm", "com.pbm.LocationEdit");
				startActivityForResult(editIntent, QUIT_RESULT);

				return true;
			case R.id.add_machine_button:
				Intent newMachineIntent = new Intent();
				newMachineIntent.putExtra("Location", location);
				newMachineIntent.setClassName("com.pbm", "com.pbm.AddMachine");
				startActivityForResult(newMachineIntent, QUIT_RESULT);

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void activityRefreshResult() {
		lmxes.clear();

		new Thread(new Runnable() {
			public void run() {
				PBMApplication app = getPBMApplication();
				location = app.getLocation(location.id);
				lmxes = location.getLmxes(LocationDetail.this);
				machines = location.getMachines(LocationDetail.this);

				loadLocationData();
			}
		}).start();
	}
}