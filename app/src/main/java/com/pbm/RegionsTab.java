package com.pbm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RegionsTab extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Region>> {
	ListView table;
	private boolean sortByDistance = false;
	private ArrayList<Region> regions;
	private RegionsPagerAdapter regionsPagerAdapter;
	private ViewPager viewPager;
	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		getLoaderManager().initLoader(0, getArguments(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.region_tab, container, false);
		return rootView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			regions = (ArrayList<Region>) savedInstanceState.getSerializable("regions");
			sortByDistance = savedInstanceState.getBoolean("sortByDistance");
		}
		regionsPagerAdapter = new RegionsPagerAdapter(getChildFragmentManager());
		viewPager = (ViewPager) getActivity().findViewById(R.id.region_pager);
		viewPager.setAdapter(regionsPagerAdapter);
	}

	@Override
	public Loader<ArrayList<Region>> onCreateLoader(int id, Bundle args) {
		Region region = new Region();
		if (args != null) {
			this.regions = (ArrayList<Region>) args.getSerializable("regions");
			this.sortByDistance = args.getBoolean("sortByDistance");
		}
		return new AsyncJsonLoader<>(getActivity(), PBMUtil.regionlessBase + "regions.json", region);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Region>> loader, ArrayList<Region> data) {
		updateLocation(getActivity().getSharedPreferences(PBMUtil.PREFS_NAME, 0));
		PBMApplication pbm = (PBMApplication) getActivity().getApplication();
		if (data != null) {
//			pbm.setRegions(new HashMap<Integer, Region>(data.size()));
			for (Region region : data) {
				pbm.addRegion(region.id, region);
			}
		}
		ArrayList<Region> regionValues = new ArrayList<Region>(pbm.getRegionValues());

			Collections.sort(regionValues, new Comparator<Region>() {
				@Override
				public int compare(Region lhs, Region rhs) {
					return lhs.formalName.compareTo(rhs.formalName);
				}
			});
		((RegionsFragment) this.regionsPagerAdapter.getItem(0)).setRegionValues(regionValues);
		((RegionsFragment) this.regionsPagerAdapter.getItem(1)).setRegionValues(regionValues);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Region>> loader) {

	}


	private void updateLocation(final SharedPreferences settings) {
		boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNet = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		String locationProvider = null;
		if (isNet) {
			locationProvider = LocationManager.NETWORK_PROVIDER;
		} else if (isGPS) {
			locationProvider = LocationManager.GPS_PROVIDER;
		}
		Log.d("com.pbm", "provider is " + locationProvider + " gps is " + isGPS + " net is " + isNet);

		locationManager.requestLocationUpdates(locationProvider, 1000 * 60, 100, new android.location.LocationListener() {
			@Override
			public void onLocationChanged(android.location.Location location) {
				SharedPreferences.Editor editor = settings.edit();
				Log.d("com.pbm", "lat: " + location.getLongitude() + " long: " + location.getLatitude() + " acc: " + location.getAccuracy());
				editor.putFloat("yourLat", (float) location.getLatitude());
				editor.putFloat("yourLon", (float) location.getLongitude());
				editor.commit();
				((RegionsFragment) regionsPagerAdapter.getItem(1)).updateLocation(); // XXX hack
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}
		});
		android.location.Location location = locationManager.getLastKnownLocation(locationProvider);
		if (location != null) {
			SharedPreferences.Editor editor = settings.edit();
			Log.d("com.pbm", "lat: " + location.getLongitude() + " long: " + location.getLatitude() + " acc: " + location.getAccuracy());
			editor.putFloat("yourLat", (float) location.getLatitude());
			editor.putFloat("yourLon", (float) location.getLongitude());
			editor.commit();
		}
	}

	public static class RegionsPagerAdapter extends FragmentPagerAdapter {

		private final RegionsFragment alphaRegions;
		private final RegionsFragment geoRegions;
		private final Bundle alphaBundle;
		private final Bundle geoBundle;

		public RegionsPagerAdapter(FragmentManager fragmentManager) {
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
		private ArrayList<Region> regionValues = new ArrayList<Region>();
		private Activity activity;
		ArrayAdapter<Region> adapter;

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			if (savedInstanceState != null) {
				sortByDistance = savedInstanceState.getBoolean("sortByDistance");
				regionValues = (ArrayList<Region>) savedInstanceState.getSerializable("regions");
			} else {
				if (getArguments() != null) {
					sortByDistance = getArguments().getBoolean("sortByDistance");
//					regionValues = (ArrayList<Region>) savedInstanceState.getSerializable("regions");
				}
			}
			setEmptyText(this.getString(R.string.loading_regions));
			setListShown(true);
			adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, regionValues);
			setListAdapter(adapter);
			updateLocation();
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			this.activity = activity;
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
			SharedPreferences settings = activity.getSharedPreferences(PBMUtil.PREFS_NAME, 0);
			float yourLat = settings.getFloat("yourLat", -1);
			float yourLon = settings.getFloat("yourLon", -1);

			if (yourLat != -1 && yourLon != -1) {
				android.location.Location yourLocation = new android.location.Location("");
				yourLocation.setLatitude(yourLat);
				yourLocation.setLongitude(yourLon);
				for (Region region : regionValues) {
					float distance = yourLocation.distanceTo(region.toAndroidLocation());
					distance = distance * PBMUtil.METERS_TO_MILES;

					region.setDistance(distance);
				}
				adapter.sort(new Comparator<Region>() {
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
			}
		}


		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			Region region = (Region) l.getItemAtPosition(position);
			if (!(region.name.equals(""))) {
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
	}

}