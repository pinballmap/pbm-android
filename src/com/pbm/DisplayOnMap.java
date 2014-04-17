package com.pbm;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
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

@SuppressLint("HandlerLeak")
public class DisplayOnMap extends PBMUtil {
	private GoogleMap map;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private Location[] formattedLocations;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_on_map);

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

	final Handler waitHandler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");

			if (total >= 100){
				formattedLocations = (Location[]) msg.getData().getSerializable("formattedLocations");
				for(int i = 0; i < formattedLocations.length; i++) {
					LatLng position = new LatLng(Float.valueOf(formattedLocations[i].lat), Float.valueOf(formattedLocations[i].lon));
					Marker marker = map.addMarker(new MarkerOptions().title(formattedLocations[i].name).snippet(formattedLocations[i].numMachines + " machines").position(position));
					
					markers.add(marker);
				}

				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (Marker marker : markers) {
					builder.include(marker.getPosition());
				}
				LatLngBounds bounds = builder.build();
										
				int width = getResources().getDisplayMetrics().widthPixels;
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, width, 5);
				map.animateCamera(cu);
				
				try {
					dismissDialog(PBMUtil.PROGRESS_DIALOG);
				} catch (java.lang.IllegalArgumentException iae) {}
			}
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode) {
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
		
		switch(id) {
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
		Handler handler;

		ProgressThread(Handler h) {
			handler = h;
		}

		public void run() {
			Serializable serializedLocations = getIntent().getSerializableExtra("Locations");
			Object[] locations = (Object[]) serializedLocations;
			PBMApplication app = (PBMApplication) getApplication();
			
			Location[] formattedLocations = new Location[locations.length];
			for(int i = 0; i < locations.length; i++) {
				Location location = (Location) locations[i];
				
				if (location.street1 == null) {
					try {
						location = PBMUtil.updateLocationData(app.getLocation(location.locationNo));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				
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