package com.pbm;

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
            storeText.setText(Html.fromHtml(getString(R.string.store)));

	}

    public void storeButton() {
        TextView blog = (TextView) findViewById(R.id.storeLink);
        blog.setMovementMethod(LinkMovementMethod.getInstance());
        blog.setText(Html.fromHtml("<a href=\"http://pinballmap.com/store\">Visit the Pinball Map Store</a>"));
    }

} 
