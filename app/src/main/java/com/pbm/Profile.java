package com.pbm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Profile extends PinballMapActivity {
	NonScrollListView locationsEditedTable, highScoresTable;
	TextView numMachinesAddedTextView, numMachinesRemovedTextView, numLocationsEditedTextView, numLocationsSuggestedTextView,
		numLmxCommentsLeftTextView, createdAtTextView, usernameTextView;
	String numMachinesAdded, numMachinesRemoved, numLocationsEdited, numLocationsSuggested, numLmxCommentsLeft, createdAt;
	ArrayList<Location> locationsEdited = new ArrayList<>();
	ArrayList<String> highScores = new ArrayList<>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		logAnalyticsHit("com.pbm.Profile");

		initializeProfileData();
	}

    @SuppressWarnings("deprecation")
	public void initializeProfileData() {
		locationsEditedTable = (NonScrollListView) findViewById(R.id.locationsEditedTable);
        locationsEditedTable.setFocusable(false);
		locationsEditedTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent myIntent = new Intent();
				com.pbm.Location location = locationsEdited.get(position);

				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, PinballMapActivity.QUIT_RESULT);
			}
		});

		highScoresTable = (NonScrollListView) findViewById(R.id.highScoresTable);
        highScoresTable.setFocusable(false);

		new Thread(new Runnable() {
			public void run() {
				try {
					getProfileData();
				} catch (UnsupportedEncodingException | InterruptedException | JSONException | ExecutionException | ParseException e) {
					e.printStackTrace();
				}

				Profile.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
					final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
					String username = settings.getString("username", "");

					View emptyScoresView = findViewById(R.id.emptyScores);
					View emptyLocationsView = findViewById(R.id.emptyLocations);

					numMachinesAddedTextView = (TextView) findViewById(R.id.numMachinesAddedTextView);
					numMachinesRemovedTextView = (TextView) findViewById(R.id.numMachinesRemovedTextView);
					numLocationsEditedTextView = (TextView) findViewById(R.id.numLocationsEditedTextView);
					numLocationsSuggestedTextView = (TextView) findViewById(R.id.numLocationsSuggestedTextView);
					numLmxCommentsLeftTextView = (TextView) findViewById(R.id.numLmxCommentsLeftTextView);
					createdAtTextView = (TextView) findViewById(R.id.createdAtTextView);
					usernameTextView = (TextView) findViewById(R.id.usernameTextView);

					numMachinesAddedTextView.setText(numMachinesAdded);
					numMachinesRemovedTextView.setText(numMachinesRemoved);
					numLocationsEditedTextView.setText(numLocationsEdited);
					numLocationsSuggestedTextView.setText(numLocationsSuggested);
					numLmxCommentsLeftTextView.setText(numLmxCommentsLeft);
					usernameTextView.setText(username);
					createdAtTextView.setText(createdAt);

					locationsEditedTable.setEmptyView(emptyLocationsView);
					locationsEditedTable.setAdapter(new LocationListAdapter(Profile.this, locationsEdited));

					Spanned[] htmlHighScores = new Spanned[highScores.size()];
					for(int i = 0 ; i < highScores.size(); i++) {
						htmlHighScores[i] = Html.fromHtml(highScores.get(i));
					}

					highScoresTable.setEmptyView(emptyScoresView);
					highScoresTable.setAdapter(new ArrayAdapter<CharSequence>(Profile.this, R.layout.custom_list_item_1, htmlHighScores));
					}
				});
			}
		}).start();
	}

	public void getProfileData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException, ParseException {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		PBMApplication app = getPBMApplication();
		TextView locationsEditedText = (TextView) findViewById(R.id.locationsEditedLabel);
        locationsEditedText.setText("Locations Edited in " + getPBMApplication().getRegion().getFormalName() + ":");

		final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
		String id = settings.getString("id", "");

		String json = new RetrieveJsonTask().execute(
			getPBMApplication().requestWithAuthDetails(regionlessBase + "users/" + id + "/profile_info.json"),
			"GET"
		).get();

		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONObject jsonProfile = jsonObject.getJSONObject("profile_info");

		numMachinesAdded = jsonProfile.getString("num_machines_added");
		numMachinesRemoved = jsonProfile.getString("num_machines_removed");
		numLocationsEdited = jsonProfile.getString("num_locations_edited");
		numLocationsSuggested = jsonProfile.getString("num_locations_suggested");
		numLmxCommentsLeft = jsonProfile.getString("num_lmx_comments_left");

		String rawCreatedAt = jsonProfile.getString("created_at").split("T")[0];
		DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
		Date dateCreatedAt = inputDF.parse(rawCreatedAt);
		createdAt = outputDF.format(dateCreatedAt);
		JSONArray jsonLocationsEdited = jsonProfile.getJSONArray("profile_list_of_edited_locations");
		for (int i = 0; i < jsonLocationsEdited.length(); i++) {
			JSONArray jsonLocation = jsonLocationsEdited.getJSONArray(i);
			int LOCATION_ID_INDEX = 0;
			int locationId = jsonLocation.getInt(LOCATION_ID_INDEX);
			int LOCATION_REGION_ID_INDEX = 2;
			int locationRegionId = jsonLocation.getInt(LOCATION_REGION_ID_INDEX);
			int currentRegionId = getSharedPreferences(PREFS_NAME, 0).getInt("region", -1);

			if (locationRegionId == currentRegionId) {
				Location location = app.getLocation(locationId);
				if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						location.setDistance(this.getLocation());
					}
					if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						location.setDistance(this.getLocation());
					}
				}
				locationsEdited.add(location);
			}
		}

		JSONArray jsonHighScores = jsonProfile.getJSONArray("profile_list_of_high_scores");
		for (int i = 0; i < jsonHighScores.length(); i++) {
			JSONArray jsonScore = jsonHighScores.getJSONArray(i);

			int MACHINE_NAME_INDEX = 1;
			int SCORE_INDEX = 2;
			int LOCATION_NAME_INDEX = 0;
			int SCORE_DATE_INDEX = 3;

			String date = jsonScore.getString(SCORE_DATE_INDEX);
			if (
				!date.equals("null") &&
				!date.equals("") &&
				date.matches("([a-zA-Z]{3})-([0-9]{2})-([0-9]{4})")
			) {
				inputDF = new SimpleDateFormat("MMM-dd-yyyy");
				outputDF = new SimpleDateFormat("MM/dd/yyyy");
				dateCreatedAt = null;
				try {
					dateCreatedAt = inputDF.parse(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				date = outputDF.format(dateCreatedAt);
			}

			String scoreText = "<u>" + jsonScore.getString(MACHINE_NAME_INDEX) + "</u><br /><b>" +
					jsonScore.getString(SCORE_INDEX) + "</b><br /> at " + jsonScore.getString(LOCATION_NAME_INDEX) +
					" on " + date;
			highScores.add(scoreText);
		}
	}
}
