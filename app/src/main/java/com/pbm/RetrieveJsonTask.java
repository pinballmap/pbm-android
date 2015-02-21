package com.pbm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.os.AsyncTask;

public class RetrieveJsonTask extends AsyncTask<String, Void, String> {
	
	private OnTaskCompleted listener;
	
    public RetrieveJsonTask(){ }

    public RetrieveJsonTask(OnTaskCompleted listener){
        this.listener=listener;
    }

	protected String doInBackground(String... urls) {
		String result = null;

		try {
			InputStream inputStream = null;
			try {
				String url = urls[0];
				String requestType = urls[1];
				
			    inputStream = PBMUtil.openHttpConnection(url, requestType);

			    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			    StringBuilder sb = new StringBuilder();

			    String line = null;
			    while ((line = reader.readLine()) != null) {
			        sb.append(line + "\n");
			    }
			    result = sb.toString();
			} catch (Exception e) { 
			}
		} catch (NullPointerException npe) {
			return result;            
		} catch (java.lang.IllegalArgumentException e) {
			return result;
		}
		
		return result;
	}
	
    protected void onPostExecute(String results){
    	if (listener != null) {
    		try {
				listener.onTaskCompleted(results);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}
    }
}