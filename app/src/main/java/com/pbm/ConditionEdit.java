package com.pbm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
					lmx.setCondition(ConditionEdit.this, condition);
					PBMApplication app = getPBMApplication();

					new RetrieveJsonTask(ConditionEdit.this).execute(
						app.requestWithAuthDetails(regionlessBase + "location_machine_xrefs/" + lmx.id + ".json?condition=" + URLEncoder.encode(condition, "UTF8")), "PUT"
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
		final JSONObject jsonObject = new JSONObject(results);
		if (jsonObject.has("location_machine")) {
			JSONObject jsonLmx = jsonObject.getJSONObject("location_machine");
			int id = jsonLmx.getInt("id");
			int locationID = jsonLmx.getInt("location_id");
			int machineID = jsonLmx.getInt("machine_id");
			getPBMApplication().loadConditions(jsonLmx, id, locationID, machineID);
		}
// {"location_machine":{"id":18524,"created_at":"2015-04-08T23:55:25.440Z",
// "updated_at":"2015-05-19T22:16:48.074Z","location_id":2719,"machine_id":1164,
// "condition":"log this","condition_date":"2015-05-19","ip":null,"user_id":null,
// "machine_score_xrefs_count":null,
// "machine_conditions":[{"id":26,"comment":"dis condition doe","location_machine_xref_id":18524,"created_at":"2015-05-15T06:13:53.288Z","updated_at":"2015-05-15T06:13:53.288Z"},
// {"id":27,"comment":"dat condition needs to go","location_machine_xref_id":18524,"created_at":"2015-05-15T06:16:05.670Z","updated_at":"2015-05-15T06:16:05.670Z"},
// {"id":28,"comment":"log this","location_machine_xref_id":18524,"created_at":"2015-05-19T22:16:48.082Z","updated_at":"2015-05-19T22:16:48.082Z"}]}}

	}
}