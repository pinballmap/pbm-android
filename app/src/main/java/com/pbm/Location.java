package com.pbm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id, zoneID, locationTypeID;
	public String name, street, city, state, zip, phone, lat, lon, website, milesInfo;
	public float distanceFromYou;

	public Location(int id, String name, String lat, String lon, int zoneID, String street, String city, String state, String zip, String phone, int locationTypeID, String website) {
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
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setLocationTypeID(int locationTypeID) {
		this.locationTypeID = locationTypeID;
	}

	public void setDistance(float distance) {
		this.distanceFromYou = distance;
	}

	public void setMilesInfo(String milesInfo) {
		this.milesInfo = milesInfo;
	}

	public int numMachines(PBMUtil activity) {
		PBMApplication app = activity.getPBMApplication();
		return app.numMachinesForLocation(this);
	}

	public String toString() {
		return milesInfo != null ? name + " " + milesInfo : name;
	}

	public List<LocationMachineXref> getLmxes(PBMUtil activity) {
		List<LocationMachineXref> locationLmxes = new ArrayList<LocationMachineXref>();
		PBMApplication app = activity.getPBMApplication();

		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.locationID == id) {
				locationLmxes.add(lmx);
			}
		}

		return locationLmxes;
	}

	public TreeMap<Integer, LocationMachineXref> getLMXMap(PBMUtil activity) {
		TreeMap<Integer, LocationMachineXref> lmxes = new TreeMap<Integer, LocationMachineXref>();
		PBMApplication app = activity.getPBMApplication();
		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.locationID == id) {
				lmxes.put(lmx.machineID, lmx);
			}
		}
		return lmxes;
	}

	public List<Machine> getMachines(PBMUtil activity) {
		List<Machine> machinesFromLmxes = new ArrayList<Machine>();
		PBMApplication app = activity.getPBMApplication();

		for (LocationMachineXref lmx : getLmxes(activity)) {
			machinesFromLmxes.add(app.getMachine(lmx.machineID));
		}

		return machinesFromLmxes;
	}

	public LocationType getLocationType(PBMUtil activity) {
		return activity.getPBMApplication().getLocationType(locationTypeID);
	}

	public void removeMachine(PBMUtil activity, LocationMachineXref lmx) {
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