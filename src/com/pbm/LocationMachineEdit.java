package com.pbm;

import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LocationMachineEdit extends PBMUtil {
	private Location location;
	private Machine machine;
	private LocationMachineXref lmx;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_machine_edit);
		
		logAnalyticsHit("com.pbm.LocationMachineEdit");

		lmx = (LocationMachineXref) getIntent().getExtras().get("lmx");

		location = lmx.getLocation(this);
		machine = lmx.getMachine(this);

		setTitle(machine.name + " @ " + location.name);
		
		loadMachineView();
	}
	
	public void loadMachineView() {
		new Thread(new Runnable() {
	        public void run() {
	        	LocationMachineEdit.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TextView conditionText = (TextView)findViewById(R.id.condition);

						if (lmx.condition != null && !lmx.condition.isEmpty() && !lmx.condition.equals("null")) {
							conditionText.setText(lmx.condition);
						}

						if (lmx.conditionDate != null && !lmx.conditionDate.isEmpty() && !lmx.conditionDate.equals("null")) {
							TextView conditionDateView = (TextView)findViewById(R.id.conditionDate);
							conditionDateView.setText("Comment made on: " + lmx.conditionDate);
						}
					}
	        	});
	        }
	    }).start();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.location_machine_edit_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.remove_button :
				new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Remove this machine?").setMessage("Are you sure?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new Thread(new Runnable() {
							public void run() {
								try {
									location.removeMachine(LocationMachineEdit.this, lmx);
									new RetrieveJsonTask().execute(regionlessBase + "location_machine_xrefs/" + Integer.toString(lmx.id) + ".json", "DELETE").get();
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (ExecutionException e) {
									e.printStackTrace();
								}
				
								LocationMachineEdit.super.runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(getBaseContext(), "OK, machine deleted.", Toast.LENGTH_LONG).show();
										
										setResult(REFRESH_RESULT);
										LocationMachineEdit.this.finish();
									}
								});
							}
						}).start();
					}
				})
				.setNegativeButton("No", null)
				.show();

				return true;
	    	default:
	    	    return super.onOptionsItemSelected(item);
		}
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.condition :
			Intent myIntent = new Intent();
			myIntent.setClassName("com.pbm", "com.pbm.ConditionEdit");
			myIntent.putExtra("lmx", lmx);
			startActivityForResult(myIntent, QUIT_RESULT);

			break;
		}
	}

	public void activityRefreshResult() {
		LocationMachineEdit.super.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getBaseContext(), "Thanks for updating that machine.", Toast.LENGTH_LONG).show();

				setResult(REFRESH_RESULT);
				LocationMachineEdit.this.finish();
			}
		});
	}
}