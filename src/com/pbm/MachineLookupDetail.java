package com.pbm;

import java.util.Arrays;
import java.util.Comparator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MachineLookupDetail extends PBMUtil {
	private Machine machine;
	private static Location[] locationsWithMachine;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.machine_lookup_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.map_titlebar);

		Bundle extras = getIntent().getExtras();
		machine = (Machine) extras.get("Machine");

		TextView title = (TextView)findViewById(R.id.title);
		title.setText(machine.name);

		ListView table = (ListView)findViewById(R.id.machineLookupDetailTable);
		table.setFastScrollEnabled(true);
		table.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();
				myIntent.putExtra("Location", (Location) parentView.getItemAtPosition(position));
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail"); 
				startActivityForResult(myIntent, QUIT_RESULT);    
			}
		});

		getMachineData(httpBase + "iphone.html?get_machine=" + machine.machineNo);

		if (locationsWithMachine != null) {
			Arrays.sort(locationsWithMachine, new Comparator<Location>() {
				public int compare(Location l1, Location l2) {
					return l1.name.toString().compareTo(l2.name.toString());
				}
			});

			table.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, locationsWithMachine));
		}
	}   

	public void getMachineData(String URL) {
		Document doc = getXMLDocument(URL);
		PBMApplication app = (PBMApplication) getApplication();

		if (doc != null) {
			NodeList itemNodes = doc.getElementsByTagName("location");
			locationsWithMachine = new Location[itemNodes.getLength()];
			for (int i = 0; i < itemNodes.getLength(); i++) { 
				Node itemNode = itemNodes.item(i); 
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) 
				{            
					Element itemElement = (Element) itemNode;    

					String locationNo = readDataFromXML("id", itemElement);
					
					if ((locationNo != null)) {
						locationsWithMachine[i] = app.getLocation(Integer.parseInt(locationNo));
					}
				} 
			}
		}
	}

	public void clickHandler(View view) {		
		switch (view.getId()) {
		case R.id.mapButton :
			if (locationsWithMachine != null) {
				Intent myIntent = new Intent();
				myIntent.putExtra("Locations", locationsWithMachine);
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, QUIT_RESULT);
			}

			break;
		}
	}
}