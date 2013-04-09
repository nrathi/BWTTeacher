package org.techbridgeworld.bwt.teacher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class MyApplication extends Application implements TextToSpeech.OnInitListener {

	public TextToSpeech myTTS;
	public SensorManager myManager;
	public ShakeEventListener myListener; 

	public String prompt, help, option;
	public int game, category; 

	@Override
	public void onCreate () {
		myTTS = new TextToSpeech(this, this); 
		myManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		myListener = new ShakeEventListener();   
		myListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				speakOut(help);
			}
		});
		new HTTPAsyncTask().execute();
		
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = myTTS.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
				Log.e("TTS", "This language is not supported");
			speakOut(prompt); 
		}
		else
			Log.e("TTS", "Initilization Failed!");
	}

	/**
	 * Use TextToSpeech to speak a string out loud
	 * @param text
	 */
	public void speakOut(String text) {
		myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
	}
	
	public class HTTPAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// Creating HTTP client
			HttpClient httpClient = new DefaultHttpClient();
			// Creating HTTP Post
			HttpPost httpPost = new HttpPost("http://192.168.1.111:3000/login");

			// Building post parameters
			// key and value pair
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
			nameValuePair.add(new BasicNameValuePair("username", "admin"));
			nameValuePair.add(new BasicNameValuePair("password", "admin"));

			// Url Encoding the POST parameters
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
			} catch (UnsupportedEncodingException e) {
				// writing error to Log
				e.printStackTrace();
			}

			// Making HTTP Request
			try {
				HttpResponse response = httpClient.execute(httpPost);
				// writing response to log
				Log.d("jeff", response.toString());
			} catch (ClientProtocolException e) {
				// writing exception to log
				e.printStackTrace();
				Log.d("jeff", "client protocol exception");
			} catch (IOException e) {
				// writing exception to log
				e.printStackTrace();
				Log.d("jeff", "io exception");
			}
			return null;
		}
		
	}
}