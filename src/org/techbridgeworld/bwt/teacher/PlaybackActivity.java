package org.techbridgeworld.bwt.teacher;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.widget.TextView;

public class PlaybackActivity extends Activity implements TextToSpeech.OnInitListener {

	private TextToSpeech tts;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetectorCompat detector; 
	
	private SensorManager manager;
	private ShakeEventListener listener;
	
	MediaPlayer player;
	
	private String playbackPrompt;
	private String playbackHelp;
	private TextView teacherPlayback;
	
	private String language; 
	private int category;
	
	private String[] options;
	private int numOptions; 
	private int currentOption; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_playback);
		
		Bundle extras = getIntent().getExtras();
		language = (String) extras.get("language"); 
		category = (Integer) extras.get("category");
		currentOption = (Integer) extras.get("currentOption"); 
		
		playbackPrompt = getResources().getString(R.string.playback_prompt);
		playbackHelp = getResources().getString(R.string.playback_help);
		playbackHelp = playbackHelp.replace("yyy", language);
		teacherPlayback = (TextView) findViewById(R.id.teacher_playback);
		
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listener = new ShakeEventListener();   
		listener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				playbackHelp = playbackHelp.replace("xxx", options[currentOption].toUpperCase(Locale.getDefault()));
				speakOut(playbackHelp);
			}
		});
		
		tts = new TextToSpeech(this, this);
		detector = new GestureDetectorCompat(this, new MyGestureListener());
		
		switch(category) {
			case 0:
				numOptions = 6;
				options = new String[numOptions]; 
				options[0] = getResources().getString(R.string.one);
				options[1] = getResources().getString(R.string.two);
				options[2] = getResources().getString(R.string.three);
				options[3] = getResources().getString(R.string.four);
				options[4] = getResources().getString(R.string.five);
				options[5] = getResources().getString(R.string.six);
				
				playbackPrompt = playbackPrompt.replace("xxx", "number");
				playbackHelp = playbackHelp.replace("zzz", "numbers");
				teacherPlayback.setText(options[currentOption]);
				teacherPlayback.setContentDescription(options[currentOption]); 
				break;
			case 1:
				numOptions = 26;
				options = new String[numOptions]; 
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
				
				playbackPrompt = playbackPrompt.replace("xxx", "letter");
				playbackHelp = playbackHelp.replace("zzz", "letters");
				teacherPlayback.setText(options[currentOption]);
				teacherPlayback.setContentDescription(options[currentOption]); 
				break;
			case 2:				
				numOptions = 15; 
				options = new String[numOptions]; 
				options[0] = getResources().getString(R.string.dot_practice);
				options[1] = getResources().getString(R.string.find_dot);
				options[2] = getResources().getString(R.string.free_play);
				options[3] = getResources().getString(R.string.free_spelling);
				options[4] = getResources().getString(R.string.good);
				options[5] = getResources().getString(R.string.learn_dots);
				options[6] = getResources().getString(R.string.learn_letters);
				options[7] = getResources().getString(R.string.letter_practice);
				options[8] = getResources().getString(R.string.no);
				options[9] = getResources().getString(R.string.now_press);
				options[10] = getResources().getString(R.string.please_press);
				options[11] = getResources().getString(R.string.please_write);
				options[12] = getResources().getString(R.string.press);
				options[13] = getResources().getString(R.string.to_write_the_letter);
				options[14] = getResources().getString(R.string.try_again);
				
				playbackPrompt = playbackPrompt.replace("xxx", "phrase");
				playbackHelp = playbackHelp.replace("zzz", "phrases");
				teacherPlayback.setText(options[currentOption]);
				teacherPlayback.setContentDescription(options[currentOption]); 
				break;
			default:
		}
		
		final String dir = getApplicationContext().getFilesDir().getPath().toString();
		player = new MediaPlayer();
		
		teacherPlayback.setOnHoverListener(new OnHoverListener() {
			@Override
			public boolean onHover(View v, MotionEvent event) {
				FileInputStream fis;
				try {
					String filename = options[currentOption].replaceAll(" ", "_");
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
				speakOut(playbackPrompt);
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
				Intent intent = new Intent(PlaybackActivity.this, CategoryActivity.class);
				intent.putExtra("language", language);
				startActivity(intent);
			}
			
			// Swipe down
			else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				Intent intent = new Intent(PlaybackActivity.this, RecordActivity.class);
				intent.putExtra("language", language);
				intent.putExtra("category", category);
				intent.putExtra("currentOption", currentOption); 
				intent.putExtra("options", options);
				startActivity(intent);
			}
			
			// Swipe left
			else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				playbackHelp = playbackHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption - 1) % options.length; 
				if(currentOption == -1) 
					currentOption += options.length;
				teacherPlayback.setText(options[currentOption]);
				teacherPlayback.setContentDescription(options[currentOption]);
			}
			
			// Swipe right
			else {
				playbackHelp = playbackHelp.replace(options[currentOption].toUpperCase(Locale.getDefault()), "xxx");
				currentOption = (currentOption + 1) % options.length; 
				teacherPlayback.setText(options[currentOption]);
				teacherPlayback.setContentDescription(options[currentOption]);
			}

			return true;
		}
	}
}