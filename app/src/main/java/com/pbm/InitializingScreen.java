package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public class InitializingScreen extends PBMUtil {

	public void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initializing_screen);

		setSupportProgressBarIndeterminateVisibility(true);

		PBMApplication app = (PBMApplication) getApplication();
		SharedPreferences settings = getSharedPreferences(PBMUtil.PREFS_NAME, 0);
		Region region = app.getRegion(settings.getInt("region", 1));

		int resID = getResources().getIdentifier(getCityNamePath(region), null, null);
		if (resID != 0) {
			ImageView cityNameImage = (ImageView) findViewById(R.id.splash_image_city);
			Drawable image = getResources().getDrawable(resID);
			cityNameImage.setImageDrawable(image);
		}

		SplashThread splashThread = new SplashThread();
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

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}