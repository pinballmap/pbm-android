package com.pbm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

		logAnalyticsHit("com.pbm.Events");

		ListView table = (ListView)findViewById(R.id.eventsTable);
		table.setFastScrollEnabled(true);
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {
				String link = eventLinks[position];
				if ((link != null) && !link.equals("")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.parse(eventLinks[position]);
					intent.setData(uri);
					startActivity(intent);
				}
			}
		});

		new Thread(new Runnable() {
	        public void run() {
	        	try {
					getEventData();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	Events.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ListView table = (ListView)findViewById(R.id.eventsTable);
						table.setAdapter(new ArrayAdapter<Spanned>(Events.this, android.R.layout.simple_list_item_1, events));
					}
	        	});
	        }
	    }).start();
	}   

	public void getEventData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(regionBase + "events.json", "GET").get();

		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray jsonEvents = jsonObject.getJSONArray("events");
		
		eventLinks = new String[jsonEvents.length()];
		for (int i = 0; i < jsonEvents.length(); i++) {
		    JSONObject event = jsonEvents.getJSONObject(i);

			String name = event.getString("name");
			String longDesc = event.getString("long_desc");
			String link = event.getString("external_link");
			String startDate = event.getString("start_date");
			String endDate = event.getString("end_date");
			
			if (startDate == "null") {
				startDate = "";
			}
			
			String eventText = "<b>" + name + "</b><br />" + longDesc + "<br /><small>" + startDate + "</small>";
			if(!endDate.equals("") && endDate != null && endDate != "null") {
				eventText += " - " + "<small>" + endDate + "</small>";
			}

			if(!link.equals("") && link != null && link != "null") {
				eventLinks[i] = link;
			}

			Spanned eventTextSpanned = Html.fromHtml(eventText);
			events.add(eventTextSpanned);
		}
	}
}