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

public class LookupByOperator extends PinballMapActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_operator);
		
		logAnalyticsHit("com.pbm.LookupByOperator");

		PBMApplication app = getPBMApplication();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Region region = app.getRegion(settings.getInt("region",  1));
		List<Operator> operators = region.operators(this);
		
		ListView table = (ListView)findViewById(R.id.operatorTable);
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();	
				myIntent.putExtra("Operator", (Operator) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationLookupDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		sort(operators);

		table.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, operators));
		
		setTable(table);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searchable_listview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void sort(List<Operator> operators) {
		Collections.sort(operators, new Comparator<Operator>() {
			public int compare(Operator t1, Operator t2) {
				return t1.name.compareTo(t2.name);
			}
		});
	}
}