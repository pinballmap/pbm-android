package com.pbm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
		//noinspection deprecation
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		rootPID = this.getTaskId();
		setTitle(getPBMApplication().getRegion().getFormalName() + " " + getString(R.string.app_name));
		setContentView(R.layout.main);
		//noinspection ConstantConditions
		getSupportActionBar().setIcon(R.mipmap.ic_launcher);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();

		PBMApplication app = getPBMApplication();
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));

		final List<String> mainMenuItems = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				mainMenuItems.add(CLOSEST_LOCATIONS);
			} else {
				if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					mainMenuItems.add(CLOSEST_LOCATIONS);
				}
			}
		}

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

		if (region != null && region.getMotd() != null && !(region.getMotd().equals(""))) {
			Toast.makeText(getBaseContext(), region.getMotd(), Toast.LENGTH_LONG).show();
		}

		ListView table = (ListView) findViewById(R.id.maintable);
		final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_item_1, mainMenuItems);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {
			Intent intent = new Intent();
			String menuItem = adapter.getItem(position);
			if (menuItem != null) {
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
			}
			startActivityForResult(intent, MENU_RESULT);
			}
		});

		table.setAdapter(adapter);
	}
}