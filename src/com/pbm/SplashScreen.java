package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

public class SplashScreen extends PBMUtil {
	private ListView table;
	private volatile SplashThread splashThread;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.splash);

		table = (ListView)findViewById(R.id.splashRegionTable);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Integer prefRegion = settings.getInt("region", -1);
		PBMApplication app = (PBMApplication) getApplication();

		
		if (!haveInternet(getBaseContext()) || !app.initializeRegions(httpBase + "iphone.html?init=2")) {
			closeWithNoInternet();
			return;
		}
	
		if (prefRegion == -1) {
			table.setVisibility(View.VISIBLE);

			table.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
					setProgressBarIndeterminateVisibility(true);
					Region region = (Region) parentView.getItemAtPosition(position);
					if (! (region.subDir.equals(""))) {
						setHttpBase(holyBase + region.subDir + "/");						
					}

					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("region", region.regionNo);
					editor.commit();

					loadSplashAndStart(region);
				}
			});

			table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getRegionValues()));
		} else {
			Region region = app.getRegion(prefRegion);
			setHttpBase(holyBase + region.subDir + "/");

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
		String cityName = region.subDir;

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
				app.initializeData(httpBase + "iphone.html?init=1");
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
}