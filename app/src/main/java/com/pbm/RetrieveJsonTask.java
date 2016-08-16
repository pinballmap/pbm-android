package com.pbm;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class RetrieveJsonTask extends AsyncTask<String, Void, String> {

	private OnTaskCompleted listener;

	public RetrieveJsonTask() {}

	public RetrieveJsonTask(OnTaskCompleted listener) {
		this.listener = listener;
	}

	protected String doInBackground(String... urls) {
		String result = null;

		try {
			InputStream inputStream;
			try {
				String url = urls[0];
				String requestType = urls[1];

				inputStream = PinballMapActivity.openHttpConnection(url, requestType);

				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				result = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (NullPointerException npe) {
			return result;
		} catch (java.lang.IllegalArgumentException e) {
			return result;
		}

		return result;
	}

	protected void onPostExecute(String results) {
		if (listener != null) {
			try {
				listener.onTaskCompleted(results);
			} catch (JSONException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}