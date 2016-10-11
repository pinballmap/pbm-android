package com.pbm;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends PinballMapActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

        logAnalyticsHit("com.pbm.About");
        blogButton();
        rateButton();
        emailButton();

	}

    @SuppressWarnings("deprecation")
    public void blogButton() {
        TextView blog = (TextView) findViewById(R.id.aboutBlog);
        blog.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= 24) {
            blog.setText(Html.fromHtml("<a href=\"http://blog.pinballmap.com\">Blog</a>",Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
        } else {
            blog.setText(Html.fromHtml("<a href=\"http://blog.pinballmap.com\">Blog</a>")); // or for older api
        }
    }

    @SuppressWarnings("deprecation")
    public void rateButton() {
        TextView rate = (TextView) findViewById(R.id.aboutRate);
        rate.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= 24) {
            rate.setText(Html.fromHtml("<a href=\"market://details?id=com.pbm\">Rate us on Google Play</a>",Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
        } else {
            rate.setText(Html.fromHtml("<a href=\"market://details?id=com.pbm\">Rate us on Google Play</a>")); // or for older api
        }
    }

    public void emailButton() {

        TextView email = (TextView) findViewById(R.id.aboutEmail);

        String deviceInfo="Device Info:";
        deviceInfo += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        deviceInfo += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        deviceInfo += "\n Device: " + android.os.Build.DEVICE;
        deviceInfo += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";

        email.setMovementMethod(LinkMovementMethod.getInstance());
        email.setText(Html.fromHtml("<a href=\"mailto:pinballmap@posteo.org?subject=PBM - Android App Feedback&body=" + deviceInfo + "\">pinballmap@posteo.org</a>"));
    }
}
