package com.pbm;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LocationEdit extends PBMUtil {
	private Location location;
	EditText phone;
	Spinner dropdown;
	String[] locationTypeNames;
	Integer[] locationTypeIDs;
	TreeMap<String, Integer> locationTypes;

	public void onCreate(Bundle savedInstanceState) {
		PBMApplication app = (PBMApplication) getApplication();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_edit);
		
		logAnalyticsHit("com.pbm.LocationEdit");

		location = (Location) getIntent().getExtras().get("Location");
		
		locationTypes = new TreeMap <String, Integer>();
		
		for (Object element : app.getLocationTypes().values()) {
			LocationType locationType = (LocationType) element;

			locationTypes.put(locationType.name, locationType.id);
		}
		
		locationTypeNames = locationTypes.keySet().toArray(new String[locationTypes.size()]);
		locationTypeIDs = locationTypes.values().toArray(new Integer[locationTypes.size()]);
		phone = (EditText)findViewById(R.id.phoneNumber);

		new Thread(new Runnable() {
	        public void run() {
	        	LocationEdit.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TextView title = (TextView)findViewById(R.id.locationEditTitle);
						title.setText("Edit Data At " + location.name);

						phone.setText(location.phone);

						dropdown = (Spinner)findViewById(R.id.locationTypeSpinner);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							LocationEdit.this,
							android.R.layout.simple_spinner_item,
							locationTypeNames
						);
						dropdown.setAdapter(adapter);
						
						if (location.locationTypeID != 0) {
							int locationTypeIndex = Arrays.asList(locationTypeIDs).indexOf(location.locationTypeID);
							dropdown.setSelection(locationTypeIndex);
						}
					}
	        	});
	        }
	    }).start();
	}

	private void updateLocation() {
		new Thread(new Runnable() {
	        public void run() {
	        	try {
	        		String locationTypeName = (String) dropdown.getSelectedItem();
	        		int locationTypeID = locationTypes.get(locationTypeName);

	        		String phoneNumber = phone.getText().toString();

	        		location.setPhone(phoneNumber);
	        		location.setLocationTypeID(locationTypeID);
	        			
	        		PBMApplication app = (PBMApplication) getApplication();
	        		app.setLocation(location.id, location);

	        		new RetrieveJsonTask().execute(
	        			regionlessBase + "locations/" + location.id + ".json?phone=" + phoneNumber + ";location_type=" + Integer.toString(locationTypeID),
	        			"PUT"
	        		).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

	        	LocationEdit.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "Thanks for updating that location!", Toast.LENGTH_LONG).show();
						
						setResult(REFRESH_RESULT);
						LocationEdit.this.finish();
					}
	        	});
	        }
	    }).start();
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
			case R.id.submitLocationEdit :
				updateLocation();
				
				break;
			default:
				break;
		}
	}
}