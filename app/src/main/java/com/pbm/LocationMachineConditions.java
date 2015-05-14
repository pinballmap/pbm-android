package com.pbm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Copyright (c) 2015, Brian Dols <brian.dols@gmail.com>
 * <p/>
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
public class LocationMachineConditions implements Serializable {
	private static final long serialVersionUID = -42859653323636935L;

	private int id;
	private int machineID;
	private int locationID;
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

}
