package com.pbm;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class EnterScore extends PinballMapActivity implements OnTaskCompleted {
	private LocationMachineXref lmx;
	private InputMethodManager inputMethodManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_score);

		Bundle extras = getIntent().getExtras();
		lmx = (LocationMachineXref) extras.get("lmx");

		logAnalyticsHit("com.pbm.EnterScore");

		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

		final Button addScore = (Button) findViewById(R.id.submitScore);
		addScore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			addScore.setEnabled(false);
			EditText currText = (EditText) findViewById(R.id.score);
			addScore(currText.getText().toString());
			}
		});
	}

	private void addScore(final String score) {
		new Thread(new Runnable() {
			public void run() {
			try {
				new RetrieveJsonTask(EnterScore.this).execute(
					getPBMApplication().requestWithAuthDetails(regionlessBase + "machine_score_xrefs.json?location_machine_xref_id=" + lmx.getId() + ";score=" + URLEncoder.encode(score, "UTF8")),
					"POST"
				).get();
			} catch (InterruptedException | ExecutionException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			EnterScore.super.runOnUiThread(new Runnable() {
				public void run() {
				final EditText currText = (EditText) findViewById(R.id.score);
				inputMethodManager.hideSoftInputFromWindow(currText.getWindowToken(), 0);

				setResult(REFRESH_RESULT);
				EnterScore.this.finish();
				}
			});
			}
		}).start();
	}

	@Override
	public void onTaskCompleted(String results) throws JSONException, InterruptedException, ExecutionException {
		final JSONObject jsonObject = new JSONObject(results);
		if (jsonObject.has("machine_score_xref")) {
			JSONObject jsonLmx = jsonObject.getJSONObject("machine_score_xref");
			int id = jsonLmx.getInt("id");
			long score = jsonLmx.getLong("score");
			String username = jsonLmx.getString("username");

			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

			lmx.addScore(
				EnterScore.this,
				new MachineScore(id, lmx.getId(), df.format(new Date()), username, score)
			);
		}
	}
}