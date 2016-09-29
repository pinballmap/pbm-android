package com.pbm;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class SuggestLocation extends PinballMapActivity {
	public void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_location);
		TextView suggestLocationText = (TextView) findViewById(R.id.submitLocationId);
		suggestLocationText.setText("Submit A Location To The " + getPBMApplication().getRegion().formalName + " Map");
		logAnalyticsHit("com.pbm.SuggestLocation");

		PBMApplication app = getPBMApplication();
		String[] machineNames = app.getMachineNamesWithMetadata();
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_item_1, machineNames);
		MultiAutoCompleteTextView autoCompleteMachinesTextView = (MultiAutoCompleteTextView) findViewById(R.id.autoCompleteMachinesTextView);
		autoCompleteMachinesTextView.setAdapter(adapter);
		autoCompleteMachinesTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
	}

	public void buttonOnClick(View view) throws UnsupportedEncodingException {
		String locationName = ((EditText) findViewById(R.id.nameField)).getText().toString();
		String machineNames = ((MultiAutoCompleteTextView) findViewById(R.id.autoCompleteMachinesTextView)).getText().toString();

		if (!locationName.isEmpty() && !machineNames.isEmpty()) {
			PBMApplication app = getPBMApplication();
			Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));

			String url = regionlessBase + "locations/suggest.json?region_id=" + region.id
					+ ";location_name=" + URLEncoder.encode(locationName, "UTF-8")
					+ ";location_street=" + URLEncoder.encode(((EditText) findViewById(R.id.streetField)).getText().toString(), "UTF-8")
					+ ";location_city=" + URLEncoder.encode(((EditText) findViewById(R.id.cityField)).getText().toString(), "UTF-8")
					+ ";location_state=" + URLEncoder.encode(((EditText) findViewById(R.id.stateField)).getText().toString(), "UTF-8")
					+ ";location_zip=" + URLEncoder.encode(((EditText) findViewById(R.id.zipField)).getText().toString(), "UTF-8")
					+ ";location_phone=" + URLEncoder.encode(((EditText) findViewById(R.id.phoneField)).getText().toString(), "UTF-8")
					+ ";location_website=" + URLEncoder.encode(((EditText) findViewById(R.id.websiteField)).getText().toString(), "UTF-8")
					+ ";location_operator=" + URLEncoder.encode(((EditText) findViewById(R.id.operatorField)).getText().toString(), "UTF-8")
					+ ";location_machines=" + URLEncoder.encode(machineNames, "UTF-8");
			try {
				new RetrieveJsonTask().execute(app.requestWithAuthDetails(url), "POST").get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			Toast.makeText(getBaseContext(), "Thank you for that submission! A region administrator will enter this location into the database shortly.", Toast.LENGTH_LONG).show();
			setResult(REFRESH_RESULT);
			SuggestLocation.this.finish();
		} else {
			Toast.makeText(getBaseContext(), "Location name and a list of machines are required fields.", Toast.LENGTH_LONG).show();
		}
	}
}

