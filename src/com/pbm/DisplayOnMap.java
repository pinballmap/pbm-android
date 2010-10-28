package com.pbm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DisplayOnMap extends MapActivity {
	private MapView map;
	private com.pbm.BalloonLayout noteBalloon;
	private Location[] formattedLocations;

	private ProgressDialog progressDialog;
	private volatile ProgressThread progressThread;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displayonmap);

		map = (MapView)findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);

		showDialog(PBMUtil.PROGRESS_DIALOG);
	}

	final Handler waitHandler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");

			if (total >= 100){
				Drawable marker = getResources().getDrawable(R.drawable.pin);

				formattedLocations = (Location[]) msg.getData().getSerializable("formattedLocations");

				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				noteBalloon = (com.pbm.BalloonLayout) layoutInflater.inflate(R.layout.popup, null);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(PBMUtil.convertPixelsToDip(200, getResources().getDisplayMetrics()), PBMUtil.convertPixelsToDip(100, getResources().getDisplayMetrics()));
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				noteBalloon.setVisibility(View.INVISIBLE);
				noteBalloon.setLayoutParams(layoutParams);   

				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
				map.getOverlays().add(new SitesOverlay(marker));
				
				if (getIntent().getExtras().get("YourLocation") != null) {
					map.getOverlays().add(new SitesOverlay(getResources().getDrawable(R.drawable.you_marker), (android.location.Location) getIntent().getExtras().get("YourLocation")));
				}

				try {
					dismissDialog(PBMUtil.PROGRESS_DIALOG);
				} catch (java.lang.IllegalArgumentException iae) {}
			}
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case PBMUtil.PROGRESS_DIALOG:
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

	protected boolean isRouteDisplayed() {
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
					location = PBMUtil.updateLocationData(app.getLocation(location.locationNo));
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

	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;

		public SitesOverlay(Drawable m, android.location.Location location) {
			super(m);
			marker = m;

			GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
			OverlayItem overlayItem = new OverlayItem(point, "foo", "bar");

			items.add(overlayItem);
			map.getController().animateTo(point);
			map.invalidate();

			populate();
		}

		public SitesOverlay(Drawable m) {
			super(m);
			marker = m;

			int minLat = Integer.MAX_VALUE;
			int minLong = Integer.MAX_VALUE;
			int maxLat = Integer.MIN_VALUE;
			int maxLong = Integer.MIN_VALUE;
			for (int i = 0; i < formattedLocations.length; i++) {
				if (formattedLocations[i] != null) {
					String coordinates[] = {formattedLocations[i].lat, formattedLocations[i].lon};

					GeoPoint point = new GeoPoint(convertMapCoordinate(coordinates[0]), convertMapCoordinate(coordinates[1]));
					items.add(new OverlayItem(point, "foo", "bar"));

					minLat  = Math.min(point.getLatitudeE6(), minLat);
					minLong = Math.min(point.getLongitudeE6(), minLong);
					maxLat  = Math.max(point.getLatitudeE6(), maxLat);
					maxLong = Math.max(point.getLongitudeE6(), maxLong);

					map.getController().animateTo(point);
				}
			}	

			map.getController().zoomToSpan(Math.abs( minLat - maxLat ), Math.abs( minLong - maxLong ));
			populate();
		}

		private int convertMapCoordinate(String coordinate) {
			double convertedCoordinate = Double.parseDouble(coordinate);
			return (int) (convertedCoordinate * 1E6);
		}

		protected OverlayItem createItem(int i) {
			return(items.get(i));
		}

		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			boundCenterBottom(marker);
		}

		public boolean onTap(int i) {
			if (i == formattedLocations.length) {
				return true;
			}

			if (noteBalloon.getVisibility() == View.INVISIBLE) {
				Location location = formattedLocations[i];
				String coordinates[] = {location.lat, location.lon};
				double lat = Double.parseDouble(coordinates[0]);
				double lon = Double.parseDouble(coordinates[1]);
				GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));

				map.addView(noteBalloon, new MapView.LayoutParams(PBMUtil.convertPixelsToDip(200, getResources().getDisplayMetrics()), PBMUtil.convertPixelsToDip(200, getResources().getDisplayMetrics()), point, MapView.LayoutParams.BOTTOM_CENTER));
				noteBalloon.setVisibility(View.VISIBLE);
				((TextView) noteBalloon.findViewById(R.id.name)).setText(location.name);
				((TextView) noteBalloon.findViewById(R.id.address)).setText(location.street1 + "\n" + location.city + ", " + location.state + ", " + location.zip);
				((TextView) noteBalloon.findViewById(R.id.phone)).setText(location.phone);

				map.getController().animateTo(point);
			} else {
				noteBalloon.setVisibility(View.INVISIBLE);
				map.removeView(noteBalloon);
			}

			return(true);
		}

		public int size() {
			return(items.size());
		}
	}
}