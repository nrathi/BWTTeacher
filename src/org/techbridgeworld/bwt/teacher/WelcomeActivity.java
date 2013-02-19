package org.techbridgeworld.bwt.teacher;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Locale;

import org.techbridgeworld.bwt.teacher.R.raw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class WelcomeActivity extends Activity implements TextToSpeech.OnInitListener {

	private TextToSpeech tts;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector;

	private SensorManager manager;
	private ShakeEventListener listener;

	private String welcomePrompt;
	private String welcomeHelp;
	private String rotate180;
	private String rotate90Right;
	private String rotate90Left; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_welcome);

		welcomePrompt = getResources().getString(R.string.welcome_prompt);
		welcomeHelp = getResources().getString(R.string.welcome_help);
		rotate180 = getResources().getString(R.string.rotate_180);
		rotate90Right = getResources().getString(R.string.rotate_90_right);
		rotate90Left = getResources().getString(R.string.rotate_90_left);

		tts = new TextToSpeech(this, this);
		detector = new GestureDetectorCompat(this, new MyGestureListener());

		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listener = new ShakeEventListener();   
		listener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				speakOut(welcomeHelp);
			}
		});
		
		SharedPreferences prefs = getSharedPreferences("BWT", 0);
		if(prefs.getBoolean("firstRunWelcome", true)) {
			welcomePrompt = getResources().getString(R.string.welcome_prompt_first);
			Class<raw> raw = R.raw.class;
			Field[] fields = raw.getFields();
			for (Field field : fields) {
				try {
					InputStream is = getResources().openRawResource(field.getInt(null));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					int size = 0;
					byte[] buffer = new byte[1024];
					while ((size = is.read(buffer, 0, 1024)) >= 0) {
						baos.write(buffer,0,size);
					}

					is.close();
					buffer = baos.toByteArray();

					FileOutputStream fos = openFileOutput(field.getName() + ".m4a", 0);
					fos.write(buffer);
					fos.close();
				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstRunWelcome", false);
			editor.commit();
		}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Intent intent = new Intent(Intent.ACTION_MAIN);
	    	intent.addCategory(Intent.CATEGORY_HOME);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
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
				speakOut(welcomePrompt);
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
				speakOut(rotate180);
			}

			// Swipe down
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(WelcomeActivity.this, PasswordActivity.class);
				startActivity(intent);
			}

			// Swipe left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				speakOut(rotate90Right);
			}

			// Swipe right
			else {
				speakOut(rotate90Left);
			}

			return true;
		}
	}
}
