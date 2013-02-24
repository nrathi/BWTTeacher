package org.techbridgeworld.bwt.teacher;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class LanguageActivity extends Activity {

	private MyApplication application;

	private TextToSpeech tts;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector;

	private SensorManager manager;
	private ShakeEventListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_language);

		application = ((MyApplication) getApplicationContext());

		String[] options = new String[3];
		options[0] = getResources().getString(R.string.english);
		options[1] = getResources().getString(R.string.kannada);
		options[2] = getResources().getString(R.string.hindi);

		application.options = options;
		application.currentOption = 0;

		application.prompt = getResources().getString(R.string.language_prompt);
		application.help = getResources().getString(R.string.language_help);
		application.textView = (TextView) findViewById(R.id.language_textview);

		tts = application.myTTS;
		application.speakOut(application.prompt);

		detector = new GestureDetectorCompat(this, new MyGestureListener());

		manager = application.myManager;
		listener = application.myListener;
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
	public boolean onTouchEvent(MotionEvent event) {
		this.detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {
			// If the user swipes up, go the home activity
			if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(LanguageActivity.this,
						HomeActivity.class);
				startActivity(intent);
			}

			// If the user swipes down, go to the category activity
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				switch (application.currentOption) {
				case 0:
					Intent intent = new Intent(LanguageActivity.this,
							CategoryActivity.class);
					intent.putExtra("language", application.getOption());
					startActivity(intent);
					break;
				case 1:
					break;
				case 2:
					break;
				default:
				}
			}

			// If the user swipes left, go to the option on the left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				application.moveLeft();
				application.changeText();
			}

			// If the user swipes right, go to the option on the right
			else {
				application.moveRight();
				application.changeText();
			}

			return true;
		}
	}
}