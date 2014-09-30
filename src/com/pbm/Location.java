package com.pbm;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public String street;
	public String city;
	public String state;
	public String zip;
	public String phone;
	public String lat;
	public String lon;
	public String website;
	public int zoneID;
	public int locationTypeID;
	public float distanceFromYou;
	public String milesInfo;

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
	
	public int numMachines(Activity activity) {
		PBMApplication app = (PBMApplication) activity.getApplication();

		return app.numMachinesForLocation(this);
	}

	public String toString() {
		return milesInfo != null ? name + " " + milesInfo : name;
	}
	
	public List<LocationMachineXref> getLmxes(Activity activity) {
		List<LocationMachineXref> locationLmxes = new ArrayList<LocationMachineXref>();
		PBMApplication app = (PBMApplication) activity.getApplication();
		
		for (LocationMachineXref lmx : app.getLmxes().values()) {
			if (lmx.locationID == id) {
				locationLmxes.add(lmx);
			}
		}

		return locationLmxes;
	}

	public List<Machine> getMachines(Activity activity) {
		List<Machine> machinesFromLmxes = new ArrayList<Machine>();
		PBMApplication app = (PBMApplication) activity.getApplication();
		
		for (LocationMachineXref lmx : getLmxes(activity)) {
			machinesFromLmxes.add(app.getMachine(lmx.machineID));
		}

		return machinesFromLmxes;
	}

	public LocationType getLocationType(Activity activity) {
		PBMApplication app = (PBMApplication) activity.getApplication();
		
		return app.getLocationType(locationTypeID);
	}
	
	public void removeMachine(Activity activity, LocationMachineXref lmx) {
		PBMApplication app = (PBMApplication) activity.getApplication();
		
		app.removeLmx(lmx);
	}
	
	public android.location.Location toAndroidLocation() {
		android.location.Location mockLocation = new android.location.Location("");
			
		try{
			mockLocation.setLatitude(Double.valueOf(lat));
			mockLocation.setLongitude(Double.valueOf(lon));
		} catch (java.lang.NumberFormatException nfe) {}
		
		return mockLocation;
	}
}