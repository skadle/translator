package com.example.translator;

import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends Activity {
	
	private EditText beforeTrans = (EditText) findViewById(R.id.editText1);
	private	EditText afterTrans = (EditText) findViewById(R.id.editText2);
	private RadioButton radio = (RadioButton) findViewById(R.id.radio0);
	private int mode;

	/**
	 * This method takes String given by user and translate the String into English
	 * @param sentence is an String value that the user wants to translate to English
	 * @param mode is an int that determines whether directly translate Korean to English or
	 * 	      korean to Japanese to English for better translation
	 * @return String sentence that is translated to English
	 * This method was mostly from a question "HTTP POST usingJSON in Java" from stackoverflow 
	 */
	private String DownloadText(String sentence, int mode) {
		String strDefinition = "";
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost request = new HttpPost(
					"http://translator.suminb.com/translate");
			String translate = URLEncoder.encode(sentence, "UTF-8");
			StringEntity params = new StringEntity(
					String.format("source=ko&target=en&mode=%d&text=%s", mode, translate));
			request.addHeader("content-type",
					"application/x-www-form-urlencoded");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			strDefinition = EntityUtils.toString(entity);
		} catch (Exception ex) {
			strDefinition = "HttpPost is not working properly";
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return strDefinition;
	}

	/**
	 * @author Wei-Meng Lee
	 * code from beginning android application development 4 by Wei-Meng Lee bit modified
	 */
	private class DownloadTextTask extends AsyncTask<Object, Void, String> {
		protected String doInBackground(Object... urls) {
			int mode = (Integer) urls[1];
			return DownloadText((String) urls[0], mode);
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

		beforeTrans.setText("전망 좋은 카페에 앉아 먹었던 티라미수");

		afterTrans.setText("전망 좋은 카페에 앉아 먹었던 티라미수");
	}

	public void onClickTranslate() {
		if(radio.isChecked())
			mode = 1;
		else
			mode = 2;
		
		String beforeVal = beforeTrans.getText().toString();
		String needToTrans = "";

		try {
			needToTrans = new DownloadTextTask().execute(beforeVal, mode).get()
					.toString();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		afterTrans.setText(needToTrans);
	}

}
