package org.techbridgeworld.bwt.teacher;

import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.widget.TextView;

public class PlaybackActivity extends Activity {

	private MyApplication application;

	private TextToSpeech tts;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector;

	private SensorManager manager;
	private ShakeEventListener listener;

	private MediaPlayer player;

	private String dir;

	private String language;
	private int category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_playback);

		application = ((MyApplication) getApplicationContext());

		Bundle extras = getIntent().getExtras();
		language = (String) extras.get("language");
		category = (Integer) extras.get("category");
		application.currentOption = (Integer) extras.get("currentOption");

		application.prompt = getResources().getString(R.string.playback_prompt);
		application.help = getResources().getString(R.string.playback_help);
		application.help = application.help.replace("yyy", language);
		application.textView = (TextView) findViewById(R.id.playback_textview);

		// Change options, prompt, help, and textView based on the category
		String options[];
		switch (category) {
		case 0:
			options = new String[6];
			options[0] = getResources().getString(R.string.one);
			options[1] = getResources().getString(R.string.two);
			options[2] = getResources().getString(R.string.three);
			options[3] = getResources().getString(R.string.four);
			options[4] = getResources().getString(R.string.five);
			options[5] = getResources().getString(R.string.six);

			application.options = options;
			application.prompt = application.prompt.replace("xxx", "number");
			application.help = application.help.replace("zzz", "numbers");
			application.textView.setText(application.getOption());
			application.textView.setContentDescription(application.getOption());
			break;
		case 1:
			options = new String[26];
			options[0] = getResources().getString(R.string.a);
			options[1] = getResources().getString(R.string.b);
			options[2] = getResources().getString(R.string.c);
			options[3] = getResources().getString(R.string.d);
			options[4] = getResources().getString(R.string.e);
			options[5] = getResources().getString(R.string.f);
			options[6] = getResources().getString(R.string.g);
			options[7] = getResources().getString(R.string.h);
			options[8] = getResources().getString(R.string.i);
			options[9] = getResources().getString(R.string.j);
			options[10] = getResources().getString(R.string.k);
			options[11] = getResources().getString(R.string.l);
			options[12] = getResources().getString(R.string.m);
			options[13] = getResources().getString(R.string.n);
			options[14] = getResources().getString(R.string.o);
			options[15] = getResources().getString(R.string.p);
			options[16] = getResources().getString(R.string.q);
			options[17] = getResources().getString(R.string.r);
			options[18] = getResources().getString(R.string.s);
			options[19] = getResources().getString(R.string.t);
			options[20] = getResources().getString(R.string.u);
			options[21] = getResources().getString(R.string.v);
			options[22] = getResources().getString(R.string.w);
			options[23] = getResources().getString(R.string.x);
			options[24] = getResources().getString(R.string.y);
			options[25] = getResources().getString(R.string.z);

			application.options = options;
			application.prompt = application.prompt.replace("xxx", "letter");
			application.help = application.help.replace("zzz", "letters");
			application.textView.setText(application.getOption());
			application.textView.setContentDescription(application.getOption());
			break;
		case 2:
			options = new String[17];
			options[0] = getResources().getString(R.string.animal_game);
			options[1] = getResources().getString(R.string.dot_practice);
			options[2] = getResources().getString(R.string.find_dot);
			options[3] = getResources().getString(R.string.free_play);
			options[4] = getResources().getString(R.string.free_spelling);
			options[5] = getResources().getString(R.string.good);
			options[6] = getResources().getString(R.string.learn_dots);
			options[7] = getResources().getString(R.string.learn_letters);
			options[8] = getResources().getString(R.string.letter_practice);
			options[9] = getResources().getString(R.string.no);
			options[10] = getResources().getString(R.string.now_press);
			options[11] = getResources().getString(R.string.please_press);
			options[12] = getResources().getString(R.string.please_write);
			options[13] = getResources().getString(
					R.string.please_write_the_animal);
			options[14] = getResources().getString(R.string.press);
			options[15] = getResources()
					.getString(R.string.to_write_the_letter);
			options[16] = getResources().getString(R.string.try_again);

			application.options = options;
			application.prompt = application.prompt.replace("xxx", "phrase");
			application.help = application.help.replace("zzz", "phrases");
			application.textView.setText(application.getOption());
			application.textView.setContentDescription(application.getOption());
			break;
		default:
		}

		tts = application.myTTS;
		application.speakOut(application.prompt);

		detector = new GestureDetectorCompat(this, new MyGestureListener());

		manager = application.myManager;
		listener = application.myListener;

		player = new MediaPlayer();
		dir = getApplicationContext().getFilesDir().getPath().toString();

		// If the user hovers on textView, play back the corresponding sound
		application.textView.setOnHoverListener(new OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {
				if (!player.isPlaying()) {
					FileInputStream fis;
					try {
						String filename = application.getOption().replaceAll(
								" ", "_");
						fis = new FileInputStream(dir + "/" + filename + ".m4a");
						player.reset();
						player.setDataSource(fis.getFD());
						fis.close();
						player.prepare();
						player.start();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return true;
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
	protected void onStop() {
		if (player != null)
			player.release();
		super.onStop();
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
			// If the user swipes down, go to the category activity
			if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(PlaybackActivity.this,
						CategoryActivity.class);
				intent.putExtra("language", language);
				startActivity(intent);
			}

			// If the user swipes down, go to the record activity
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(PlaybackActivity.this,
						RecordActivity.class);
				intent.putExtra("language", language);
				intent.putExtra("category", category);
				intent.putExtra("currentOption", application.currentOption);
				intent.putExtra("options", application.options);
				startActivity(intent);
			}

			// If the user swipes right, go to the next option on the left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				application.moveLeft();
				application.changeText();
			}

			// If the user swipes right, go to the next option on the right
			else {
				application.moveRight();
				application.changeText();
			}

			return true;
		}
	}
}