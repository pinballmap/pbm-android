package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LookupByLocationType extends PinballMapActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_location_type);

		logAnalyticsHit("com.pbm.LookupByLocationType");

		initializeLocationTypeTable();
	}

	public void initializeLocationTypeTable() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Region region = getPBMApplication().getRegion(settings.getInt("region",  1));
		List<LocationType> locationTypes = region.locationTypes(this);
		
		ListView locationTypeTable = (ListView)findViewById(R.id.locationTypeTable);
		locationTypeTable.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();
				myIntent.putExtra("LocationType", (LocationType) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");

				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		sort(locationTypes);

		locationTypeTable.setAdapter(new ArrayAdapter<>(this, R.layout.custom_list_item_1, locationTypes));
		
		setTable(locationTypeTable);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searchable_listview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void sort(List<LocationType> types) {
		Collections.sort(types, new Comparator<LocationType>() {
			public int compare(LocationType t1, LocationType t2) {
				return t1.getName().compareTo(t2.getName());
			}
		});
	}
}