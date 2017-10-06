package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

public class Zone implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id, isPrimary;
	public String name;

	public Zone(int id, String name, int isPrimary) {
	    this.id = id;
	    this.name = name;
	    this.isPrimary = isPrimary;
	}
	
	public String toString() {
		return name;
	}
	
	public static Zone allZone() {
		return new Zone(0, "All", 0);
	}

	public static Zone newFromDBCursor(Cursor cursor) {
		return new Zone(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.ZoneContract.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.ZoneContract.COLUMN_NAME)),
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.ZoneContract.COLUMN_IS_PRIMARY))
		);
	}
}