package com.pbm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

public class RegionTabListener implements ActionBar.TabListener {
	Fragment fragment;
	
	public RegionTabListener(Fragment fragment) {
		this.fragment = fragment;
	}
	
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.region_tab_container, fragment);
	}
	
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}
	
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
}