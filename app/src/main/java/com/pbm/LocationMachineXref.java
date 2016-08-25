package com.pbm;

import android.app.Activity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationMachineXref implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public int machineID;
	public int locationID;
	public String condition;
	public String conditionDate;
	public String lastUpdatedByUsername;

	public LocationMachineXref(int id, int locationID, int machineID, String condition, String conditionDate, String lastUpdatedByUsername) throws ParseException {
		this.id = id;
		this.locationID = locationID;
		this.machineID = machineID;
		this.condition = condition;
		this.lastUpdatedByUsername = lastUpdatedByUsername;

		String formattedConditionDate = "";
		if (conditionDate != null && !conditionDate.equalsIgnoreCase("null") && !conditionDate.isEmpty()){
			Date rawDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(conditionDate);
			formattedConditionDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(rawDate);
		}
		this.conditionDate = formattedConditionDate;
	}

	public Location getLocation(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();
		return app.getLocation(locationID);
	}

	public Machine getMachine(Activity activity) {
		return ((PBMApplication) activity.getApplicationContext()).getMachine(machineID);
	}

	public void addScore(Activity activity, MachineScore score) {
		PBMApplication app = (PBMApplication) activity.getApplication();
		app.addMachineScore(id, score);
	}

	public void setCondition(Activity activity, String condition, String lastUpdatedByUsername) {
		this.condition = condition;
		this.lastUpdatedByUsername = lastUpdatedByUsername;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		this.conditionDate = format.format(new Date());

		PBMApplication app = (PBMApplication) activity.getApplication();
		app.setLmx(this);
	}
}