package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.Window;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SplashScreen extends PBMUtil {
	//	private GoogleApiClient locationClient;
	private LocationManager locationManager;

	public void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_tab_container);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		final SharedPreferences settings = getSharedPreferences(PBMUtil.PREFS_NAME, 0);
		PreferenceManager.setDefaultValues(this, PBMUtil.PREFS_NAME, 0, R.xml.preferences, false);
		Integer prefRegion = settings.getInt("region", -1);
		PBMApplication app = getPBMApplication();

		try {
			if (!haveInternet(getBaseContext())) {
				closeWithNoInternet();
				return;
			}
			if (!app.initializeRegions()) {
				closeOnMissingServer();
				return;
			}
		} catch (UnsupportedEncodingException | InterruptedException | ExecutionException | JSONException e) {
			e.printStackTrace();
		}

		if (prefRegion != -1) {
			Region region = app.getRegion(prefRegion);
			setRegionBase(httpBase + apiPath + "region/" + region.name + "/");						

			Intent myIntent = new Intent();	
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			myIntent.setClassName("com.pbm", "com.pbm.InitializingScreen");
			startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);

		} else {
			final ArrayList<Region> regionValues = app.getRegionValues();
			RegionsTab regionsTab = new RegionsTab();
			Bundle b = new Bundle();
			b.putSerializable("regions", regionValues);
			regionsTab.setArguments(b);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.region_fragment, regionsTab);
			fragmentTransaction.commit();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeItem(R.id.prefs);
		return super.onPrepareOptionsMenu(menu);
	}
}