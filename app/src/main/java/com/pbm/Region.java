package com.pbm;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Region implements Serializable, JSONConverter<Region> {
	private int id;
	private String name, formalName, motd, lat, lon;
	private float distanceFromYou;
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

	android.location.Location toAndroidLocation() {
		android.location.Location mockLocation = new android.location.Location("");

		try {
			mockLocation.setLatitude(Double.valueOf(lat));
			mockLocation.setLongitude(Double.valueOf(lon));
		} catch (java.lang.NumberFormatException nfe) {
			nfe.printStackTrace();
		}

		return mockLocation;
	}

	List<String> cities(PinballMapActivity activity) {
		PBMApplication app = activity.getPBMApplication();

		List<String> cities = new ArrayList<>();

		ArrayList<Location> locations = app.getLocationValues();

		for (Location location : locations) {
			String city = location.getCity();

			if (city != null && !cities.contains(city)) {
				cities.add(city);
			}
		}

		return cities;
	}

	List<Operator> operators(PinballMapActivity activity) {
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

	List<LocationType> locationTypes(PinballMapActivity activity) {
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

	static Region newFromDBCursor(Cursor cursor) {
		return new Region(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.RegionContract.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.RegionContract.COLUMN_NAME)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.RegionContract.COLUMN_FORMAL_NAME)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.RegionContract.COLUMN_MOTD)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.RegionContract.COLUMN_LAT)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.RegionContract.COLUMN_LON)),
				null
		);
	}

	String getFormalName() {
		return formalName;
	}

	String getLat() {
		return lat;
	}

	String getLon() {
		return lon;
	}

	float getDistanceFromYou() {
		return distanceFromYou;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String getMotd() {
		return motd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}