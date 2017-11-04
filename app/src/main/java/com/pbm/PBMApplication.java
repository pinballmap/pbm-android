package com.pbm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class PBMApplication extends Application {

	private long dataLoadTimestamp;
	private android.location.Location location;
	private PBMDbHelper dbHelper;
	private SQLiteDatabase readableDB;
	private SQLiteDatabase writeableDB;

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
	}

	private enum TrackerName { APP_TRACKER }

	private HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

	public void setDbHelper(Context context) {
		this.dbHelper = new PBMDbHelper(context);
	}

	public PBMDbHelper getDbHelper() {
		return this.dbHelper;
	}

	public void setReadableDB(PBMDbHelper dbHelper) { this.readableDB = dbHelper.getReadableDatabase(); }

	public SQLiteDatabase getReadableDB() { return this.readableDB; }

	public void setWriteableDB(PBMDbHelper dbHelper) { this.writeableDB = dbHelper.getWritableDatabase(); }

	public SQLiteDatabase getWriteableDB() { return this.writeableDB; }

	synchronized Tracker getTracker() {
		if (!mTrackers.containsKey(TrackerName.APP_TRACKER)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = analytics.newTracker(R.xml.app_tracker);
			mTrackers.put(TrackerName.APP_TRACKER, t);
		}

		return mTrackers.get(TrackerName.APP_TRACKER);
	}

	public HashMap<Integer, Location> getLocations() {
		Cursor cursor = getReadableDB().query(
				PBMContract.LocationContract.TABLE_NAME,
				PBMContract.LocationContract.PROJECTION,
				null,
				null,
				null,
				null,
				null
		);

		@SuppressLint("UseSparseArrays") HashMap<Integer, Location> hashMap = new HashMap<>();
		while(cursor.moveToNext()) {
			com.pbm.Location location = com.pbm.Location.newFromDBCursor(cursor);
			hashMap.put(location.getId(), location);
		}
		cursor.close();

		return hashMap;
	}

	public HashMap<Integer, com.pbm.Machine> getMachines() {
		Cursor cursor = getReadableDB().query(
			PBMContract.MachineContract.TABLE_NAME,
			PBMContract.MachineContract.PROJECTION,
			null,
			null,
			null,
			null,
			null
		);

		@SuppressLint("UseSparseArrays") HashMap<Integer, Machine> hashMap = new HashMap<>();
		while(cursor.moveToNext()) {
			Machine machine = Machine.newFromDBCursor(cursor);
			hashMap.put(machine.getId(), machine);
		}
		cursor.close();

		return hashMap;
	}

	public HashMap<Integer, com.pbm.LocationMachineXref> getLmxes() throws ParseException {
		Cursor cursor = getReadableDB().query(
			PBMContract.LocationMachineXrefContract.TABLE_NAME,
			PBMContract.LocationMachineXrefContract.PROJECTION,
			null,
			null,
			null,
			null,
			null
		);

		@SuppressLint("UseSparseArrays") HashMap<Integer, LocationMachineXref> hashMap = new HashMap<>();
		while(cursor.moveToNext()) {
			LocationMachineXref lmx = LocationMachineXref.newFromDBCursor(cursor);
			hashMap.put(lmx.getId(), lmx);
		}
		cursor.close();

		return hashMap;
	}

	public ArrayList<MachineScore> getMachineScores() {
		Cursor cursor = getReadableDB().query(
			PBMContract.MachineScoreContract.TABLE_NAME,
			PBMContract.MachineScoreContract.PROJECTION,
			null,
			null,
			null,
			null,
			null
		);

		ArrayList<MachineScore> arrayList = new ArrayList<>();
		while(cursor.moveToNext()) {
			arrayList.add(MachineScore.newDBFromCursor(cursor));
		}
		cursor.close();

		return arrayList;
	}

	public LocationMachineConditions getLmxConditionsByID(Integer id) throws ParseException {
		LocationMachineXref lmx = getLmx(id);

		String selection = PBMContract.ConditionContract.COLUMN_LOCATION_MACHINE_XREF_ID + " = ?";
		String[] selectionArgs = { id.toString() };

		Cursor cursor = getReadableDB().query(
			PBMContract.ConditionContract.TABLE_NAME,
			PBMContract.ConditionContract.PROJECTION,
			selection,
			selectionArgs,
			null,
			null,
			null
		);

		ArrayList<Condition> conditions = new ArrayList<>();
		while(cursor.moveToNext()) {
			conditions.add(Condition.newFromDBCursor(cursor));
		}
		cursor.close();

		return new LocationMachineConditions(lmx.getId(), lmx.getMachineID(), lmx.getLocationID(), conditions);
	}

	public HashMap<Integer, com.pbm.Zone> getZones() {
		Cursor cursor = getReadableDB().query(
			PBMContract.ZoneContract.TABLE_NAME,
			PBMContract.ZoneContract.PROJECTION,
			null,
			null,
			null,
			null,
			null
		);

		@SuppressLint("UseSparseArrays") HashMap<Integer, Zone> hashMap = new HashMap<>();
		while(cursor.moveToNext()) {
			Zone zone = Zone.newFromDBCursor(cursor);
			hashMap.put(zone.getId(), zone);
		}
		cursor.close();

		return hashMap;
	}

	public HashMap<Integer, com.pbm.LocationType> getLocationTypes() {
		Cursor cursor = getReadableDB().query(
			PBMContract.LocationTypeContract.TABLE_NAME,
			PBMContract.LocationTypeContract.PROJECTION,
			null,
			null,
			null,
			null,
			null
		);

		@SuppressLint("UseSparseArrays") HashMap<Integer, LocationType> hashMap = new HashMap<>();
		while(cursor.moveToNext()) {
			LocationType locationType = LocationType.newFromDBCursor(cursor);
			hashMap.put(locationType.getId(), locationType);
		}
		cursor.close();

		return hashMap;
	}

	public HashMap<Integer, com.pbm.Region> getRegions() {
		Cursor cursor = getReadableDB().query(
			PBMContract.RegionContract.TABLE_NAME,
			PBMContract.RegionContract.PROJECTION,
			null,
			null,
			null,
			null,
			null
		);

		@SuppressLint("UseSparseArrays") HashMap<Integer, Region> hashMap = new HashMap<>();
		while(cursor.moveToNext()) {
			Region region = Region.newFromDBCursor(cursor);
			hashMap.put(region.getId(), region);
		}
		cursor.close();

		return hashMap;
	}

	public HashMap<Integer, com.pbm.Operator> getOperators() {
		Cursor cursor = getReadableDB().query(
			PBMContract.OperatorContract.TABLE_NAME,
			PBMContract.OperatorContract.PROJECTION,
			null,
			null,
			null,
			null,
			null
		);

		@SuppressLint("UseSparseArrays") HashMap<Integer, Operator> hashMap = new HashMap<>();
		while (cursor.moveToNext()) {
			Operator operator = Operator.newFromDBCursor(cursor);

			hashMap.put(operator.getId(), operator);
		}
		cursor.close();

		return hashMap;
	}

	public void updateLocation(com.pbm.Location location) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.LocationContract.COLUMN_ZONE_ID, location.getZoneID());
		values.put(PBMContract.LocationContract.COLUMN_LOCATION_TYPE_ID, location.getLocationTypeID());
		values.put(PBMContract.LocationContract.COLUMN_OPERATOR_ID, location.getOperatorID());
		values.put(PBMContract.LocationContract.COLUMN_NAME, location.getName());
		values.put(PBMContract.LocationContract.COLUMN_STREET, location.getStreet());
		values.put(PBMContract.LocationContract.COLUMN_CITY, location.getCity());
		values.put(PBMContract.LocationContract.COLUMN_STATE, location.getState());
		values.put(PBMContract.LocationContract.COLUMN_ZIP, location.getZip());
		values.put(PBMContract.LocationContract.COLUMN_PHONE, location.getPhone());
		values.put(PBMContract.LocationContract.COLUMN_LAT, location.getLat());
		values.put(PBMContract.LocationContract.COLUMN_LON, location.getLon());
		values.put(PBMContract.LocationContract.COLUMN_WEBSITE, location.getWebsite());
		values.put(PBMContract.LocationContract.COLUMN_MILES_INFO, location.getMilesInfo());
		values.put(PBMContract.LocationContract.COLUMN_LAST_UPDATED_BY_USERNAME, location.getLastUpdatedByUsername());
		values.put(PBMContract.LocationContract.COLUMN_DATE_LAST_UPDATED, location.getDateLastUpdated());
		values.put(PBMContract.LocationContract.COLUMN_DESCRIPTION, location.getDescription());
		values.put(PBMContract.LocationContract.COLUMN_DISTANCE_FROM_YOU, location.getDistanceFromYou());

		String selection = PBMContract.LocationContract.COLUMN_ID + "= ?";
		String[] selectionArgs = { String.valueOf(location.getId()) };

		getWriteableDB().update(
				PBMContract.LocationContract.TABLE_NAME,
				values,
				selection,
				selectionArgs
		);
	}

	public void updateMachine(Machine machine) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.MachineContract.COLUMN_NAME, machine.getName());
		values.put(PBMContract.MachineContract.COLUMN_YEAR, machine.getYear());
		values.put(PBMContract.MachineContract.COLUMN_MANUFACTURER, machine.getManufacturer());
		values.put(PBMContract.MachineContract.COLUMN_EXISTS_IN_REGION, machine.getExistsInRegion());
		values.put(PBMContract.MachineContract.COLUMN_GROUP_ID, machine.getGroupId());
		values.put(PBMContract.MachineContract.COLUMN_NUM_LOCATIONS, machine.getNumLocations());

		String selection = PBMContract.MachineContract.COLUMN_ID + "= ?";
		String[] selectionArgs = { String.valueOf(machine.getId()) };

		getWriteableDB().update(
			PBMContract.MachineContract.TABLE_NAME,
			values,
			selection,
			selectionArgs
		);
	}

	public void updateLmx(LocationMachineXref lmx) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_MACHINE_ID, lmx.getMachineID());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_LOCATION_ID, lmx.getLocationID());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_CONDITION, lmx.getCondition());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_CONDITION_DATE, lmx.getConditionDate());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_LAST_UPDATED_BY_USERNAME, lmx.getLastUpdatedByUsername());

		String selection = PBMContract.LocationMachineXrefContract.COLUMN_ID + "= ?";
		String[] selectionArgs = { String.valueOf(lmx.getId()) };

		getWriteableDB().update(
			PBMContract.LocationMachineXrefContract.TABLE_NAME,
			values,
			selection,
			selectionArgs
		);
	}

	public Region getRegion() {
		return getRegion(getSharedPreferences(PinballMapActivity.PREFS_NAME, 0).getInt("region", -1));
	}

	public String[] getMachineNamesWithMetadata() {
		HashMap<Integer, com.pbm.Machine> machines = getMachines();

		String names[] = new String[machines.size()];

		int i = 0;
		for (Machine machine : machines.values()) {
			names[i] = machine.getName() + " [" + machine.getManufacturer() + " - " + machine.getYear() + "]";

			i++;
		}

		Arrays.sort(names);

		return names;
	}

	void addMachineCondition(Condition machineCondition) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.ConditionContract.COLUMN_ID, machineCondition.getId());
		values.put(PBMContract.ConditionContract.COLUMN_DATE, machineCondition.getDate());
		values.put(PBMContract.ConditionContract.COLUMN_DESCRIPTION, machineCondition.getDescription());
		values.put(PBMContract.ConditionContract.COLUMN_LOCATION_MACHINE_XREF_ID, machineCondition.getLmxId());
		values.put(PBMContract.ConditionContract.COLUMN_USERNAME, machineCondition.getUsername());

		getWriteableDB().insert(PBMContract.ConditionContract.TABLE_NAME, null, values);
	}

	void addMachineScore(MachineScore machineScore) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.MachineScoreContract.COLUMN_ID, machineScore.getId());
		values.put(PBMContract.MachineScoreContract.COLUMN_LOCATION_MACHINE_XREF_ID, machineScore.getLmxId());
		values.put(PBMContract.MachineScoreContract.COLUMN_USERNAME, machineScore.getUsername());
		values.put(PBMContract.MachineScoreContract.COLUMN_SCORE, machineScore.getScore());
		values.put(PBMContract.MachineScoreContract.COLUMN_DATE_CREATED, machineScore.getDate());

		getWriteableDB().insert(PBMContract.MachineScoreContract.TABLE_NAME, null, values);
	}

	public ArrayList<MachineScore> getMachineScoresByLMXId(Integer id) {
		ArrayList<MachineScore> scores = new ArrayList<>();

		for (MachineScore score : getMachineScores()) {
			if (score.getLmxId() == id) {
				scores.add(score);
			}
		}

		return scores;
	}

	public void removeLmx(LocationMachineXref lmx) {
		String selection = PBMContract.LocationMachineXrefContract.COLUMN_ID + " = ?";
		String[] selectionArgs = { Integer.toString(lmx.getId()) };
		getWriteableDB().delete(PBMContract.LocationMachineXrefContract.TABLE_NAME, selection, selectionArgs);
	}

	void addLocation(Location location) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.LocationContract.COLUMN_ID, location.getId());
		values.put(PBMContract.LocationContract.COLUMN_ZONE_ID, location.getZoneID());
		values.put(PBMContract.LocationContract.COLUMN_LOCATION_TYPE_ID, location.getLocationTypeID());
		values.put(PBMContract.LocationContract.COLUMN_OPERATOR_ID, location.getOperatorID());
		values.put(PBMContract.LocationContract.COLUMN_NAME, location.getName());
		values.put(PBMContract.LocationContract.COLUMN_STREET, location.getStreet());
		values.put(PBMContract.LocationContract.COLUMN_CITY, location.getCity());
		values.put(PBMContract.LocationContract.COLUMN_STATE, location.getState());
		values.put(PBMContract.LocationContract.COLUMN_ZIP, location.getZip());
		values.put(PBMContract.LocationContract.COLUMN_PHONE, location.getPhone());
		values.put(PBMContract.LocationContract.COLUMN_LAT, location.getLat());
		values.put(PBMContract.LocationContract.COLUMN_LON, location.getLon());
		values.put(PBMContract.LocationContract.COLUMN_WEBSITE, location.getWebsite());
		values.put(PBMContract.LocationContract.COLUMN_MILES_INFO, location.getMilesInfo());
		values.put(PBMContract.LocationContract.COLUMN_LAST_UPDATED_BY_USERNAME, location.getLastUpdatedByUsername());
		values.put(PBMContract.LocationContract.COLUMN_DATE_LAST_UPDATED, location.getDateLastUpdated());
		values.put(PBMContract.LocationContract.COLUMN_DESCRIPTION, location.getDescription());
		values.put(PBMContract.LocationContract.COLUMN_DISTANCE_FROM_YOU, location.getDistanceFromYou());
		values.put(PBMContract.LocationContract.COLUMN_NUM_MACHINES, location.getNumMachines());

		getWriteableDB().insert(PBMContract.LocationContract.TABLE_NAME, null, values);
	}

	public void addMachine(Machine machine) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.MachineContract.COLUMN_ID, machine.getId());
		values.put(PBMContract.MachineContract.COLUMN_NAME, machine.getName());
		values.put(PBMContract.MachineContract.COLUMN_YEAR, machine.getYear());
		values.put(PBMContract.MachineContract.COLUMN_MANUFACTURER, machine.getManufacturer());
		values.put(PBMContract.MachineContract.COLUMN_GROUP_ID, machine.getGroupId());
		values.put(PBMContract.MachineContract.COLUMN_NUM_LOCATIONS, machine.getNumLocations());
		values.put(PBMContract.MachineContract.COLUMN_EXISTS_IN_REGION, machine.getExistsInRegion());

		getWriteableDB().insert(PBMContract.MachineContract.TABLE_NAME, null, values);
	}

	void addOperator(Operator operator) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.OperatorContract.COLUMN_ID, operator.getId());
		values.put(PBMContract.OperatorContract.COLUMN_NAME, operator.getName());

		getWriteableDB().insert(PBMContract.OperatorContract.TABLE_NAME, null, values);
	}

	public Operator getOperator(Integer id) {
		String selection = PBMContract.OperatorContract.COLUMN_ID + " = ?";
		String[] selectionArgs = { id.toString() };

		Cursor cursor = getReadableDB().query(
				PBMContract.OperatorContract.TABLE_NAME,
				PBMContract.OperatorContract.PROJECTION,
				selection,
				selectionArgs,
				null,
				null,
				null
		);

		Operator operator = null;
		while(cursor.moveToNext()) {
			operator = Operator.newFromDBCursor(cursor);
		}
		cursor.close();

		return operator;
	}

	void addLocationType(LocationType locationType) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.LocationTypeContract.COLUMN_ID, locationType.getId());
		values.put(PBMContract.LocationTypeContract.COLUMN_NAME, locationType.getName());

		getWriteableDB().insert(PBMContract.LocationTypeContract.TABLE_NAME, null, values);
	}

	public LocationType getLocationType(Integer id) {
		String selection = PBMContract.LocationTypeContract.COLUMN_ID + " = ?";
		String[] selectionArgs = { id.toString() };

		Cursor cursor = getReadableDB().query(
				PBMContract.LocationTypeContract.TABLE_NAME,
				PBMContract.LocationTypeContract.PROJECTION,
				selection,
				selectionArgs,
				null,
				null,
				null
		);

		LocationType locationType = null;
		while(cursor.moveToNext()) {
			locationType = LocationType.newFromDBCursor(cursor);
		}
		cursor.close();

		return locationType;
	}

	public void addLocationMachineXref(LocationMachineXref lmx) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_ID, lmx.getId());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_MACHINE_ID, lmx.getMachineID());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_LOCATION_ID, lmx.getLocationID());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_CONDITION, lmx.getCondition());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_CONDITION_DATE, lmx.getConditionDate());
		values.put(PBMContract.LocationMachineXrefContract.COLUMN_LAST_UPDATED_BY_USERNAME, lmx.getLastUpdatedByUsername());

		getWriteableDB().insert(PBMContract.LocationMachineXrefContract.TABLE_NAME, null, values);
	}

	public LocationMachineXref getLmxFromMachine(Machine machine, List<LocationMachineXref> lmxes) {
		for (LocationMachineXref lmx : lmxes) {
			if (lmx.getMachineID() == machine.getId()) {
				return lmx;
			}
		}

		return null;
	}

	public LocationMachineXref getLmx(Integer id) throws ParseException {
		String selection = PBMContract.LocationMachineXrefContract.COLUMN_ID + " = ?";
		String[] selectionArgs = { id.toString() };

		Cursor cursor = getReadableDB().query(
				PBMContract.LocationMachineXrefContract.TABLE_NAME,
				PBMContract.LocationMachineXrefContract.PROJECTION,
				selection,
				selectionArgs,
				null,
				null,
				null
		);

		LocationMachineXref lmx = null;
		while(cursor.moveToNext()) {
			lmx = LocationMachineXref.newFromDBCursor(cursor);
		}
		cursor.close();

		return lmx;
	}

	public int numMachinesForLocation(Location location) throws ParseException {
		Cursor cursor = getReadableDB().rawQuery("select count(*) from " + PBMContract.LocationMachineXrefContract.TABLE_NAME +
				" where location_id=" + String.valueOf(location.getId()), null);
		cursor.moveToFirst();
		int count= cursor.getInt(0);
		cursor.close();

		return count;
	}

	public Machine getMachine(Integer id) {
		String selection = PBMContract.MachineContract.COLUMN_ID + " = ?";
		String[] selectionArgs = { id.toString() };

		Cursor cursor = getReadableDB().query(
				PBMContract.MachineContract.TABLE_NAME,
				PBMContract.MachineContract.PROJECTION,
				selection,
				selectionArgs,
				null,
				null,
				null
		);

		Machine machine = null;
		while(cursor.moveToNext()) {
			machine = Machine.newFromDBCursor(cursor);
		}
		cursor.close();

		return machine;
	}

	public Machine getMachineByMetadata(String name, String year, String manufacturer) {
		ArrayList<Machine> machines = getMachineValues(true);
		for (Object baseMachine : machines) {
			Machine machine = (Machine) baseMachine;

			if (
				machine.getName().equalsIgnoreCase(name) &&
				(!year.isEmpty() && machine.getYear().equalsIgnoreCase(year)) &&
				(!manufacturer.isEmpty() && machine.getManufacturer().equalsIgnoreCase(manufacturer))
			) {
				return machine;
			}
		}

		return null;
	}

	public Location getLocationByName(String name) {
		List<Location> locations = getLocationValues();
		for (Location location : locations) {
			if (location.getName().equals(name)) {
				return location;
			}
		}
		return null;
	}

	public com.pbm.Location getLocation(Integer id) {
		String selection = PBMContract.LocationContract.COLUMN_ID + " = ?";
		String[] selectionArgs = { id.toString() };

		Cursor cursor = getReadableDB().query(
				PBMContract.LocationContract.TABLE_NAME,
				PBMContract.LocationContract.PROJECTION,
				selection,
				selectionArgs,
				null,
				null,
				null
		);

		com.pbm.Location location = null;
		while(cursor.moveToNext()) {
			location = com.pbm.Location.newFromDBCursor(cursor);
		}
		cursor.close();

		return location;
	}

	public Region getRegion(Integer id) {
		String selection = PBMContract.RegionContract.COLUMN_ID + " = ?";
		String[] selectionArgs = {id.toString()};

		Cursor cursor = getReadableDB().query(
				PBMContract.RegionContract.TABLE_NAME,
				PBMContract.RegionContract.PROJECTION,
				selection,
				selectionArgs,
				null,
				null,
				null
		);

		Region region = null;
		while (cursor.moveToNext()) {
			region = Region.newFromDBCursor(cursor);
		}
		cursor.close();

		return region;
	}

	void addZone(Zone zone) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.ZoneContract.COLUMN_ID, zone.getId());
		values.put(PBMContract.ZoneContract.COLUMN_NAME, zone.getName());
		values.put(PBMContract.ZoneContract.COLUMN_IS_PRIMARY, zone.getIsPrimary());

		getWriteableDB().insert(PBMContract.ZoneContract.TABLE_NAME, null, values);
	}

	public void addRegion(Region region) {
		ContentValues values = new ContentValues();
		values.put(PBMContract.RegionContract.COLUMN_ID, region.getId());
		values.put(PBMContract.RegionContract.COLUMN_NAME, region.getName());
		values.put(PBMContract.RegionContract.COLUMN_FORMAL_NAME, region.getFormalName());
		values.put(PBMContract.RegionContract.COLUMN_MOTD, region.getMotd());
		values.put(PBMContract.RegionContract.COLUMN_LAT, region.getLat());
		values.put(PBMContract.RegionContract.COLUMN_LON, region.getLon());
		values.put(PBMContract.RegionContract.COLUMN_DISTANCE_FROM_YOU, region.getDistanceFromYou());

		getWriteableDB().insert(PBMContract.RegionContract.TABLE_NAME, null, values);
	}

	public ArrayList<Region> getRegionValues() {
		ArrayList<Region> regionValues = new ArrayList<>(getRegions().values());

		Collections.sort(regionValues, new Comparator<Region>() {
			public int compare(Region r1, Region r2) {
			return r1.getFormalName().compareTo(r2.getFormalName());
			}
		});

		return regionValues;
	}

	public ArrayList<Location> getLocationValues() {
		ArrayList<Location> locationValues = new ArrayList<>(getLocations().values());

		Collections.sort(locationValues, new Comparator<Location>() {
			public int compare(Location l1, Location l2) {
			return l1.getName().compareTo(l2.getName());
			}
		});

		return locationValues;
	}

	public ArrayList<Machine> getMachineValues(boolean displayAllMachines) {
		ArrayList<Machine> machineValues = new ArrayList<>();

		for (Machine machine : getMachines().values()) {
			if (displayAllMachines || machine.getExistsInRegion()) {
				machineValues.add(machine);
			}
		}

		Collections.sort(machineValues, new Comparator<Object>() {
			public int compare(Object lhs, Object rhs) {
			Machine m1 = (Machine) lhs;
			Machine m2 = (Machine) rhs;

			return m1.getName().replaceAll("^(?i)The ", "").compareTo(m2.getName().replaceAll("^(?i)The ", ""));
			}
		});

		return machineValues;
	}

	public int getMachineIDFromMachineMetadata(String name, String year, String manufacturer) throws InterruptedException, ExecutionException, JSONException {
		int machineID = -1;

		Machine machine = getMachineByMetadata(name, year, manufacturer);
		if (machine != null) {
			machine.setExistsInRegion(true);

			machineID = machine.getId();
		}

		return machineID;
	}

	public String requestWithAuthDetails(String origRequest) {
		final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
		PreferenceManager.setDefaultValues(this, PinballMapActivity.PREFS_NAME, 0, R.xml.preferences, false);
		String authToken = settings.getString("authToken", "");
		String email = settings.getString("email", "");

		String authDetails = "user_email=" + email + ";user_token=" + authToken;

		return origRequest + (!origRequest.contains("?") ? "?" : ";") + authDetails;
	}

	public void initializeData() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException, ParseException {
		dataLoadTimestamp = System.currentTimeMillis();
		Log.d("com.pbm", "initializing data");
		getDbHelper().removeAll();

		Log.d("com.pbm", "TIMING STARTING REGION");
		initializeRegions();
		Log.d("com.pbm", "TIMING STARTING MACHINES");
		initializeAllMachines();
		Log.d("com.pbm", "TIMING STARTING TAG REGION MACHINES");
		initializeRegionMachines();
		Log.d("com.pbm", "TIMING STARTING LOCATIONS");
		initializeLocations();
		Log.d("com.pbm", "TIMING STARTING LOCATION TYPES");
		initializeLocationTypes();
		Log.d("com.pbm", "TIMING STARTING ZONES");
		initializeZones();
		Log.d("com.pbm", "TIMING STARTING OPERATORS");
		initializeOperators();
	}

	void initializeRegionMachines() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		Region region = getRegion();
		String json = new RetrieveJsonTask().execute(
				requestWithAuthDetails(PinballMapActivity.regionlessBase + "machines.json?no_details=1;region_id=" + region.getId()),
				"GET"
		).get();

		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray machines = jsonObject.getJSONArray("machines");

		List<String> machineIds = new ArrayList<>();
		for (int i = 0; i < machines.length(); i++) {
			try {
				JSONObject machine = machines.getJSONObject(i);
				machineIds.add(machine.getString("id"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Cursor cursor = getWriteableDB().rawQuery("update machines set exists_in_region = 1 where id in (" + TextUtils.join(", ", machineIds) + ")", null);
		cursor.moveToFirst();
		cursor.close();
	}

	void initializeMachineScores() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionBase + "machine_score_xrefs.json"), "GET"
		).get();
		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);
		JSONArray scores = jsonObject.getJSONArray("machine_score_xrefs");

		for (int i = 0; i < scores.length(); i++) {
			try {
				JSONObject score = scores.getJSONObject(i);

				String id = score.getString("id");
				String lmxId = score.getString("location_machine_xref_id");
				String username = score.getString("username");
				String highScore = score.getString("score");

				String dateCreated = null;
				if (score.has("created_at") && !score.getString("created_at").equals("null")) {
					dateCreated = score.getString("created_at");

					DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
					DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

					String formattedDateCreated = "";
					try {
						Date startDate = inputDF.parse(dateCreated);
						formattedDateCreated = outputDF.format(startDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					dateCreated = formattedDateCreated;
				}

				if ((id != null) && (lmxId != null) && (highScore != null)) {
					addMachineScore(new MachineScore(Integer.parseInt(id), Integer.parseInt(lmxId), dateCreated, username, Long.parseLong(highScore)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void initializeOperators() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionBase + "operators.json"),
			"GET"
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
					addOperator(new Operator(Integer.parseInt(id), name));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void initializeLocationTypes() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionlessBase + "location_types.json"),
			"GET"
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
					addLocationType(new LocationType(Integer.parseInt(id), name));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void initializeAllMachines() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionlessBase + "machines.json?no_details=1"),
			"GET"
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

				String machineGroupId = machine.getString("machine_group_id");
				if (machineGroupId.equals("null")) {
					machineGroupId = "";
				}

				if ((id != null) && (name != null)) {
					addMachine(new Machine(Integer.parseInt(id), name, year, manufacturer, false, machineGroupId));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void initializeZones() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
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
				addZone(new Zone(Integer.parseInt(id), name, isPrimary ? 1 : 0));
			}
		}
	}

	void loadLmx(int lmxId) throws ExecutionException, InterruptedException, JSONException, ParseException {
		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionlessBase + "location_machine_xrefs/" + Integer.toString(lmxId) + ".json"),
			"GET"
		).get();

		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);

		if (jsonObject.has("location_machine")) {
			JSONObject lmx = jsonObject.getJSONObject("location_machine");

			int lmxID = lmx.getInt("id");
			int lmxLocationID = lmx.getInt("location_id");
			int machineID = lmx.getInt("machine_id");
			String condition = lmx.getString("condition");
			String conditionDate = lmx.getString("condition_date");
			if (conditionDate.equals("null")) {
				conditionDate = null;
			}

			String username;
			try {
				username = lmx.getString("last_updated_by_username");
			} catch (JSONException e) {
				username = "";
			}

			if (Integer.toString(machineID) != null) {
				addLocationMachineXref(
					new LocationMachineXref(lmxID, lmxLocationID, machineID, condition, conditionDate, username)
				);
			}
		}
	}

	void loadLmxesForLocation(Location location) throws ExecutionException, InterruptedException, JSONException, ParseException {
		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionBase + "locations/" + Integer.toString(location.getId()) + ".json"),
			"GET"
		).get();

		if (json == null) {
			return;
		}

		JSONObject jsonObject = new JSONObject(json);

		JSONArray lmxes = null;
		if (jsonObject.has("location_machine_xrefs")) {
			lmxes = jsonObject.getJSONArray("location_machine_xrefs");
		}

		if (lmxes != null && lmxes.length() > 0) for (int x = 0; x < lmxes.length(); x++) {
			JSONObject lmx = lmxes.getJSONObject(x);

			int lmxID = lmx.getInt("id");
			int lmxLocationID = lmx.getInt("location_id");
			int machineID = lmx.getInt("machine_id");
			String condition = lmx.getString("condition");
			String conditionDate = lmx.getString("condition_date");
			if (conditionDate.equals("null")) {
				conditionDate = null;
			}

			String username;
			try {
				username = lmx.getString("last_updated_by_username");
			} catch (JSONException e) {
				username = "";
			}

			Machine machine = getMachine(machineID);

			if (machine != null) {
				machine.setExistsInRegion(true);
				updateMachine(machine);

				addLocationMachineXref(
					new LocationMachineXref(lmxID, lmxLocationID, machineID, condition, conditionDate, username)
				);
				loadConditions(lmx);
				loadScores(lmx);
			}
		}
	}

	void initializeLocations() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException, ParseException {
		String json = new RetrieveJsonTask().execute(
			requestWithAuthDetails(PinballMapActivity.regionBase + "locations.json?no_details=1"),
			"GET"
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
			String description = location.getString("description");
			String numMachines = location.getString("num_machines");

			String dateLastUpdated = null;
			if (location.has("date_last_updated") && !location.getString("date_last_updated").equals("null")) {
				dateLastUpdated = location.getString("date_last_updated");

				DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

				String formattedDateLastUpdated = "";
				try {
					Date startDate = inputDF.parse(dateLastUpdated);
					formattedDateLastUpdated = outputDF.format(startDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				dateLastUpdated = formattedDateLastUpdated;
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
								lastUpdatedByUsername, description, numMachines);
				addLocation(newLocation);
			}
		}
	}

	void loadConditions(JSONObject lmx) throws JSONException {
		if (lmx.has("machine_conditions")) {
			JSONArray conditions = lmx.getJSONArray("machine_conditions");

			for (int conditionIndex = 0; conditionIndex < conditions.length(); conditionIndex++) {
				JSONObject pastCondition = conditions.getJSONObject(conditionIndex);

				String pastConditionUsername;
				try {
					pastConditionUsername = pastCondition.getString("username");
				} catch (JSONException e) {
					pastConditionUsername = "";
				}

				Condition machineCondition = new Condition(
					pastCondition.getInt("id"),
					pastCondition.getString("updated_at"),
					pastCondition.getString("comment"),
					lmx.getInt("id"),
					pastConditionUsername
				);
				addMachineCondition(machineCondition);
			}
		}
	}

	void loadScores(JSONObject lmx) throws JSONException {
		if (lmx.has("machine_score_xrefs")) {
			JSONArray scores = lmx.getJSONArray("machine_score_xrefs");

			for (int i = 0; i < scores.length(); i++) {
				try {
					JSONObject score = scores.getJSONObject(i);

					String id = score.getString("id");
					String lmxId = score.getString("location_machine_xref_id");
					String username = score.getString("username");
					String highScore = score.getString("score");

					String dateCreated = null;
					if (score.has("created_at") && !score.getString("created_at").equals("null")) {
						dateCreated = score.getString("created_at");

						DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
						DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

						String formattedDateCreated = "";
						try {
							Date startDate = inputDF.parse(dateCreated);
							formattedDateCreated = outputDF.format(startDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}

						dateCreated = formattedDateCreated;
					}

					if ((id != null) && (lmxId != null) && (highScore != null)) {
						addMachineScore(new MachineScore(Integer.parseInt(id), Integer.parseInt(lmxId), dateCreated, username, Long.parseLong(highScore)));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public boolean initializeRegions() throws UnsupportedEncodingException, InterruptedException, ExecutionException, JSONException {
		String json = new RetrieveJsonTask().execute(
			PinballMapActivity.regionlessBase + "regions.json",
			"GET"
		).get();

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
			addRegion(new Region(Integer.parseInt(id), name, formalName, motd, lat, lon, emailAddresses));
		}
		return true;
	}

	public boolean userIsAuthenticated() {
		final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);

		return !settings.getString("username", "").equals("");
	}
}