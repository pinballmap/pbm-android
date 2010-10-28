package com.pbm;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class LocationMachineEdit extends PBMUtil {
	private Location location;
	private Machine machine;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.locationmachineedit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.locationmachineedittitlebar);

		location = (Location) getIntent().getExtras().get("Location");
		machine = (Machine) getIntent().getExtras().get("Machine");

		getMachineData(location, machine);

		TextView title = (TextView)findViewById(R.id.title);
		title.setText(machine.name + " @ " + location.name);

		TextView conditionText = (TextView)findViewById(R.id.condition);
		conditionText.setText(machine.condition);

		if (machine.conditionDate != null) {
			TextView conditionDateView = (TextView)findViewById(R.id.conditionDate);
			conditionDateView.setText("Comment made on: " + machine.conditionDate);
		}
	}

	private void getMachineData(Location searchLocation, Machine searchMachine) {
		Document doc = getXMLDocument(httpBase + "iphone.html?get_location=" + searchLocation.locationNo);

		if (doc != null) {
			NodeList itemNodes = doc.getElementsByTagName("machine"); 
			for (int i = 0; i < itemNodes.getLength(); i++) { 
				Node itemNode = itemNodes.item(i); 
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
					Element itemElement = (Element) itemNode;     

					String id = readDataFromXML("id", itemElement);
					String[] cond = readDataFromXML("condition", itemElement, "date");

					if ((id != null) && (Integer.parseInt(id) == searchMachine.machineNo)) {
						String condition = "";
						String conditionDate = "";

						if (cond[CONDITION] != null) {
							condition = cond[CONDITION];
						} else {
							condition = "";
						}

						if (cond[CONDITION_DATE] != null) {
							conditionDate = cond[CONDITION_DATE];
						} else {
							conditionDate = null;
						}

						machine = new Machine(machine.machineNo, machine.name, machine.numLocations, condition, conditionDate);
					}
				}
			}
		}
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.condition :
			Intent myIntent = new Intent();
			myIntent.setClassName("com.pbm", "com.pbm.ConditionEdit");
			myIntent.putExtra("Location", location);
			myIntent.putExtra("Machine", machine);
			startActivityForResult(myIntent, QUIT_RESULT);

			break;
		case R.id.removeMachineButton :
			Builder builder = new AlertDialog.Builder(this);

			builder.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Remove this machine?").setMessage("For realsies?");

			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					sendOneWayRequestToServer("modify_location=" + location.locationNo + ";action=remove_machine;machine_no=" + machine.machineNo);
					Toast.makeText(getBaseContext(), "OK, machine deleted.", Toast.LENGTH_LONG).show();

					setResult(REFRESH_RESULT);
					finish();
				}
			});

			builder.setNegativeButton("No", null);
			builder.show();

			break;
		}
	}

	public void activityRefreshResult() {
		Intent myIntent = new Intent();
		myIntent.putExtra("Machine", machine);
		myIntent.putExtra("Location", location);
		myIntent.setClassName("com.pbm", "com.pbm.LocationMachineEdit");
		startActivityForResult(myIntent, QUIT_RESULT);
		this.finish();
	}
}