package com.pbm;

import java.io.UnsupportedEncodingException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SplashScreen extends PBMUtil implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private ListView table;
	private LocationClient locationClient;
	private volatile SplashThread splashThread;
	private android.location.Location yourLocation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.splash);

		table = (ListView)findViewById(R.id.splashRegionTable);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Integer prefRegion = settings.getInt("region", -1);
		PBMApplication app = (PBMApplication) getApplication();

		try {
			if (!haveInternet(getBaseContext()) || !app.initializeRegions()) {
				closeWithNoInternet();
				return;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient = new LocationClient(this, this, this);
        }

		if (prefRegion == -1) {
			table.setVisibility(View.VISIBLE);

			table.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
					setProgressBarIndeterminateVisibility(true);
					Region region = (Region) parentView.getItemAtPosition(position);
					if (! (region.name.equals(""))) {
						setRegionBase(httpBase + apiPath + "region/" + region.name + "/");						
					}

					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("region", region.id);
					editor.commit();

					loadSplashAndStart(region);
				}
			});

			table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getRegionValues()));
		} else {
			Region region = app.getRegion(prefRegion);
			setRegionBase(httpBase + apiPath + "region/" + region.name + "/");						

			loadSplashAndStart(region);
		}
	}
	
	private void loadSplashAndStart(Region region) {
		setProgressBarIndeterminateVisibility(true);
		((TextView) findViewById(R.id.greeting)).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.spacer)).setVisibility(View.INVISIBLE);
		table.setVisibility(View.INVISIBLE);

		ImageView splashImage = (ImageView) findViewById(R.id.splash_image);
		splashImage.setVisibility(View.VISIBLE);

		int resID = getResources().getIdentifier(getCityNamePath(region), null, null);		
		if (resID != 0) {
			ImageView cityNameImage = (ImageView) findViewById(R.id.splash_image_city);
			Drawable image = getResources().getDrawable(resID);
			cityNameImage.setImageDrawable(image);	
			cityNameImage.setVisibility(View.VISIBLE);
		}

		splashThread = new SplashThread();
		splashThread.start();
	}

	private String getCityNamePath(Region region) {
		String cityNamePath = "com.pbm:drawable/";
		String cityName = region.name;

		if (cityName.equals("")) {
			cityName = "portland_city";
		} else {
			cityName += "_city";
		}

		cityNamePath += cityName;
		return cityNamePath;
	}

	private class SplashThread extends Thread {
		public void run() {
			try {
				PBMApplication app = (PBMApplication) getApplication();
				app.initializeData();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				Intent myIntent = new Intent();
				myIntent.setClassName("com.pbm", "com.pbm.PBMMenu");
				startActivity(myIntent);

				finish();
				interrupt();
			}
		}
	}

	public static void startApp() {}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void onStart() {
		super.onStart();
		
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.connect();
        }
	}

	public void onStop() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onStop();
	}

	public void onPause() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onPause();
	}

	public void onDestroy() {
        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
        	locationClient.disconnect();
        }

		super.onDestroy();
	}

	public void onConnected(Bundle arg0) {
		if (locationClient != null) {
			yourLocation = locationClient.getLastLocation();

			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putFloat("yourLat", (float) yourLocation.getLatitude());
			editor.putFloat("yourLon", (float) yourLocation.getLongitude());
			editor.commit();
		}
	}

	public void onConnectionFailed(ConnectionResult arg0) {}
	public void onDisconnected() {}
	public void onLocationChanged(Location arg0) {}
}