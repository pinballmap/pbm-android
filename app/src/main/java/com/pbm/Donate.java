package com.pbm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
				String dataUri = "";
				try {
					dataUri = "data:text/html," + URLEncoder.encode(html, "UTF-8").replaceAll("\\+", "%20");
				} catch (UnsupportedEncodingException e) {
					return;
				}

				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(dataUri));
				startActivity(intent);
			}
		});
	}   

	private static String readTrimRawTextFile(Context ctx, int resId) {
		InputStream inputStream = ctx.getResources().openRawResource(resId);

		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while ((line = buffreader.readLine()) != null) {
				text.append(line.trim());
			}
		}
		catch (IOException e) {
			return null;
		}
		return text.toString();
	}
} 
