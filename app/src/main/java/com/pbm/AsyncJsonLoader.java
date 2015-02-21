package com.pbm;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by dols on 2/21/15.
 */
public class AsyncJsonLoader<T> extends AsyncTaskLoader<ArrayList<T>> {

	String url;
	String requestType;
	ArrayList<T> data = null;
	JSONConverter<T> type;

	public AsyncJsonLoader (Context context, String url, String requestType, JSONConverter<T> type) {
		super(context);
		this.url = url;
		this.requestType = requestType;
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
		ArrayList<T> alist = new ArrayList<T>();
		InputStream inputStream = null;
		try {
			inputStream = PBMUtil.openHttpConnection(this.url, this.requestType);

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			JSONObject jsonObject = new JSONObject(result);
			Log.i("json", result);
			JSONArray objs = jsonObject.getJSONArray(type.getJsonLabel());
			for (int i = 0; i < objs.length(); i++) {
				alist.add(this.type.fromJSON(objs.getJSONObject(i)));
			}
			return alist;
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
