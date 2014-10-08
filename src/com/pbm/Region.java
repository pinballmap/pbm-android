package com.pbm;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Region {
	public int id;
	public String name, formalName, motd, lat, lon;
	public float distanceFromYou;
	List<String>emailAddresses = new ArrayList<String>();

	public Region(int id, String name, String formalName, String motd, String lat, String lon, List<String> emailAddresses) {
	    this.id = id;
	    this.name = name;
	    this.formalName = formalName;
	    this.motd = motd;
	    this.emailAddresses = emailAddresses;
	    this.lat = lat;
	    this.lon = lon;
	}
	
	public String toString() {
		return formalName;
	}

	public void setDistance(float distance) {
		this.distanceFromYou = distance;
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