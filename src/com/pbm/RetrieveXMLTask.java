package com.pbm;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

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

	protected void onPostExecute(Document doc) {
    }
}