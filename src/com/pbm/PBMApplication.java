package com.pbm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.annotation.SuppressLint;
import android.app.Application;

@SuppressLint("UseSparseArrays")
public class PBMApplication extends Application {
	public enum TrackerName {
	    APP_TRACKER
	}

	private HashMap<Integer, com.pbm.Location> locations   = new HashMap<Integer, Location>();
	private HashMap<Integer, com.pbm.Machine>  machines    = new HashMap<Integer, Machine>();
	private HashMap<Integer, com.pbm.Zone>     zones       = new HashMap<Integer, Zone>();
	private HashMap<Integer, com.pbm.Region>   regions     = new HashMap<Integer, Region>();
	
	public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	
	synchronized Tracker getTracker() {
		if (!mTrackers.containsKey(TrackerName.APP_TRACKER)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		    Tracker t = analytics.newTracker(R.xml.app_tracker);
		    mTrackers.put(TrackerName.APP_TRACKER, t);
		}

		return mTrackers.get(TrackerName.APP_TRACKER);
	}

	public void setLocations(HashMap<Integer, com.pbm.Location> locations) {
		this.locations = locations;
	}
	public HashMap<Integer, com.pbm.Location> getLocations() {
		return locations;
	}
	public void setMachines(HashMap<Integer, com.pbm.Machine> machines) {
		this.machines = machines;
	}
	public HashMap<Integer, com.pbm.Machine> getMachines() {
		return machines;
	}
	public void setZones(HashMap<Integer, com.pbm.Zone> zones) {
		this.zones = zones;
	}
	public HashMap<Integer, com.pbm.Zone> getZones() {
		return zones;
	}
	public void addLocation(Integer id, Location location) {
		this.locations.put(id, location);
	}
	public void addMachine(Integer id, Machine machine) {
		this.machines.put(id, machine);
	}
	public Machine getMachine(Integer id) {
		return machines.get(id);
	}
	public Location getLocationByName(String name) {
		Object[] locations = getLocationValues();
		for (int i = 0; i < locations.length; i++) {
			Location location = (Location) locations[i];
			if (location.name.equals(name)) {
				return location;
			}
		}
		
		return null;
	}
	public Location getLocation(Integer id) {
		return locations.get(id);
	}
	public Region getRegion(Integer id) {
		return regions.get(id);
	}
	public void addZone(Integer id, Zone zone) {
		this.zones.put(id, zone);
	}
	public void addRegion(Integer id, Region region) {
		this.regions.put(id, region);
	}
	public void setRegions(HashMap<Integer, com.pbm.Region> regions) {
		this.regions = regions;
	}
	public HashMap<Integer, com.pbm.Region> getRegions() {
		return regions;
	}
	public Object[] getRegionValues() {
		Object[] regionValues = getRegions().values().toArray();

		Arrays.sort(regionValues, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Region r1 = (Region) o1;
				Region r2 = (Region) o2;
				return r1.formalName.compareTo(r2.formalName);
			}
		});

		return regionValues;
	}
	public Object[] getLocationValues() {
		Object[] locationValues = getLocations().values().toArray();

		Arrays.sort(locationValues, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Location l1 = (Location) o1;
				Location l2 = (Location) o2;
				return l1.name.toString().compareTo(l2.name.toString());
			}
		});

		return locationValues;
	}
	public List<Object> getMachineValues(boolean displayAllMachines) {
		List<Object> machineValues = new ArrayList<Object>();

		if (displayAllMachines) {
			machineValues = Arrays.asList(getMachines().values().toArray());
		} else {
			for (Machine machine : getMachines().values()) {
				if (machine.existsInRegion) {
					machineValues.add(machine);
				}
			}
		}
		
		Collections.sort(machineValues, new Comparator<Object>() {
			public int compare(Object lhs, Object rhs) {
				Machine m1 = (Machine) lhs;
				Machine m2 = (Machine) rhs;

				return m1.name.replaceAll("^(?i)The ", "").compareTo(m2.name.replaceAll("^(?i)The ", ""));
			}
	    });

		return machineValues;
	}
	
	public void initializeAllMachines() throws UnsupportedEncodingException, InterruptedException, ExecutionException {
		Document doc = new RetrieveXMLTask().execute(PBMUtil.apiPath + "machines.xml").get();
		if (doc == null) {
			return;
		}
		
		NodeList itemNodes = doc.getElementsByTagName("machine"); 
		for (int i = 0; i < itemNodes.getLength(); i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
				Element itemElement = (Element) itemNode;                 

				String name = PBMUtil.readDataFromXML("name", itemElement);
				String id = PBMUtil.readDataFromXML("id", itemElement);

				if ((id != null) && (name != null)) {
					addMachine(Integer.parseInt(id), new Machine(Integer.parseInt(id), name, false));
				}
			} 
		}
	}

	public void initializeData(String URL) throws UnsupportedEncodingException, InterruptedException, ExecutionException {
		locations.clear();
		machines.clear();
		zones.clear();
		
		initializeAllMachines();

		Document doc = new RetrieveXMLTask().execute(URL).get();
		
		if (doc == null) {
			return;
		}
		
		NodeList itemNodes = doc.getElementsByTagName("location"); 
		for (int i = 0; i < itemNodes.getLength(); i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) { 
				Element itemElement = (Element) itemNode;                 

				String name = PBMUtil.readDataFromXML("name", itemElement);
				String id = PBMUtil.readDataFromXML("id", itemElement);
				String lat = PBMUtil.readDataFromXML("lat", itemElement);
				String lon = PBMUtil.readDataFromXML("lon", itemElement);
				String zone = PBMUtil.readDataFromXML("neighborhood", itemElement);
				String numMachines = PBMUtil.readDataFromXML("numMachines", itemElement);
				String zoneNo = PBMUtil.readDataFromXML("zoneNo", itemElement);

				if ((id != null) && (name != null) && (lat != null) && (lon != null) && (zoneNo != null) && (zone != null) && (numMachines != null)) {
					if(zoneNo.equals("")) {
						zoneNo = "0";
					}
					
					Location location = new com.pbm.Location(
						Integer.parseInt(id), name, lat, lon, zone, Integer.parseInt(numMachines), Integer.parseInt(zoneNo), null, null, null, null, null, 0
					);
					addLocation(Integer.parseInt(id), location);
				}
			} 
		}

		itemNodes = doc.getElementsByTagName("machine"); 
		for (int i = 0; i < itemNodes.getLength(); i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
				Element itemElement = (Element) itemNode;                 

				String name = PBMUtil.readDataFromXML("name", itemElement);
				String id = PBMUtil.readDataFromXML("id", itemElement);
				String numLocations = PBMUtil.readDataFromXML("numLocations", itemElement);

				if ((id != null) && (name != null) && (numLocations != null)) {
					Machine machine = getMachine(Integer.parseInt(id));
					machine.setNumLocations(Integer.parseInt(numLocations));
					machine.setExistsInRegion(true);
				}
			} 
		}

		itemNodes = doc.getElementsByTagName("zone"); 
		for (int i = 0; i < itemNodes.getLength(); i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
				Element itemElement = (Element) itemNode;  

				String name = PBMUtil.readDataFromXML("name", itemElement);
				String id = PBMUtil.readDataFromXML("id", itemElement);
				String shortName = PBMUtil.readDataFromXML("shortName", itemElement);
				String isPrimary = PBMUtil.readDataFromXML("isPrimary", itemElement);

				if ((id != null) && (name != null) && (shortName != null) && (isPrimary != null)){
					addZone(Integer.parseInt(id), new Zone(Integer.parseInt(id), name, shortName, Integer.parseInt(isPrimary)));
				}
			} 
		}
	}

	public void initializeMachines(String URL) throws UnsupportedEncodingException, InterruptedException, ExecutionException {
		Document doc = new RetrieveXMLTask().execute(URL).get();
		
		NodeList itemNodes = doc.getElementsByTagName("machine"); 
		for (int i = 0; i < itemNodes.getLength(); i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
				Element itemElement = (Element) itemNode;                 

				String name = PBMUtil.readDataFromXML("name", itemElement);
				String id = PBMUtil.readDataFromXML("id", itemElement);
				String numLocations = PBMUtil.readDataFromXML("numLocations", itemElement);

				if ((id != null) && (name != null) && (numLocations != null)) {
					addMachine(Integer.parseInt(id), new Machine(Integer.parseInt(id), name, Integer.parseInt(numLocations)));
				}
			} 
		}
	}
	
	public boolean initializeRegions() throws UnsupportedEncodingException, InterruptedException, ExecutionException {
		Document doc = new RetrieveXMLTask().execute(PBMUtil.apiPath + "regions.xml").get();
		
		if (doc == null) {
			return false;
		}
		
		NodeList itemNodes = doc.getElementsByTagName("region"); 

		for (int i = 0; i < itemNodes.getLength(); i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
				Element itemElement = (Element) itemNode;     

				String id = PBMUtil.readDataFromXML("id", itemElement);
				String name = PBMUtil.readDataFromXML("name", itemElement);
				String formalName = PBMUtil.readDataFromXML("full-name", itemElement);
				String motd = PBMUtil.readDataFromXML("motd", itemElement);
				List<String> emailAddresses = PBMUtil.readListDataFromXML("all-admin-email-address", itemElement);

				addRegion(Integer.parseInt(id), new Region(Integer.parseInt(id), name, formalName, motd, emailAddresses));
			}
		}
		return true;
	}
}