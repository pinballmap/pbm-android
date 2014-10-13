package com.pbm;

import java.io.Serializable;
import java.lang.String;

public class LocationType implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;

	public LocationType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static LocationType blankLocationType() {
		return new LocationType(0, "");
	}
}