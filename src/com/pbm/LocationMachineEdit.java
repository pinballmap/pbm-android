package com.pbm;

import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class LocationMachineEdit extends PBMUtil {
	private Location location;
	private Machine machine;
	private LocationMachineXref lmx;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.location_machine_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.location_machine_edit_titlebar);
		
		logAnalyticsHit("com.pbm.LocationMachineEdit");

		lmx = (LocationMachineXref) getIntent().getExtras().get("lmx");
		
		location = lmx.getLocation(this);
		machine = lmx.getMachine(this);
		
		new Thread(new Runnable() {
	        public void run() {
	        	LocationMachineEdit.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TextView title = (TextView)findViewById(R.id.title);
						title.setText(machine.name + " @ " + location.name);

						TextView conditionText = (TextView)findViewById(R.id.condition);

						if (lmx.condition != null && !lmx.condition.isEmpty() && lmx.condition != "null") {
							Log.e("!!!!!", "-" + lmx.condition + "-");
							conditionText.setText(lmx.condition);
						}

						if (lmx.conditionDate != null && !lmx.conditionDate.isEmpty() && lmx.conditionDate != "null") {
							TextView conditionDateView = (TextView)findViewById(R.id.conditionDate);
							conditionDateView.setText("Comment made on: " + lmx.conditionDate);
						}
					}
	        	});
	        }
	    }).start();
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.condition :
			Intent myIntent = new Intent();
			myIntent.setClassName("com.pbm", "com.pbm.ConditionEdit");
			myIntent.putExtra("lmx", lmx);
			startActivityForResult(myIntent, QUIT_RESULT);

			break;
		case R.id.removeMachineButton :
			Builder builder = new AlertDialog.Builder(this);

			builder.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Remove this machine?").setMessage("Are you sure?");

			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new Thread(new Runnable() {
						public void run() {
							try {
								new RetrieveJsonTask().execute(regionlessBase + "location_machine_xrefs/" + Integer.toString(lmx.id) + ".json", "DELETE").get();
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.printStackTrace();
							}

							LocationMachineEdit.super.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getBaseContext(), "OK, machine deleted.", Toast.LENGTH_LONG).show();
									
									setResult(REFRESH_RESULT);
									LocationMachineEdit.this.finish();
								}
							});
						}
					}).start();
				}
			});

			builder.setNegativeButton("No", null);
			builder.show();

			break;
		}
	}

	public void activityRefreshResult() {
		Intent myIntent = new Intent();
		myIntent.putExtra("lmx", lmx);
		myIntent.setClassName("com.pbm", "com.pbm.LocationMachineEdit");
		startActivityForResult(myIntent, QUIT_RESULT);
		this.finish();
	}
}