package com.pbm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ConditionEdit extends PinballMapActivity implements OnTaskCompleted {
	private LocationMachineXref lmx;
	private InputMethodManager inputMethodManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.condition_edit);

		Bundle extras = getIntent().getExtras();
		lmx = (LocationMachineXref) extras.get("lmx");

		logAnalyticsHit("com.pbm.ConditionEdit");

		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	private void updateCondition(final String condition) {
		new Thread(new Runnable() {
			public void run() {
				try {
					final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);

					lmx.setCondition(ConditionEdit.this, condition, settings.getString("username", ""));
					PBMApplication app = getPBMApplication();

					new RetrieveJsonTask(ConditionEdit.this).execute(
						app.requestWithAuthDetails(regionlessBase + "location_machine_xrefs/" + lmx.getId() + ".json?condition=" + URLEncoder.encode(condition, "UTF8")),
						"PUT"
					).get();
				} catch (InterruptedException | ExecutionException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				ConditionEdit.super.runOnUiThread(new Runnable() {
					public void run() {
					final EditText currText = (EditText) findViewById(R.id.condition);
					inputMethodManager.hideSoftInputFromWindow(currText.getWindowToken(), 0);

					setResult(REFRESH_RESULT);
					ConditionEdit.this.finish();
					}
				});
			}
		}).start();
	}

	public void clickHandler(View view) {
		switch (view.getId()) {
			case R.id.submitCondition:
				EditText currText = (EditText) findViewById(R.id.condition);
				updateCondition(currText.getText().toString());
				break;
			default:
				break;
		}
	}

	@Override
	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		Log.d("com.pbm.condition", results);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(results);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (rootNode.has("location_machine")) {
			getPBMApplication().loadConditions(rootNode.path("location_machine"));
		}

		Location location = lmx.getLocation(this);
		SharedPreferences settings = this.getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
		location.setDateLastUpdated(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
		location.setLastUpdatedByUsername(settings.getString("username", ""));
		getPBMApplication().updateLocation(location);
	}
}