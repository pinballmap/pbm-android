package com.pbm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RegionsTab extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<Region>> {
    ListView table;
    boolean sortByDistance = false;
    Region[] regions;

	public RegionsTab() {
		if (getArguments() != null) {
			regions = (Region[]) getArguments().getSerializable("regions");
			sortByDistance = getArguments().getBoolean("sortByDistance");
		}
	}
//	public RegionsTab(Object[] regions, boolean sortByDistance) {
//		this.regions = regions;
//		this.sortByDistance = sortByDistance;
//	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEmptyText("loading regions");
//		table = (ListView) getView().findViewById(android.R.id.list);
//		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1,
//				null, new String[] {"Region"}, new int[] {android.R.id.text1}, 0);
//		setListAdapter(adapter);
		setListShown(false);
		getLoaderManager().initLoader(0, getArguments(), this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Region region = (Region) l.getItemAtPosition(position);
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

	public ArrayList<Region> sortByDistance(ArrayList<Region> regions) {
//		Region[] sortedRegions = new Region[regions.length];
		
		SharedPreferences settings = getActivity().getSharedPreferences(PBMUtil.PREFS_NAME, 0);
		float yourLat = settings.getFloat("yourLat", -1);
		float yourLon = settings.getFloat("yourLon", -1);
		
		if (yourLat != -1 && yourLon != -1) {
			android.location.Location yourLocation = new android.location.Location("");
			yourLocation.setLatitude(yourLat);
			yourLocation.setLongitude(yourLon);

			for (Region region: regions) {
				float distance = yourLocation.distanceTo(region.toAndroidLocation());
				distance = distance * PBMUtil.METERS_TO_MILES;

				region.setDistance(distance);
				
//				sortedRegions[i] = region;
			}

			Collections.sort(regions, new Comparator<Region>() {
						@Override
						public int compare(Region r1, Region r2) {
							if (r1.distanceFromYou < r2.distanceFromYou) {
								return -1;
							}
							if (r1.distanceFromYou > r2.distanceFromYou) {
								return 1;
							}

							return 0;
						}

					});


			return regions;
		} else {
			return regions;
		}
	}

//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//		return inflater.inflate(R.layout.region_tab, container, false);
//	}

	@Override
	public Loader<ArrayList<Region>> onCreateLoader(int id, Bundle args) {
		Region region = new Region();
		return new AsyncJsonLoader<>(getActivity(), PBMUtil.regionlessBase + "regions.json", "GET", region);
	}


	@Override
	public void onLoadFinished(Loader<ArrayList<Region>> loader, ArrayList<Region> data) {
		PBMApplication pbm = (PBMApplication) getActivity().getApplication();
		if (data != null) {
			for (Region region : data) {
				pbm.addRegion(region.id, region);
			}
		}
		ArrayList<Region> regionValues = new ArrayList<Region> (pbm.getRegions().values());

//		this.regions = (Region[]) pbm.getRegions().values().toArray();
		if (this.sortByDistance) {
			sortByDistance(regionValues);
		}
		setListAdapter(new ArrayAdapter<Object>(getActivity(), android.R.layout.simple_list_item_1, regionValues.toArray()));
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Region>> loader) {

	}

}