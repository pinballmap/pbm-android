package com.pbm;

import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Region implements Serializable, JSONConverter<Region> {
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

	public Region() {
	}
	public Region fromJSON(JSONObject region) throws JSONException {
		this.id = region.getInt("id");
		this.name = region.getString("name");
		this.formalName = region.getString("full_name");
		this.motd = region.getString("motd");
		this.lat = region.getString("lat");
		this.lon = region.getString("lon");

		this.emailAddresses = null;

		if (region.has("all_admin_email_address")) {
			JSONArray jsonEmailAddresses = region.getJSONArray("all_admin_email_address");
			for (int x = 0; x < jsonEmailAddresses.length(); x++) {
				this.emailAddresses.add(jsonEmailAddresses.getString(x));
			}
		}
		return this;
	}

	@Override
	public String getJsonLabel() {
		return "regions";
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

	public List<LocationType> locationTypes(Activity activity) {
		PBMApplication app = (PBMApplication) activity.getApplication();

		List<LocationType> locationTypes = new ArrayList<LocationType>();

		ArrayList<Location> locations = app.getLocationValues();
		for (Location location: locations) {

			LocationType type = location.getLocationType(activity);
			
			if (type != null && !locationTypes.contains(type)) {
				locationTypes.add(type);
			}
		}
		
		return locationTypes;
	}
}