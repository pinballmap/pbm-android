package com.pbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
	private List<Machine> machines = new ArrayList<Machine>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.location_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.location_detail_titlebar);

		machines.clear();

		location = (Location) getIntent().getExtras().get("Location");
		if (location.street1 == null) {
			PBMApplication app = (PBMApplication) getApplication();
			location = updateLocationData(app.getLocation(location.locationNo));
		}
		machines = getLocationMachineData(location);

		TextView title = (TextView)findViewById(R.id.title);
		title.setText(location.name);

		TextView locationName = (TextView)findViewById(R.id.locationName);
		locationName.setText(location.name + "\n\t" + location.street1 + "\n\t" + location.city + " " + location.state + " " + location.zip + "\n\t" + location.phone);

		table = (ListView)findViewById(R.id.locationDetailTable);
		table.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();
				myIntent.putExtra("Location", location);
				myIntent.putExtra("Machine", (Machine) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationMachineEdit");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		updateTable();
	}

	private void updateTable() {
		Collections.sort(machines, new Comparator<Machine>() {
			public int compare(Machine m1, Machine m2) {
				try {
					return m1.name.compareTo(m2.name);
				} catch (java.lang.NullPointerException nep) {
					return 0;
				}
			}
		});

		table.setAdapter(new ArrayAdapter<Machine>(this, android.R.layout.simple_list_item_1, machines));
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
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
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + location.street1 + ", " + location.city + ", " + location.state + ", " + location.zip));
			startActivity(intent);

			break;
		default:
			break;
		}
	}

	public void activityRefreshResult() {
		machines.clear();
		machines = getLocationMachineData(location);
		updateTable();
	}
}