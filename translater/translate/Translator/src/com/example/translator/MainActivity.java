package com.example.translator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

	private InputStream OpenHttpConnection(String urlString) throws IOException{
		InputStream in = null;
		int response = -1;
		
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		
		if(!(conn instanceof HttpURLConnection)){
			throw new IOException("Not an HTTp connection");
		}
		try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        }
        catch (Exception ex)
        {
        	Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
		return in;
		
	}
	
	private String DownloadText(String URL)
    {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
        } catch (IOException e) {
        	Log.d("MainActivity", e.getLocalizedMessage());
            return "";
        }
        
        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[BUFFER_SIZE];          
        try {
            while ((charRead = isr.read(inputBuffer))>0) {                    
                //---convert the chars to a String---
                String readString = 
                    String.copyValueOf(inputBuffer, 0, charRead);                    
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
        	Log.d("NetworkingActivity", e.getLocalizedMessage());
            return "";
        }    
        return str;        
    }
	
	 @Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.activity_main);
	     
	     EditText beforeTrans = (EditText) findViewById(R.id.editText1);
	     beforeTrans.setText("Hello World!");
	}
	
	public void onClickTranslate(){
		EditText beforeTrans = (EditText) findViewById(R.id.editText1);
		String beforeVal = beforeTrans.getText().toString();
		
		EditText afterTrans = (EditText) findViewById(R.id.editText2);
		afterTrans.setText(beforeVal);
	}

}
