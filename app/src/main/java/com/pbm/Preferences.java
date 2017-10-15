package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class Preferences extends PinballMapActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_tab_container);

		logAnalyticsHit("com.pbm.Preferences");

		final SharedPreferences settings = getSharedPreferences(PinballMapActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("region", -1);
		editor.apply();

		Intent myIntent = new Intent();
		myIntent.setClassName("com.pbm", "com.pbm.SplashScreen");
		startActivityForResult(myIntent, PinballMapActivity.QUIT_RESULT);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}