package com.pbm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class AddMachine extends PBMUtil {	
	private Location location;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_machine);
		
		logAnalyticsHit("com.pbm.AddMachine");

		location = (Location) getIntent().getExtras().get("Location");

		ListView table = (ListView)findViewById(R.id.addMachineTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parentView, View selectedView, final int position, long id) {	
		
				new Thread(new Runnable() {
					public void run() {
						Machine machine = (Machine) parentView.getItemAtPosition(position);
						machine.setExistsInRegion(true);
						try {
							new RetrieveJsonTask().execute(getAddMachineURL("", machine), "POST").get();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						} catch (JSONException e) {
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

		PBMApplication app = (PBMApplication) getApplication();		
		table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getMachineValues(true)));
	}   

	public void submitHandler(View view) {		
		EditText manualName = (EditText) findViewById(R.id.manualNewMachine);
		String manualMachineName = manualName.getText().toString();

		if (manualMachineName.length() > 0) {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("Reinitializing machine information");
			dialog.show();

			new Thread(new Runnable() {
				public void run() {
					EditText manualName = (EditText) findViewById(R.id.manualNewMachine);
					String manualMachineName = manualName.getText().toString();

					try {
						new RetrieveJsonTask().execute(getAddMachineURL(manualMachineName, null), "POST").get();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}

					AddMachine.super.runOnUiThread(new Runnable() {
						public void run() {
							dialog.dismiss();
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
		} else {
			String json = new RetrieveJsonTask().execute(
				regionBase + "machines.json?machine_name=" + name + ";location_id=" + location.id,
				"POST"
			).get();
					
			JSONObject jsonObject = new JSONObject(json);

			machineID = jsonObject.getInt("id");
			app.addMachine(machineID, new Machine(machineID, name, null, null, true));
		}
		
		return machineID;
	}

	private String getAddMachineURL(String manualMachineName, Machine machine) throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String addMachineURL = regionBase + "location_machine_xrefs.json?location_id=" + location.id;

		if (machine != null) {
			addMachineURL += ";machine_id=" + machine.id;
		} else {
			int machineID = getMachineIDFromMachineName(URLEncoder.encode(manualMachineName, "UTF8"));
			addMachineURL += ";machine_id=" + machineID;
		}

		return addMachineURL;
	}
}