package com.pbm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LocationEdit extends PBMUtil implements OnTaskCompleted {
	private Location location;
	EditText phone;
	EditText website;
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
		setTitle("Edit Data At " + location.name);
		
		locationTypes = new TreeMap <String, Integer>();
		
		for (Object element : app.getLocationTypes().values()) {
			LocationType locationType = (LocationType) element;

			locationTypes.put(locationType.name, locationType.id);
		}
		
		locationTypeNames = locationTypes.keySet().toArray(new String[locationTypes.size()]);
		locationTypeIDs = locationTypes.values().toArray(new Integer[locationTypes.size()]);
		phone = (EditText)findViewById(R.id.phoneNumber);
		website = (EditText)findViewById(R.id.editWebsite);
		
		loadLocationEditData();
	}
	
	private void loadLocationEditData() {
		new Thread(new Runnable() {
	        public void run() {
	        	LocationEdit.super.runOnUiThread(new Runnable() {
					public void run() {
						phone.setText((location.phone.equals("null")) ? "" : location.phone);
						website.setText((location.website.equals("null")) ? "" : location.website);

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
	        		String phoneNumber = URLEncoder.encode(phone.getText().toString(), "UTF-8");
	        		String locationWebsite = website.getText().toString();

	        		new RetrieveJsonTask(LocationEdit.this).execute(
	        			regionlessBase + "locations/" + location.id + ".json?phone=" + phoneNumber + ";location_type=" + Integer.toString(locationTypeID) + ";website=" + URLDecoder.decode(locationWebsite, "UTF-8"),
	        			"PUT"
	        		).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
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

	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		PBMApplication app = (PBMApplication) getApplication();
		
		final JSONObject jsonObject = new JSONObject(results);
		
		if (jsonObject.has("location")) {
			JSONObject jsonLocation = jsonObject.getJSONObject("location");

	        location.setPhone(jsonLocation.getString("phone"));
	        location.setLocationTypeID(jsonLocation.getInt("location_type_id"));
	        location.setWebsite(jsonLocation.getString("website"));
	        	
	        app.setLocation(location.id, location);

	        LocationEdit.super.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getBaseContext(), "Thanks for updating that location!", Toast.LENGTH_LONG).show();
					
					setResult(REFRESH_RESULT);
					LocationEdit.this.finish();
				}
	        });
			
			return;
		}
		
		if (jsonObject.has("errors")) {
	        LocationEdit.super.runOnUiThread(new Runnable() {
				public void run() {
					String error = null;
					try {
						error = URLDecoder.decode(jsonObject.getString("errors"), "UTF-8");
						error = error.replace("\\/", "/");
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
					
					loadLocationEditData();
				}
	        });
			
			return;
		}
	}
}