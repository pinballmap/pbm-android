package com.pbm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dols on 2/21/15.
 */
public interface JSONConverter<T> {

	public final static String jsonLabel = "";
	public T fromJSON(JSONObject json) throws JSONException;
}
