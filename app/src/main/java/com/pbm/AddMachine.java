package com.pbm;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AddMachine extends PinballMapActivity implements OnTaskCompleted {
	private Location location;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_machine);
		
		logAnalyticsHit("com.pbm.AddMachine");

		location = (Location) getIntent().getExtras().get("Location");
		
		setTitle(location.name);

		ListView table = (ListView)findViewById(R.id.addMachineTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);

		PBMApplication app = getPBMApplication();
		String[] machineNames = app.getMachineNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, machineNames);
		AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.manualNewMachine);
		actv.setAdapter(adapter);
		final ArrayList<Machine> allMachines = app.getMachineValues(true);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parentView, View selectedView, final int position, long id) {	
		
				new Thread(new Runnable() {
					public void run() {
						Machine machine = allMachines.get(position);
						machine.setExistsInRegion(true);

						try {
							new RetrieveJsonTask(AddMachine.this).execute(
								regionlessBase + "location_machine_xrefs.json?location_id=" + location.id + ";machine_id=" + machine.id,
								"POST"
							).get();
						} catch (InterruptedException | ExecutionException e) {
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

		table.setAdapter(new MachineListAdapter(this, allMachines, true));
	}   

	public void submitHandler(View view) {		
		final String manualMachineName = ((AutoCompleteTextView) findViewById(R.id.manualNewMachine)).getText().toString();

		if (manualMachineName.length() > 0) {
			new Thread(new Runnable() {
				public void run() {
					try {
						int machineID = getMachineIDFromMachineName(manualMachineName);
						if (machineID != -1) {
							new RetrieveJsonTask(AddMachine.this).execute(
								regionlessBase + "location_machine_xrefs.json?location_id=" + location.id + ";machine_id=" + machineID,
								"POST"
							).get();
						} else {
							new RetrieveJsonTask(AddMachine.this).execute(
								regionlessBase + "machines.json?machine_name=" + URLEncoder.encode(manualMachineName, "UTF8") + ";location_id=" + location.id,
								"POST"
							).get();
						}
					} catch (InterruptedException | ExecutionException | UnsupportedEncodingException | JSONException e) {
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
		PBMApplication app = getPBMApplication();
		int machineID = -1;

		Machine machine = app.getMachineByName(name);
		if (machine != null) {
			machine.setExistsInRegion(true);
			
			machineID = machine.id;
		}
		
		return machineID;
	}

	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		PBMApplication app = getPBMApplication();

		JSONObject jsonObject = new JSONObject(results);
		
		if (jsonObject.has("machine")) {
			JSONObject jsonMachine = jsonObject.getJSONObject("machine");
			app.addMachine(jsonMachine.getInt("id"), new Machine(jsonMachine.getInt("id"), jsonMachine.getString("name"), null, null, true));
			
			new RetrieveJsonTask(AddMachine.this).execute(
				regionlessBase + "location_machine_xrefs.json?location_id=" + location.id + ";machine_id=" + jsonMachine.getString("id"), "POST"
			).get();
			
			return;
		}
		
		if (jsonObject.has("location_machine")) {
			JSONObject jsonLmx = jsonObject.getJSONObject("location_machine");
			int id = jsonLmx.getInt("id");
			int locationID = jsonLmx.getInt("location_id");
			int machineID = jsonLmx.getInt("machine_id");

			app.addLocationMachineXref(id, new com.pbm.LocationMachineXref(id, locationID, machineID, "", ""));
			app.loadConditions(jsonLmx, id, locationID, machineID);

			return;
		}
	}
}