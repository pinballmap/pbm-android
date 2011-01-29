package com.pbm;

import java.lang.String;

public class Region {
	public int regionNo;
	public String name, subDir, motd, email;

	public Region(int newRegionNo, String newName, String newSubDir, String newMotd, String newEmail) {
	    regionNo = newRegionNo;
	    name = newName;
	    subDir = newSubDir;
	    motd = newMotd;
	    email = newEmail;
	}
	
	public String toString() {
		return name;
	}
}