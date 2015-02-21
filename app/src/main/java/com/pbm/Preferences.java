package com.pbm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;

public class Preferences extends PBMUtil {
	private	ActionBar.Tab regionsByNameTab, regionsByLocationTab;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_tab_container);

		logAnalyticsHit("com.pbm.Preferences");

		PBMApplication app = (PBMApplication) getApplication();
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
		Bundle b = new Bundle();
		b.putSerializable("regions", app.getRegionValues());
		b.putBoolean("sortByDistance", false);
		Fragment regionsByName = getSupportFragmentManager().findFragmentById(R.layout.region_tab);
		regionsByName.setArguments(b);

		Bundle b_loc = new Bundle();
		b_loc.putSerializable("regions", app.getRegionValues());
		b_loc.putBoolean("sortByDistance", true);
		Fragment regionsByLocation = getSupportFragmentManager().findFragmentById(R.layout.region_tab);
		//		Fragment regionsByLocation = new RegionsTab(app.getRegionValues(), true);
	
		regionsByNameTab = actionBar.newTab().setText("Sorted Alphabetically");
    	regionsByLocationTab = actionBar.newTab().setText("Sorted By Distance");
    
    	regionsByNameTab.setTabListener(new RegionTabListener(regionsByName));
    	regionsByLocationTab.setTabListener(new RegionTabListener(regionsByLocation));
    
    	actionBar.addTab(regionsByNameTab);
    	actionBar.addTab(regionsByLocationTab);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}