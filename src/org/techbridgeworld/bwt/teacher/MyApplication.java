package org.techbridgeworld.bwt.teacher;

import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

public class MyApplication extends Application implements TextToSpeech.OnInitListener {

	public TextToSpeech myTTS;
	public SensorManager myManager;
	public ShakeEventListener myListener; 
	
	public String prompt;
	public String help;
	
	public TextView textView;
	
	public String[] options;
	public int currentOption;
	
	@Override
	public void onCreate () {
		myTTS = new TextToSpeech(this, this); 
		myManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		myListener = new ShakeEventListener();   
		myListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				if(options != null)
					help = help.replace("xxx", options[currentOption].toUpperCase(Locale.US));
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
		}
		else
			Log.e("TTS", "Initilization Failed!");
	}
	
	/**
	 * Update help and shift currentOption to the left
	 */
	public void moveLeft() {
		help = help.replace(options[currentOption].toUpperCase(Locale.US), "xxx");
		currentOption = currentOption == 0 ? options.length - 1 : currentOption - 1;
	}
	
	/**
	 * Update help and shift currentOption to the right
	 */
	public void moveRight() {
		help = help.replace(options[currentOption].toUpperCase(Locale.US), "xxx");
		currentOption = (currentOption + 1) % options.length; 
	}
	
	/**
	 * Update textView with the new current option
	 */
	public void changeText() {
		textView.setText(options[currentOption]);
		textView.setContentDescription(options[currentOption]);
	}
	
	/**
	 * Update prompt with the new current option
	 */
	public void changePrompt() {
		prompt = getResources().getString(R.string.record) + options[currentOption];
		speakOut(prompt);
	}
	
	/**
	 * Return the current option
	 * @return the current option
	 */
	public String getOption() {
		return options[currentOption]; 
	}
	
	/**
	 * Use TextToSpeech to speak a string out loud
	 * @param text
	 */
	public void speakOut(String text) {
		myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

}