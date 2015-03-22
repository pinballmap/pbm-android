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

/**
 * Copyright (c) 2015, Brian Dols <brian.dols@gmail.com>
 * <p/>
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
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
		ArrayList<T> alist = new ArrayList<T>();
		InputStream inputStream;
		try {
			inputStream = PBMUtil.openHttpConnection(this.url, this.requestType);

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
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
