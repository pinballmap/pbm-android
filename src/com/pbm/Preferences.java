package com.pbm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class Preferences extends PBMUtil {
	private ListView table;
	private ProgressDialog progressDialog;
	private ProgressThread progressThread;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		
		table = (ListView)findViewById(R.id.preferencesRegionTable);
		table.setFastScrollEnabled(true);
		table.setTextFilterEnabled(true);
		
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Region region = (Region) parentView.getItemAtPosition(position);
				table.setEnabled(false);
				PBMMenu.setHttpBase(holyBase + region.subDir + "/");	

				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				settings.edit().putInt("region", region.regionNo).commit();

				showDialog(PROGRESS_DIALOG);

				Thread splashTread = new Thread() {
					public void run() {
						try {
						} finally {
						}
					}
				};
				splashTread.start();
			}
		});
		
		PBMApplication app = (PBMApplication) getApplication();
		table.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, app.getRegionValues()));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	final Handler waitHandler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");

			if (total >= 100){
				try{
					dismissDialog(PROGRESS_DIALOG); 

					setResult(RESET_RESULT);
					finish();
				} catch (java.lang.IllegalArgumentException iae) {}
			}
		}
	};

	protected Dialog onCreateDialog(int id) { 
		switch(id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading...");
			progressThread = new ProgressThread(waitHandler);
			progressThread.start();

			return progressDialog;
		default:
			return null;
		}
	}

	private class ProgressThread extends Thread {
		Handler mHandler;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			PBMApplication app = (PBMApplication) getApplication();
			app.initializeData(httpBase + "iphone.html?init=1");
			
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", 100);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}
}