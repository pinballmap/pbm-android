package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PBMMenu extends PinballMapActivity {
	public static int rootPID;

	public static final String LOOKUP_BY_LOCATION = "Lookup By Location";
	public static final String LOOKUP_BY_MACHINE = "Lookup By Machine";
	public static final String LOOKUP_BY_ZONE = "Lookup By Zone";
	public static final String LOOKUP_BY_OPERATOR = "Lookup By Operator";
	public static final String LOOKUP_BY_CITY = "Lookup By City";
	public static final String LOOKUP_BY_LOCATION_TYPE = "Lookup By Location Type";
	public static final String RECENTLY_ADDED = "Recently Added";
	public static final String RECENT_HIGH_SCORES = "Recent High Scores";
	public static final String EVENTS = "Events";
	public static final String CLOSEST_LOCATIONS = "Closest Locations";


	public void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		rootPID = this.getTaskId();
		setTitle(getPBMApplication().getRegion().formalName + " " + getString(R.string.app_name));
		setContentView(R.layout.main);
		getSupportActionBar().setIcon(R.drawable.icon);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();

		PBMApplication app = getPBMApplication();
		Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));

		final List<String> mainMenuItems = new ArrayList<>();
		mainMenuItems.add(LOOKUP_BY_LOCATION);
		mainMenuItems.add(LOOKUP_BY_MACHINE);
		mainMenuItems.add(LOOKUP_BY_CITY);

		if (app.getZones().values().size() > 0) {
			mainMenuItems.add(LOOKUP_BY_ZONE);
		}

		if (app.getOperators().values().size() > 0) {
			mainMenuItems.add(LOOKUP_BY_OPERATOR);
		}

		if (region != null && region.locationTypes(this) != null) {
			mainMenuItems.add(LOOKUP_BY_LOCATION_TYPE);
		}

		mainMenuItems.add(RECENTLY_ADDED);
		mainMenuItems.add(RECENT_HIGH_SCORES);
		mainMenuItems.add(EVENTS);
		mainMenuItems.add(CLOSEST_LOCATIONS);

		if (region != null && region.motd != null && !(region.motd.equals(""))) {
			Toast.makeText(getBaseContext(), region.motd, Toast.LENGTH_LONG).show();
		}

		ListView table = (ListView) findViewById(R.id.maintable);
		final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_item_1, mainMenuItems);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {
			Intent intent = new Intent();
			String menuItem = adapter.getItem(position);

			switch (menuItem) {
				case LOOKUP_BY_LOCATION:
					intent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");
					break;
				case LOOKUP_BY_MACHINE:
					intent.setClassName("com.pbm", "com.pbm.LookupByMachineList");
					break;
				case LOOKUP_BY_CITY:
					intent.setClassName("com.pbm", "com.pbm.LookupByCity");
					break;
				case LOOKUP_BY_ZONE:
					intent.setClassName("com.pbm", "com.pbm.LookupByZoneList");
					break;
				case LOOKUP_BY_OPERATOR:
					intent.setClassName("com.pbm", "com.pbm.LookupByOperator");
					break;
				case RECENTLY_ADDED:
					intent.setClassName("com.pbm", "com.pbm.RecentlyAdded");
					break;
				case RECENT_HIGH_SCORES:
					intent.setClassName("com.pbm", "com.pbm.RecentScores");
					break;
				case EVENTS:
					intent.setClassName("com.pbm", "com.pbm.Events");
					break;
				case CLOSEST_LOCATIONS:
					intent.setClassName("com.pbm", "com.pbm.CloseLocations");
					break;
				case LOOKUP_BY_LOCATION_TYPE:
					intent.setClassName("com.pbm", "com.pbm.LookupByLocationType");
					break;
				default:
					intent.setClassName("com.pbm", "com.pbm.PBMMenu");
					break;
			}

			startActivityForResult(intent, MENU_RESULT);
			}
		});

		table.setAdapter(adapter);
	}
}