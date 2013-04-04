package org.techbridgeworld.bwt.teacher;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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

		String options[] = null;
		switch (application.game) {
		//Hangman
		case 0: 
			options = new String[3]; 
			options[0] = getResources().getString(R.string.numbers);
			options[1] = getResources().getString(R.string.letters);
			options[2] = getResources().getString(R.string.phrases);
			break;
		//Animal Game
		case 1: 
			options = new String[3];
			options[0] = getResources().getString(R.string.numbers);
			options[1] = getResources().getString(R.string.letters);
			options[2] = getResources().getString(R.string.phrases);
			break;
		//Learn Letters
		case 2:
			options = new String[3];
			options[0] = getResources().getString(R.string.numbers);
			options[1] = getResources().getString(R.string.letters);
			options[2] = getResources().getString(R.string.phrases);
			break;
		//Learn Dots
		case 3:
			options = new String[2];
			options[0] = getResources().getString(R.string.numbers);
			options[1] = getResources().getString(R.string.phrases);
			break;
		default:
		}
		
		buttons = new Button[3];
		buttons[0] = (Button) findViewById(R.id.one);
		buttons[1] = (Button) findViewById(R.id.two);
		buttons[2] = (Button) findViewById(R.id.three);
		
		for(int i = 0; i < options.length; i++) {
			final int j = i; 
			buttons[i].setText(options[i]);
			buttons[i].setContentDescription(options[i]);
			buttons[i].setVisibility(View.VISIBLE);
			buttons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					application.category = application.game * 3 + j;
					Intent intent = new Intent(CategoryActivity.this, OptionsActivity.class);
					startActivity(intent);
				}
			}); 
		}
		
		manager = application.myManager;
		listener = application.myListener;
		
		tts = application.myTTS;
		application.speakOut(application.prompt);
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