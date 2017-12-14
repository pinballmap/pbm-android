package com.pbm;

import android.database.Cursor;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		String nonTimestampDate = date.split("T")[0];
		if (
			!nonTimestampDate.equals("null") &&
			!nonTimestampDate.equals("") &&
			nonTimestampDate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")
		) {
			DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat outputDF = new SimpleDateFormat("MM/dd/yyyy");
			Date dateCreatedAt = null;
			try {
				dateCreatedAt = inputDF.parse(nonTimestampDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return outputDF.format(dateCreatedAt);
		} else {
			return nonTimestampDate;
		}
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
