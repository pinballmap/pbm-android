package com.pbm;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class InitializeData extends Thread {
	public PBMApplication app;

	public InitializeData(PBMApplication app) {
		this.app = app;
	}

	public void run() {
		app.setIsDataInitialized(false);

		for(Thread thread : app.getInitializationThreads()) {
			thread.interrupt();
		}

		BackgroundInitializer machineInitialize = new BackgroundInitializer(
				new Command() {
					public void execute() {
						try {
							Log.d("com.pbm", "TIMING STARTING MACHINES");
							app.initializeAllMachines();
							app.initializeRegionMachines();
							Log.d("com.pbm", "TIMING ENDING MACHINES");
						} catch (IOException | JSONException | ExecutionException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
		);
		app.addInitializationThread(machineInitialize);
		machineInitialize.start();

		BackgroundInitializer locationInitialize = new BackgroundInitializer(
				new Command() {
					public void execute() {
						try {
							Log.d("com.pbm", "TIMING STARTING LOCATION, LOCATION TYPES, OPERATORS");
							app.initializeLocations();
							app.initializeLocationTypes();
							app.initializeOperators();
							Log.d("com.pbm", "TIMING ENDING LOCATION, LOCATION TYPES, OPERATORS");
						} catch (IOException | JSONException | ExecutionException | InterruptedException | ParseException e) {
							e.printStackTrace();
						}
					}
				}
		);
		app.addInitializationThread(locationInitialize);
		locationInitialize.start();

		BackgroundInitializer zonesInitialize = new BackgroundInitializer(
				new Command() {
					public void execute() {
						try {
							Log.d("com.pbm", "TIMING STARTING ZONES");
							app.initializeZones();
							Log.d("com.pbm", "TIMING ENDING ZONES");
						} catch (IOException | JSONException | ExecutionException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
		);
		app.addInitializationThread(zonesInitialize);
		zonesInitialize.start();

		try {
			locationInitialize.join();
			machineInitialize.join();
			zonesInitialize.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		app.setIsDataInitialized(true);
		Log.d("com.pbm", "ALL DONE");
	}
}