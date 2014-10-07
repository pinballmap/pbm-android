package com.pbm;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class LookupByMachineList extends PBMUtil {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_machine_list);

		final PBMApplication app = (PBMApplication) getApplication();

		logAnalyticsHit("com.pbm.LookupByMachineList");
		
		final ArrayList<Machine> machines = app.getMachineValues(false);
		ListView table = (ListView)findViewById(R.id.machineLookupListTable);
		table.setAdapter(new MachineListAdapter(this, machines, true));
		table.setFastScrollEnabled(true);
		
		table.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View arg1, int touchIndex, long arg3) {	
				Intent myIntent = new Intent();
				Machine machine = machines.get(touchIndex);
				myIntent.putExtra("Machine", machine);
				myIntent.setClassName("com.pbm", "com.pbm.MachineLookupDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		super.onCreate(savedInstanceState, table);
	}   

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searchable_listview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
