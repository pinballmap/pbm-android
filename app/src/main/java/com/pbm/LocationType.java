package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

public class LocationType implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;

	public LocationType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static LocationType blankLocationType() {
		return new LocationType(0, "");
	}
	
	public String toString() {
		return name;
	}

	public static LocationType newFromDBCursor(Cursor cursor) {
		return new LocationType(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.LocationTypeContract.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.LocationTypeContract.COLUMN_NAME))
		);
	}
}