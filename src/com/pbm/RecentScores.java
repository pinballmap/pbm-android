package com.pbm;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecentScores extends PBMUtil {
	private List<Spanned> recentScores = new ArrayList<Spanned>();
	final private static int NUM_RECENT_SCORES_TO_SHOW = 20;	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.recent_scores);

		logAnalyticsHit("com.pbm.RecentScores");

		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Loading...");
		dialog.show();

		new Thread(new Runnable() {
	        public void run() {
	        	try {
					getLocationData(httpBase + getScoreRSSName());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
	        	RecentScores.super.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						ListView table = (ListView)findViewById(R.id.recentscorestable);
						table.setAdapter(new ArrayAdapter<Spanned>(RecentScores.this, android.R.layout.simple_list_item_1, recentScores));
					}
	        	});
	        }
	    }).start();
	}

	public void getLocationData(String URL) throws UnsupportedEncodingException, InterruptedException, ExecutionException {
		Document doc = new RetrieveXMLTask().execute(URL).get();

		if (doc == null) {
			return;
		}

		NodeList itemNodes = doc.getElementsByTagName("item"); 
		for (int i = 0; i < NUM_RECENT_SCORES_TO_SHOW; i++) { 
			Node itemNode = itemNodes.item(i); 
			if ((itemNode != null) && (itemNode.getNodeType() == Node.ELEMENT_NODE)) {            
				Element itemElement = (Element) itemNode;   	
				String title = readDataFromXML("title", itemElement);

				Matcher matcher = Pattern.compile("(.*):(.*)\\,\\swith(.*)\\sby(.*)\\son\\s(.*)").matcher(title);
				if (matcher.find()) {
					String locationName = matcher.group(1);		
					double score = Double.parseDouble(matcher.group(3));
					DecimalFormat formatter = new DecimalFormat("#,###");

					title = locationName + "<br />" + 
					matcher.group(2) + " with " + formatter.format(score) + " by <b>" + matcher.group(4) + "</b>" + 
					"<br /><small>" + matcher.group(5) + "</small>";
				}

				Spanned titleSpanned = Html.fromHtml(title);
				recentScores.add(titleSpanned);
			}
		}
	}

	public String getScoreRSSName() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		Integer prefRegion = settings.getInt("region", -1);

		PBMApplication app = (PBMApplication) getApplication();
		Region region = app.getRegion(prefRegion);
		if (region.subDir.equals("")) {
			return "scores.rss";
		} else {
			return region.subDir + "_scores.rss"; 
		}
	}
}