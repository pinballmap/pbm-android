package com.pbm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class LocationDetail extends PinballMapActivity {
	private Location location;
	private List<LocationMachineXref> lmxes = new ArrayList<>();
	private List<Machine> machines = new ArrayList<>();

	@SuppressWarnings("ConstantConditions")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		logAnalyticsHit("com.pbm.LocationDetail");

		lmxes.clear();
		setTitle("");

		location = (Location) getIntent().getExtras().get("Location");

		if (location != null) {
			setupConfirmLocationButton();
			try {
				loadLocationData();
			} catch (InterruptedException | ExecutionException | ParseException | JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setupConfirmLocationButton() {
		Button confirmLocationButton = (Button) findViewById(R.id.confirmLocationButton);

		if (!getPBMApplication().userIsAuthenticated()) {
			confirmLocationButton.setText(R.string.login_to_update);
		}

		confirmLocationButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			try{
				final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);

				if (!getPBMApplication().userIsAuthenticated()) {
					Intent intent = new Intent();
					intent.setClassName("com.pbm", "com.pbm.Login");
					startActivityForResult(intent, QUIT_RESULT);
				} else {
					new RetrieveJsonTask().execute(
						getPBMApplication().requestWithAuthDetails(PinballMapActivity.regionlessBase + "locations/" + location.getId() + "/confirm.json"),
						"PUT"
					).get();
					Toast.makeText(getBaseContext(), "Thanks for confirming this spot!", Toast.LENGTH_LONG).show();

					location.setDateLastUpdated(new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(new Date()));
					location.setLastUpdatedByUsername(settings.getString("username", ""));

					String lastUpdatedInfo = location.getDateLastUpdated() + " by ";

					TextView locationLastUpdated = (TextView) findViewById(R.id.locationLastUpdated);
					locationLastUpdated.setVisibility(View.VISIBLE);
                    locationLastUpdated.setText(Html.fromHtml("<b>Last updated:</> " + lastUpdatedInfo + "<b>" + location.getLastUpdatedByUsername() + "</b>"));
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_detail_menu, menu);

		if (!getPBMApplication().userIsAuthenticated()) {
			menu.removeItem(R.id.add_machine_button);
			menu.removeItem(R.id.edit_button);
		}

		return super.onCreateOptionsMenu(menu);
	}

	private void loadLocationData() throws InterruptedException, ExecutionException, ParseException, JSONException, IOException {
		enableLoadingSpinnerForView((ViewGroup)findViewById(R.id.locationDetailLinearLayout));
		getPBMApplication().loadLocationDetail(location);
		location = getPBMApplication().getLocation(location.getId());

		lmxes = location.getLmxes(LocationDetail.this);
		machines = location.getMachines(LocationDetail.this);

		final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		TextView locationName = (TextView) findViewById(R.id.locationName);
		TextView locationLastUpdated = (TextView) findViewById(R.id.locationLastUpdated);
		TextView locationType = (TextView) findViewById(R.id.locationType);
		TextView locationMetadata = (TextView) findViewById(R.id.locationMetadata);
		TextView locationWebsite = (TextView) findViewById(R.id.website);
		TextView locationPhone = (TextView) findViewById(R.id.locationPhone);
		TextView locationOperator = (TextView) findViewById(R.id.operator);
		TextView locationDescription = (TextView) findViewById(R.id.description);
		TextView locationDistance = (TextView) findViewById(R.id.distance);

		if (location.getDateLastUpdated() != null && !location.getDateLastUpdated().equals("") && !location.getDateLastUpdated().equals("null")) {
			String lastUpdatedInfo = location.getDateLastUpdated();

			if (location.getDateLastUpdated() != null && !location.getLastUpdatedByUsername().equals("") && !location.getLastUpdatedByUsername().equals("null")) {
				lastUpdatedInfo = lastUpdatedInfo + " by <b>" + location.getLastUpdatedByUsername() + "</b>";
			}

			locationLastUpdated.setVisibility(View.VISIBLE);
			locationLastUpdated.setText(Html.fromHtml("<b>Last updated:</b> " + lastUpdatedInfo));
		} else {
			locationLastUpdated.setVisibility(View.GONE);
		}

		String locationTypeName = "";
		LocationType type = location.getLocationType(LocationDetail.this);
		if (type != null) {
			locationTypeName = type.getName();
		}

		locationName.setText(location.getName());
		locationMetadata.setText(
			TextUtils.join(", ", new String[]{location.getStreet(), location.getCity(), location.getState(), location.getZip()})
		);

		if (location.getPhone() != null && !location.getPhone().equals("") && !location.getPhone().equals("null")) {
			locationPhone.setVisibility(View.VISIBLE);
			locationPhone.setText(location.getPhone());
		} else {
			locationPhone.setVisibility(View.GONE);
		}

		if (!locationTypeName.equals("") && !locationTypeName.equals("null")) {
			locationType.setVisibility(View.VISIBLE);
				locationType.setText(Html.fromHtml("<i>Location Type:</i> " + locationTypeName));
		} else {
			locationType.setVisibility(View.GONE);
		}

		if (location.getWebsite() != null && !location.getWebsite().equals("") && !location.getWebsite().equals("null")) {
			locationWebsite.setVisibility(View.VISIBLE);
			locationWebsite.setMovementMethod(LinkMovementMethod.getInstance());
			locationWebsite.setText(Html.fromHtml("<a href=\""+ location.getWebsite() +"\">Website</a>"));
			locationWebsite.setClickable(true);
		} else {
			locationWebsite.setVisibility(View.GONE);
		}

		if (location.getDescription() != null && !location.getDescription().equals("") && !location.getDescription().equals("null")) {
			locationDescription.setVisibility(View.VISIBLE);
				locationDescription.setText(Html.fromHtml("<i>Description:</i> " + location.getDescription()));

		} else {
			locationDescription.setVisibility(View.GONE);
		}

		Operator operator = location.getOperator(getPBMActivity());
		if (operator != null) {
			locationOperator.setVisibility(View.VISIBLE);
				locationOperator.setText(Html.fromHtml("<i>Operated By:</i> " + operator.getName()));
		} else {
			locationOperator.setVisibility(View.GONE);
		}

		location.setDistance(this.getLocation());
        if (ActivityCompat.checkSelfPermission(LocationDetail.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationDetail.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationDistance.setText(Html.fromHtml("<i>Distance:</i> " + location.getMilesInfo()));
            } else {
                if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationDistance.setText(Html.fromHtml("<i>Distance:</i> " + location.getMilesInfo())); // or for older api
                }
            }
        } else {
            locationDistance.setVisibility(View.GONE);
        }

		updateLMXTable();
		disableLoadingSpinner();
	}

	private void updateLMXTable() {
		try {
			Collections.sort(machines, new Comparator<Machine>() {
				public int compare(Machine m1, Machine m2) {
				return m1.getName().replaceAll("^(?i)The ", "").compareTo(m2.getName().replaceAll("^(?i)The ", ""));
				}
			});
		} catch (java.lang.NullPointerException nep) {
			nep.printStackTrace();
		}

		LinearLayout lmxTable = (LinearLayout) findViewById(R.id.lmxTable);
		lmxTable.removeAllViewsInLayout();

		for (Machine machine : machines) {
			lmxTable.addView(getLMXView(getPBMApplication().getLmxFromMachine(machine, lmxes), lmxTable));
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_button:
				Intent myIntent = new Intent();
				myIntent.putExtra("Locations", new Location[]{location});
				myIntent.setClassName("com.pbm", "com.pbm.DisplayOnMap");
				startActivityForResult(myIntent, QUIT_RESULT);

				return true;
			case R.id.edit_button:
				Intent editIntent = new Intent();
				editIntent.putExtra("Location", location);
				editIntent.setClassName("com.pbm", "com.pbm.LocationEdit");
				startActivityForResult(editIntent, QUIT_RESULT);

				return true;
			case R.id.add_machine_button:
				Intent newMachineIntent = new Intent();
				newMachineIntent.putExtra("Location", location);
				newMachineIntent.setClassName("com.pbm", "com.pbm.AddMachine");
				startActivityForResult(newMachineIntent, QUIT_RESULT);

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void activityRefreshResult() {
		lmxes.clear();

		try {
			loadLocationData();
		} catch (InterruptedException | ExecutionException | ParseException | JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	public View getLMXView(final LocationMachineXref lmx, LinearLayout lmxTable) {
		MachineViewHolder holder;
		LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());

		View row = layoutInflater.inflate(R.layout.machine_condition_view, lmxTable, false);
		holder = new MachineViewHolder();
		holder.name = row.findViewById(R.id.machine_info);
		holder.machineSelectButton = row.findViewById(R.id.machineSelectButton);
		holder.condition = row.findViewById(R.id.machine_condition);
		holder.conditionMeta = row.findViewById(R.id.machine_condition_meta);

		row.setTag(holder);

		Machine machine = lmx.getMachine(this);

            holder.name.setText(Html.fromHtml("<b>" + machine.getName() + "</b>" + " " + "<i>" + machine.metaData() + "</i>"));

		String conditionText = "";
		String conditionTextMeta = "";
		if (!lmx.getCondition().equals("null") && !lmx.getCondition().equals("")) {
			conditionText += lmx.getCondition();
			if (!lmx.getConditionDate().equals("null") && !lmx.getCondition().equals("")) {
				conditionTextMeta += "<i>" + getBaseContext().getString(R.string.updated_on) + "</i> " + lmx.getConditionDate();
			}

			String lastUpdatedByUsername = lmx.getLastUpdatedByUsername();
			if(lastUpdatedByUsername != null && !lastUpdatedByUsername.isEmpty()) {
				conditionTextMeta += " by<b> " + lastUpdatedByUsername + "</b>";
			}

                holder.condition.setText(Html.fromHtml(conditionText));
				holder.conditionMeta.setText(Html.fromHtml(conditionTextMeta));

		} else {
			holder.condition.setVisibility(View.GONE);
			holder.conditionMeta.setVisibility(View.GONE);
		}

		row.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
			Intent myIntent = new Intent();
			myIntent.putExtra("lmx", lmx);
			myIntent.setClassName("com.pbm", "com.pbm.LocationMachineEdit");

			startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		return row;
	}

	private class MachineViewHolder {
		TextView name;
		ImageView machineSelectButton;
		TextView condition;
		TextView conditionMeta;
	}
}
