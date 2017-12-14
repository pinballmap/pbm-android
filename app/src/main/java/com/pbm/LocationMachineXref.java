package com.pbm;

import android.app.Activity;
import android.database.Cursor;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationMachineXref implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id, machineID, locationID;
	private String condition, conditionDate, lastUpdatedByUsername;

	public LocationMachineXref(int id, int locationID, int machineID, String condition, String conditionDate, String lastUpdatedByUsername) throws ParseException {
		this.id = id;
		this.locationID = locationID;
		this.machineID = machineID;
		this.condition = condition;
		this.lastUpdatedByUsername = lastUpdatedByUsername;

		String formattedConditionDate = "";
		if (conditionDate != null && !conditionDate.equalsIgnoreCase("null") && !conditionDate.isEmpty()){
			formattedConditionDate = conditionDate;
		}
		this.conditionDate = formattedConditionDate;
	}

	public Location getLocation(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();
		return app.getLocation(locationID);
	}

	Machine getMachine(Activity activity) {
		return ((PBMApplication) activity.getApplicationContext()).getMachine(machineID);
	}

	void addScore(Activity activity, MachineScore score) {
		PBMApplication app = (PBMApplication) activity.getApplication();
		app.addMachineScore(score);
	}

	void setCondition(Activity activity, String condition, String lastUpdatedByUsername) {
		this.condition = condition;
		this.lastUpdatedByUsername = lastUpdatedByUsername;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		this.conditionDate = format.format(new Date());

		PBMApplication app = (PBMApplication) activity.getApplication();
		app.updateLmx(this);
	}

	static LocationMachineXref newFromDBCursor(Cursor cursor) throws ParseException {
		return new LocationMachineXref(
			cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationMachineXrefContract.COLUMN_ID)),
			cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationMachineXrefContract.COLUMN_LOCATION_ID)),
			cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationMachineXrefContract.COLUMN_MACHINE_ID)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationMachineXrefContract.COLUMN_CONDITION)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationMachineXrefContract.COLUMN_CONDITION_DATE)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationMachineXrefContract.COLUMN_LAST_UPDATED_BY_USERNAME))
		);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	String getConditionDate() {
		if (
			!conditionDate.equals("null") &&
			!conditionDate.equals("") &&
			conditionDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")
		) {
			DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy");
			Date dateCreatedAt = null;
			try {
				dateCreatedAt = inputDF.parse(conditionDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return outputDF.format(dateCreatedAt);
		} else {
			return conditionDate;
		}
	}

	String getLastUpdatedByUsername() {
		return lastUpdatedByUsername;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	int getMachineID() {
		return machineID;
	}

	int getLocationID() {
		return locationID;
	}
}