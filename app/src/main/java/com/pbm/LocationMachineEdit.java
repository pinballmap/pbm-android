package com.pbm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class LocationMachineEdit extends PinballMapActivity {
	private Location location;
	private LocationMachineXref lmx;
	private ConditionsArrayAdapter adapter;
	private View.OnClickListener removeHandler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_machine_edit);

		logAnalyticsHit("com.pbm.LocationMachineEdit");

		lmx = (LocationMachineXref) getIntent().getExtras().get("lmx");

		location = lmx.getLocation(this);
		Machine machine = getPBMApplication().getMachine(lmx.machineID);

		setTitle(machine.name + " @ " + location.name);
		removeHandler = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeMachineDialog();
			}
		};
		Button removeMachine = (Button) findViewById(R.id.remove_machine_button);
		removeMachine.setOnClickListener(removeHandler);

		Button addMachine = (Button) findViewById(R.id.add_condition_button);
		addMachine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent();
				myIntent.setClassName("com.pbm", "com.pbm.ConditionEdit");
				myIntent.putExtra("lmx", lmx);
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});
	}

	private void removeMachineDialog() {
		new AlertDialog.Builder(LocationMachineEdit.this)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Remove this machine?").setMessage("Are you sure?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new Thread(new Runnable() {
							public void run() {
								try {
									location.removeMachine(LocationMachineEdit.this, lmx);
									new RetrieveJsonTask().execute(regionlessBase + "location_machine_xrefs/" + Integer.toString(lmx.id) + ".json", "DELETE").get();
								} catch (InterruptedException | ExecutionException e) {
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
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_machine_edit_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.remove_button:
				removeMachineDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadConditions();
	}

	private void loadConditions() {
		Log.d("com.pbm", "location Machine edit resume");
		ListView listView = (ListView) findViewById(android.R.id.list);
		View emptyView = findViewById(android.R.id.empty);
		listView.setEmptyView(emptyView);

		final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		adapter = new ConditionsArrayAdapter(this, inflater,
				getPBMApplication().getLmxConditionsByID(lmx.id).getConditions());
		listView.setAdapter(adapter);
		adapter.sort(new Comparator<Condition>() {
			@Override
			public int compare(Condition lhs, Condition rhs) {
				return rhs.getDate().compareTo(lhs.getDate());
			}
		});

	}

	public void clickHandler(View view) {
		switch (view.getId()) {
			case R.id.condition:
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