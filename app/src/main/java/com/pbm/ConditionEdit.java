package com.pbm;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class ConditionEdit extends PBMUtil {
	private LocationMachineXref lmx;
	private InputMethodManager inputMethodManager;

	@SuppressWarnings("static-access")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.condition_edit);

		Bundle extras = getIntent().getExtras();
		lmx = (LocationMachineXref) extras.get("lmx");

		logAnalyticsHit("com.pbm.ConditionEdit");

		// remove per Ryan
//		EditText condition = (EditText)findViewById(R.id.condition);
//		condition.setText(lmx.condition.equals("null") ? "" : lmx.condition);

		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(inputMethodManager.SHOW_FORCED, 0);
	}

	private void updateCondition(final String condition) {
		new Thread(new Runnable() {
			public void run() {
				try {
					lmx.setCondition(ConditionEdit.this, condition);
					new RetrieveJsonTask().execute(regionlessBase + "location_machine_xrefs/" + lmx.id + ".json?condition=" + URLEncoder.encode(condition, "UTF8"), "PUT").get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				ConditionEdit.super.runOnUiThread(new Runnable() {
					public void run() {
						final EditText currText = (EditText) findViewById(R.id.condition);
						inputMethodManager.hideSoftInputFromWindow(currText.getWindowToken(), 0);

						setResult(REFRESH_RESULT);
						ConditionEdit.this.finish();
					}
				});
			}
		}).start();
	}

	public void clickHandler(View view) {
		switch (view.getId()) {
			case R.id.submitCondition:
				EditText currText = (EditText) findViewById(R.id.condition);
				updateCondition(currText.getText().toString());

				break;
			case R.id.deleteCondition:
				updateCondition(" ");
				break;
			default:
				break;
		}
	}
}