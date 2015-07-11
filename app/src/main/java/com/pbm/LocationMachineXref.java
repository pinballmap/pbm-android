package com.pbm;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationMachineXref implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public int machineID;
	public int locationID;
	public String condition;
	public String conditionDate;

	public LocationMachineXref(int id, int locationID, int machineID, String condition, String conditionDate) {
		this.id = id;
		this.locationID = locationID;
		this.machineID = machineID;
		this.condition = condition;
		this.conditionDate = conditionDate;
	}

	public Location getLocation(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();
		return app.getLocation(locationID);
	}

	public Machine getMachine(Activity activity) {
		return ((PBMApplication) activity.getApplicationContext()).getMachine(machineID);
	}


	public void setCondition(Activity activity, String condition) {
		this.condition = condition;

		@SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		this.conditionDate = format.format(new Date());

		PBMApplication app = (PBMApplication) activity.getApplication();
		app.setLmx(this);
	}
}