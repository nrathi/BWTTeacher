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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameActivity extends Activity {

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
		
		application.prompt = getResources().getString(R.string.game_prompt);
		application.help = getResources().getString(R.string.game_help);
		
		String[] options = new String[4];
		options[0] = getResources().getString(R.string.hangman);
		options[1] = getResources().getString(R.string.animal_game);
		options[2] = getResources().getString(R.string.learn_letters);
		options[3] = getResources().getString(R.string.learn_dots);
		
		buttons = new Button[5];
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
					application.game = j;
					Intent intent = new Intent(GameActivity.this, CategoryActivity.class);
					startActivity(intent);
				}
			}); 
		}

		// If this is the first run of the welcome activity, save the audio
		// files on internal storage
		SharedPreferences prefs = getSharedPreferences("BWT", 0);
		if (prefs.getBoolean("firstRun", true)) {
			Class<raw> raw = R.raw.class;
			Field[] fields = raw.getFields();
			for (Field field : fields) {
				try {
					InputStream is = getResources().openRawResource(field.getInt(null));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					int size = 0;
					byte[] buffer = new byte[1024];
					while ((size = is.read(buffer, 0, 1024)) >= 0) {
						baos.write(buffer, 0, size);
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
			editor.putBoolean("firstRun", false);
			editor.commit();
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
}
