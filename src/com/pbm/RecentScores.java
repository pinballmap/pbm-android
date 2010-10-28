package com.pbm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecentScores extends PBMUtil {
	private ListView table;
	private ProgressDialog progressDialog;
	private ProgressThread progressThread;
	private List<Spanned> recentScores = new ArrayList<Spanned>();
	final private static int NUM_RECENT_SCORES_TO_SHOW = 20;	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.recentscores);
		table = (ListView)findViewById(R.id.recentscorestable);

		showDialog(PROGRESS_DIALOG);		
	}

	public void getLocationData(String URL) {
		Document doc = getXMLDocument(URL);
		
		if (doc == null) {
			return;
		}

		NodeList itemNodes = doc.getElementsByTagName("item"); 
		for (int i = 0; i < NUM_RECENT_SCORES_TO_SHOW; i++) { 
			Node itemNode = itemNodes.item(i); 
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
				Element itemElement = (Element) itemNode;   	
				String title = readDataFromXML("title", itemElement);

				Matcher matcher = Pattern.compile("(.*):(.*)\\,\\swith(.*)\\sby(.*)\\son\\s(.*)").matcher(title);
				if (matcher.find()) {
					String locationName = matcher.group(1);		
					title = locationName + "<br />" + 
					matcher.group(2) + " with " + matcher.group(3) + " by <b>" + matcher.group(4) + "</b>" + 
					"<br /><small>" + matcher.group(5) + "</small>";
				} else {
					//Log.i("new high score", title);
				}

				Spanned titleSpanned = Html.fromHtml(title);
				recentScores.add(titleSpanned);
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

				showTable(recentScores);
			}
		}
	};

	public void showTable(List<Spanned> items) {
		table.setAdapter(new ArrayAdapter<Spanned>(this, android.R.layout.simple_list_item_1, items));
	}

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
		Handler handler;

		ProgressThread(Handler h) {
			handler = h;
		}

		public String getScoreRSSName() {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			Integer prefRegion = settings.getInt("region", -1);

			PBMApplication app = (PBMApplication) getApplication();
			Region region = app.getRegion(prefRegion);
			if (region.subDir.equals("")) {
				return "scores";
			} else {
				return region.subDir + "_scores.rss"; 
			}
		}

		public void run() {
			getLocationData(httpBase + getScoreRSSName() + ".rss");

			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", 100);
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}
}