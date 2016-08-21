package com.pbm;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationMachineConditions implements Serializable {
	private static final long serialVersionUID = -42859653323636935L;

	private int id, machineID, locationID;
	private ArrayList<Condition> conditions;

	public LocationMachineConditions(int id, int machineID, int locationID, ArrayList<Condition> conditions) {
		this.id = id;
		this.machineID = machineID;
		this.locationID = locationID;
		this.conditions = conditions;
	}

	public int getMachineID() {
		return machineID;
	}

	public int getLocationID() {
		return locationID;
	}

	public ArrayList<Condition> getConditions() {
		return conditions;
	}

	public void addCondition(Condition condition) {
		if (conditions != null) {
			conditions.add(condition);
		}
	}

	public void removeCondition(Condition condition) {
		if (condition != null) {
			conditions.remove(condition);
		}
	}
}
