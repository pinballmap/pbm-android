package com.pbm;

import org.json.JSONException;
import org.json.JSONObject;

interface JSONConverter<T> {

	public T fromJSON(JSONObject json) throws JSONException;

	String getJsonLabel();
}
