package com.pbm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		logAnalyticsHit("com.pbm.LocationDetail");

		lmxes.clear();

		location = (Location) getIntent().getExtras().get("Location");

		if (location != null) {
			setTitle("");
			setupConfirmLocationButton();
			loadLocationData();
		}
	}

    @SuppressWarnings("deprecation")
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
						getPBMApplication().requestWithAuthDetails(PinballMapActivity.regionlessBase + "locations/" + location.id + "/confirm.json"),
						"PUT"
					).get();
					Toast.makeText(getBaseContext(), "Thanks for confirming this spot!", Toast.LENGTH_LONG).show();

					location.dateLastUpdated = new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(new Date());
					location.lastUpdatedByUsername = settings.getString("username", "");

					String lastUpdatedInfo = location.dateLastUpdated + " by ";

					TextView locationLastUpdated = (TextView) findViewById(R.id.locationLastUpdated);
					locationLastUpdated.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        locationLastUpdated.setText(Html.fromHtml("<b>Last updated:</> " + lastUpdatedInfo + "<b>" + location.lastUpdatedByUsername + "</b>",Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
                    } else {
                        locationLastUpdated.setText(Html.fromHtml("<b>Last updated:</> " + lastUpdatedInfo + "<b>" + location.lastUpdatedByUsername + "</b>")); // or for older api
                    }
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

    @SuppressWarnings("deprecation")
	private void loadLocationData() {
		new Thread(new Runnable() {
			public void run() {
			LocationDetail.super.runOnUiThread(new Runnable() {
				public void run() {
				lmxes = location.getLmxes(LocationDetail.this);
				machines = location.getMachines(LocationDetail.this);
				final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                PBMApplication app = getPBMApplication();

				TextView locationName = (TextView) findViewById(R.id.locationName);
				TextView locationLastUpdated = (TextView) findViewById(R.id.locationLastUpdated);
				TextView locationType = (TextView) findViewById(R.id.locationType);
				TextView locationMetadata = (TextView) findViewById(R.id.locationMetadata);
				TextView locationWebsite = (TextView) findViewById(R.id.website);
				TextView locationPhone = (TextView) findViewById(R.id.locationPhone);
				TextView locationOperator = (TextView) findViewById(R.id.operator);
				TextView locationDescription = (TextView) findViewById(R.id.description);
                TextView locationDistance = (TextView) findViewById(R.id.distance);

                    location.dateLastUpdated = new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(new Date());
				if (location.dateLastUpdated != null && !location.dateLastUpdated.equals("") && !location.dateLastUpdated.equals("null")) {
					String lastUpdatedInfo = location.dateLastUpdated;

					if (location.lastUpdatedByUsername != null && !location.lastUpdatedByUsername.equals("") && !location.lastUpdatedByUsername.equals("null")) {
						lastUpdatedInfo = lastUpdatedInfo + " by <b>" + location.lastUpdatedByUsername + "</b>";
					}

					locationLastUpdated.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        locationLastUpdated.setText(Html.fromHtml("<b>Last updated:</b> " + lastUpdatedInfo,Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
                    } else {
                        locationLastUpdated.setText(Html.fromHtml("<b>Last updated:</b> " + lastUpdatedInfo)); // or for older api
                    }
				} else {
					locationLastUpdated.setVisibility(View.GONE);
				}

				String locationTypeName = "";
				LocationType type = location.getLocationType(LocationDetail.this);
				if (type != null) {
					locationTypeName = type.name;
				}

				locationName.setText(location.name);
				locationMetadata.setText(
					TextUtils.join(", ", new String[]{location.street, location.city, location.state, location.zip})
				);

				if (location.phone != null && !location.phone.equals("") && !location.phone.equals("null")) {
					locationPhone.setVisibility(View.VISIBLE);
					locationPhone.setText(location.phone);
				} else {
					locationPhone.setVisibility(View.GONE);
				}

				if (!locationTypeName.equals("") && !locationTypeName.equals("null")) {
					locationType.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        locationType.setText(Html.fromHtml("<i>Location Type:</i> " + locationTypeName,Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
                    } else {
                        locationType.setText(Html.fromHtml("<i>Location Type:</i> " + locationTypeName)); // or for older api
                    }
				} else {
					locationType.setVisibility(View.GONE);
				}

				if (location.website != null && !location.website.equals("") && !location.website.equals("null")) {
					locationWebsite.setVisibility(View.VISIBLE);
					locationWebsite.setMovementMethod(LinkMovementMethod.getInstance());
					locationWebsite.setText(location.website);
				} else {
					locationWebsite.setVisibility(View.GONE);
				}

				if (location.description != null && !location.description.equals("") && !location.description.equals("null")) {
					locationDescription.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        locationDescription.setText(Html.fromHtml("<i>Description:</i> " + location.description,Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
                    } else {
                        locationDescription.setText(Html.fromHtml("<i>Description:</i> " + location.description)); // or for older api
                    }

				} else {
					locationDescription.setVisibility(View.GONE);
				}

				Operator operator = location.getOperator(getPBMActivity());
				if (operator != null) {
					locationOperator.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        locationOperator.setText(Html.fromHtml("<i>Operated By:</i> " + operator.name,Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
                    } else {
                        locationOperator.setText(Html.fromHtml("<i>Operated By:</i> " + operator.name)); // or for older api
                    }
				} else {
					locationOperator.setVisibility(View.GONE);
				}

					if (ActivityCompat.checkSelfPermission(LocationDetail.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationDetail.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
						if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							if (Build.VERSION.SDK_INT >= 24) {
								locationDistance.setText(Html.fromHtml("<i>Distance:</i> " + location.milesInfo, Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
							} else {
								locationDistance.setText(Html.fromHtml("<i>Distance:</i> " + location.milesInfo)); // or for older api
							}
						} else {
							if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
								if (Build.VERSION.SDK_INT >= 24) {
									locationDistance.setText(Html.fromHtml("<i>Distance:</i> " + location.milesInfo, Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
								} else {
									locationDistance.setText(Html.fromHtml("<i>Distance:</i> " + location.milesInfo)); // or for older api
								}
							}
						}
					} else {
						locationDistance.setVisibility(View.GONE);
					}

				updateLMXTable();
				}
			});
			}
		}).start();
	}

	private void updateLMXTable() {
		try {
			Collections.sort(machines, new Comparator<Machine>() {
				public int compare(Machine m1, Machine m2) {
				return m1.name.replaceAll("^(?i)The ", "").compareTo(m2.name.replaceAll("^(?i)The ", ""));
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

		new Thread(new Runnable() {
			public void run() {
			PBMApplication app = getPBMApplication();
			location = app.getLocation(location.id);
			lmxes = location.getLmxes(LocationDetail.this);
			machines = location.getMachines(LocationDetail.this);

			loadLocationData();
			}
		}).start();
	}

    @SuppressWarnings("deprecation")
	public View getLMXView(final LocationMachineXref lmx, LinearLayout lmxTable) {
		MachineViewHolder holder;
		LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());

		View row = layoutInflater.inflate(R.layout.machine_condition_view, lmxTable, false);
		holder = new MachineViewHolder();
		holder.name = (TextView) row.findViewById(R.id.machine_info);
		holder.machineSelectButton = (ImageView) row.findViewById(R.id.machineSelectButton);
		holder.condition = (TextView) row.findViewById(R.id.machine_condition);

		row.setTag(holder);

		Machine machine = lmx.getMachine(this);

        if (Build.VERSION.SDK_INT >= 24) {
            holder.name.setText(Html.fromHtml("<b>" + machine.name + "</b>" + " " + "<i>" + machine.metaData() + "</i>",Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
        } else {
            holder.name.setText(Html.fromHtml("<b>" + machine.name + "</b>" + " " + "<i>" + machine.metaData() + "</i>")); // or for older api
        }

		String conditionText = "";
		if (!lmx.condition.equals("null") && !lmx.condition.equals("")) {
			conditionText += lmx.condition;
			if (!lmx.conditionDate.equals("null") && !lmx.condition.equals("")) {
				conditionText += "<br /><i>&nbsp;" + getBaseContext().getString(R.string.updated_on) + "</i> " + lmx.conditionDate;
			}

			String lastUpdatedByUsername = lmx.lastUpdatedByUsername;
			if(lastUpdatedByUsername != null && !lastUpdatedByUsername.isEmpty()) {
				conditionText += " by<b> " + lastUpdatedByUsername + "</b>";
			}

            if (Build.VERSION.SDK_INT >= 24) {
                holder.condition.setText(Html.fromHtml(conditionText,Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
            } else {
                holder.condition.setText(Html.fromHtml(conditionText)); // or for older api
            }

		} else {
			holder.condition.setVisibility(View.GONE);
		}

		row.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
			Intent myIntent = new Intent();
			PBMApplication app = getPBMApplication();
			myIntent.putExtra("lmx", lmx);
			myIntent.setClassName("com.pbm", "com.pbm.LocationMachineEdit");

			startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		return row;
	}

	class MachineViewHolder {
		TextView name;
		ImageView machineSelectButton;
		TextView condition;
	}
}
