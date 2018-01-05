package com.pbm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Events extends PinballMapActivity {
	private String[] eventLinks;
	private List<Spanned> events = new ArrayList<>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);

		logAnalyticsHit("com.pbm.Events");

		initializeEventsTable();
	}

	public void initializeEventsTable() {
		ListView eventsTable = (ListView) findViewById(R.id.eventsTable);
		eventsTable.setFastScrollEnabled(true);
		eventsTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
				} catch (UnsupportedEncodingException | InterruptedException | JSONException | ExecutionException | ParseException e) {
					e.printStackTrace();
				}
				Events.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
					ListView eventsTable = (ListView) findViewById(R.id.eventsTable);
					eventsTable.setAdapter(new ArrayAdapter<>(Events.this, R.layout.custom_list_item_1, events));
					}
				});
			}
		}).start();
	}

    @SuppressWarnings("deprecation")
	public void getEventData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException, ParseException {
		String json = new RetrieveJsonTask().execute(
			getPBMApplication().requestWithAuthDetails(regionBase + "events.json"),
			"GET"
		).get();

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
			Location location = null;

			DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

			if (!event.isNull("location_id")) {
				location = getPBMApplication().getLocation(event.getInt("location_id"));
			}

			if (startDate.equals("null")) {
				startDate = "";
			} else {
				Date startDateDate = inputDF.parse(startDate);
				startDate = outputDF.format(startDateDate);
			}

			String eventText = "<b>" + name + "</b> <br/>";
			if (location != null) {
				eventText += "At " + location.getName() + "<br/>";
			}
			eventText += "<br />" + longDesc + "<br />";
			eventText += "<small>" + startDate + "</small>";
			if (!endDate.equals("") && !endDate.equals("null")) {
				Date endDateDate = inputDF.parse(endDate);
				endDate = outputDF.format(endDateDate);

				eventText += " - " + "<small>" + endDate + "</small>";
			}

			if (!link.equals("") && !link.equals("null")) {
				eventLinks[i] = link;
			}

            Spanned eventTextSpanned;
            eventTextSpanned = Html.fromHtml(eventText);
			events.add(eventTextSpanned);
		}
	}
}
