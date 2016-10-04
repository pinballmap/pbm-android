package com.pbm;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class SuggestRegion extends PinballMapActivity {
	public void onCreate(Bundle savedInstanceState) {
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_region);

		logAnalyticsHit("com.pbm.SuggestRegion");
	}

	public void buttonOnClick(View view) throws UnsupportedEncodingException {
		String regionName = ((EditText) findViewById(R.id.regionNameField)).getText().toString();

		if (!regionName.isEmpty()) {
			String url = regionlessBase + "regions/suggest.json?"
					+ "region_name=" + URLEncoder.encode(regionName, "UTF-8")
					+ ";comments=" + URLEncoder.encode(((EditText) findViewById(R.id.commentsField)).getText().toString(), "UTF-8");
			try {
				new RetrieveJsonTask().execute(
					getPBMApplication().requestWithAuthDetails(url),
					"POST"
				).get();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}

			Toast.makeText(getBaseContext(), "Thank you for that submission! We'll be in touch.", Toast.LENGTH_LONG).show();
			setResult(REFRESH_RESULT);
			SuggestRegion.this.finish();
		} else {
			Toast.makeText(getBaseContext(), "A suggested region name is a required field.", Toast.LENGTH_LONG).show();
		}
	}
}