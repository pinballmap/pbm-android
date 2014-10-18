package com.pbm;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import android.content.Intent;
import android.os.Bundle;

public class PBMMenu extends PBMUtil {
	public static int rootPID;
	
	public static final String LOOKUP_BY_LOCATION = "Lookup By Location";
	public static final String LOOKUP_BY_MACHINE = "Lookup By Machine";
	public static final String LOOKUP_BY_ZONE = "Lookup By Zone";
	public static final String LOOKUP_BY_LOCATION_TYPE = "Lookup By Location Type";
	public static final String RECENTLY_ADDED = "Recently Added";
	public static final String RECENT_HIGH_SCORES = "Recent High Scores";
	public static final String EVENTS = "Events";
	public static final String CLOSEST_LOCATIONS = "Closest Locations";
	public static final String SUGGEST_A_LOCATION = "Suggest A Location";


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		rootPID = this.getTaskId();
		setContentView(R.layout.main);

		PBMApplication app = (PBMApplication) getApplication();
		Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));
		
		final List<String> mainMenuItems = new ArrayList<String>();
		mainMenuItems.add(LOOKUP_BY_LOCATION);
		mainMenuItems.add(LOOKUP_BY_MACHINE);

		if (app.getZones().values().size() > 0) {
			mainMenuItems.add(LOOKUP_BY_ZONE);
		}
		
		if (region.locationTypes(this) != null) {
			mainMenuItems.add(LOOKUP_BY_LOCATION_TYPE);
		}

		mainMenuItems.add(RECENTLY_ADDED);
		mainMenuItems.add(RECENT_HIGH_SCORES);
		mainMenuItems.add(EVENTS);
		mainMenuItems.add(CLOSEST_LOCATIONS);
		mainMenuItems.add(SUGGEST_A_LOCATION);

		if (region != null && region.motd != null && !(region.motd.equals(""))) {
			Toast.makeText(getBaseContext(), region.motd, Toast.LENGTH_LONG).show();
		}

		ListView table = (ListView)findViewById(R.id.maintable);
		final ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mainMenuItems);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	

				Intent intent = new Intent();
				String menuItem = (String) adapter.getItem(position);
				
				if (menuItem.equals(LOOKUP_BY_LOCATION)) {
					intent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");
				} else if (menuItem.equals(LOOKUP_BY_MACHINE)) {
					intent.setClassName("com.pbm", "com.pbm.LookupByMachineList");
				} else if (menuItem.equals(LOOKUP_BY_ZONE)) {
					intent.setClassName("com.pbm", "com.pbm.LookupByZoneList");
				} else if (menuItem.equals(RECENTLY_ADDED)) {
					intent.setClassName("com.pbm", "com.pbm.RecentlyAdded");
				} else if (menuItem.equals(RECENT_HIGH_SCORES)) {
					intent.setClassName("com.pbm", "com.pbm.RecentScores");
				} else if (menuItem.equals(EVENTS)) {
					intent.setClassName("com.pbm", "com.pbm.Events");
				} else if (menuItem.equals(CLOSEST_LOCATIONS)) {
					intent.setClassName("com.pbm", "com.pbm.CloseLocations");
				} else if (menuItem.equals(SUGGEST_A_LOCATION)) {
					intent.setClassName("com.pbm", "com.pbm.SuggestLocation");
				} else if (menuItem.equals(LOOKUP_BY_LOCATION_TYPE)) {
					intent.setClassName("com.pbm", "com.pbm.LookupByLocationType");
				} else {
					intent.setClassName("com.pbm", "com.pbm.PBMMenu");
				}

				startActivityForResult(intent, MENU_RESULT);
			}
		});

		table.setAdapter(adapter);
	}   

	public void activityResetResult() {}
}