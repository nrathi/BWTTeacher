package org.techbridgeworld.bwt.teacher;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class LanguageActivity extends Activity implements TextToSpeech.OnInitListener {

	private TextToSpeech tts;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector; 
	
	private SensorManager manager;
	private ShakeEventListener listener;
	
	private String languagePrompt;
	private String languageHelp;
	private TextView teacherLanguage; 
	
	private String[] options;
	private int numOptions = 3; 
	private int currentOption = 0; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_language);
		
		options = new String[numOptions]; 
		options[0] = getResources().getString(R.string.english);
		options[1] = getResources().getString(R.string.kannada);
		options[2] = getResources().getString(R.string.hindi);
		
		languagePrompt = getResources().getString(R.string.language_prompt);
		languageHelp = getResources().getString(R.string.language_help);
		teacherLanguage = (TextView) findViewById(R.id.teacher_language);
		
		tts = new TextToSpeech(this, this);
		detector = new GestureDetectorCompat(this, new MyGestureListener());
		
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listener = new ShakeEventListener();   
		listener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				languageHelp = languageHelp.replace("xxx", options[currentOption].toUpperCase(Locale.getDefault()));
				speakOut(languageHelp);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		manager.registerListener(listener,
				manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		manager.unregisterListener(listener);
		super.onPause();
	}
	
    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
	
	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
		this.detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
				Log.e("TTS", "This language is not supported");
			else
				speakOut(languagePrompt);
		}
		else
			Log.e("TTS", "Initilization Failed!");
	}
	
	private void speakOut(String text) {
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			// Swipe up
			if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(LanguageActivity.this, HomeActivity.class);
				startActivity(intent);
			}

			// Swipe down
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				switch(currentOption) {
					case 0:
						Intent intent = new Intent(LanguageActivity.this, CategoryActivity.class);
						intent.putExtra("language", options[currentOption]);
						startActivity(intent);
						break;
					case 1:
						//Intent intent = new Intent(LanguageActivity.this, PasswordActivity.class);
						//startActivity(intent);
						break;
					case 2:
						//Intent intent = new Intent(LanguageActivity.this, PasswordActivity.class);
						//startActivity(intent);		
						break;
					default:
				}
			}
			
			// Swipe left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				languageHelp = languageHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption - 1) % numOptions; 
				if(currentOption == -1) 
					currentOption += numOptions;
				teacherLanguage.setText(options[currentOption]);
				teacherLanguage.setContentDescription(options[currentOption]);
			}
			
			// Swipe right
			else {
				languageHelp = languageHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption + 1) % numOptions; 
				teacherLanguage.setText(options[currentOption]);
				teacherLanguage.setContentDescription(options[currentOption]);
			}

			return true;
		}
	}
}