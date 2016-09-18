package com.pbm;

import android.os.Bundle;

public class About extends PinballMapActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

        logAnalyticsHit("com.pbm.About");
	}   
}