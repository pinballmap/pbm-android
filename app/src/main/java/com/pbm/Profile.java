package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
	private final int LOCATION_ID_INDEX = 0;
	private final int LOCATION_REGION_ID_INDEX = 2;
	private final int MACHINE_NAME_INDEX = 1;
	private final int SCORE_INDEX = 2;
	private final int LOCATION_NAME_INDEX = 0;
	private final int SCORE_DATE_INDEX = 3;

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

	public void initializeProfileData() {
		locationsEditedTable = (NonScrollListView) findViewById(R.id.locationsEditedTable);
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

		new Thread(new Runnable() {
			public void run() {
				try {
					getProfileData();
				} catch (UnsupportedEncodingException | InterruptedException | JSONException | ExecutionException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Profile.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
						String username = settings.getString("username", "");

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

						locationsEditedTable.setAdapter(new LocationListAdapter(Profile.this, locationsEdited));

						Spanned[] htmlHighScores = new Spanned[highScores.size()];
						for(int i = 0 ; i < highScores.size(); i++) {
							htmlHighScores[i] = Html.fromHtml(highScores.get(i));
						}

						highScoresTable.setAdapter(new ArrayAdapter<CharSequence>(Profile.this, R.layout.custom_list_item_1, htmlHighScores));
					}
				});
			}
		}).start();
	}

	public void getProfileData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException, ParseException {
		PBMApplication app = getPBMApplication();
		TextView locationsEditedText = (TextView) findViewById(R.id.locationsEditedLabel);
        locationsEditedText.setText("Locations Edited in " + getPBMApplication().getRegion().formalName + ":");

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
		DateFormat outputDF = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
		Date dateCreatedAt = inputDF.parse(rawCreatedAt);
		createdAt = outputDF.format(dateCreatedAt);

		JSONArray jsonLocationsEdited = jsonProfile.getJSONArray("profile_list_of_edited_locations");
		for (int i = 0; i < jsonLocationsEdited.length(); i++) {
			JSONArray jsonLocation = jsonLocationsEdited.getJSONArray(i);
			int locationId = jsonLocation.getInt(LOCATION_ID_INDEX);
			int locationRegionId = jsonLocation.getInt(LOCATION_REGION_ID_INDEX);
			int currentRegionId = getSharedPreferences(PREFS_NAME, 0).getInt("region", -1);

			if (locationRegionId == currentRegionId) {
				locationsEdited.add(app.getLocation(locationId));
			}
		}

		JSONArray jsonHighScores = jsonProfile.getJSONArray("profile_list_of_high_scores");
		for (int i = 0; i < jsonHighScores.length(); i++) {
			JSONArray jsonScore = jsonHighScores.getJSONArray(i);

			String scoreText = "<u>" + jsonScore.getString(MACHINE_NAME_INDEX) + "</u><br /><b>" +
					jsonScore.getString(SCORE_INDEX) + "</b><br /> at " + jsonScore.getString(LOCATION_NAME_INDEX) +
					" on " + jsonScore.getString(SCORE_DATE_INDEX);
			highScores.add(scoreText);
		}
	}
}
