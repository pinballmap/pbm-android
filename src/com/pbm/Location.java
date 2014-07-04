package com.pbm;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

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
	public int zoneID;
	public float distanceFromYou;
	public String milesInfo;

	public Location(int id, String name, String lat, String lon, int zoneID, String street, String city, String state, String zip, String phone) {
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
		if (milesInfo != null) {
			String formattedName = name + milesInfo;
			milesInfo = null;
			return formattedName;
		}
		
		return name;
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
			Log.i("!!!!!!", Integer.toString(lmx.machineID));
			machinesFromLmxes.add(app.getMachine(lmx.machineID));
		}

		return machinesFromLmxes;
	}
	
}