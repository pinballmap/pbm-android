package com.pbm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class SuggestLocation extends PBMUtil {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.suggest_location);

		logAnalyticsHit("com.pbm.SuggestLocation");
		
		PBMApplication app = (PBMApplication) getApplication();
		String[] machineNames = app.getMachineNames();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, machineNames);
		MultiAutoCompleteTextView mactv = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		mactv.setAdapter(adapter);
		mactv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
	}
	
	public void buttonOnClick(View view) throws UnsupportedEncodingException {
		PBMApplication app = (PBMApplication) getApplication();
		Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));

		String url = regionlessBase + "locations/suggest.json?region_id=" + region.id
				+ ";location_name=" + URLEncoder.encode(((EditText) findViewById(R.id.nameField)).getText().toString(), "UTF-8")
				+ ";location_street=" + URLEncoder.encode(((EditText) findViewById(R.id.streetField)).getText().toString(), "UTF-8")
				+ ";location_city=" + URLEncoder.encode(((EditText) findViewById(R.id.cityField)).getText().toString(), "UTF-8")
				+ ";location_state=" + URLEncoder.encode(((EditText) findViewById(R.id.stateField)).getText().toString(), "UTF-8")
				+ ";location_zip=" + URLEncoder.encode(((EditText) findViewById(R.id.zipField)).getText().toString(), "UTF-8")
				+ ";location_phone=" + URLEncoder.encode(((EditText) findViewById(R.id.phoneField)).getText().toString(), "UTF-8")
				+ ";location_website=" + URLEncoder.encode(((EditText) findViewById(R.id.websiteField)).getText().toString(), "UTF-8")
				+ ";location_operator=" + URLEncoder.encode(((EditText) findViewById(R.id.operatorField)).getText().toString(), "UTF-8")
				+ ";location_machines=" + URLEncoder.encode(((MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1)).getText().toString(), "UTF-8")
		;
		try {
			new RetrieveJsonTask().execute(url, "POST").get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		Toast.makeText(getBaseContext(), "Thank you for that submission! A region administrator will enter this location into the database shortly.", Toast.LENGTH_LONG).show();
		setResult(REFRESH_RESULT);
		SuggestLocation.this.finish();
	}
}

