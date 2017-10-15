package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

public class Machine implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id, numLocations;
	private String name, year, manufacturer, groupId;
	private boolean existsInRegion;

	public Machine(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Machine(int id, String name, String year, String manufacturer, boolean existsInRegion, String groupId) {
		this.id = id;
		this.name = name;
		this.year = year;
		this.manufacturer = manufacturer;
		this.existsInRegion = existsInRegion;
		this.groupId = groupId;
	}

	public Machine(int id, String name, int numLocations) {
		this.id = id;
		this.name = name;
		this.numLocations = numLocations;
	}

	void setExistsInRegion(boolean existsInRegion) {
		this.existsInRegion = existsInRegion;
	}
	
	public String toString() {
		return name;
	}

	String metaData() {
		return "(" + manufacturer + ", " + year + ")";
	}

	static Machine newFromDBCursor(Cursor cursor) {
		return new Machine(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.MachineContract.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.MachineContract.COLUMN_NAME)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.MachineContract.COLUMN_YEAR)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.MachineContract.COLUMN_MANUFACTURER)),
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.MachineContract.COLUMN_EXISTS_IN_REGION)) > 0,
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.MachineContract.COLUMN_GROUP_ID))
		);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	String getManufacturer() {
		return manufacturer;
	}

	String getYear() {
		return year;
	}

	String getGroupId() {
		return groupId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	int getNumLocations() {
		return numLocations;
	}

	boolean getExistsInRegion() {
		return existsInRegion;
	}
}