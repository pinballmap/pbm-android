package com.pbm;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class ContactAdmin extends PinballMapActivity {
	public void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_admin);
		TextView contactAdmin = (TextView) findViewById(R.id.contact_admin_id);
		contactAdmin.setText("Send a Message to the " + getPBMApplication().getRegion().formalName + " Admin");
		logAnalyticsHit("com.pbm.ContactAdmin");
	}

	public void buttonOnClick(View view) throws UnsupportedEncodingException {
		String message = ((EditText) findViewById(R.id.messageField)).getText().toString();

		if (!message.isEmpty()) {
			PBMApplication app = getPBMApplication();
			Region region = app.getRegion(getSharedPreferences(PREFS_NAME, 0).getInt("region", -1));

			String url = regionlessBase + "regions/contact.json?region_id=" + region.id
					+ ";message=" + URLEncoder.encode(message, "UTF-8")
					+ ";name=" + URLEncoder.encode(((EditText) findViewById(R.id.submitterNameField)).getText().toString(), "UTF-8")
					+ ";email=" + URLEncoder.encode(((EditText) findViewById(R.id.submitterEmailField)).getText().toString(), "UTF-8");
			try {
				new RetrieveJsonTask().execute(app.requestWithAuthDetails(url), "POST").get();
				Toast.makeText(getBaseContext(), "Thank you for that submission! We'll be in touch.", Toast.LENGTH_LONG).show();
				setResult(REFRESH_RESULT);
				ContactAdmin.this.finish();
			} catch (InterruptedException | ExecutionException e) {
				Toast.makeText(getBaseContext(), "Sorry, there was a server error. Please try again later.", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getBaseContext(), "Message is a required field.", Toast.LENGTH_LONG).show();
		}
	}
}