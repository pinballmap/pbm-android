package com.pbm;

import java.io.Serializable;

public class MachineScore implements Serializable {
	private static final long serialVersionUID = 2470212492505135031L;
	private final String dateCreated;

	private final int id;
	private int lmxId;
	private final String username;
	private final long score;

	public MachineScore(int id, int lmxId, String dateCreated, String username, long score) {
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

	public int getLmxId() {
		return lmxId;
	}

	public long getScore() { return score; }
}
