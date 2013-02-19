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

public class CategoryActivity extends Activity implements TextToSpeech.OnInitListener {

	private TextToSpeech tts;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector; 
	
	private SensorManager manager;
	private ShakeEventListener listener;
	
	private String categoryPrompt;
	private String categoryHelp;
	private TextView teacherCategory;
	
	private String language;
	
	private String[] options;
	private int numOptions = 3; 
	private int currentOption = 0; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_category);

		Bundle extras = getIntent().getExtras();
		language = (String) extras.get("language");
		
		options = new String[numOptions]; 
		options[0] = getResources().getString(R.string.numbers);
		options[1] = getResources().getString(R.string.letters);
		options[2] = getResources().getString(R.string.phrases);
		
		categoryPrompt = getResources().getString(R.string.category_prompt);
		categoryHelp = getResources().getString(R.string.category_help);
		categoryHelp = categoryHelp.replace("yyy", language); 
		teacherCategory = (TextView) findViewById(R.id.teacher_category);
		
		tts = new TextToSpeech(this, this);
		detector = new GestureDetectorCompat(this, new MyGestureListener());
		
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listener = new ShakeEventListener();   
		listener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				categoryHelp = categoryHelp.replace("xxx", options[currentOption].toUpperCase(Locale.getDefault()));
				speakOut(categoryHelp);
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
				speakOut(categoryPrompt);
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
				Intent intent = new Intent(CategoryActivity.this, LanguageActivity.class);
				startActivity(intent);
			}

			// Swipe down
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(CategoryActivity.this, PlaybackActivity.class);
				intent.putExtra("language", language); 
				intent.putExtra("category", currentOption);
				intent.putExtra("currentOption", 0); 
				startActivity(intent);
			}
			
			// Swipe left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				categoryHelp = categoryHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption - 1) % numOptions; 
				if(currentOption == -1) 
					currentOption += numOptions;
				teacherCategory.setText(options[currentOption]);
				teacherCategory.setContentDescription(options[currentOption]);
			}
			
			// Swipe right
			else {
				categoryHelp = categoryHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption + 1) % numOptions;
				teacherCategory.setText(options[currentOption]);
				teacherCategory.setContentDescription(options[currentOption]);
			}

			return true;
		}
	}
}