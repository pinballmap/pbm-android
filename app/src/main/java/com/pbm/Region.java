package com.pbm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Region implements Serializable, JSONConverter<Region> {
	public int id;
	public String name;
	public String formalName;
	public String motd;
	private String lat;
	private String lon;
	public float distanceFromYou;
	private List<String> emailAddresses = new ArrayList<>();

	public Region(int id, String name, String formalName, String motd, String lat, String lon, List<String> emailAddresses) {
		this.id = id;
		this.name = name;
		this.formalName = formalName;
		this.motd = motd;
		this.emailAddresses = emailAddresses;
		this.lat = lat;
		this.lon = lon;
	}

	public Region() {}

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

		try {
			mockLocation.setLatitude(Double.valueOf(lat));
			mockLocation.setLongitude(Double.valueOf(lon));
		} catch (java.lang.NumberFormatException nfe) {
			nfe.printStackTrace();
		}

		return mockLocation;
	}

	public List<String> cities(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();

		List<String> cities = new ArrayList<>();

		ArrayList<Location> locations = app.getLocationValues();

		for (Location location : locations) {
			String city = location.city;

			if (city != null && !cities.contains(city)) {
				cities.add(city);
			}
		}

		return cities;
	}

	public List<Operator> operators(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();

		List<Operator> operators = new ArrayList<>();

		ArrayList<Location> locations = app.getLocationValues();
		for (Location location : locations) {

			Operator operator = location.getOperator(activity);

			if (operator != null && !operators.contains(operator)) {
				operators.add(operator);
			}
		}

		return operators;
	}

	public List<LocationType> locationTypes(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();

		List<LocationType> locationTypes = new ArrayList<>();

		ArrayList<Location> locations = app.getLocationValues();
		for (Location location : locations) {

			LocationType type = location.getLocationType(activity);

			if (type != null && !locationTypes.contains(type)) {
				locationTypes.add(type);
			}
		}

		return locationTypes;
	}
}