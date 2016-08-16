package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class LookupByMachineList extends PinballMapActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lookup_by_machine_list);

		logAnalyticsHit("com.pbm.LookupByMachineList");
		
		final ArrayList<Machine> machines = getPBMApplication().getMachineValues(false);
		ListView machineLookupListTable = (ListView)findViewById(R.id.machineLookupListTable);
		machineLookupListTable.setAdapter(new MachineListAdapter(this, machines, true));
		machineLookupListTable.setFastScrollEnabled(true);
		
		machineLookupListTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View arg1, int touchIndex, long arg3) {	
			Intent myIntent = new Intent();
			Machine machine = machines.get(touchIndex);
			myIntent.putExtra("Machine", machine);
			myIntent.setClassName("com.pbm", "com.pbm.MachineLookupDetail");

			startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		setTable(machineLookupListTable);
	}   

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.searchable_listview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
