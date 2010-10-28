package com.pbm;

import java.lang.String;

public class Region {
	public int regionNo;
	public String name;
	public String subDir;
	public String motd;

	public Region(int newRegionNo, String newName, String newSubDir, String newMotd) {
	    regionNo = newRegionNo;
	    name = newName;
	    subDir = newSubDir;
	    motd = newMotd;
	}
	
	public String toString() {
		return name;
	}
}