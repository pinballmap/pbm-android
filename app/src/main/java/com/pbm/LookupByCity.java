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

import java.util.List;

public class LookupByCity extends PinballMapActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_city);
		
		logAnalyticsHit("com.pbm.LookupByCity");

		PBMApplication app = getPBMApplication();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Region region = app.getRegion(settings.getInt("region",  1));
		List<String> cities = region.cities(this);
		
		ListView cityTable = (ListView)findViewById(R.id.cityTable);
		cityTable.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
			Intent myIntent = new Intent();	
			myIntent.putExtra("City", (String) parentView.getItemAtPosition(position));
			myIntent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");

			startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		java.util.Collections.sort(cities);

		cityTable.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities));
		
		setTable(cityTable);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searchable_listview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}