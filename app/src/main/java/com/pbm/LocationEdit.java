package com.pbm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class LocationEdit extends PinballMapActivity implements OnTaskCompleted {
	private Location location;
	private EditText phone, website, description;
	private TreeMap<String, Integer> operators, locationTypes;
	private String[] operatorNames, locationTypeNames;
	private Integer[] operatorIDs, locationTypeIDs;
	private Spinner locationTypeSpinner, operatorSpinner;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_edit);

		logAnalyticsHit("com.pbm.LocationEdit");

		location = (Location) getIntent().getExtras().get("Location");
		if (location != null) {
			setTitle("Edit Data At " + location.getName());
		}

		initializeLocationTypes();
		if (!getPBMApplication().getOperators().isEmpty()) {
			initializeOperators();
		}

		phone = (EditText) findViewById(R.id.phoneNumber);
		website = (EditText) findViewById(R.id.editWebsite);
		description = (EditText) findViewById(R.id.editDescription);

		loadLocationEditData();
	}

	public void initializeOperators() {
		operators = new TreeMap<>();

		Operator blankOperator = Operator.blankOperator();
		operators.put(blankOperator.getName(), blankOperator.getId());
		for (Object element : getPBMApplication().getOperators().values()) {
			Operator operator = (Operator) element;

			operators.put(operator.getName(), operator.getId());
		}

		operatorNames = operators.keySet().toArray(new String[operators.size()]);
		operatorIDs = operators.values().toArray(new Integer[operators.size()]);
	}

	public void initializeLocationTypes() {
		locationTypes = new TreeMap<>();

		LocationType blankLocationType = LocationType.blankLocationType();
		locationTypes.put(blankLocationType.getName(), blankLocationType.getId());
		for (Object element : getPBMApplication().getLocationTypes().values()) {
			LocationType locationType = (LocationType) element;

			locationTypes.put(locationType.getName(), locationType.getId());
		}

		locationTypeNames = locationTypes.keySet().toArray(new String[locationTypes.size()]);
		locationTypeIDs = locationTypes.values().toArray(new Integer[locationTypes.size()]);
	}

	private void loadLocationEditData() {
		new Thread(new Runnable() {
			public void run() {
			LocationEdit.super.runOnUiThread(new Runnable() {
				public void run() {
				phone.setText((location.getPhone().equals("null")) ? "" : location.getPhone());
				website.setText((location.getWebsite().equals("null")) ? "" : location.getWebsite());
				description.setText((location.getDescription().equals(("null")) ? "" : location.getDescription()));

				locationTypeSpinner = (Spinner) findViewById(R.id.locationTypeSpinner);
				ArrayAdapter<String> locationTypeAdapter = new ArrayAdapter<>(
					LocationEdit.this,
					android.R.layout.simple_spinner_item,
					locationTypeNames
				);
				locationTypeSpinner.setAdapter(locationTypeAdapter);

				if (location.getLocationTypeID() != 0) {
					int locationTypeIndex = Arrays.asList(locationTypeIDs).indexOf(location.getLocationTypeID());
					locationTypeSpinner.setSelection(locationTypeIndex);
				}

				if (!getPBMApplication().getOperators().isEmpty()) {
					operatorSpinner = (Spinner) findViewById(R.id.operatorSpinner);
					ArrayAdapter<String> operatorAdapter = new ArrayAdapter<>(
							LocationEdit.this,
							android.R.layout.simple_spinner_item,
							operatorNames
					);
					operatorSpinner.setAdapter(operatorAdapter);

					if (location.getOperatorID() != 0) {
						int operatorIndex = Arrays.asList(operatorIDs).indexOf(location.getOperatorID());
						operatorSpinner.setSelection(operatorIndex);
					}
				} else {
					operatorSpinner = (Spinner) findViewById(R.id.operatorSpinner);
					operatorSpinner.setVisibility(View.GONE);
					View operatorText = findViewById(R.id.enterOperator);
					operatorText.setVisibility(View.GONE);
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
				String locationTypeName = (String) locationTypeSpinner.getSelectedItem();
				String phoneNumber = URLEncoder.encode(phone.getText().toString(), "UTF-8");
				String locationDescription = URLEncoder.encode(description.getText().toString(), "utf-8");
				String locationWebsite = website.getText().toString();
				int locationTypeID = locationTypes.get(locationTypeName);

				String locationTypeString = "";
				if (locationTypeID != 0) {
					locationTypeString = Integer.toString(locationTypeID);
				}

				String operatorString = "";
				if (!getPBMApplication().getOperators().isEmpty()) {
					String operatorName = (String) operatorSpinner.getSelectedItem();
					int operatorID = operators.get(operatorName);
					if (operatorID != 0) {
						operatorString = Integer.toString(operatorID);
					}
				}

				PBMApplication app = getPBMApplication();

				new RetrieveJsonTask(LocationEdit.this).execute(
					app.requestWithAuthDetails(regionlessBase + "locations/" + location.getId() + ".json?phone=" + phoneNumber + ";location_type=" + locationTypeString + ";operator_id=" + operatorString + ";website=" + URLDecoder.decode(locationWebsite, "UTF-8") + ";description=" + locationDescription),
					"PUT"
				).get();
			} catch (InterruptedException | ExecutionException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			}
		}).start();
	}

	public void clickHandler(View view) {
		switch (view.getId()) {
			case R.id.submitLocationEdit:
				updateLocation();

				break;
			default:
				break;
		}
	}

	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		PBMApplication app = getPBMApplication();

		final JSONObject jsonObject = new JSONObject(results);

		if (jsonObject.has("location")) {
			JSONObject jsonLocation = jsonObject.getJSONObject("location");

			location.setPhone(jsonLocation.getString("phone"));
			location.setWebsite(jsonLocation.getString("website"));
			location.setDescription(jsonLocation.getString("description"));

			if (!jsonLocation.getString("location_type_id").equals("null")) {
				location.setLocationTypeID(jsonLocation.getInt("location_type_id"));
			} else {
				location.setLocationTypeID(LocationType.blankLocationType().getId());
			}

			if (!jsonLocation.getString("operator_id").equals("null")) {
				location.setOperatorID(jsonLocation.getInt("operator_id"));
			} else {
				location.setOperatorID(Operator.blankOperator().getId());
			}

			SharedPreferences settings = this.getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
			location.setDateLastUpdated(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
			location.setLastUpdatedByUsername(settings.getString("username", ""));
			app.updateLocation(location);

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
				} catch (JSONException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();

				loadLocationEditData();
				}
			});
		}
	}
}