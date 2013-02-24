package org.techbridgeworld.bwt.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PasswordActivity extends Activity {

	private MyApplication application;

	private TextToSpeech tts;

	private Vibrator vibrator;

	private SensorManager manager;
	private ShakeEventListener listener;

	private Button one, two, three, four;
	private String actualPassword = "1123";
	private String enteredPassword = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_password);

		application = ((MyApplication) getApplicationContext());

		OnClickListener[] listeners = new OnClickListener[4];
		for (int i = 0; i < listeners.length; i++) {
			final int j = i + 1;
			listeners[i] = new OnClickListener() {
				@Override
				public void onClick(View v) {
					enteredPassword += j;
				}
			};
		}

		one = (Button) findViewById(R.id.one);
		one.setOnClickListener(listeners[0]);

		two = (Button) findViewById(R.id.two);
		two.setOnClickListener(listeners[1]);

		three = (Button) findViewById(R.id.three);
		three.setOnClickListener(listeners[2]);

		four = (Button) findViewById(R.id.four);
		four.setOnClickListener(listeners[3]);

		application.prompt = getResources().getString(R.string.password_prompt);
		application.help = getResources().getString(R.string.password_help);

		tts = application.myTTS;
		application.speakOut(application.prompt);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		manager = application.myManager;
		listener = application.myListener;

		// If this is the first run of the password activity, give them a more
		// detailed prompt
		SharedPreferences prefs = getSharedPreferences("BWT", 0);
		if (prefs.getBoolean("firstRunPassword", true)) {
			application.prompt = getResources().getString(
					R.string.password_prompt_first);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstRunPassword", false);
			editor.commit();
		}

		// Check if the entered password is correct or incorrect every 500 ms
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (enteredPassword.equals(actualPassword)) {
					Intent intent = new Intent(PasswordActivity.this,
							HomeActivity.class);
					startActivity(intent);
				} else if (enteredPassword.length() >= 4) {
					int dot = 500;
					long[] pattern = { 0, dot, dot, dot };
					vibrator.vibrate(pattern, -1);
					enteredPassword = "";
					handler.postDelayed(this, 500);
				} else
					handler.postDelayed(this, 500);
			}
		};
		handler.postDelayed(runnable, 500);
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
}