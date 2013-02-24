package org.techbridgeworld.bwt.teacher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.widget.TextView;

public class RecordActivity extends Activity {

	private MyApplication application;

	private TextToSpeech tts;

	private Vibrator vibrator;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector;

	private SensorManager manager;
	private ShakeEventListener listener;

	private MediaRecorder recorder;
	private MediaPlayer player;

	private Context context;
	private String dir;
	private String filename;

	private String language;
	private int category;

	private boolean recording;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_record);

		application = ((MyApplication) getApplicationContext());

		Bundle extras = getIntent().getExtras();
		language = (String) extras.get("language");
		category = (Integer) extras.get("category");
		application.options = (String[]) extras.get("options");
		application.currentOption = (Integer) extras.get("currentOption");
		recording = false;

		application.prompt = getResources().getString(R.string.record_prompt);
		application.prompt = application.prompt.replace("xxx",
				application.getOption());
		application.help = getResources().getString(R.string.record_help);
		application.textView = (TextView) findViewById(R.id.record_textview);

		manager = application.myManager;
		listener = application.myListener;

		// If this is the first run of the record activity, give them a more
		// detailed prompt
		SharedPreferences prefs = getSharedPreferences("BWT", 0);
		if (prefs.getBoolean("firstRunRecord", true)) {
			application.prompt = getResources().getString(
					R.string.record_prompt_first);
			application.prompt = application.prompt.replace("xxx",
					application.getOption());
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstRunRecord", false);
			editor.commit();
		}

		tts = application.myTTS;
		application.speakOut(application.prompt);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		detector = new GestureDetectorCompat(this, new MyGestureListener());

		context = getApplicationContext();
		dir = context.getFilesDir().getPath().toString();
		recorder = new MediaRecorder();
		player = new MediaPlayer();

		// If the user hovers on textView, vibrate once to indicate the
		// beginning of a recording; when the user releases, vibrate again
		// to indicate the end of the recording
		application.textView.setOnHoverListener(new OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {
				if (recording == false) {
					filename = application.getOption().replaceAll(" ", "_");
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					recorder.setOutputFile(dir + "/" + filename + "_temp.m4a");

					try {
						recorder.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					vibrator.vibrate(200);
					recorder.start();
					recording = true;
				} else {
					recorder.reset();
					vibrator.vibrate(200);

					FileInputStream fis;
					try {
						fis = new FileInputStream(dir + "/" + filename
								+ "_temp.m4a");
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
					recording = false;
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
		if (recorder != null)
			recorder.release();
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
			// If the user swipes down, save the recording and go to the
			// playback activity
			if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				context.deleteFile(filename + "_temp.m4a");

				Intent intent = new Intent(RecordActivity.this,
						PlaybackActivity.class);
				intent.putExtra("language", language);
				intent.putExtra("category", category);
				intent.putExtra("currentOption", application.currentOption);
				startActivity(intent);
			}

			// If the user swipes down, don't save the recording and go to the
			// playback activity
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				context.deleteFile(filename + ".m4a");
				File oldFile = context
						.getFileStreamPath(filename + "_temp.m4a");
				File newFile = context.getFileStreamPath(filename + ".m4a");
				oldFile.renameTo(newFile);

				Intent intent = new Intent(RecordActivity.this,
						PlaybackActivity.class);
				intent.putExtra("language", language);
				intent.putExtra("category", category);
				intent.putExtra("currentOption", application.currentOption);
				startActivity(intent);
			}

			// If the user swipes right, save the recording and go to the next
			// option on the left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				context.deleteFile(filename + ".m4a");
				File oldFile = context
						.getFileStreamPath(filename + "_temp.m4a");
				File newFile = context.getFileStreamPath(filename + ".m4a");
				oldFile.renameTo(newFile);

				application.moveRight();
				application.changePrompt();
			}

			// If the user swipes right, save the recording and go to the next
			// option on the left
			else {
				context.deleteFile(filename + ".m4a");
				File oldFile = context
						.getFileStreamPath(filename + "_temp.m4a");
				File newFile = context.getFileStreamPath(filename + ".m4a");
				oldFile.renameTo(newFile);

				application.moveRight();
				application.changePrompt();
			}

			return true;
		}
	}
}