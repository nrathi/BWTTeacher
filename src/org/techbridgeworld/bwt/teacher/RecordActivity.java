package org.techbridgeworld.bwt.teacher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

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
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.widget.TextView;

public class RecordActivity extends Activity implements TextToSpeech.OnInitListener {

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
	
	private String recordPrompt;
	private String recordHelp;
	private TextView teacherRecord;
	
	private String language;
	private int category;
	private String[] options;
	private int currentOption;

	private boolean recording; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_record);
		
		Bundle extras = getIntent().getExtras();
		language = (String) extras.get("language"); 
		category = (Integer) extras.get("category"); 
		options = (String[]) extras.get("options");
		currentOption = (Integer) extras.get("currentOption"); 
		recording = false;
		
		recordPrompt = getResources().getString(R.string.record_prompt);
		recordPrompt = recordPrompt.replace("xxx", options[currentOption]);
		recordHelp = getResources().getString(R.string.record_help);
		teacherRecord = (TextView) findViewById(R.id.teacher_record);
		
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listener = new ShakeEventListener();   
		listener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				recordHelp = recordHelp.replace("xxx", options[currentOption].toUpperCase(Locale.getDefault()));
				speakOut(recordHelp);
			}
		});
		
		SharedPreferences prefs = getSharedPreferences("BWT", 0);
		if(prefs.getBoolean("firstRunRecord", true)) {
			recordPrompt = getResources().getString(R.string.record_prompt_first);
			recordPrompt = recordPrompt.replace("xxx", options[currentOption]);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstRunRecord", false);
			editor.commit();
		}
		
		tts = new TextToSpeech(this, this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		detector = new GestureDetectorCompat(this, new MyGestureListener());

		context = getApplicationContext(); 
		dir = context.getFilesDir().getPath().toString();
		recorder = new MediaRecorder();
		player = new MediaPlayer();
		
		teacherRecord.setOnHoverListener(new OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {
				if(recording == false) {
					filename = options[currentOption].replaceAll(" ", "_"); 
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
				}
				else {
					recorder.reset();
		        	vibrator.vibrate(200);
		        	
					FileInputStream fis;
					try {
						fis = new FileInputStream(dir + "/" + filename + "_temp.m4a");
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
		if(recorder != null)
			recorder.release();
		if(player != null)
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
				speakOut(recordPrompt);
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
				context.deleteFile(filename + "_temp.m4a");
				
				Intent intent = new Intent(RecordActivity.this, PlaybackActivity.class);
				intent.putExtra("language", language);
				intent.putExtra("category", category); 
				intent.putExtra("currentOption", currentOption);
				startActivity(intent);
			}
			
			// Swipe down
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				context.deleteFile(filename + ".m4a");
				File oldFile = context.getFileStreamPath(filename + "_temp.m4a");
				File newFile = context.getFileStreamPath(filename + ".m4a");
				oldFile.renameTo(newFile); 
				
				Intent intent = new Intent(RecordActivity.this, PlaybackActivity.class);
				intent.putExtra("language", language);
				intent.putExtra("category", category); 
				intent.putExtra("currentOption", currentOption);
				startActivity(intent);
			}
			
			// Swipe left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				context.deleteFile(filename + ".m4a");
				File oldFile = context.getFileStreamPath(filename + "_temp.m4a");
				File newFile = context.getFileStreamPath(filename + ".m4a");
				oldFile.renameTo(newFile); 
				
				recordHelp = recordHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption - 1) % options.length; 
				if(currentOption == -1) 
					currentOption += options.length;
				recordPrompt = "Record " + options[currentOption];
				speakOut(recordPrompt);
			}
			
			// Swipe right
			else {
				context.deleteFile(filename + ".m4a");
				File oldFile = context.getFileStreamPath(filename + "_temp.m4a");
				File newFile = context.getFileStreamPath(filename + ".m4a");
				oldFile.renameTo(newFile); 
				
				recordHelp = recordHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption + 1) % options.length;
				recordPrompt = "Record " + options[currentOption];
				speakOut(recordPrompt);
			}

			return true;
		}
	}
}