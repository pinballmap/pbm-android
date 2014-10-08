package com.pbm;

import java.util.Arrays;
import java.util.Comparator;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RegionsTab extends Fragment {
    ListView table;
    boolean sortByDistance;
    Object[] regions;

	public RegionsTab(Object[] regions, boolean sortByDistance) {
		this.regions = regions;
		this.sortByDistance = sortByDistance;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		table = (ListView) getView().findViewById(R.id.regionListView);

		if (this.sortByDistance) {
			this.regions = sortByDistance(regions);
		}
			  
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Region region = (Region) parentView.getItemAtPosition(position);
				if (! (region.name.equals(""))) {
					PBMUtil.setRegionBase(PBMUtil.httpBase + PBMUtil.apiPath + "region/" + region.name + "/");						
				}

				SharedPreferences settings = getActivity().getSharedPreferences(PBMUtil.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("region", region.id);
				editor.commit();

				Intent myIntent = new Intent();	
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				myIntent.setClassName("com.pbm", "com.pbm.InitializingScreen");
				startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
			}
		});

		table.setAdapter(new ArrayAdapter<Object>(getActivity(), android.R.layout.simple_list_item_1, this.regions));
	}

	public Object[] sortByDistance(Object[] regions) {
		Object[] sortedRegions = new Object[regions.length];
		
		SharedPreferences settings = getActivity().getSharedPreferences(PBMUtil.PREFS_NAME, 0);
		float yourLat = settings.getFloat("yourLat", -1);
		float yourLon = settings.getFloat("yourLon", -1);
		
		if (yourLat != -1 && yourLon != -1) {
			android.location.Location yourLocation = new android.location.Location("");
			yourLocation.setLatitude(yourLat);
			yourLocation.setLongitude(yourLon);

			for (int i = 0; i < regions.length; i++) {
				Region region = (Region) regions[i];
				
				float distance = yourLocation.distanceTo(region.toAndroidLocation()); 
				distance = (float) (distance * PBMUtil.METERS_TO_MILES);	

				region.setDistance(distance);
				
				sortedRegions[i] = region;
			}

			Arrays.sort(sortedRegions, new Comparator<Object>() {
	            public int compare(Object o1, Object o2) {
	            	Region r1 = (Region) o1;
	            	Region r2 = (Region) o2;
	            	if (r1.distanceFromYou < r2.distanceFromYou) {
	            		return -1;
	                }

	            	if (r1.distanceFromYou > r2.distanceFromYou) {
	                	return 1;
	                }

	                return 0;
	            }
			});

			return sortedRegions;
		} else {
			return regions;
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.region_tab, container, false);
	}
}