package com.pbm;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;

@SuppressLint("HandlerLeak")
public class Preferences extends PBMUtil {
	private	ActionBar.Tab regionsByNameTab, regionsByLocationTab;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_tab_container);

		logAnalyticsHit("com.pbm.Preferences");

		PBMApplication app = (PBMApplication) getApplication();
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
		Fragment regionsByName = new RegionsTab(app.getRegionValues(), false);
		Fragment regionsByLocation = new RegionsTab(app.getRegionValues(), true);
	
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