package com.pbm;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class AsyncJsonLoader<T> extends AsyncTaskLoader<ArrayList<T>> {
	private final String url;
	private final String requestType;
	private ArrayList<T> data = null;
	private final JSONConverter<T> type;

	public AsyncJsonLoader(Context context, String url, JSONConverter<T> type) {
		super(context);
		this.url = url;
		this.requestType = "GET";
		this.type = type;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (data != null) {
			deliverResult(data);
		} else {
			forceLoad();
		}
	}

	@Override
	public ArrayList<T> loadInBackground() {
		String result;
		ArrayList<T> arrayList = new ArrayList<T>();
		InputStream inputStream;
		try {
			inputStream = PinballMapActivity.openHttpConnection(this.url, this.requestType);

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder stringBuilder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			result = stringBuilder.toString();
			JSONObject jsonObject = new JSONObject(result);
			Log.i("com.pbm", result);
			JSONArray objs = jsonObject.getJSONArray(type.getJsonLabel());
			for (int i = 0; i < objs.length(); i++) {
				arrayList.add(this.type.fromJSON(objs.getJSONObject(i)));
			}
			return arrayList;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void deliverResult(ArrayList<T> data) {
		super.deliverResult(data);
		this.data = data;
	}
}
