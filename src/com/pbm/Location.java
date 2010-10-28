package com.pbm;

import java.io.Serializable;
import java.lang.String;

public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	public int locationNo;
	public String name;
	public String street1;
	public String city;
	public String state;
	public String zip;
	public String phone;
	public String lat;
	public String lon;
	public String zone;
	public int zoneNo;
	public int numMachines;
	public float distanceFromYou;
	public String milesInfo;

	public Location(int nLocationNo, String nName, String nLat, String nLon, String nZone, int nNumMachines, 
			int nZoneNo, String nStreet1, String nCity, String nState, String nZip, String nPhone, float nDistance) {
		locationNo = nLocationNo;
		name = nName;
		lat = nLat;
		lon = nLon;
		zone = nZone;
		zoneNo = nZoneNo;
		numMachines = nNumMachines;
		state = nState;
		street1 = nStreet1;
		city = nCity;
		zip = nZip;
		phone = nPhone;
		distanceFromYou = nDistance;
	}

	public void updateAddress(String nStreet1, String nCity, String nState, String nZip, String nPhone) {
		street1 = nStreet1;
		city = nCity;
		state = nState;
		zip = nZip;
		phone = nPhone;
	}
	
	public void setDistance(float nDistance) {
		distanceFromYou = nDistance;
	}
	
	public void setMilesInfo(String nMilesInfo) {
		milesInfo = nMilesInfo;
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