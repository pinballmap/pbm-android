package com.pbm;

import java.io.Serializable;
import java.lang.String;

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
}