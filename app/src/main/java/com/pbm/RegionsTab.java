package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RegionsTab extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Region>> {
	private RegionsPagerAdapter regionsPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(0, getArguments(), this);
		getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.region_tab, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		regionsPagerAdapter = new RegionsPagerAdapter(getChildFragmentManager());

		ViewPager viewPager = getActivity().findViewById(R.id.region_pager);
		viewPager.setAdapter(regionsPagerAdapter);

		TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(viewPager);
	}

	@Override
	public Loader<ArrayList<Region>> onCreateLoader(int id, Bundle args) {
		return new AsyncJsonLoader<>(getActivity(), PinballMapActivity.regionlessBase + "regions.json", new Region());
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Region>> loader, ArrayList<Region> data) {
		PBMApplication pbm = (PBMApplication) getActivity().getApplication();
		if (data != null) {
			for (Region region : data) {
				pbm.addRegion(region);
			}
		}
		ArrayList<Region> regionValues = new ArrayList<>(pbm.getRegionValues());
		Collections.sort(regionValues, new Comparator<Region>() {
			@Override
			public int compare(Region lhs, Region rhs) {
			return lhs.getFormalName().compareTo(rhs.getFormalName());
			}
		}); //TODO consider saving & restoring this to/from savedInstanceState
		((RegionsFragment) this.regionsPagerAdapter.getItem(0)).setRegionValues(regionValues);
		((RegionsFragment) this.regionsPagerAdapter.getItem(1)).setRegionValues(regionValues);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Region>> loader) {}

	protected void updateLocation() {
		((RegionsFragment) regionsPagerAdapter.getItem(1)).updateLocation();
	}

	private static class RegionsPagerAdapter extends FragmentPagerAdapter {

		private final RegionsFragment alphaRegions;
		private final RegionsFragment geoRegions;
		private final Bundle alphaBundle;
		private final Bundle geoBundle;

		RegionsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			alphaRegions = new RegionsFragment();
			alphaBundle = new Bundle();
			alphaBundle.putBoolean("sortByDistance", false);
			alphaRegions.setArguments(alphaBundle);
			geoRegions = new RegionsFragment();
			geoBundle = new Bundle();
			geoBundle.putBoolean("sortByDistance", true);
			geoRegions.setArguments(geoBundle);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return alphaRegions;
			} else if (position == 1) {
				return geoRegions;
			}
			return new Fragment();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position == 0) {
				return "Alphabetically";
			} else if (position == 1) {
				return "By Distance To You";
			}
			return super.getPageTitle(position);
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

	public static class RegionsFragment extends ListFragment {
		boolean sortByDistance = false;
		private ArrayList<Region> regionValues = new ArrayList<>();
		ArrayAdapter<Region> adapter;

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (getArguments() != null) {
				sortByDistance = getArguments().getBoolean("sortByDistance");
			}
			setEmptyText(this.getString(R.string.loading_regions));
			setListShown(true);
			adapter = new ArrayAdapter<>(getActivity(), R.layout.custom_list_item_1, regionValues);
			setListAdapter(adapter);
			updateLocation();
		}

		public void setRegionValues(ArrayList<Region> regions) {
			regionValues = regions;
			if (adapter != null) {
				adapter.clear();
				adapter.addAll(regionValues);
				updateLocation();
			}
		}

		public void updateLocation() {
			if (sortByDistance) {
				sortByDistance();
			}
		}

		public void sortByDistance() {
			android.location.Location yourLocation = ((PinballMapActivity) getActivity()).getLocation();
			if (yourLocation != null) {
				for (Region region : regionValues) {
					float distance = yourLocation.distanceTo(region.toAndroidLocation());
					distance = distance * PinballMapActivity.METERS_TO_MILES;
					region.setDistance(distance);
				}
				adapter.sort(new Comparator<Region>() {
					@Override
					public int compare(Region r1, Region r2) {
					if (r1.getDistanceFromYou() < r2.getDistanceFromYou()) {
						return -1;
					}
					if (r1.getDistanceFromYou() > r2.getDistanceFromYou()) {
						return 1;
					}
					return 0;
					}

				});
			}
		}


		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			Region region = (Region) l.getItemAtPosition(position);
			if (!(region.getName().equals(""))) {
				PinballMapActivity.setRegionBase(PinballMapActivity.httpBase + PinballMapActivity.apiPath + "region/" + region.getName() + "/");
			}

			SharedPreferences settings = getActivity().getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("region", region.getId());
			editor.apply();

			Intent myIntent = new Intent();
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			myIntent.setClassName("com.pbm", "com.pbm.InitializingScreen");
			startActivityForResult(myIntent, PinballMapActivity.QUIT_RESULT);
		}
	}
}