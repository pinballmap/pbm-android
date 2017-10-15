package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

class MachineScore implements Serializable {
	private static final long serialVersionUID = 2470212492505135031L;
	private final String dateCreated;

	private final int id;
	private int lmxId;
	private final String username;
	private final long score;

	MachineScore(int id, int lmxId, String dateCreated, String username, long score) {
		this.id = id;
		this.dateCreated = dateCreated;
		this.username = username;
		this.lmxId = lmxId;
		this.score = score;
	}

	public String getUsername() {
		return username;
	}

	public String getDate() {
		return dateCreated;
	}

	public int getId() {
		return id;
	}

	int getLmxId() {
		return lmxId;
	}

	public long getScore() { return score; }

	static MachineScore newDBFromCursor(Cursor cursor) {
		return new MachineScore(
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.MachineScoreContract.COLUMN_ID)),
				cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.MachineScoreContract.COLUMN_LOCATION_MACHINE_XREF_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.MachineScoreContract.COLUMN_DATE_CREATED)),
				cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.MachineScoreContract.COLUMN_USERNAME)),
				cursor.getLong(cursor.getColumnIndexOrThrow(PBMContract.MachineScoreContract.COLUMN_SCORE))
		);
	}
}
