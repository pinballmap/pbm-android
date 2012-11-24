package com.pbm;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

public class PBMUtil extends Activity {	
	public static final int MENU_RESULT     = 8;
	public static final int QUIT_RESULT     = 42;
	public static final int RESET_RESULT    = 23;
	public static final int REFRESH_RESULT  = 30;
	public static final int CONDITION_DATE  = 0;
	public static final int CONDITION       = 1;
	public static final int PROGRESS_DIALOG = 0;
	public static final int MENU_PREFS      = 0;
	public static final int MENU_ABOUT      = 1;
	public static final int MENU_QUIT       = 2;
	public static final int HTTP_RETRIES    = 5;
	public static final String PREFS_NAME = "pbmPrefs";

	public final static String holyBase = "http://pinballmap.com/";
	public static String httpBase = "http://pinballmap.com/";

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(MENU_PREFS, MENU_PREFS, MENU_PREFS, "Preferences");
		menu.add(MENU_ABOUT, MENU_ABOUT, MENU_ABOUT, "About");
		menu.add(MENU_QUIT, MENU_QUIT, MENU_QUIT, "Quit");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_PREFS:
			Intent myIntent = new Intent();
			myIntent.setClassName("com.pbm", "com.pbm.Preferences");
			startActivityForResult(myIntent, QUIT_RESULT);

			return true;
		case MENU_ABOUT:
			Intent aboutIntent = new Intent();
			aboutIntent.setClassName("com.pbm", "com.pbm.About");
			startActivityForResult(aboutIntent, QUIT_RESULT);

			return true;
		case MENU_QUIT:
			setResult(QUIT_RESULT);
			super.finish();
			this.finish();  

			return true;
		}
		return false;
	}

	public void activityQuitResult() {
		setResult(QUIT_RESULT);
		super.finish();
		this.finish();
	}

	public void activityResetResult() {
		setResult(RESET_RESULT);
		super.finish();
		this.finish();
	}

	public void activityRefreshResult() {}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode) {
		case QUIT_RESULT:
			activityQuitResult();
			break;
		case RESET_RESULT:
			activityResetResult();
			break;
		case REFRESH_RESULT:
			activityRefreshResult();
		default:
			break;
		}
	}

	public static void setHttpBase(String newBase) {
		httpBase = newBase;
	}

	protected void sendOneWayRequestToServer(String requestString) {
		InputStream in = null;	

		try {
			String URL = httpBase + "iphone.html?" + requestString;
			in = openHttpConnection(URL);

			@SuppressWarnings("unused")
			Document doc = null;
			DocumentBuilderFactory dbf = 
				DocumentBuilderFactory.newInstance();
			DocumentBuilder db;

			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(in);

				in.close();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}        
		} catch (IOException e1) {
			e1.printStackTrace();            
		}
	}

	protected static String readDataFromXML(String tagName, Element itemElement) {
		String dataName = "";
		Element titleElement = null;
		NodeList textNodes = null;

		NodeList titleNodes = (itemElement).getElementsByTagName(tagName);
		if (titleNodes.item(0) != null) {
			titleElement = (Element) titleNodes.item(0);

			textNodes = ((Node) titleElement).getChildNodes();
			if (textNodes.item(0) != null) {
				dataName = (((Node) textNodes.item(0)).getNodeValue());
			}
		}
		dataName = dataName.replaceAll("%", "%25");

		return URLDecoder.decode(dataName);
	}

	protected static String[] readDataFromXML(String tagName, Element itemElement, String attribute) {
		String[] itemAndAttribute = new String[2];
		Element titleElement = null;
		NodeList textNodes = null;

		NodeList titleNodes = (itemElement).getElementsByTagName(tagName);
		if (titleNodes.item(0) != null) {
			titleElement = (Element) titleNodes.item(0);

			textNodes = ((Node) titleElement).getChildNodes();
			if (textNodes.item(0) != null) {
				itemAndAttribute[0] = titleElement.getAttribute(attribute);
				String nodeValue = (((Node) textNodes.item(0)).getNodeValue());
				nodeValue = nodeValue.replaceAll("%", "%25");
				itemAndAttribute[1] = URLDecoder.decode(nodeValue);
			}
		}

		return itemAndAttribute;
	}

	public static InputStream openHttpConnection(String urlString) throws IOException {
		URL url = new URL(urlString); 
		try{
			for (int attempt = 0; attempt < HTTP_RETRIES; attempt++) {
				URLConnection urlConnection = url.openConnection();

				if (!(urlConnection instanceof HttpURLConnection))                     
					throw new IOException("Not an HTTP connection");

				HttpURLConnection httpConn = (HttpURLConnection) urlConnection;
				httpConn.setAllowUserInteraction(false);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod("GET");

				httpConn.connect(); 
				InputStream inputStream = httpConn.getInputStream();                                 

				if ((httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) && (inputStream != null)) {
					return inputStream;                                 
				}

				httpConn.disconnect();
			}
		} catch (Exception ex) {
			return null;
		}
		return null;     
	}

	public static Document getXMLDocument(String URL) {
		Document doc = null;

		try {
			InputStream inputStream = openHttpConnection(URL);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(inputStream);
			doc.getDocumentElement().normalize();

			inputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();            
		} catch (NullPointerException npe) {
			return doc;            
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (java.lang.IllegalArgumentException e) {
			return doc;
		}


		return doc;
	}

	public static int convertPixelsToDip(int dipValue, DisplayMetrics displayMetrics) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dipValue, displayMetrics);
	}

	public static boolean haveInternet(Context context) {
		NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}

		return true;
	}

	public void closeWithNoInternet() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Get some Internet, dude")
		.setMessage("This application requires an Internet connection, sorry.")
		.setPositiveButton("Bummer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activityQuitResult();		            
			}
		}).show();

		return;
	}

	public static Location updateLocationData(Location location) {	
		if (location == null) {
			return null;
		}

		Document doc = getXMLDocument(PBMUtil.httpBase + "iphone.html?get_location=" + location.locationNo);

		if (doc != null) {
			NodeList itemNodes = doc.getElementsByTagName("location"); 
			for (int i = 0; i < itemNodes.getLength(); i++) { 
				Node itemNode = itemNodes.item(i); 
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
					Element itemElement = (Element) itemNode;                 

					String street1 = PBMUtil.readDataFromXML("street1", itemElement);
					String city = PBMUtil.readDataFromXML("city", itemElement);
					String state = PBMUtil.readDataFromXML("state", itemElement);
					String zip = PBMUtil.readDataFromXML("zip", itemElement);
					String phone = PBMUtil.readDataFromXML("phone", itemElement);

					location.updateAddress(street1, city, state, zip, phone);
				} 
			}

			return location;
		}

		return location;
	}

	public List<Machine> getLocationMachineData(Location location) {
		if (location == null) {
			return null;
		}

		List<Machine> machines = new ArrayList<Machine>();

		Document doc = getXMLDocument(PBMUtil.httpBase + "iphone.html?get_location=" + location.locationNo);

		if (doc != null) {
			NodeList itemNodes = doc.getElementsByTagName("machine"); 
			for (int i = 0; i < itemNodes.getLength(); i++) { 
				Node itemNode = itemNodes.item(i); 
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {            
					Element itemElement = (Element) itemNode;     

					String id = readDataFromXML("id", itemElement);				
					if ((id != null)) {
						PBMApplication app = (PBMApplication) getApplication();
						Machine machine = app.getMachine(Integer.parseInt(id));

						machines.add(machine);
					}
				}
			}
			return machines;
		}

		return null;
	}
}
