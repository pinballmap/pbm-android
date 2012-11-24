package com.pbm;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter; 
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Events extends PBMUtil {
	private String[] eventLinks;
	private List<Spanned> events = new ArrayList<Spanned>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		ListView table = (ListView)findViewById(R.id.eventsTable);
		table.setFastScrollEnabled(true);
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(eventLinks[position]);
				intent.setData(uri);
				startActivity(intent);
			}
		});

		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Loading...");
		dialog.show();
		
		new Thread(new Runnable() {
	        public void run() {

	        	getEventData(httpBase + "iphone.html?init=3");
	        	Events.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						ListView table = (ListView)findViewById(R.id.eventsTable);
						table.setAdapter(new ArrayAdapter<Spanned>(Events.this, android.R.layout.simple_list_item_1, events));
					}
	        	});
	        }
	    }).start();
	}   

	public void getEventData(String URL) {
		Document doc = getXMLDocument(URL);
		NodeList itemNodes = doc.getElementsByTagName("event"); 

		eventLinks = new String[itemNodes.getLength()];
		for (int i = 0; i < itemNodes.getLength(); i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
				Element itemElement = (Element) itemNode;                 

				String name = readDataFromXML("name", itemElement);
				String desc = readDataFromXML("longDesc", itemElement);
				String link = readDataFromXML("link", itemElement);
				String start = readDataFromXML("startDate", itemElement);
				String end = readDataFromXML("endDate", itemElement);

				String eventText = "<b>" + name + "</b><br />" + desc + "<br /><small>" + start + "</small>";
				if(!end.equals("")) {
					eventText += " - " + "<small>" + end + "</small>";
				}

				if(!link.equals("")) {
					eventLinks[i] = link;
				}

				Spanned eventTextSpanned = Html.fromHtml(eventText);
				events.add(eventTextSpanned);
			}
		}
	}
}