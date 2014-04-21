package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LookupByMachineList extends PBMUtil {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_machine_list);

		final PBMApplication app = (PBMApplication) getApplication();

		logAnalyticsHit("com.pbm.LookupByMachineList");
		
		ListView table = (ListView)findViewById(R.id.machineLookupListTable);
		table.setFastScrollEnabled(true);
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View arg1, int touchIndex, long arg3) {	
				Intent myIntent = new Intent();
				myIntent.putExtra("Machine", (Machine) adapterView.getItemAtPosition(touchIndex));
				myIntent.setClassName("com.pbm", "com.pbm.MachineLookupDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getMachineValues(false)));
	}   
}
