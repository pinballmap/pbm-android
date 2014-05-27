package com.pbm;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Region {
	public int id;
	public String name, formalName, motd;
	List<String>emailAddresses = new ArrayList<String>();

	public Region(int id, String name, String formalName, String motd, List<String> emailAddresses) {
	    this.id = id;
	    this.name = name;
	    this.formalName = formalName;
	    this.motd = motd;
	    this.emailAddresses = emailAddresses;
	}
	
	public String toString() {
		return formalName;
	}
}