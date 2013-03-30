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

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

@TargetApi(11)
public class MainActivity extends Activity {
	
	//private EditText beforeTrans = (EditText) findViewById(R.id.editText1);
	//private EditText afterTrans = (EditText) findViewById(R.id.editText2);
	//private RadioButton radio = (RadioButton) findViewById(R.id.radio0);
	private int mode;
	private String source;
	private String target;

	/**
	 * This method takes String given by user and translate the String into English
	 * @param sentence is an String value that the user wants to translate to English
	 * @param mode is an int that determines whether directly translate Korean to English or
	 * 	      korean to Japanese to English for better translation
	 * @return String sentence that is translated to English
	 * This method was mostly from a question "HTTP POST usingJSON in Java" from stackoverflow 
	 */
	private String DownloadText(String sentence, int mode, String source, String target) {
		String strDefinition = "";
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost request = new HttpPost(
					"http://translator.suminb.com/translate");
			String translate = URLEncoder.encode(sentence, "UTF-8");
			StringEntity params = new StringEntity(
					String.format("sl=%s&tl=%s&m=%d&t=%s", source , target , mode, translate));
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
	private class DownloadTextTask extends AsyncTask<Object, String, String> {
		protected void onPreExecute() {
			EditText afterTrans = (EditText) findViewById(R.id.editText2);
			afterTrans.setText("(Translating...)");
		}
		protected String doInBackground(Object... urls) {
			int mode = (Integer) urls[1];
			String source = (String) urls[2];
			String target = (String) urls[3];
			
			return DownloadText((String) urls[0], mode, source, target);
		}
		
		protected void onPostExecute(String result) {
			EditText afterTrans = (EditText) findViewById(R.id.editText2);
			afterTrans.setText(result);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		EditText beforeTrans = (EditText) findViewById(R.id.editText1);
		EditText afterTrans = (EditText) findViewById(R.id.editText2);

		Button trans = (Button) findViewById(R.id.button1);

		trans.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onClickTranslate();
			}
		});
 
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
 
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.language_array2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
 
		beforeTrans.setText("여러분이 몰랐던 구글 번역기");

		afterTrans.setText("Google translation that you did not know");
	}

	public void onClickTranslate() {
		
		EditText beforeTrans = (EditText) findViewById(R.id.editText1);
		EditText afterTrans = (EditText) findViewById(R.id.editText2);
		RadioButton radio = (RadioButton) findViewById(R.id.radio0);
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
		
		String inputLan = spinner.getSelectedItem().toString();
		String outputLan = spinner2.getSelectedItem().toString();
		
		if(radio.isChecked())
			mode = 2;
		else
			mode = 1;
		
		if(inputLan.compareTo("한국어") == 0){
			source = "ko";
		} else if(inputLan.compareTo("영어") == 0){
			source = "en";
		} else if(inputLan.compareTo("프랑스어") == 0){
			source = "fr";
		} else if(inputLan.compareTo("일본어") == 0){
			source = "ja";
		} else if(inputLan.compareTo("스페인어") == 0){
			source = "es";
		} else if(inputLan.compareTo("러시아어") == 0){
			source = "ru";
		} else {
			source = "id";
		}
		
		if(outputLan.compareTo("한국어") == 0){
			target = "ko";
		} else if(outputLan.compareTo("영어") == 0){
			target = "en";
		} else if(outputLan.compareTo("프랑스어") == 0){
			target = "fr";
		} else if(outputLan.compareTo("일본어") == 0){
			target = "ja";
		} else if(outputLan.compareTo("스페인어") == 0){
			target = "es";
		} else if(outputLan.compareTo("러시아어") == 0){
			target = "ru";
		} else {
			target = "id";
		}
				
		String beforeVal = beforeTrans.getText().toString();

		new DownloadTextTask().execute(beforeVal, mode, source, target);
	}

}
