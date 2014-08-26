package com.pbm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class AddMachine extends PBMUtil implements OnTaskCompleted {	
	private Location location;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_machine);
		
		logAnalyticsHit("com.pbm.AddMachine");

		location = (Location) getIntent().getExtras().get("Location");

		ListView table = (ListView)findViewById(R.id.addMachineTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);

		PBMApplication app = (PBMApplication) getApplication();
		String[] machineNames = app.getMachineNames();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, machineNames);
		AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.manualNewMachine);
		actv.setAdapter(adapter);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parentView, View selectedView, final int position, long id) {	
		
				new Thread(new Runnable() {
					public void run() {
						Machine machine = (Machine) parentView.getItemAtPosition(position);
						machine.setExistsInRegion(true);

						try {
							new RetrieveJsonTask().execute(
								regionlessBase + "location_machine_xrefs.json?location_id=" + location.id + ";machine_id=" + machine.id,
								"POST"
							).get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						AddMachine.super.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getBaseContext(), "Thanks for adding that machine!", Toast.LENGTH_LONG).show();
								setResult(REFRESH_RESULT);
								AddMachine.this.finish();
							}
						});
					}
				}).start();
			}
		});

		table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getMachineValues(true)));
	}   

	public void submitHandler(View view) {		
		final String manualMachineName = ((AutoCompleteTextView) findViewById(R.id.manualNewMachine)).getText().toString();

		if (manualMachineName.length() > 0) {
			new Thread(new Runnable() {
				public void run() {
					try {
						int machineID = getMachineIDFromMachineName(manualMachineName);
						if (machineID != -1) {
							new RetrieveJsonTask().execute(
								regionlessBase + "location_machine_xrefs.json?location_id=" + location.id + ";machine_id=" + machineID,
								"POST"
							).get();
						} else {
							new RetrieveJsonTask(AddMachine.this).execute(
								regionlessBase + "machines.json?machine_name=" + URLEncoder.encode(manualMachineName, "UTF8") + ";location_id=" + location.id,
								"POST"
							).get();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					AddMachine.super.runOnUiThread(new Runnable() {
						public void run() {
							setResult(REFRESH_RESULT);
							AddMachine.this.finish();
						}
					});
				}
			}).start();
		}
	}
	
	private int getMachineIDFromMachineName(String name) throws InterruptedException, ExecutionException, JSONException {
		PBMApplication app = (PBMApplication) getApplication();
		int machineID = -1;

		Machine machine = app.getMachineByName(name);
		if (machine != null) {
			machine.setExistsInRegion(true);
			
			machineID = machine.id;
		}
		
		return machineID;
	}

	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		PBMApplication app = (PBMApplication) getApplication();

		JSONObject jsonObject = new JSONObject(results);
		JSONObject jsonMachine = jsonObject.getJSONObject("machine");

		app.addMachine(jsonMachine.getInt("id"), new Machine(jsonMachine.getInt("id"), jsonMachine.getString("name"), null, null, true));

		new RetrieveJsonTask().execute(
			regionlessBase + "location_machine_xrefs.json?location_id=" + location.id + ";machine_id=" + jsonMachine.getString("id"),
			"POST"
		).get();
	}
}