package com.pbm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class RecentlyAdded extends PBMUtil {
	private final int NUM_ADDED_TO_SHOW = 20;
	private List<Spanned> recentAdds = new ArrayList<Spanned>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.recently_added);

		logAnalyticsHit("com.pbm.RecentlyAdded");

		new Thread(new Runnable() {
	        public void run() {
	        	try {
					getLocationData();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	RecentlyAdded.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showTable(recentAdds);
					}
	        	});
	        }
	    }).start();
	}   

	public void getLocationData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(regionBase + "location_machine_xrefs.json?limit=" + NUM_ADDED_TO_SHOW, "GET").get();

		if (json == null) {
			return;
		}
		
		JSONObject jsonObject = new JSONObject(json);
		JSONArray lmxes = jsonObject.getJSONArray("location_machine_xrefs");
		
		PBMApplication app = (PBMApplication) getApplication();
		for (int i = 0; i < lmxes.length(); i++) {
			JSONObject lmxJson = lmxes.getJSONObject(i);
			
			int id = lmxJson.getInt("id");
			String createdAt = lmxJson.getString("created_at").split("T")[0];
			LocationMachineXref lmx = app.getLmx(id);
			
			String textToShow = "<b>" + lmx.getMachine(this).name + "</b> was added to <b>" + lmx.getLocation(this).name + "</b>";
			textToShow += "<br /><small>Added on " + createdAt + "</small>";

			recentAdds.add(Html.fromHtml(textToShow));
		}
	}

	public void showTable(List<Spanned> locations) {
		ListView table = (ListView)findViewById(R.id.recentlyAddedTable);

		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();
				Spanned spanned = (Spanned) parentView.getItemAtPosition(position);
				String locationName = spanned.toString().split(" was added to ")[1];
				locationName = locationName.split("\n")[0];

				PBMApplication app = (PBMApplication) getApplication();
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

		table.setAdapter(new ArrayAdapter<Spanned>(this, android.R.layout.simple_list_item_1, locations));
	}
}