package com.pbm;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class RecentlyAdded extends PinballMapActivity {
	private List<Spanned> recentAdds = new ArrayList<>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recently_added);

		logAnalyticsHit("com.pbm.RecentlyAdded");
		enableLoadingSpinnerForView(R.id.recentRelativeLayout);

		new Thread(new Runnable() {
			public void run() {
				while (!getPBMApplication().getIsDataInitialized()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				try {
					getLocationData();
				} catch (InterruptedException | ExecutionException | JSONException | ParseException | IOException e) {
					e.printStackTrace();
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						disableLoadingSpinner();
						showTable(recentAdds);
					}
				});
			}
		}).start();
	}

    @SuppressWarnings("deprecation")
	public void getLocationData() throws IOException, InterruptedException, ExecutionException, JSONException, ParseException {
		int NUM_ADDED_TO_SHOW = 20;
		PBMApplication app = getPBMApplication();

		String json = new RetrieveJsonTask().execute(
			app.requestWithAuthDetails(regionBase + "location_machine_xrefs.json?limit=" + NUM_ADDED_TO_SHOW),
			"GET"
		).get();

		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray lmxes = jsonObject.getJSONArray("location_machine_xrefs");

		for (int i = 0; i < lmxes.length(); i++) {
			JSONObject lmxJson = lmxes.getJSONObject(i);

			JSONObject locationJson = lmxJson.getJSONObject("location");
			JSONObject machineJson = lmxJson.getJSONObject("machine");

			Location location = getPBMApplication().getLocation(locationJson.getInt("id"));
			if (this.getLocation() != null) {
				location.setDistance(this.getLocation());
			}

			String milesInfo;
			milesInfo = location.getMilesInfo() != null ? " (" + location.getMilesInfo() + ")" : "";

			String rawCreatedAt = lmxJson.getString("created_at").split("T")[0];
			DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
			Date dateCreatedAt = inputDF.parse(rawCreatedAt);
			String createdAt = outputDF.format(dateCreatedAt);

			String textToShow = "<b>" + machineJson.getString("name") + "</b> was added to <b>" + location.getName() + "</b> (" + locationJson.getString("city") + ")" + milesInfo;
			textToShow += "<br /><small>" + createdAt + "</small>";

			recentAdds.add(Html.fromHtml(textToShow));
		}
	}

	public void showTable(List<Spanned> locations) {
		ListView table = (ListView) findViewById(R.id.recentlyAddedTable);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {
			Intent myIntent = new Intent();
			Spanned spanned = (Spanned) parentView.getItemAtPosition(position);
			String locationName = spanned.toString().split(" was added to ")[1];
			locationName = locationName.split(" \\(")[0];
			locationName = locationName.split("\n")[0];

			PBMApplication app = getPBMApplication();
			Location location = app.getLocationByName(locationName);

			if (location == null) {
				Toast.makeText(getBaseContext(), "Sorry, can't find that location", Toast.LENGTH_LONG).show();
				return;
			}

			myIntent.putExtra("Location", location);
			myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
			startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		table.setAdapter(new ArrayAdapter<>(this, R.layout.custom_list_item_1, locations));
	}
}
