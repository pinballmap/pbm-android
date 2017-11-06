package com.pbm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AddMachine extends PinballMapActivity implements OnTaskCompleted {
	private Location location;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_machine);
		
		logAnalyticsHit("com.pbm.AddMachine");

		location = (Location) getIntent().getExtras().get("Location");

		if (location != null) {
			setTitle(location.getName());
			initializeAddMachineTable();
			initializeManualNewMachineTextView();
		}

	}

	public void initializeManualNewMachineTextView() {
		String[] machineNames = getPBMApplication().getMachineNamesWithMetadata();
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_list_item_1, machineNames);
		AutoCompleteTextView manualNewMachineTextView = (AutoCompleteTextView) findViewById(R.id.manualNewMachineTextView);
		manualNewMachineTextView.setAdapter(adapter);
	}

	public void initializeAddMachineTable() {
		ListView addMachineTable = (ListView)findViewById(R.id.addMachineTable);
		addMachineTable.setFastScrollEnabled(true);
		addMachineTable.setTextFilterEnabled(true);

		final ArrayList<Machine> allMachines = getPBMApplication().getMachineValues(true);
		addMachineTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parentView, View selectedView, final int position, long id) {
			new Thread(new Runnable() {
				public void run() {
				Machine machine = allMachines.get(position);
				machine.setExistsInRegion(true);

				try {
					new RetrieveJsonTask(AddMachine.this).execute(
						getPBMApplication().requestWithAuthDetails(regionlessBase + "location_machine_xrefs.json?location_id=" + location.getId() + ";machine_id=" + machine.getId()),
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

		addMachineTable.setAdapter(new MachineListAdapter(this, allMachines, true));
	}

	public void submitHandler(View view) {		
		final String manualMachineMetadata = ((AutoCompleteTextView) findViewById(R.id.manualNewMachineTextView)).getText().toString();

		if (manualMachineMetadata.length() > 0) {
			new Thread(new Runnable() {
				public void run() {
				try {
					PBMApplication app = getPBMApplication();

					String rawMetadata = manualMachineMetadata.substring(manualMachineMetadata.indexOf("[")+1, manualMachineMetadata.indexOf("]"));
					String machineName = manualMachineMetadata.substring(0, manualMachineMetadata.indexOf("[")-1);
					String[] metadata = rawMetadata.split("-");

					int machineID = app.getMachineIDFromMachineMetadata(machineName.trim(), metadata[1].trim(), metadata[0].trim());
					if (machineID != -1) {
						new RetrieveJsonTask(AddMachine.this).execute(
							app.requestWithAuthDetails(regionlessBase + "location_machine_xrefs.json?location_id=" + location.getId() + ";machine_id=" + machineID),
							"POST"
						).get();
					} else {
						new RetrieveJsonTask(AddMachine.this).execute(
							app.requestWithAuthDetails(regionlessBase + "machines.json?machine_name=" + URLEncoder.encode(manualMachineMetadata, "UTF8") + ";location_id=" + location.getId()),
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

	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		PBMApplication app = getPBMApplication();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(results);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (rootNode.has("machine")) {
			JsonNode jsonMachine = rootNode.path("machine");
			app.addMachine(new Machine(jsonMachine.path("id").asInt(), jsonMachine.path("name").asText(), null, null, true, null));
			
			new RetrieveJsonTask(AddMachine.this).execute(
				app.requestWithAuthDetails(regionlessBase + "location_machine_xrefs.json?location_id=" + location.getId() + ";machine_id=" + jsonMachine.path("id").asText()),
				"POST"
			).get();
			
			return;
		}
		
		if (rootNode.has("location_machine")) {
			JsonNode jsonLmx = rootNode.path("location_machine");
			int id = jsonLmx.path("id").asInt();
			int locationID = jsonLmx.path("location_id").asInt();
			int machineID = jsonLmx.path("machine_id").asInt();

			try {
				app.addLocationMachineXref(new LocationMachineXref(id, locationID, machineID, "", "", ""));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			app.loadConditions(jsonLmx);
		}

		SharedPreferences settings = this.getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
		location.setDateLastUpdated(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date()));
		location.setLastUpdatedByUsername(settings.getString("username", ""));
		app.updateLocation(location);
	}
}