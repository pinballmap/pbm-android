package com.pbm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

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

		location = (Location) getIntent().getExtras().get("Location");

		ListView table = (ListView)findViewById(R.id.addMachineTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parentView, View selectedView, final int position, long id) {	
		
				new Thread(new Runnable() {
					public void run() {
						Machine machine = (Machine) parentView.getItemAtPosition(position);
						try {
							sendOneWayRequestToServer(getAddMachineURL("", machine));
						} catch (UnsupportedEncodingException e) {
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
		table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getMachineValues()));
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
						sendOneWayRequestToServer(getAddMachineURL(manualMachineName, null));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					PBMApplication app = (PBMApplication) getApplication();
					try {
						app.initializeMachines(httpBase + "iphone.html?init=1");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
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

	private String getAddMachineURL(String manualMachineName, Machine machine) throws UnsupportedEncodingException {
		String addMachineURL = "modify_location=" + location.locationNo + ";action=add_machine";

		if (manualMachineName.length() > 0) {
			addMachineURL += ";machine_name=" + URLEncoder.encode(manualMachineName, "UTF8");
		} else {
			addMachineURL += ";machine_no=" + machine.machineNo;
		}

		return addMachineURL;
	}
}