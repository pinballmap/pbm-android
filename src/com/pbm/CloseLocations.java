package com.pbm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.location.Location;

@SuppressLint("HandlerLeak")
public class CloseLocations extends FragmentActivity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private ListView table;
	private android.location.Location yourLocation;
	
	private LocationClient locationClient;
	private List<com.pbm.Location> locationsForMap;
	private static final int maxMilesFromYourLocation = 10;
	private static final int maxNumMachinesToDisplayOnMap = 25;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.close_locations);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.map_titlebar);
		
		Tracker tracker = ((PBMApplication) getApplication()).getTracker();
        tracker.setScreenName("com.pbm.CloseLocations");
        tracker.send(new HitBuilders.AppViewBuilder().build());

		TextView title = (TextView)findViewById(R.id.title);
		title.setText("Close locations");

		table = (ListView)findViewById(R.id.closeLocationsTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);
		
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient = new LocationClient(this, this, this);
        } else {
			Toast.makeText(getBaseContext(), "I couldn't get a fix on your position. Try again, please.", Toast.LENGTH_LONG).show();
        }
	}   

	private List<com.pbm.Location> sortLocations(List<com.pbm.Location> locations) {
		Collections.sort(locations, new Comparator<com.pbm.Location>() {
			public int compare(com.pbm.Location l1, com.pbm.Location l2) {
				Float distanceFromYou1 = Float.valueOf(l1.distanceFromYou);
				Float distanceFromYou2 = Float.valueOf(l2.distanceFromYou);

				return distanceFromYou1.compareTo(distanceFromYou2);
			}
		});

		return locations;
	}
	
	public void showTable(List<com.pbm.Location> locations) {
		table.setAdapter(new ClosestLocationsAdapter(this));
	}

	public void onStart() {
		super.onStart();
		
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.connect();
        }
	}

	public void onStop() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onStop();
	}

	public void onPause() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onPause();
	}

	public void onDestroy() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onDestroy();
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.mapButton :
			if ((locationsForMap == null) || (locationsForMap.size() == 0)) {
				return;
			}

			int numLocationsToDisplay = locationsForMap.size();
			if (numLocationsToDisplay > maxNumMachinesToDisplayOnMap) {
				numLocationsToDisplay = maxNumMachinesToDisplayOnMap - 1;
			}

			Intent myIntent = new Intent();
			myIntent.putExtra("Locations", sortLocations(locationsForMap).subList(0, numLocationsToDisplay).toArray());
			myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
			startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);

			break;
		}
	}

	public void onProviderDisabled(String arg0) {}
	public void onProviderEnabled(String arg0) {}
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}	

	private class ClosestLocationsAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ClosestLocationsAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return locationsForMap.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.close_locations_listview, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(locationsForMap.get(position).name);
			holder.distance.setText(locationsForMap.get(position).milesInfo);

			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView distance;
		}
	}

	public void onConnected(Bundle arg0) {
		yourLocation = locationClient.getLastLocation();
		locationsForMap = new ArrayList<com.pbm.Location>();

		PBMApplication app = (PBMApplication) getApplication();
		for (int i = 0; i < app.getLocationValues().length; i++) {
			com.pbm.Location location = (com.pbm.Location) app.getLocationValues()[i];

			float distance = yourLocation.distanceTo(location.toAndroidLocation()); 
			distance = (float) (distance * PBMUtil.METERS_TO_MILES);	

			if (distance < maxMilesFromYourLocation) {
				NumberFormat formatter = new DecimalFormat(".00");
				location.setMilesInfo(formatter.format(distance) + " miles");
				location.setDistance(distance);

				locationsForMap.add(location);
			}
		}

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {
				Intent myIntent = new Intent();						
				com.pbm.Location location = locationsForMap.get(position);

				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);
			}
		});

		showTable(sortLocations(locationsForMap));
	}

	public void onDisconnected() { }
	public void onLocationChanged(Location arg0) { }
	public void onConnectionFailed(ConnectionResult arg0) { }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(PBMUtil.MENU_PREFS, PBMUtil.MENU_PREFS, PBMUtil.MENU_PREFS, "Preferences");
		menu.add(PBMUtil.MENU_ABOUT, PBMUtil.MENU_ABOUT, PBMUtil.MENU_ABOUT, "About");
		menu.add(PBMUtil.MENU_QUIT, PBMUtil.MENU_QUIT, PBMUtil.MENU_QUIT, "Quit");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case PBMUtil.MENU_PREFS:
			Intent myIntent = new Intent();
			myIntent.setClassName("com.pbm", "com.pbm.Preferences");
			startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);

			return true;
		case PBMUtil.MENU_ABOUT:
			Intent aboutIntent = new Intent();
			aboutIntent.setClassName("com.pbm", "com.pbm.About");
			startActivityForResult(aboutIntent, PBMUtil.QUIT_RESULT);

			return true;
		case PBMUtil.MENU_QUIT:
			setResult(PBMUtil.QUIT_RESULT);
			super.finish();
			this.finish();  

			return true;
		}
		return false;
	}
}
