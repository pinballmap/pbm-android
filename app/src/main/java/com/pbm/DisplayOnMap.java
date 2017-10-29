package com.pbm;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.widget.Toast;

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
import java.util.Arrays;

import static com.pbm.R.id.map;

public class DisplayOnMap extends PinballMapActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
	private ArrayList<Marker> markers = new ArrayList<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_on_map);
		logAnalyticsHit("com.pbm.DisplayOnMap");
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        Serializable serializedLocations = getIntent().getSerializableExtra("Locations");
        Object[] arrayDataObject = (Object[]) serializedLocations;
        Location[] locations = Arrays.copyOf(arrayDataObject, arrayDataObject.length,Location[].class);
        for (Location location : locations) {
            LatLng position = new LatLng(Float.valueOf(location.getLat()), Float.valueOf(location.getLon()));
            Marker marker = mMap.addMarker(new MarkerOptions().title(location.getName())
                .snippet(location.getNumMachines() + " machines").position(position));
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

        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                Intent myIntent = new Intent();
                Location location = getPBMApplication().getLocationByName(marker.getTitle());

                myIntent.putExtra("Location", location);
                myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
                startActivityForResult(myIntent, QUIT_RESULT);
            }
        });

	}

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case PinballMapActivity.QUIT_RESULT:
				setResult(PinballMapActivity.QUIT_RESULT);
				android.os.Process.killProcess(android.os.Process.myPid());

				break;
			case PinballMapActivity.RESET_RESULT:
				setResult(PinballMapActivity.RESET_RESULT);

				android.os.Process.killProcess(android.os.Process.myPid());

				break;
			default:
				break;
		}
	}
}