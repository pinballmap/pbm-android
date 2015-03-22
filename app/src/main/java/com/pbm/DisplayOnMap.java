package com.pbm;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class DisplayOnMap extends PBMUtil {
	private GoogleMap map;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private Activity activity;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_on_map);

		activity = this;

		showDialog(PBMUtil.PROGRESS_DIALOG);

		logAnalyticsHit("com.pbm.DisplayOnMap");

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			public void onInfoWindowClick(Marker marker) {
				Intent myIntent = new Intent();

				PBMApplication app = (PBMApplication) getApplication();
				Location location = app.getLocationByName(marker.getTitle());

				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});
	}

	private Handler waitHandler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");

			if (total >= 100) {
				Location[] formattedLocations = (Location[]) msg.getData().getSerializable("formattedLocations");
				for (Location formattedLocation : formattedLocations) {
					LatLng position = new LatLng(Float.valueOf(formattedLocation.lat), Float.valueOf(formattedLocation.lon));
					Marker marker = map.addMarker(new MarkerOptions().title(formattedLocation.name).snippet(formattedLocation.numMachines(activity) + " machines").position(position));

					markers.add(marker);
				}

				if (markers.size() > 0) {
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					for (Marker marker : markers) {
						builder.include(marker.getPosition());
					}
					LatLngBounds bounds = builder.build();

					int width = getResources().getDisplayMetrics().widthPixels;
					int height = getResources().getDisplayMetrics().heightPixels;

					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 5);
					map.animateCamera(cu);
				}

				try {
					dismissDialog(PBMUtil.PROGRESS_DIALOG);
				} catch (java.lang.IllegalArgumentException iae) {
					iae.printStackTrace();
				}
			}
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case PBMUtil.QUIT_RESULT:
				setResult(PBMUtil.QUIT_RESULT);
				super.finish();
				this.finish();

				break;
			case PBMUtil.RESET_RESULT:
				setResult(PBMUtil.RESET_RESULT);
				super.finish();
				this.finish();

				break;
			default:
				break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		ProgressDialog progressDialog;
		ProgressThread progressThread;

		switch (id) {
			case PBMUtil.PROGRESS_DIALOG:
				progressDialog = new ProgressDialog(this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("Loading...");
				progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);
				progressThread = new ProgressThread(waitHandler);
				progressThread.start();

				return progressDialog;
			default:
				return null;
		}
	}

	private class ProgressThread extends Thread {
		final Handler handler;

		ProgressThread(Handler h) {
			handler = h;
		}

		public void run() {
			Serializable serializedLocations = getIntent().getSerializableExtra("Locations");
			Object[] locations = (Object[]) serializedLocations;
			Location[] formattedLocations = new Location[locations.length];

			for (int i = 0; i < locations.length; i++) {
				Location location = (Location) locations[i];

				formattedLocations[i] = location;
			}

			Message msg = handler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putSerializable("formattedLocations", formattedLocations);
			bundle.putInt("total", 100);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
}