package com.pbm;

import java.io.Serializable;
import java.lang.String;

public class Zone implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id, isPrimary;
	public String name, shortName;

	public Zone(int id, String name, String shortName, int isPrimary) {
	    this.id = id;
	    this.name = name;
	    this.shortName = shortName;
	    this.isPrimary = isPrimary;
	}
	
	public String toString() {
		return name;
	}
}