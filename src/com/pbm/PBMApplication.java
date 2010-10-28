package com.pbm;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Application;

public class PBMApplication extends Application {
	private HashMap<Integer, com.pbm.Location> locations = new HashMap<Integer, Location>();
	private HashMap<Integer, com.pbm.Machine>  machines  = new HashMap<Integer, Machine>();
	private HashMap<Integer, com.pbm.Zone>     zones     = new HashMap<Integer, Zone>();
	private HashMap<Integer, com.pbm.Region>   regions   = new HashMap<Integer, Region>();

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
				return r1.name.compareTo(r2.name);
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
	public Object[] getMachineValues() {
		Object[] machineValues = getMachines().values().toArray();

		Arrays.sort(machineValues, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Machine m1 = (Machine) o1;
				Machine m2 = (Machine) o2;
				return m1.name.compareTo(m2.name);
			}
		});

		return machineValues;
	}
	
	public void initializeData(String URL) {
		locations.clear();
		machines.clear();
		zones.clear();

		Document doc = PBMUtil.getXMLDocument(URL);
		
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
					addMachine(Integer.parseInt(id), new Machine(Integer.parseInt(id), name, Integer.parseInt(numLocations)));
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

	public void initializeMachines(String URL) {
		Document doc = PBMUtil.getXMLDocument(URL);
		
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
	
	public boolean initializeRegions(String URL) {
		Document doc = PBMUtil.getXMLDocument(URL);
		
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
				String subDir = PBMUtil.readDataFromXML("subdir", itemElement);
				String motd = PBMUtil.readDataFromXML("motd", itemElement);

				addRegion(Integer.parseInt(id), new Region(Integer.parseInt(id), name, subDir, motd));
			}
		}
		return true;
	}
}