package com.pbm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class PBMApplication extends Application {

	private long dataLoadTimestamp;
	private android.location.Location location;

	public long getDataLoadTimestamp() {
		return dataLoadTimestamp;
	}

	public void setDataLoadTimestamp(long dataLoadTimestamp) {
		this.dataLoadTimestamp = dataLoadTimestamp;
	}

	public android.location.Location getLocation() {
		return this.location;
	}

	public void setLocation(android.location.Location location) {
		this.location = location;
		Log.d("com.pbm.location", "set location to " + location);
	}

	public enum TrackerName {
		APP_TRACKER
	}

	private HashMap<Integer, com.pbm.Location> locations = new HashMap<>();
	private HashMap<Integer, com.pbm.LocationType> locationTypes = new HashMap<>();
	private HashMap<Integer, com.pbm.Machine> machines = new HashMap<>();
	private HashMap<Integer, com.pbm.LocationMachineXref> lmxes = new HashMap<>();
	private HashMap<Integer, com.pbm.LocationMachineConditions> lmxConditions = new HashMap<>();
	private HashMap<Integer, com.pbm.Zone> zones = new HashMap<>();
	private HashMap<Integer, com.pbm.Region> regions = new HashMap<>();
	private HashMap<Integer, com.pbm.Operator> operators = new HashMap<>();

	private HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

	synchronized Tracker getTracker() {
		if (!mTrackers.containsKey(TrackerName.APP_TRACKER)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = analytics.newTracker(R.xml.app_tracker);
			mTrackers.put(TrackerName.APP_TRACKER, t);
		}

		return mTrackers.get(TrackerName.APP_TRACKER);
	}

	public void setLocations(HashMap<Integer, com.pbm.Location> locations) {
		this.locations = locations;
	}

	public void setLocation(Integer index, com.pbm.Location location) {
		this.locations.put(index, location);
	}

	public Region getRegion() {
		return getRegion(getSharedPreferences(PinballMapActivity.PREFS_NAME, 0).getInt("region", -1));
	}

	public HashMap<Integer, com.pbm.Location> getLocations() {
		return locations;
	}

	public void setMachines(HashMap<Integer, com.pbm.Machine> machines) {
		this.machines = machines;
	}

	HashMap<Integer, com.pbm.Machine> getMachines() {
		return machines;
	}

	public String[] getMachineNames() {
		HashMap<Integer, com.pbm.Machine> machines = getMachines();

		String names[] = new String[machines.size()];

		int i = 0;
		for (Machine machine : machines.values()) {
			names[i] = machine.name;

			i++;
		}

		Arrays.sort(names);

		return names;
	}

	public String[] getMachineNamesWithMetadata() {
		HashMap<Integer, com.pbm.Machine> machines = getMachines();

		String names[] = new String[machines.size()];

		int i = 0;
		for (Machine machine : machines.values()) {
			names[i] = machine.name + " (" + machine.manufacturer + " - " + machine.year + ")";

			i++;
		}

		Arrays.sort(names);

		return names;
	}

	public void setZones(HashMap<Integer, com.pbm.Zone> zones) {
		this.zones = zones;
	}

	public HashMap<Integer, com.pbm.LocationMachineXref> getLmxes() {
		return lmxes;
	}

	public void setLmxes(HashMap<Integer, com.pbm.LocationMachineXref> lmxes) {
		this.lmxes = lmxes;
	}

	public void setLmxConditions(HashMap<Integer, LocationMachineConditions> lmxConditions) {
		this.lmxConditions = lmxConditions;
	}

	public void setLmx(com.pbm.LocationMachineXref lmx) {
		this.lmxes.put(lmx.id, lmx);
	}

	public void removeLmx(LocationMachineXref lmx) {
		this.lmxes.remove(lmx.id);
	}

	public HashMap<Integer, LocationMachineConditions> getLmxConditionsMap() {
		return lmxConditions;
	}

	public ArrayList<LocationMachineConditions> getLmxConditions() {
		return new ArrayList<>(lmxConditions.values());
	}

	public LocationMachineConditions getLmxConditionsByID(Integer id) {
		return this.lmxConditions.get(id);
	}

	public void removeLocationMachine(Integer id) {
		lmxConditions.remove(id);
	}

	public HashMap<Integer, com.pbm.Zone> getZones() {
		return zones;
	}

	void addLocation(Integer id, Location location) {
		this.locations.put(id, location);
	}

	public void addMachine(Integer id, Machine machine) {
		this.machines.put(id, machine);
	}

	void addOperator(Integer id, Operator operator) { this.operators.put(id, operator); }

	public Operator getOperator(Integer id) { return operators.get(id); }

	public HashMap<Integer, com.pbm.Operator> getOperators() { return operators; }

	void addLocationType(Integer id, LocationType name) { this.locationTypes.put(id, name); }

	public LocationType getLocationType(Integer id) {
		return locationTypes.get(id);
	}

	public HashMap<Integer, com.pbm.LocationType> getLocationTypes() {
		return locationTypes;
	}

	public void setLocationTypes(HashMap<Integer, com.pbm.LocationType> locationTypes) {
		this.locationTypes = locationTypes;
	}

	public void addLocationMachineXref(Integer id, LocationMachineXref lmx) {
		this.lmxes.put(id, lmx);
	}

	public void addLocationMachineConditions(Integer id, LocationMachineConditions locationMachineConditions) {
		this.lmxConditions.put(id, locationMachineConditions);
	}

	public LocationMachineXref getLmxFromMachine(Machine machine, List<LocationMachineXref> lmxes) {
		for (LocationMachineXref lmx : lmxes) {
			if (lmx.machineID == machine.id) {
				return lmx;
			}
		}

		return null;
	}

	public LocationMachineXref getLmx(Integer id) {
		return lmxes.get(id);
	}

	public int numMachinesForLocation(Location location) {
		int numMachines = 0;
		for (LocationMachineXref lmx : getLmxes().values()) {
			if (lmx.locationID == location.id) {
				numMachines += 1;
			}
		}

		return numMachines;
	}

	public Machine getMachine(Integer id) {
		return machines.get(id);
	}

	public Machine getMachineByName(String name) {
		ArrayList<Machine> machines = getMachineValues(true);
		for (Object baseMachine : machines) {
			Machine machine = (Machine) baseMachine;
			if (machine.name.equalsIgnoreCase(name)) {
				return machine;
			}
		}

		return null;
	}

	public Location getLocationByName(String name) {
		List<Location> locations = getLocationValues();
		for (Location location : locations) {
			if (location.name.equals(name)) {
				return location;
			}
		}
		return null;
	}

	public Location getLocation(Integer id) {
		return locations.get(id);
	}

	public Region getRegion(Integer id) {
		return regions.get(id);
	}

	void addZone(Integer id, Zone zone) {
		this.zones.put(id, zone);
	}

	public void addRegion(Integer id, Region region) {
		this.regions.put(id, region);
	}

	public void setRegions(HashMap<Integer, com.pbm.Region> regions) {
		this.regions = regions;
	}

	public HashMap<Integer, com.pbm.Region> getRegions() {
		return regions;
	}

	public ArrayList<Region> getRegionValues() {
		ArrayList<Region> regionValues = new ArrayList<>(getRegions().values());

		Collections.sort(regionValues, new Comparator<Region>() {
			public int compare(Region r1, Region r2) {
				return r1.formalName.compareTo(r2.formalName);
			}
		});

		return regionValues;
	}

	public ArrayList<Location> getLocationValues() {
		ArrayList<Location> locationValues = new ArrayList<>(getLocations().values());

		Collections.sort(locationValues, new Comparator<Location>() {
			public int compare(Location l1, Location l2) {
				return l1.name.compareTo(l2.name);
			}
		});

		return locationValues;
	}

	public ArrayList<Machine> getMachineValues(boolean displayAllMachines) {
		ArrayList<Machine> machineValues = new ArrayList<>();

		for (Machine machine : getMachines().values()) {
			if (displayAllMachines || machine.existsInRegion) {
				machineValues.add(machine);
			}
		}

		Collections.sort(machineValues, new Comparator<Object>() {
			public int compare(Object lhs, Object rhs) {
				Machine m1 = (Machine) lhs;
				Machine m2 = (Machine) rhs;

				return m1.name.replaceAll("^(?i)The ", "").compareTo(m2.name.replaceAll("^(?i)The ", ""));
			}
		});

		return machineValues;
	}

	public String requestWithAuthDetails(String origRequest) {
		final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
		PreferenceManager.setDefaultValues(this, PinballMapActivity.PREFS_NAME, 0, R.xml.preferences, false);
		String authToken = settings.getString("authToken", "");
		String email = settings.getString("email", "");

		String authDetails = "user_email=" + email + ";user_token=" + authToken;

		return origRequest + (origRequest.indexOf("?") == -1 ? "?" : ";") + authDetails;
	}

	public void initializeData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		dataLoadTimestamp = System.currentTimeMillis();
		Log.d("com.pbm", "initializing data");
		initializeAllMachines();
		initializeLocations();
		initializeLocationTypes();
		initializeZones();
		initializeOperators();
	}

	void initializeOperators() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		operators.clear();

		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionBase + "operators.json"), "GET"
		).get();
		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray types = jsonObject.getJSONArray("operators");

		for (int i = 0; i < types.length(); i++) {
			try {
				JSONObject type = types.getJSONObject(i);
				String name = type.getString("name");
				String id = type.getString("id");

				if ((id != null) && (name != null)) {
					addOperator(Integer.parseInt(id), new Operator(Integer.parseInt(id), name));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void initializeLocationTypes() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		locationTypes.clear();

		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionlessBase + "location_types.json"), "GET"
		).get();
		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray types = jsonObject.getJSONArray("location_types");

		for (int i = 0; i < types.length(); i++) {
			try {
				JSONObject type = types.getJSONObject(i);
				String name = type.getString("name");
				String id = type.getString("id");

				if ((id != null) && (name != null)) {
					addLocationType(Integer.parseInt(id), new LocationType(Integer.parseInt(id), name));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void initializeAllMachines() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		machines.clear();

		String json = new RetrieveJsonTask().execute(
            requestWithAuthDetails(PinballMapActivity.regionlessBase + "machines.json"), "GET"
		).get();

		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray machines = jsonObject.getJSONArray("machines");

		for (int i = 0; i < machines.length(); i++) {
			try {
				JSONObject machine = machines.getJSONObject(i);
				String name = machine.getString("name");
				String id = machine.getString("id");
				String year = machine.getString("year");
				String manufacturer = machine.getString("manufacturer");

				if ((id != null) && (name != null)) {
					addMachine(Integer.parseInt(id), new Machine(Integer.parseInt(id), name, year, manufacturer, false));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void initializeZones() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		zones.clear();

		String json = new RetrieveJsonTask().execute(
            requestWithAuthDetails(PinballMapActivity.regionBase + "zones.json"), "GET"
        ).get();
		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray zones = jsonObject.getJSONArray("zones");

		for (int i = 0; i < zones.length(); i++) {
			JSONObject zone = zones.getJSONObject(i);

			String name = zone.getString("name");
			String id = zone.getString("id");
			Boolean isPrimary = zone.getBoolean("is_primary");

			if ((id != null) && (name != null)) {
				addZone(Integer.parseInt(id), new Zone(Integer.parseInt(id), name, isPrimary ? 1 : 0));
			}
		}
	}

	void initializeLocations() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		lmxes.clear();
		locations.clear();

		String json = new RetrieveJsonTask().execute(
            requestWithAuthDetails(PinballMapActivity.regionBase + "locations.json"), "GET"
        ).get();
		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray locations = jsonObject.getJSONArray("locations");

		for (int i = 0; i < locations.length(); i++) {
			JSONObject location = locations.getJSONObject(i);

			int id = location.getInt("id");
			String name = location.getString("name");
			String lat = location.getString("lat");
			String lon = location.getString("lon");
			String street = location.getString("street");
			String city = location.getString("city");
			String zip = location.getString("zip");
			String phone = location.getString("phone");
			String state = location.getString("state");
			String website = location.getString("website");
			String lastUpdatedByUsername = location.getString("last_updated_by_username");

			String dateLastUpdated = null;
			if (location.has("date_last_updated") && !location.getString("date_last_updated").equals("null")) {
				dateLastUpdated = location.getString("date_last_updated");
			}

			int zoneID = 0;
			if (location.has("zone_id") && !location.getString("zone_id").equals("null")) {
				zoneID = location.getInt("zone_id");
			}

			int locationTypeID = 0;
			if (location.has("location_type_id") && !location.getString("location_type_id").equals("null")) {
				locationTypeID = location.getInt("location_type_id");
			}

			int operatorID = 0;
			if (location.has("operator_id") && !location.getString("operator_id").equals("null")) {
				operatorID = location.getInt("operator_id");
			}

			if ((name != null) && (lat != null) && (lon != null)) {
				Location newLocation =
						new com.pbm.Location(id, name, lat, lon, zoneID, street, city, state, zip,
								phone, locationTypeID, website, operatorID, dateLastUpdated,
								lastUpdatedByUsername);
				addLocation(id, newLocation);
			}

			JSONArray lmxes = null;
			if (location.has("location_machine_xrefs")) {
				lmxes = location.getJSONArray("location_machine_xrefs");
			}

			if (lmxes != null && lmxes.length() > 0) {
				for (int x = 0; x < lmxes.length(); x++) {
					JSONObject lmx = lmxes.getJSONObject(x);

					int lmxID = lmx.getInt("id");
					int lmxLocationID = lmx.getInt("location_id");
					int machineID = lmx.getInt("machine_id");
					String condition = lmx.getString("condition");
					String conditionDate = lmx.getString("condition_date");

					Machine machine = getMachine(machineID);

					if (machine != null) {
						machine.setExistsInRegion(true);

						addLocationMachineXref(
							lmxID,
							new com.pbm.LocationMachineXref(lmxID, lmxLocationID, machineID, condition, conditionDate)
						);
						loadConditions(lmx, lmxID, lmxLocationID, machineID);
					}
				}
			}
		}
	}

	void loadConditions(JSONObject lmx, int lmxID, int lmxLocationID, int machineID) throws JSONException {
		if (lmx.has("machine_conditions")) {
			JSONArray conditions = lmx.getJSONArray("machine_conditions");
			@SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			ArrayList<Condition> conditionList = new ArrayList<>();
			for (int conditionIndex = 0; conditionIndex < conditions.length(); conditionIndex++) {
				JSONObject pastCondition = conditions.getJSONObject(conditionIndex);
				try {
					conditionList.add(new Condition(pastCondition.getInt("id"),
						dateFormat.parse(pastCondition.getString("updated_at")),
						pastCondition.getString("comment"),
						lmxID,
						pastCondition.getString("username")
					));
					Log.d("lmxconditions", pastCondition.getString("updated_at") + " " + pastCondition.getString("comment"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			LocationMachineConditions machineConditions = new LocationMachineConditions(lmxID, machineID, lmxLocationID,
					conditionList);
			addLocationMachineConditions(lmxID, machineConditions);
		}
	}

	public boolean initializeRegions() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(PinballMapActivity.regionlessBase + "regions.json", "GET").get();
		if (json == null) {
			return false;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray regions = jsonObject.getJSONArray("regions");

		for (int i = 0; i < regions.length(); i++) {
			JSONObject region = regions.getJSONObject(i);
			String id = region.getString("id");
			String name = region.getString("name");
			String formalName = region.getString("full_name");
			String motd = region.getString("motd");
			String lat = region.getString("lat");
			String lon = region.getString("lon");
			List<String> emailAddresses = new ArrayList<>();

			if (region.has("all_admin_email_address")) {
				JSONArray jsonEmailAddresses = region.getJSONArray("all_admin_email_address");
				for (int x = 0; x < jsonEmailAddresses.length(); x++) {
					emailAddresses.add(jsonEmailAddresses.getString(x));
				}
			}
			addRegion(Integer.parseInt(id), new Region(Integer.parseInt(id), name, formalName, motd, lat, lon, emailAddresses));
		}
		return true;
	}

}