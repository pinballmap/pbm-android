package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class DisplayOnMap extends PinballMapActivity implements OnMapReadyCallback {
	private ArrayList<Marker> markers = new ArrayList<>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_on_map);
		logAnalyticsHit("com.pbm.DisplayOnMap");
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		googleMap.setMyLocationEnabled(true);

		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			public void onInfoWindowClick(Marker marker) {
				Intent myIntent = new Intent();

				PBMApplication app = getPBMApplication();
				Location location = app.getLocationByName(marker.getTitle());

				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});
		Serializable serializedLocations = getIntent().getSerializableExtra("Locations");
		Object[] locations = (Object[]) serializedLocations;
		for (int i = 0; i < locations.length; i++) {
			Location location = (Location) locations[i];
			LatLng position = new LatLng(Float.valueOf(location.lat), Float.valueOf(location.lon));
			Marker marker = googleMap.addMarker(new MarkerOptions().title(location.name)
					.snippet(location.numMachines(this) + " machines").position(position));
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
			googleMap.animateCamera(cu);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case PinballMapActivity.QUIT_RESULT:
				setResult(PinballMapActivity.QUIT_RESULT);
				super.finish();
				this.finish();

				break;
			case PinballMapActivity.RESET_RESULT:
				setResult(PinballMapActivity.RESET_RESULT);
				super.finish();
				this.finish();

				break;
			default:
				break;
		}
	}


}