package org.techbridgeworld.bwt.teacher;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CategoryActivity extends Activity {

	private MyApplication application;
	private TextToSpeech tts;
	private Button[] buttons;

	private SensorManager manager;
	private ShakeEventListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		application = ((MyApplication) getApplicationContext());
		
		application.prompt = getResources().getString(R.string.category_prompt);
		application.help = getResources().getString(R.string.category_help);
		
		tts = application.myTTS;
		application.speakOut(application.prompt);
		
		manager = application.myManager;
		listener = application.myListener;

		String options[] = null;
		switch (application.game) {
		//Learn Dots
		case 0: 
			options = new String[2];
			options[0] = getResources().getString(R.string.numbers);
			options[1] = getResources().getString(R.string.instructions);
			break;
		//Animal Game and Learn Letters
		case 1:
		case 2: 
			options = new String[3];
			options[0] = getResources().getString(R.string.numbers);
			options[1] = getResources().getString(R.string.letters);
			options[2] = getResources().getString(R.string.instructions);
			break;
		//Hangman
		case 3:
			options = new String[4]; 
			options[0] = getResources().getString(R.string.numbers);
			options[1] = getResources().getString(R.string.letters);
			options[2] = getResources().getString(R.string.instructions);
			options[3] = getResources().getString(R.string.words);
			break;
		default:
		}
		
		buttons = new Button[4];
		buttons[0] = (Button) findViewById(R.id.one);
		buttons[1] = (Button) findViewById(R.id.two);
		buttons[2] = (Button) findViewById(R.id.three);
		buttons[3] = (Button) findViewById(R.id.four);
		
		for(int i = 0; i < options.length; i++) {
			final int j = i; 
			buttons[i].setText(options[i]);
			buttons[i].setContentDescription(options[i]);
			buttons[i].setVisibility(View.VISIBLE);
			buttons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					application.category = application.game * 3 + j - 1;
					application.category += application.game == 0 ? 1 : 0;
					Intent intent = new Intent(CategoryActivity.this, OptionsActivity.class);
					startActivity(intent);
				}
			}); 
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
		tts.stop();
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(CategoryActivity.this, GameActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}