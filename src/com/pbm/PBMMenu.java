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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		rootPID = this.getTaskId();
		setContentView(R.layout.main);

		final List<String> mainMenuItems = new ArrayList<String>();
		mainMenuItems.add("Lookup By Machine");
		mainMenuItems.add("Lookup By Location");
		mainMenuItems.add("Recently Added");
		mainMenuItems.add("Recent High Scores");
		mainMenuItems.add("Events");
		mainMenuItems.add("Closest Locations");

		ListView table = (ListView)findViewById(R.id.maintable);
		table.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView parentView, View selectedView, int position, long id) {	

				Intent myIntent = new Intent();

				switch (position) {
				case 0: 
					myIntent.setClassName("com.pbm", "com.pbm.LookupByMachineList"); break;
				case 1:
					myIntent.setClassName("com.pbm", "com.pbm.LookupByLocationList"); break;
				case 2:
					myIntent.setClassName("com.pbm", "com.pbm.RecentlyAdded"); break;
				case 3:
					myIntent.setClassName("com.pbm", "com.pbm.RecentScores"); break;
				case 4:
					myIntent.setClassName("com.pbm", "com.pbm.Events"); break;
				case 5:
					myIntent.setClassName("com.pbm", "com.pbm.CloseLocations"); break;
				default:	
					myIntent.setClassName("com.pbm", "com.pbm.LookupByMachineList"); break;
				}

				startActivityForResult(myIntent, MENU_RESULT);
			}
		});
		
		PBMApplication app = (PBMApplication) getApplication();
		Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));
		
		if (region.motd != null && !(region.motd.equals(""))) {
			Toast.makeText(getBaseContext(), region.motd, Toast.LENGTH_LONG).show();
		}
		
		table.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mainMenuItems));
	}   
	
	public void activityResetResult() {}
}