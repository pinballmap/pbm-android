package com.pbm;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class ContactAdmin extends PBMUtil {
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_admin);

		logAnalyticsHit("com.pbm.ContactAdmin");
	}
	
	public void buttonOnClick(View view) throws UnsupportedEncodingException {
		String message = ((EditText) findViewById(R.id.messageField)).getText().toString();

		if (message != null && !message.isEmpty()) {
			PBMApplication app = (PBMApplication) getApplication();
			Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));
				
			String url = regionlessBase + "regions/contact.json?region_id=" + region.id
					+ ";message=" + URLEncoder.encode(message, "UTF-8")
					+ ";name=" + URLEncoder.encode(((EditText) findViewById(R.id.submitterNameField)).getText().toString(), "UTF-8")
					+ ";email=" + URLEncoder.encode(((EditText) findViewById(R.id.submitterEmailField)).getText().toString(), "UTF-8")
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
			ContactAdmin.this.finish();
		} else {
			Toast.makeText(getBaseContext(), "Message is a required field.", Toast.LENGTH_LONG).show();
		}
	}
}