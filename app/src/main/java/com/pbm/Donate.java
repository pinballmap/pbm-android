package com.pbm;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@SuppressWarnings("deprecation")
public class Donate extends PinballMapActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate);

        logAnalyticsHit("com.pbm.Donate");

		TextView donateText  = (TextView) findViewById(R.id.donateText);
        donateText.setText(Html.fromHtml(getString(R.string.donate)));

		ImageView paypalButton = (ImageView)findViewById(R.id.donateButton);

		final String html = readTrimRawTextFile(this, R.raw.paypal_form);

		paypalButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				WebView browser = new WebView(getApplicationContext());
				browser.getSettings().setJavaScriptEnabled(true);
				browser.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				browser.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
				setContentView(browser);
				finish();
			}
		});
	}   

	private static String readTrimRawTextFile(Context ctx, int resId) {
		InputStream inputStream = ctx.getResources().openRawResource(resId);

		InputStreamReader inputReader = new InputStreamReader(inputStream);
		BufferedReader buffReader = new BufferedReader(inputReader);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while ((line = buffReader.readLine()) != null) {
				text.append(line.trim());
			}
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}
} 
