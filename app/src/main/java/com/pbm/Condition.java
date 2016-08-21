package com.pbm;

import java.io.Serializable;
import java.util.Date;

public class Condition implements Serializable {
	private static final long serialVersionUID = 2470212492505135031L;
	private final Date date;

	private final String description;
	private int lmxId;
	private final int id;
	private final String username;

	public Condition(int id, Date date, String description, int lmxId, String username) {
		this.id = id;
		this.date = date;
		this.description = description;
		this.lmxId = lmxId;
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public Date getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	public int getLmxId() { return lmxId; }

	public String getUsername() { return username; }
}
