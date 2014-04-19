package com.pbm;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Region {
	public int regionNo;
	public String name, formalName, motd;
	List<String>emailAddresses = new ArrayList<String>();

	public Region(int newRegionNo, String newName, String newFormalName, String newMotd, List<String> newEmailAddresses) {
	    regionNo = newRegionNo;
	    name = newName;
	    formalName = newFormalName;
	    motd = newMotd;
	    emailAddresses = newEmailAddresses;
	}
	
	public String toString() {
		return formalName;
	}
}