package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

public class Condition implements Serializable {
	private static final long serialVersionUID = 2470212492505135031L;

	private final String date, description, username;
	private int lmxId;
	private final int id;

	public Condition(int id, String date, String description, int lmxId, String username) {
		this.id = id;
		this.date = date;
		this.description = description;
		this.lmxId = lmxId;
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	int getLmxId() { return lmxId; }

	public String getUsername() { return username; }

	static Condition newFromDBCursor(Cursor cursor) {
		return new Condition(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.ConditionContract.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.ConditionContract.COLUMN_DATE)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.ConditionContract.COLUMN_DESCRIPTION)),
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.ConditionContract.COLUMN_LOCATION_MACHINE_XREF_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.ConditionContract.COLUMN_USERNAME))
		);
	}
}
