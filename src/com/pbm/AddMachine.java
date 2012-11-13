package com.pbm;

import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private ProgressDialog progressDialog;
	private ProgressThread progressThread;

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
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				addMachine((Machine) parentView.getItemAtPosition(position));
				setResult(REFRESH_RESULT);

				finish();
			}
		});

		PBMApplication app = (PBMApplication) getApplication();		
		table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getMachineValues()));
	}   

	public void addMachine(final Machine machine) {
		Toast.makeText(getBaseContext(), "Thanks for adding that machine!", Toast.LENGTH_LONG).show();
		sendOneWayRequestToServer(getAddMachineURL("", machine));
	}

	public void submitHandler(View view) {		
		EditText manualName = (EditText) findViewById(R.id.manualNewMachine);
		String manualMachineName = manualName.getText().toString();
		if (manualMachineName.length() > 0) {
			showDialog(PROGRESS_DIALOG);
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

	final Handler waitHandler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			progressDialog.setProgress(total);
			if (total >= 100){
				try{
					dismissDialog(PROGRESS_DIALOG); 

					setResult(REFRESH_RESULT);
					finish();
				} catch (java.lang.IllegalArgumentException iae) {}
			}
		}
	};

	protected Dialog onCreateDialog(int id) { 
		switch(id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Reinitializing machine information");
			progressThread = new ProgressThread(waitHandler);
			progressThread.start();

			return progressDialog;
		default:
			return null;
		}
	}

	private class ProgressThread extends Thread {
		Handler mHandler;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			EditText manualName = (EditText) findViewById(R.id.manualNewMachine);
			String manualMachineName = manualName.getText().toString();
			sendOneWayRequestToServer(getAddMachineURL(manualMachineName, null));

			PBMApplication app = (PBMApplication) getApplication();
			app.initializeMachines(httpBase + "iphone.html?init=1");

			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", 100);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}
}