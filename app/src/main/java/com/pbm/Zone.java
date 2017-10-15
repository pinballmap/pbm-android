package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

public class Zone implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id, isPrimary;
	private String name;

	public Zone(int id, String name, int isPrimary) {
	    this.id = id;
	    this.name = name;
	    this.isPrimary = isPrimary;
	}
	
	public String toString() {
		return name;
	}

	static Zone newFromDBCursor(Cursor cursor) {
		return new Zone(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.ZoneContract.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.ZoneContract.COLUMN_NAME)),
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.ZoneContract.COLUMN_IS_PRIMARY))
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

	int getIsPrimary() {
		return isPrimary;
	}
}