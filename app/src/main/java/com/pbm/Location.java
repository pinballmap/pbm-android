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
	public int id, zoneID, locationTypeID, operatorID;
	public String name, street, city, state, zip, phone, lat, lon, website, milesInfo,
			lastUpdatedByUsername, dateLastUpdated, description;
	public float distanceFromYou;

	public Location(int id, String name, String lat, String lon, int zoneID, String street,
					String city, String state, String zip, String phone, int locationTypeID,
					String website, int operatorID, String dateLastUpdated,
					String lastUpdatedByUsername, String description
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
	}

	protected static Comparator<Location> byNearestDistance = new Comparator<com.pbm.Location>() {
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

	public void setLocationTypeID(int locationTypeID) {
		this.locationTypeID = locationTypeID;
	}

	public void setOperatorID(int operatorID) { this.operatorID = operatorID; }

	public void setDistance(android.location.Location location) {
		float distance = location.distanceTo(toAndroidLocation());
		this.distanceFromYou = distance * PinballMapActivity.METERS_TO_MILES;

		NumberFormat formatter = new DecimalFormat(".00");
		setMilesInfo(formatter.format(this.distanceFromYou) + " miles");
	}

	public void setMilesInfo(String milesInfo) {
		this.milesInfo = milesInfo;
	}

	public int numMachines(PinballMapActivity activity) throws ParseException {
		PBMApplication app = activity.getPBMApplication();
		return app.numMachinesForLocation(this);
	}

	public String toString() {
		return milesInfo != null ? name + " " + milesInfo : name;
	}

	public List<LocationMachineXref> getLmxes(PinballMapActivity activity) throws ParseException {
		List<LocationMachineXref> locationLmxes = new ArrayList<>();
		PBMApplication app = activity.getPBMApplication();

		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.locationID == id) {
				locationLmxes.add(lmx);
			}
		}

		return locationLmxes;
	}

	public List<Machine> getMachines(PinballMapActivity activity) throws ParseException {
		List<Machine> machinesFromLmxes = new ArrayList<>();
		PBMApplication app = activity.getPBMApplication();

		for (LocationMachineXref lmx : getLmxes(activity)) {
			machinesFromLmxes.add(app.getMachine(lmx.machineID));
		}

		return machinesFromLmxes;
	}

	public LocationType getLocationType(PinballMapActivity activity) {
		return activity.getPBMApplication().getLocationType(locationTypeID);
	}

	public Operator getOperator(PinballMapActivity activity) {
		return activity.getPBMApplication().getOperator(operatorID);
	}

	public void removeMachine(PinballMapActivity activity, LocationMachineXref lmx) {
		activity.getPBMApplication().removeLmx(lmx);
	}

	public android.location.Location toAndroidLocation() {
		android.location.Location mockLocation = new android.location.Location("");

		try {
			mockLocation.setLatitude(Double.valueOf(lat));
			mockLocation.setLongitude(Double.valueOf(lon));
		} catch (java.lang.NumberFormatException nfe) {
			nfe.printStackTrace();
		}

		return mockLocation;
	}

	public static com.pbm.Location newFromDBCursor(Cursor cursor) {
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
					cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationContract.COLUMN_DESCRIPTION))
		);
	}
}