package com.pbm;

import java.io.Serializable;
import java.lang.String;

public class Zone implements Serializable {
	private static final long serialVersionUID = 1L;
	public int zoneNo;
	public String name;
	public String shortName;
	public int isPrimary;

	public Zone(int newZoneNo, String newName, String newShortName, int newIsPrimary) {
	    zoneNo = newZoneNo;
	    name = newName;
	    shortName = newShortName;
	    isPrimary = newIsPrimary;
	}
	
	public String toString() {
		return name;
	}
}