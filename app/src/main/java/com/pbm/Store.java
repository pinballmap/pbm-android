package com.pbm;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import android.widget.TextView;


@SuppressWarnings("deprecation")
public class Store extends PinballMapActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store);

        logAnalyticsHit("com.pbm.Store");
		storeButton();

		TextView storeText  = (TextView) findViewById(R.id.storeText);
        if (Build.VERSION.SDK_INT >= 24) {
            storeText.setText(Html.fromHtml(getString(R.string.store),Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
        } else {
            storeText.setText(Html.fromHtml(getString(R.string.store))); // or for older api
        }

	}

    public void storeButton() {
        TextView blog = (TextView) findViewById(R.id.storeLink);
        blog.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= 24) {
            blog.setText(Html.fromHtml("<a href=\"http://pinballmap.com/store\">Visit the Pinball Map Store</a>",Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
        } else {
            blog.setText(Html.fromHtml("<a href=\"http://pinballmap.com/store\">Visit the Pinball Map Store</a>")); // or for older api
        }
    }

} 