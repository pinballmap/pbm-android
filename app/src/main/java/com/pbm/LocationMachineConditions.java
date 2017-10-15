package com.pbm;

import java.io.Serializable;
import java.util.ArrayList;

class LocationMachineConditions implements Serializable {
	private static final long serialVersionUID = -42859653323636935L;

	private int id, machineID, locationID;
	private ArrayList<Condition> conditions;

	LocationMachineConditions(int id, int machineID, int locationID, ArrayList<Condition> conditions) {
		this.id = id;
		this.machineID = machineID;
		this.locationID = locationID;
		this.conditions = conditions;
	}

	ArrayList<Condition> getConditions() {
		return conditions;
	}
}
