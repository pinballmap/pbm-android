package com.pbm;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class RecentlyAdded extends PBMUtil {
	private ProgressDialog progressDialog;
	private ProgressThread progressThread;
	private ListView table;
	private final int NUM_ADDED_TO_SHOW = 20;

	private List<Spanned> recentAdds = new ArrayList<Spanned>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.recently_added);

		table = (ListView)findViewById(R.id.recentlyAddedTable);

		showDialog(PROGRESS_DIALOG);		
	}   

	public void getLocationData(String URL)
	{
		Document doc = getXMLDocument(URL);

		if (doc == null) {
			return;
		}

		NodeList itemNodes = doc.getElementsByTagName("item"); 
		for (int i = 0; i < NUM_ADDED_TO_SHOW; i++) { 
			Node itemNode = itemNodes.item(i); 
			if ((itemNode != null) && (itemNode.getNodeType() == Node.ELEMENT_NODE)) {            
				Element itemElement = (Element) itemNode;                 
				String title = readDataFromXML("title", itemElement);
				String[] splitString = (title.split(" was added to "));

				if (splitString.length > 0) {
					title = "<b>" + splitString[0] + "</b> was added to <b>" + splitString[1] + "</b>";
					title += "<br /><small>" + readDataFromXML("description", itemElement) + "</small>";
				}

				recentAdds.add(Html.fromHtml(title));
			}
		}
	}

	final Handler waitHandler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			progressDialog.setProgress(total);
			if (total >= 100){
				try{
					dismissDialog(PROGRESS_DIALOG); 
				} catch (java.lang.IllegalArgumentException iae) {}

				showTable(recentAdds);
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

	public void showTable(List<Spanned> locations) {
		table.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parentView, View selectedView, int position, long id) {	
				Intent myIntent = new Intent();
				Spanned spanned = (Spanned) parentView.getItemAtPosition(position);
				String locationName = spanned.toString().split(" was added to ")[1];
				locationName = locationName.split("\n")[0];

				PBMApplication app = (PBMApplication) getApplication();
				Location location = app.getLocationByName(locationName);

				if (location == null) {
					Toast.makeText(getBaseContext(), "Sorry, can't find that location", Toast.LENGTH_LONG).show();
					return;
				}

				myIntent.putExtra("Location", location);
				myIntent.setClassName("com.pbm", "com.pbm.LocationDetail");
				startActivityForResult(myIntent, QUIT_RESULT);
			}
		});

		table.setAdapter(new ArrayAdapter<Spanned>(this, android.R.layout.simple_list_item_1, locations));
	}

	private class ProgressThread extends Thread {
		Handler mHandler;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		public String getLocationRSSName() {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			Integer prefRegion = settings.getInt("region", -1);

			PBMApplication app = (PBMApplication) getApplication();
			Region region = app.getRegion(prefRegion);
			try {
				if (region.subDir.equals("")) {
					return "locations";
				} else {
					return region.subDir; 
				}
			} catch (NullPointerException npe) {
				return "";
			}
		}

		public void run() {
			getLocationData(httpBase + getLocationRSSName() + ".rss");

			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", 100);
			msg.setData(b);
			mHandler.sendMessage(msg);
		}
	}
}