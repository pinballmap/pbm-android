package com.pbm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class SuggestRegion extends PBMUtil {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.suggest_region);

		logAnalyticsHit("com.pbm.SuggestRegion");
	}
	
	public void buttonOnClick(View view) throws UnsupportedEncodingException {
		String name = ((EditText) findViewById(R.id.submitterNameField)).getText().toString();
		String email = ((EditText) findViewById(R.id.submitterEmailField)).getText().toString();
		String regionName = ((EditText) findViewById(R.id.regionNameField)).getText().toString();

		if (name != null && !name.isEmpty() && email != null && !email.isEmpty() && regionName != null && !regionName.isEmpty()) {
			String url = regionlessBase + "regions/suggest.json?"
					+ "region_name=" + URLEncoder.encode(regionName, "UTF-8")
					+ ";name=" + URLEncoder.encode(name, "UTF-8")
					+ ";email=" + URLEncoder.encode(email, "UTF-8")
					+ ";comments=" + URLEncoder.encode(((EditText) findViewById(R.id.commentsField)).getText().toString(), "UTF-8")
			;
			try {
				new RetrieveJsonTask().execute(url, "POST").get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
				
			Toast.makeText(getBaseContext(), "Thank you for that submission! We'll be in touch.", Toast.LENGTH_LONG).show();
			setResult(REFRESH_RESULT);
			SuggestRegion.this.finish();
		} else {
			Toast.makeText(getBaseContext(), "Your name, email address, and a suggested region name are required fields.", Toast.LENGTH_LONG).show();
		}
	}
}