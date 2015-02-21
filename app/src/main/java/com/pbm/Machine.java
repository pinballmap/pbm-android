package com.pbm;

import java.io.Serializable;
import java.lang.String;

public class Machine implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public String year;
	public String manufacturer;
	public int numLocations;
	public boolean existsInRegion;

	public Machine(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Machine(int id, String name, String year, String manufacturer, boolean existsInRegion) {
		this.id = id;
		this.name = name;
		this.year = year;
		this.manufacturer = manufacturer;
		this.existsInRegion = existsInRegion;
	}

	public Machine(int id, String name, int numLocations) {
		this.id = id;
		this.name = name;
		this.numLocations = numLocations;
	}
	
	public void setNumLocations(int numLocations) {
		this.numLocations = numLocations;
	}
	
	public void setExistsInRegion(boolean existsInRegion) {
		this.existsInRegion = existsInRegion;
	}
	
	public String toString() {
		return name;
	}

	public String metaData() {
		return "(" + manufacturer + ", " + year + ")";
	}
}