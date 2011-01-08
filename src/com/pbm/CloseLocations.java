package com.pbm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
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

public class CloseLocations extends PBMUtil implements LocationListener {
	private ListView table;
	private ProgressDialog progressDialog;
	private ProgressThread progressThread;
	private android.location.Location yourLocation;
	private LocationManager locationManager;
	private List<com.pbm.Location> locationsForMap;
	private static final int maxMilesFromYourLocation = 10;
	private static final int maxNumMachinesToDisplayOnMap = 25;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.close_locations);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.map_titlebar);

		TextView title = (TextView)findViewById(R.id.title);
		title.setText("Close locations");

		table = (ListView)findViewById(R.id.closeLocationsTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(new Criteria(), true);

		if (provider != null) {
			locationManager.requestLocationUpdates(provider, 0, 0, this);
			showDialog(PROGRESS_DIALOG);	
		} else {
			Toast.makeText(getBaseContext(), "I couldn't get a fix on your position. Try again, please.", Toast.LENGTH_LONG).show();
			activityQuitResult();
		}
	}   

	final Handler waitHandler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");

			if (total >= 100){
				locationsForMap = new ArrayList<com.pbm.Location>();

				PBMApplication app = (PBMApplication) getApplication();
				for (int i = 0; i < app.getLocationValues().length; i++) {
					com.pbm.Location location = (com.pbm.Location) app.getLocationValues()[i];
					android.location.Location mockLocation = new android.location.Location(LocationManager.GPS_PROVIDER);	

					try{
						mockLocation.setLatitude(new Double(location.lat));
						mockLocation.setLongitude(new Double(location.lon));
					} catch (java.lang.NumberFormatException nfe) {
					}

					float distance = yourLocation.distanceTo(mockLocation); 
					distance = (float) (distance * 0.000621371192);	

					if (distance < maxMilesFromYourLocation) {
						NumberFormat formatter = new DecimalFormat(".00");
						location.setMilesInfo(formatter.format(distance) + " miles");
						location.setDistance(distance);

						locationsForMap.add(location);
					}
				}

				table.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings("unchecked")
					public void onItemClick(AdapterView parentView, View selectedView, int position, long id) {
						Intent myIntent = new Intent();						
						com.pbm.Location location = locationsForMap.get(position);

						if (location.street1 == null) {
							PBMApplication app = (PBMApplication) getApplication();
							location = PBMUtil.updateLocationData(app.getLocation(location.locationNo));
						}

						myIntent.putExtra("Location", location);
						myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
						startActivityForResult(myIntent, QUIT_RESULT);
					}
				});

				showTable(sortLocations(locationsForMap));

				try{
					dismissDialog(PROGRESS_DIALOG);
					progressThread.stop();
				} catch (java.lang.IllegalArgumentException iae) {}
			}
		}
	};

	private List<com.pbm.Location> sortLocations(List<com.pbm.Location> locations) {
		Collections.sort(locations, new Comparator<com.pbm.Location>() {
			public int compare(com.pbm.Location l1, com.pbm.Location l2) {
				Float distanceFromYou1 = new Float(l1.distanceFromYou);
				Float distanceFromYou2 = new Float(l2.distanceFromYou);

				return distanceFromYou1.compareTo(distanceFromYou2);
			}
		});

		return locations;
	}

	public void showTable(List<com.pbm.Location> locations) {
		table.setAdapter(new ClosestLocationsAdapter(this));
	}

	public void onStop() {
		super.onStop();
		progressThread.stop();
		locationManager.removeUpdates(this); 
	}

	public void onPause() {
		super.onPause();
		progressThread.stop();
		locationManager.removeUpdates(this); 
	}

	public void onDestroy() {
		super.onDestroy();
		try {
			progressThread.stop();
			locationManager.removeUpdates(this); 
		} catch (NullPointerException npe){}
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
			myIntent.putExtra("YourLocation", yourLocation);
			myIntent.putExtra("Locations", sortLocations(locationsForMap).subList(0, numLocationsToDisplay).toArray());
			myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
			startActivityForResult(myIntent, QUIT_RESULT);

			break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressThread = new ProgressThread(waitHandler);
			progressThread.start();

			return progressDialog;
		default:
			return null;
		}
	}

	public void onLocationChanged(Location location) {
		if (location != null) {
			yourLocation = location;
			locationManager.removeUpdates(this); 
		}
	}

	public void onProviderDisabled(String arg0) {}
	public void onProviderEnabled(String arg0) {}
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}	

	private class ProgressThread extends Thread {
		Handler handler;

		ProgressThread(Handler h) {
			handler = h;
		}

		public void run() {
			while (yourLocation == null) {}

			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", 100);
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}

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
}