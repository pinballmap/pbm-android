package com.pbm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;

public class Preferences extends PBMUtil {
	private	ActionBar.Tab regionsByNameTab, regionsByLocationTab;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_tab_container);

		logAnalyticsHit("com.pbm.Preferences");

		final SharedPreferences settings = getSharedPreferences(PBMUtil.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("region", -1);
		editor.commit();

		Intent myIntent = new Intent();
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		myIntent.setClassName("com.pbm", "com.pbm.SplashScreen");
		startActivityForResult(myIntent, PBMUtil.QUIT_RESULT);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}