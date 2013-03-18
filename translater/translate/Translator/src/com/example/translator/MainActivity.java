package com.example.translator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	//code from beginning android application development 4 by Wei-Meng Lee
	private InputStream OpenHttpConnection(String urlString) throws IOException {
		InputStream in = null;
		int response = 1;

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");
		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception ex) {
			Log.d("Networking", ex.getLocalizedMessage());
			throw new IOException("Error connecting");
		}
		return in;
	}

	//code from beginning android application development 4 by Wei-Meng Lee
	private String DownloadText(String word) {
		InputStream in = null;
        String strDefinition = "";
        try {
            in = OpenHttpConnection(
"http://services.aonaware.com/DictService/DictService.asmx/Define?word=" + word);
            Document doc = null;
            DocumentBuilderFactory dbf = 
                DocumentBuilderFactory.newInstance();
            DocumentBuilder db;            
            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
            doc.getDocumentElement().normalize(); 
            
            //---retrieve all the <Definition> elements---
            NodeList definitionElements = 
                doc.getElementsByTagName("Definition"); 
            
            //---iterate through each <Definition> elements---
            for (int i = 0; i < definitionElements.getLength(); i++) { 
                Node itemNode = definitionElements.item(i); 
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) 
                {            
                    //---convert the Definition node into an Element---
                    Element definitionElement = (Element) itemNode;
                    
                    //---get all the <WordDefinition> elements under 
                    // the <Definition> element---
                    NodeList wordDefinitionElements = 
                        (definitionElement).getElementsByTagName(
                        "WordDefinition");
                                        
                    strDefinition = "";
                    //---iterate through each <WordDefinition> elements---
                    for (int j = 0; j < wordDefinitionElements.getLength(); j++) {                    
                        //---convert a <WordDefinition> node into an Element---
                        Element wordDefinitionElement = 
                            (Element) wordDefinitionElements.item(j);
                        
                        //---get all the child nodes under the 
                        // <WordDefinition> element---
                        NodeList textNodes = 
                            ((Node) wordDefinitionElement).getChildNodes();
                        
                        strDefinition += 
                            ((Node) textNodes.item(0)).getNodeValue() + ". \n";    
                    }
                    
                } 
            }
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }   
        //---return the definitions of the word---
        return strDefinition;
    }

	//code from beginning android application development 4 by Wei-Meng Lee bit modified
	private class DownloadTextTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			return DownloadText(urls[0]);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button trans = (Button) findViewById(R.id.button1);

		trans.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onClickTranslate();
			}
		});

		EditText beforeTrans = (EditText) findViewById(R.id.editText1);
		beforeTrans.setText("Hello World!");
	}

	public void onClickTranslate() {
		EditText beforeTrans = (EditText) findViewById(R.id.editText1);
		String beforeVal = beforeTrans.getText().toString().substring(0, 5);
		
		String needToTrans ="";
		
		try {
			needToTrans = new DownloadTextTask().execute(beforeVal).get().toString();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EditText afterTrans = (EditText) findViewById(R.id.editText2);
		afterTrans.setText(needToTrans);
	}

}
