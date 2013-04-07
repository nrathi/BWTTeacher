package org.techbridgeworld.bwt.teacher;

import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
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

}