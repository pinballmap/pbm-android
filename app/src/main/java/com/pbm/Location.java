package com.pbm;

import android.database.Cursor;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id, zoneID, locationTypeID, operatorID;
	private String name, street, city, state, zip, phone, lat, lon, website, milesInfo, lastUpdatedByUsername, dateLastUpdated, description, numMachines;
	private float distanceFromYou;

	String getLat() {
		return lat;
	}
	String getLon() {
		return lon;
	}
	String getMilesInfo() {
		return milesInfo;
	}
	String getNumMachines() { return numMachines; }

	float getDistanceFromYou() { return distanceFromYou; }

	public Location(int id, String name, String lat, String lon, int zoneID, String street,
					String city, String state, String zip, String phone, int locationTypeID,
					String website, int operatorID, String dateLastUpdated,
					String lastUpdatedByUsername, String description, String numMachines
	) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.zoneID = zoneID;
		this.state = state;
		this.street = street;
		this.city = city;
		this.zip = zip;
		this.phone = phone;
		this.website = website;
		this.locationTypeID = locationTypeID;
		this.operatorID = operatorID;
		this.dateLastUpdated = dateLastUpdated;
		this.lastUpdatedByUsername = lastUpdatedByUsername;
		this.description = description;
		this.numMachines = numMachines;
	}

	static Comparator<Location> byNearestDistance = new Comparator<com.pbm.Location>() {
		public int compare(com.pbm.Location l1, com.pbm.Location l2) {
			Float distanceFromYou1 = l1.distanceFromYou;
			Float distanceFromYou2 = l2.distanceFromYou;
			return distanceFromYou1.compareTo(distanceFromYou2);
		}
	};

	public void setDescription(String description) { this.description = description; }

	public void setWebsite(String website) {
		this.website = website;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setNumMachines(String numMachines) { this.numMachines = numMachines; }

	void setLocationTypeID(int locationTypeID) {
		this.locationTypeID = locationTypeID;
	}

	void setOperatorID(int operatorID) { this.operatorID = operatorID; }

	public void setDistance(android.location.Location location) {
		float distance = location.distanceTo(toAndroidLocation());
		this.distanceFromYou = distance * PinballMapActivity.METERS_TO_MILES;

		NumberFormat formatter = new DecimalFormat(".00");
		setMilesInfo(formatter.format(this.distanceFromYou) + " miles");
	}

	private void setMilesInfo(String milesInfo) {
		this.milesInfo = milesInfo;
	}

	public String toString() {
		return milesInfo != null ? name + " " + milesInfo : name;
	}

	List<LocationMachineXref> getLmxes(PinballMapActivity activity) throws ParseException {
		List<LocationMachineXref> locationLmxes = new ArrayList<>();
		PBMApplication app = activity.getPBMApplication();

		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.getLocationID() == id) {
				locationLmxes.add(lmx);
			}
		}

		return locationLmxes;
	}

	List<Machine> getMachines(PinballMapActivity activity) throws ParseException {
		List<Machine> machinesFromLmxes = new ArrayList<>();
		PBMApplication app = activity.getPBMApplication();

		for (LocationMachineXref lmx : getLmxes(activity)) {
			machinesFromLmxes.add(app.getMachine(lmx.getMachineID()));
		}

		return machinesFromLmxes;
	}

	LocationType getLocationType(PinballMapActivity activity) {
		return activity.getPBMApplication().getLocationType(locationTypeID);
	}

	Operator getOperator(PinballMapActivity activity) {
		return activity.getPBMApplication().getOperator(operatorID);
	}

	void removeMachine(PinballMapActivity activity, LocationMachineXref lmx) {
		activity.getPBMApplication().removeLmx(lmx);
	}

	private android.location.Location toAndroidLocation() {
		android.location.Location mockLocation = new android.location.Location("");

		try {
			mockLocation.setLatitude(Double.valueOf(lat));
			mockLocation.setLongitude(Double.valueOf(lon));
		} catch (java.lang.NumberFormatException nfe) {
			nfe.printStackTrace();
		}

		return mockLocation;
	}

	static com.pbm.Location newFromDBCursor(Cursor cursor) {
		return new com.pbm.Location(
			cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_ID)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_NAME)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_LAT)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_LON)),
			cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_ZONE_ID)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_STREET)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_CITY)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_STATE)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_ZIP)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_PHONE)),
			cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_LOCATION_TYPE_ID)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_WEBSITE)),
			cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_OPERATOR_ID)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_DATE_LAST_UPDATED)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_LAST_UPDATED_BY_USERNAME)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_DESCRIPTION)),
			cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_NUM_MACHINES))
		);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	int getZoneID() {
		return zoneID;
	}

	int getOperatorID() {
		return operatorID;
	}

	int getLocationTypeID() {
		return locationTypeID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	String getState() {
		return state;
	}

	String getZip() {
		return zip;
	}

	public String getPhone() {
		return phone;
	}

	public String getWebsite() {
		return website;
	}

	String getLastUpdatedByUsername() {
		return lastUpdatedByUsername;
	}

	void setLastUpdatedByUsername(String lastUpdatedByUsername) {
		this.lastUpdatedByUsername = lastUpdatedByUsername;
	}

	String getDateLastUpdated() {
		return dateLastUpdated;
	}

	void setDateLastUpdated(String dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	public String getDescription() {
		return description;
	}
}