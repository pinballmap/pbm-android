package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

class LocationType implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;

	LocationType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	static LocationType blankLocationType() {
		return new LocationType(0, "");
	}
	
	public String toString() {
		return name;
	}

	static LocationType newFromDBCursor(Cursor cursor) {
		return new LocationType(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationTypeContract.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationTypeContract.COLUMN_NAME))
		);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}