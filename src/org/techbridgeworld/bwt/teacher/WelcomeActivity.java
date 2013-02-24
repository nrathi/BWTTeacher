package org.techbridgeworld.bwt.teacher;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.techbridgeworld.bwt.teacher.R.raw;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class WelcomeActivity extends Activity {

	private MyApplication application;

	private TextToSpeech tts;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector;

	private SensorManager manager;
	private ShakeEventListener listener;

	private String rotate180;
	private String rotate90Right;
	private String rotate90Left;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_welcome);

		application = ((MyApplication) getApplicationContext());

		application.prompt = getResources().getString(R.string.welcome_prompt);
		application.help = getResources().getString(R.string.welcome_help);

		rotate180 = getResources().getString(R.string.rotate_180);
		rotate90Right = getResources().getString(R.string.rotate_90_right);
		rotate90Left = getResources().getString(R.string.rotate_90_left);

		tts = application.myTTS;
		application.speakOut(application.prompt);

		detector = new GestureDetectorCompat(this, new MyGestureListener());

		manager = application.myManager;
		listener = application.myListener;

		// If this is the first run of the welcome activity, save the audio
		// files on internal storage
		SharedPreferences prefs = getSharedPreferences("BWT", 0);
		if (prefs.getBoolean("firstRunWelcome", true)) {
			application.prompt = getResources().getString(
					R.string.welcome_prompt_first);
			Class<raw> raw = R.raw.class;
			Field[] fields = raw.getFields();
			for (Field field : fields) {
				try {
					InputStream is = getResources().openRawResource(
							field.getInt(null));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					int size = 0;
					byte[] buffer = new byte[1024];
					while ((size = is.read(buffer, 0, 1024)) >= 0) {
						baos.write(buffer, 0, size);
					}
					is.close();
					buffer = baos.toByteArray();

					FileOutputStream fos = openFileOutput(field.getName()
							+ ".m4a", 0);
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

	// If the user presses back, go to the home screen
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
	public boolean onTouchEvent(MotionEvent event) {
		this.detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {
			// If the user swipes up instead of down, tell them to rotate the
			// phone 180 degrees
			if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				application.speakOut(rotate180);
			}

			// If the user swipes down, go to the password activity
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(WelcomeActivity.this,
						PasswordActivity.class);
				startActivity(intent);
			}

			// If the user swipes left instead of down, tell them to rotate the
			// phone 90 degrees to the right
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				application.speakOut(rotate90Right);
			}

			// If the user swipes right instead of down, tell them to rotate the
			// phone 90 degrees to the left
			else {
				application.speakOut(rotate90Left);
			}

			return true;
		}
	}
}
