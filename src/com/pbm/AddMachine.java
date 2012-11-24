package com.pbm;

import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class AddMachine extends PBMUtil {	
	private Location location;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.add_machine);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.add_machine_titlebar);

		location = (Location) getIntent().getExtras().get("Location");

		TextView title = (TextView)findViewById(R.id.title);
		title.setText("Add machine to " + location.name);

		ListView table = (ListView)findViewById(R.id.addMachineTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(final AdapterView<?> parentView, View selectedView, final int position, long id) {	
		
				new Thread(new Runnable() {
					public void run() {
						Machine machine = (Machine) parentView.getItemAtPosition(position);
						sendOneWayRequestToServer(getAddMachineURL("", machine));
						AddMachine.super.runOnUiThread(new Runnable() {
							@Override
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
					sendOneWayRequestToServer(getAddMachineURL(manualMachineName, null));

					PBMApplication app = (PBMApplication) getApplication();
					app.initializeMachines(httpBase + "iphone.html?init=1");
					AddMachine.super.runOnUiThread(new Runnable() {
						@Override
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

	private String getAddMachineURL(String manualMachineName, Machine machine) {
		String addMachineURL = "modify_location=" + location.locationNo + ";action=add_machine";

		if (manualMachineName.length() > 0) {
			addMachineURL += ";machine_name=" + URLEncoder.encode(manualMachineName);
		} else {
			addMachineURL += ";machine_no=" + machine.machineNo;
		}

		return addMachineURL;
	}
}