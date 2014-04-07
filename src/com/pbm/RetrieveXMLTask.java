package com.pbm;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

public class RetrieveXMLTask extends AsyncTask<String, Void, Document> {

	protected Document doInBackground(String... urls) {
		Document doc = null;
		
		try {
			InputStream inputStream = PBMUtil.openHttpConnection(urls[0]);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(inputStream);
			doc.getDocumentElement().normalize();

			inputStream.close();
		} catch (IOException ioe) {
			Log.e("1", ioe.toString());
			ioe.printStackTrace();            
		} catch (NullPointerException npe) {
			Log.e("2", npe.toString());
			return doc;            
		} catch (ParserConfigurationException e) {
			Log.e("3", e.toString());
			e.printStackTrace();
		} catch (SAXException e) {
			Log.e("4", e.toString());
			e.printStackTrace();
		} catch (java.lang.IllegalArgumentException e) {
			Log.e("5", e.toString());
			return doc;
		}

		return doc;
	}

	protected void onPostExecute(Document doc) {
    }
}