package com.pbm;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id, zoneID, locationTypeID, operatorID;
	public String name, street, city, state, zip, phone, lat, lon, website, milesInfo,
			lastUpdatedByUsername, dateLastUpdated;
	public float distanceFromYou;

	public Location(int id, String name, String lat, String lon, int zoneID, String street,
					String city, String state, String zip, String phone, int locationTypeID,
					String website, int operatorID, String dateLastUpdated,
					String lastUpdatedByUsername
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
	}

	protected static Comparator<Location> byNearestDistance = new Comparator<com.pbm.Location>() {
		public int compare(com.pbm.Location l1, com.pbm.Location l2) {
			Float distanceFromYou1 = l1.distanceFromYou;
			Float distanceFromYou2 = l2.distanceFromYou;
			return distanceFromYou1.compareTo(distanceFromYou2);
		}
	};

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
	public void setDistance(float distance) {
		this.distanceFromYou = distance;
	}

	public void setMilesInfo(String milesInfo) {
		this.milesInfo = milesInfo;
	}

	public int numMachines(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();
		return app.numMachinesForLocation(this);
	}

	public String toString() {
		return milesInfo != null ? name + " " + milesInfo : name;
	}

	public List<LocationMachineXref> getLmxes(PinballMapActivity activity) {
		List<LocationMachineXref> locationLmxes = new ArrayList<>();
		PBMApplication app = activity.getPBMApplication();

		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.locationID == id) {
				locationLmxes.add(lmx);
			}
		}

		return locationLmxes;
	}

	public TreeMap<Integer, LocationMachineXref> getLMXMap(PinballMapActivity activity) {
		TreeMap<Integer, LocationMachineXref> lmxes = new TreeMap<>();
		PBMApplication app = activity.getPBMApplication();
		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.locationID == id) {
				lmxes.put(lmx.machineID, lmx);
			}
		}
		return lmxes;
	}

	public List<Machine> getMachines(PinballMapActivity activity) {
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
}