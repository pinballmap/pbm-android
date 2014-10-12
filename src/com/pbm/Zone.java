package com.pbm;

import java.io.Serializable;
import java.lang.String;

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
}